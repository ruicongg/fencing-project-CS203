package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.HashSet;
import java.util.List;

import org.fencing.demo.events.Event;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.fencing.demo.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AftEventIntegrationTest extends BaseIntegrationTest {

    private Player player1;
    private Player player2;
    private Match grpMatch;
    private Match knockoutMatch;

    @BeforeEach
    void setUp() {
        super.setUp();

        // Save players
        player1 = createValidPlayer(1);
        player2 = createValidPlayer(2);
        playerRepository.save(player1);
        playerRepository.save(player2);

        // Save PlayerRanks (only after groupStage is created)
        PlayerRank playerRank1 = createPlayerRank(player1, event, groupStage);
        PlayerRank playerRank2 = createPlayerRank(player2, event, groupStage);

        // Create and save matches
        grpMatch = createValidGroupMatch();
        groupStage.getMatches().add(grpMatch);
        matchRepository.save(grpMatch);
        groupStageRepository.save(groupStage);

        knockoutMatch = createValidKnockoutMatch();
        knockoutStage.getMatches().add(knockoutMatch);
        matchRepository.save(knockoutMatch);
        knockoutStageRepository.save(knockoutStage);

        playerRepository.save(player1);
        playerRepository.save(player2);

        playerRank1.updateAfterMatch(grpMatch.getPlayer1Score(), grpMatch.getPlayer2Score(), playerRank2);
        playerRank2.updateAfterMatch(grpMatch.getPlayer2Score(), grpMatch.getPlayer1Score(), playerRank1);

        playerRank1.updateAfterMatch(knockoutMatch.getPlayer1Score(), knockoutMatch.getPlayer2Score(), playerRank2);
        playerRank2.updateAfterMatch(knockoutMatch.getPlayer2Score(), knockoutMatch.getPlayer1Score(), playerRank1);

        event.getRankings().add(playerRank1);
        event.getRankings().add(playerRank2);
        eventRepository.save(event);

        player1.getPlayerRanks().add(playerRank1);
        player2.getPlayerRanks().add(playerRank2);
        playerRepository.save(player1);
        playerRepository.save(player2);

        System.out.println();
        System.out.println("player1ranks: " + player1.getPlayerRanks());
        System.out.println("player2ranks: " + player2.getPlayerRanks());
        System.out.println();
    }

    // @AfterEach
    // void tearDown(){
    // playerRepository.deleteAll();
    // userRepository.deleteAll();
    // matchRepository.deleteAll();
    // groupStageRepository.deleteAll();
    // knockoutStageRepository.deleteAll();
    // eventRepository.deleteAll();
    // tournamentRepository.deleteAll();

    // }

    @Test
    public void endEvent_Success() throws Exception {
        // Arrange
        // Create a URI for the endEvent endpoint
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/elo");

        System.out.println();
        for (Player player : playerRepository.findAll()) {
            System.out.println("Players at the start: " + player.getElo());
        }

        // Act
        ResponseEntity<List<Player>> response = restTemplate
            .exchange(uri, HttpMethod.PUT, new HttpEntity<>(null, createHeaders(adminToken)),
                new ParameterizedTypeReference<List<Player>>() {});

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected OK status");

        // Get the list of players from the response
        List<Player> updatedPlayers = response.getBody();

        if (updatedPlayers != null) {
            System.out.println("ELOs at the end:");
            for (Player player : updatedPlayers) {
                System.out.println("Player " + player.getId() + ": " + player.getElo());
            }
        }
    }

    @Test
    // passed
    public void endEvent_InvalidEventId_Failure() throws Exception {
        Long invalidId = -1L; // Use an invalid ID
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + invalidId + "/elo");

        ResponseEntity<String> result = restTemplate
            .exchange(uri, HttpMethod.PUT, new HttpEntity<>(null, createHeaders(adminToken)), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    // //Need check
    // need seperate set up
    @Test
    public void endEvent_IncompleteMatches_Failure() throws Exception {
        grpMatch.setPlayer1Score(0);
        grpMatch.setPlayer2Score(0);
        matchRepository.save(grpMatch);
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/elo");

        ResponseEntity<String> result = restTemplate
            .exchange(uri, HttpMethod.PUT, new HttpEntity<>(null, createHeaders(adminToken)), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void endEvent_ForbiddenForRegularUser_Failure() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events" + event.getId() + "/elo");

        ResponseEntity<String> result = restTemplate
            .exchange(uri, HttpMethod.PUT, new HttpEntity<>(null, createHeaders(userToken)), String.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    // Helper methods for creating valid entities

    private PlayerRank createPlayerRank(Player player, Event event, GroupStage groupStage) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        // playerRank.setGroupStage(groupStage);
        playerRank.initializeTempElo();
        return playerRank;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setEmail("player" + id + "@email.com");
        player.setUsername("player" + id + "_" + System.currentTimeMillis()); // Ensures unique username per player
        player.setPassword(passwordEncoder.encode("playerPass"));
        player.setElo(1500);
        player.setReached2400(false);
        player.setRole(Role.USER);
        player.setMatchesAsPlayer1(new HashSet<Match>());
        player.setMatchesAsPlayer2(new HashSet<Match>());
        player.setPlayerRanks(new HashSet<PlayerRank>());
        return player;
    }

    private Match createValidGroupMatch() {
        Match match = new Match();
        match.setGroupStage(groupStage);
        match.setEvent(event);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setPlayer1Score(4);
        match.setPlayer2Score(7);

        player1.getMatchesAsPlayer1().add(match);
        player2.getMatchesAsPlayer2().add(match);

        return match;

    }

    private Match createValidKnockoutMatch() {
        Match match = new Match();
        match.setKnockoutStage(knockoutStage);
        match.setEvent(event);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setPlayer1Score(4);
        match.setPlayer2Score(7);

        player1.getMatchesAsPlayer1().add(match);
        player2.getMatchesAsPlayer2().add(match);

        return match;

    }

}
