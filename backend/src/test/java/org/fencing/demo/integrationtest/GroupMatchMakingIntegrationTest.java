package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fencing.demo.events.Event;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class GroupMatchMakingIntegrationTest extends BaseIntegrationTest{

    @BeforeEach
    void setUp() {
        super.setUp();
        int[] elos = {1800, 1750, 1700, 1650, 1600, 1550, 1500, 1450};
        setUpWithPlayersInEvent(elos);
    }

    @Test
    void createGroupStages_Success() {
        String url = "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage";
        
        ResponseEntity<GroupStage[]> response = restTemplate
            .withBasicAuth("admin", "adminPass")
            .postForEntity(createUrl(url), null, GroupStage[].class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        GroupStage[] groupStages = response.getBody();
        assertNotNull(groupStages);
        assertEquals(2, groupStages.length);
        
        // Verify group stages are properly linked to event
        for (GroupStage stage : groupStages) {
            assertEquals(event.getId(), stage.getEvent().getId());
        }
    }

    @Test
    void createMatches_Success() {
        createGroupStages_Success();
        String url = "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/matches";
        
        ResponseEntity<Match[]> response = restTemplate
            .withBasicAuth("admin", "adminPass")
            .postForEntity(createUrl(url), null, Match[].class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Match[] matches = response.getBody();
        assertNotNull(matches);
        assertEquals(12, matches.length);
        
        // Verify match distribution and properties
        verifyMatchProperties(matches);
    }

    @Test
    void createGroupStages_DuplicateCreation_Failure() {
        createGroupStages_Success();
        
        String url = "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage";
        ResponseEntity<String> response = restTemplate
            .withBasicAuth("admin", "adminPass")
            .postForEntity(createUrl(url), null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createGroupStages_InvalidEventId_Failure() {
        String url = "/tournaments/" + tournament.getId() + "/events/99999/groupStage";
        
        ResponseEntity<String> response = restTemplate
            .withBasicAuth("admin", "adminPass")
            .postForEntity(createUrl(url), null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void verifyEloDistribution_Success() {
        createGroupStages_Success();
        createMatches_Success();
        
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        List<GroupStage> stages = updatedEvent.getGroupStages();
        
        // Verify each group has a mix of high and low ELO players
        for (GroupStage stage : stages) {
            Set<Player> groupPlayers = stage.getMatches().stream()
                .flatMap(m -> Stream.of(m.getPlayer1(), m.getPlayer2()))
                .collect(Collectors.toSet());
            
            List<Integer> elos = groupPlayers.stream()
                .map(Player::getElo)
                .sorted()
                .collect(Collectors.toList());
            
            assertTrue(elos.get(elos.size()-1) - elos.get(0) >= 200, 
                "Each group should have a good ELO spread");
        }
    }

    private void verifyMatchProperties(Match[] matches) {
        Set<String> uniquePairings = new HashSet<>();
        
        for (Match match : matches) {
            assertNotNull(match.getPlayer1());
            assertNotNull(match.getPlayer2());
            assertNotNull(match.getGroupStage());
            assertEquals(event.getId(), match.getEvent().getId());
            
            // Verify no duplicate pairings
            String pairing = Math.min(match.getPlayer1().getId(), match.getPlayer2().getId()) +
                           "-" + Math.max(match.getPlayer1().getId(), match.getPlayer2().getId());
            assertTrue(uniquePairings.add(pairing), "No duplicate pairings should exist");
        }
    }










    // @Test //passed!
    //  public void addInitialMatchForGroupStage_Success() throws Exception {

    //     groupStageRepository.deleteAll();

    //     URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/matches");


    //     ResponseEntity<List<Match>> result = restTemplate.withBasicAuth("admin", "adminPass")
    //                                                 .exchange(uri, 
    //                                                     HttpMethod.POST, 
    //                                                     null, 
    //                                                     new ParameterizedTypeReference<List<Match>>() {});
    //     //System.out.println(result.getBody());
    //     assertEquals(HttpStatus.CREATED, result.getStatusCode());
    //     assertNotNull(result.getBody());
    //     assertTrue(result.getBody().size() > 0);
    // }

    // @Test // passed
    // public void addMatchesForKnockoutStage_Success() throws Exception {
        
    //     URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + knockoutStage.getId() + "/matches");

    //     ResponseEntity<List<Match>> result = restTemplate.withBasicAuth("admin", "adminPass")
    //         .exchange(uri, 
    //                  HttpMethod.POST, 
    //                  null, 
    //                  new ParameterizedTypeReference<List<Match>>() {});

    //     assertEquals(HttpStatus.CREATED, result.getStatusCode());
    //     assertNotNull(result.getBody());
    //     assertTrue(result.getBody().size() > 0);
    // }


    private PlayerRank createPlayerRank(Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        return playerRank;
    }
}
