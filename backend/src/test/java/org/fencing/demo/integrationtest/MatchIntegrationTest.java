package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.time.*;
import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.fencing.demo.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        addPlayersToEvent(event, groupStage);

    }

    // @Test
    // public void addInitialMatchForGroupStage_NullGroupStageFound_Failure() throws
    // Exception {

    // URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
    // "/events/" + event.getId() + "/groupStage/matches");

    // // When: Sending a POST request to add matches for a non-existent group stage
    // ResponseEntity<String> result = restTemplate.withBasicAuth("admin",
    // "adminPass")
    // .postForEntity(uri, null, String.class);

    // // Then: Assert that the request fails with an appropriate error message
    // assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    // assertTrue(result.getBody().contains("No groupStage found for event"));
    // }

    // @Test
    // public void addMatchesForKnockoutStage_NullKnockoutStage_Failure() throws
    // Exception {

    // URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
    // "/events/" + event.getId() + "/knockoutStage/null/matches");

    // // When: Sending a POST request to add matches for a non-existent knockout
    // stage
    // ResponseEntity<String> result = restTemplate.withBasicAuth("admin",
    // "adminPass")
    // .postForEntity(uri, null, String.class);

    // // Then: Assert that the request fails with an appropriate error message
    // assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    // assertTrue(result.getBody().contains("No KnockoutStage found for event"));
    // }

    @Test // passed
    public void addInitialMatchForGroupStage_EventNotFound_Failure() throws Exception {
        long nonExistentEventId = 999L;

        URI uri = createUrl("/tournaments/1/events/" + nonExistentEventId + "/groupStage/matches");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test // passed
    public void addMatchesForKnockoutStage_EventNotFound_Failure() throws Exception {
        long nonExistentEventId = 999L;

        URI uri = createUrl("/tournaments/1/events/" + nonExistentEventId + "/knockoutStage/1/matches");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    // Helper methods for creating valid entities
    private Event addPlayersToEvent(Event event, GroupStage groupStage) {
        SortedSet<PlayerRank> playerRanks = event.getRankings();
        for (int i = 1; i <= 8; i++) {
            Player player = createValidPlayer(i);
            playerRepository.save(player);
            PlayerRank playerRank = createPlayerRank(player, event);
            playerRanks.add(playerRank);
        }
        event.setRankings(playerRanks);
        return eventRepository.save(event);
    }

    private PlayerRank createPlayerRank(Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        player.getPlayerRanks().add(playerRank);
        return playerRank;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setId((long) id);
        player.setElo(1200 + (id * 100));
        player.setEmail("player" + id + "@email.com");
        player.setUsername("player" + id);
        player.setPassword(passwordEncoder.encode("playerPass"));
        player.setRole(Role.USER);

        return playerRepository.save(player);
    }

}
