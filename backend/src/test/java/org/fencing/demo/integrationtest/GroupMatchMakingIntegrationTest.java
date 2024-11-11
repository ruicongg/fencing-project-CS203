package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.*;

import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.match.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupMatchMakingIntegrationTest extends BaseIntegrationTest{

    @BeforeEach
    void setUp() {
        super.setUp();
        groupStageRepository.deleteAll();
        int[] elos = {1800, 1750, 1700, 1650, 1600, 1550, 1500, 1450};
        setUpWithPlayersInEvent(elos);
    }


    @Test
    public void addGroupStage_AdminUser_Success() throws Exception {
        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage");

        System.out.println("Initial event: " + groupStage.getEvent());

        HttpEntity<?> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<List<GroupStage>> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<GroupStage>>() {
                });

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isEmpty());
        assertNotNull(result.getBody().get(0).getId());
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





}
