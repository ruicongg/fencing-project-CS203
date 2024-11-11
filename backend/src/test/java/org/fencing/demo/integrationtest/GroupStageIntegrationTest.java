package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.*;

import org.fencing.demo.events.*;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.fencing.demo.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GroupStageIntegrationTest extends BaseIntegrationTest{

    List<Player> players;
    @BeforeEach
    void setUp() {

        super.setUp();
        players = new ArrayList<>();

        //add 8 player into database
        for(int i = 1; i <= 8; i++){
                Player tempPlayer = new Player("player" + i, passwordEncoder.encode("password" + i), "player" + i + "@example.com", Role.USER);
                tempPlayer.setElo(1700);
                playerRepository.save(tempPlayer);
                players.add(tempPlayer);
        }
        for(Player p:players){
                event.getRankings().add(createPlayerRank(p, event));
        }
        event = eventRepository.save(event);
    }


    @Test
    public void getGroupStage_ValidGroupStageId_Success() throws Exception {
        Long id = groupStage.getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        ResponseEntity<GroupStage> result = restTemplate.getForEntity(uri, GroupStage.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(groupStage.getId(), result.getBody().getId());
    }

    @Test
    public void getGroupStage_InvalidGroupStageId_Failure() throws Exception {
        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/999");

        ResponseEntity<GroupStage> result = restTemplate.getForEntity(uri, GroupStage.class);

        assertEquals(404, result.getStatusCode().value());
    }


    @Test
    public void addGroupStage_RegularUser_Failure() throws Exception {
        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage");


        HttpEntity<GroupStage> request = new HttpEntity<>(groupStage, createHeaders(userToken));
        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, GroupStage.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateGroupStage_AdminUser_Success() throws Exception {
        // Create a valid GroupStage
        Long id = groupStage.getId();

        // System.out.println("Before adding match:"+groupStage);
        // System.out.println("GroupStage ID:" + groupStage.getId());
        // System.out.println("Matches:" + groupStage.getMatches());
        // Create the URI for the PUT request
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        // Add a new match to the GroupStage
        Match newMatch = Match.builder()
                .player1(players.get(1))
                .player2(players.get(2))
                .player1Score(15)
                .player2Score(10)
                .event(event) // Make sure event is associated with the match
                .groupStage(groupStage)
                .build();
        matchRepository.save(newMatch);
        groupStage.getMatches().add(newMatch);

        // Create the HTTP request with the updated GroupStage
        HttpEntity<GroupStage> request = new HttpEntity<>(groupStage, createHeaders(adminToken));

        // Execute the PUT request

        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, GroupStage.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(groupStage.getId(), result.getBody().getId());
        // Assert the results
        // assertEquals("smtth", result.getBody());
    }

    @Test
    public void updateGroupStage_RegularUser_Failure() throws Exception {
        Long id = groupStage.getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        GroupStage updatedGroupStage = GroupStage.builder()
                .id(id)
                .event(event)
                .build();

        HttpEntity<GroupStage> request = new HttpEntity<>(updatedGroupStage, createHeaders(userToken));
        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, GroupStage.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateGroupStage_InvalidId_Failure() throws Exception {
        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/999");

        GroupStage updateGroupStage = GroupStage.builder()
                .id(999L)
                .event(event)
                .build();

        HttpEntity<GroupStage> request = new HttpEntity<>(updateGroupStage, createHeaders(adminToken));
        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, GroupStage.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteGroupStage_AdminUser_Success() throws Exception {
        // Create and save a valid GroupStage

        Long id = groupStage.getId();

        // Construct the URI for the DELETE request
        URI uri = URI.create(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        // Log the ID of the GroupStage to be deleted
        // System.out.println("Deleting GroupStage with ID: " + id);

        // Create the HTTP request entity with headers
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));

        // Execute the DELETE request
        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);
        // Log the response status
        // System.out.println("Response Status: " + result.getStatusCode());

        // Assert the response status code and database state
        assertEquals(204, result.getStatusCode().value());
        // Verify that the GroupStage has been deleted
    }

    @Test
    public void deleteGroupStage_RegularUser_Failure() throws Exception {
        Long id = groupStage.getId();
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(403, result.getStatusCode().value());
        assertTrue(groupStageRepository.existsById(id));
    }

    @Test
    public void deleteGroupStage_InvalidId_Failure() throws Exception {
        Long id = 999L;
        assertFalse(groupStageRepository.existsById(id));
        URI uri = URI.create(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/999");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }


    private PlayerRank createPlayerRank(Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        return playerRank;
    }


}
