package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.groupstage.GroupStageRepository;
import org.fencing.demo.knockoutmatchmaking.KnockoutMatchMakingService;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.match.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class KnockoutMatchMakingIntegrationTest extends BaseIntegrationTest {

        @Autowired 
        private KnockoutMatchMakingService knockoutMatchMakingService; 
 
        @Autowired 
        private GroupStageRepository groupStageRepository; 
 
        @BeforeEach 
        void setUp() { 
                super.setUp(); 
                knockoutStageRepository.deleteAll(); 
                groupStageRepository.deleteAll(); 
                // Setup players 
                int[] elos = new int[32]; 
                for (int i = 0; i < 32; i++) { 
                        elos[i] = 1000 + i; 
                } 
                setUpWithPlayersInEvent(elos); 
                // Create and complete group stages 
                GroupStage groupStage = new GroupStage(); 
                groupStage.setEvent(event); 
                groupStage.setMatches(new ArrayList<>()); 
                event.getGroupStages().add(groupStage); 
 
                // Create a completed match 
                Match match = new Match(); 
                match.setEvent(event); 
                match.setGroupStage(groupStage); 
                match.setPlayer1(event.getRankings().first().getPlayer()); 
                match.setPlayer2(event.getRankings().last().getPlayer()); 
                match.setFinished(true); 
                match.setPlayer1Score(5); 
                match.setPlayer2Score(3); 
 
                groupStage.getMatches().add(match); 
 
                eventRepository.save(event); 
                groupStageRepository.save(groupStage); 
                matchRepository.save(match); 
        }

        @Test 
        void createKnockoutStage_AdminUser_Success() throws Exception { 
 
                String url = "/tournaments/" + tournament.getId() + 
                                "/events/" + event.getId() + 
                                "/knockoutStage"; 
 
                ResponseEntity<KnockoutStage> response = restTemplate.exchange( 
                                createUrl(url), 
                                HttpMethod.POST, 
                                new HttpEntity<>(null, createHeaders(adminToken)), 
                                KnockoutStage.class); 
 
                assertEquals(HttpStatus.CREATED, response.getStatusCode()); 
                assertNotNull(response.getBody()); 
                assertEquals(event.getId(), response.getBody().getEvent().getId()); 
        } 
 
        @Test 
        void createKnockoutStage_RegularUser_Failure() throws Exception { 
                String url = "/tournaments/" + tournament.getId() + 
                                "/events/" + event.getId() + 
                                "/knockoutStage"; 
 
                ResponseEntity<KnockoutStage> response = restTemplate.exchange( 
                                createUrl(url), 
                                HttpMethod.POST, 
                                new HttpEntity<>(null, createHeaders(userToken)), 
                                KnockoutStage.class); 
 
                assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); 
        } 
 
        @Test 
        void createMatches_Success() throws Exception { 
                // Create knockout stage 
                ResponseEntity<KnockoutStage> stageResponse = restTemplate.exchange( 
                                createUrl("/tournaments/" + tournament.getId() + 
                                                "/events/" + event.getId() + 
                                                "/knockoutStage"), 
                                HttpMethod.POST, 
                                new HttpEntity<>(null, createHeaders(adminToken)), 
                                KnockoutStage.class); 
 
                KnockoutStage knockoutStage = stageResponse.getBody(); 
                assertNotNull(knockoutStage); 
 
                // Create matches 
                String url = "/tournaments/" + tournament.getId() + 
                                "/events/" + event.getId() + 
                                "/knockoutStage/" + knockoutStage.getId() + 
                                "/matches"; 
 
                ResponseEntity<List<Match>> response = restTemplate.exchange( 
                                createUrl(url), 
                                HttpMethod.POST, 
                                new HttpEntity<>(null, createHeaders(adminToken)), 
                                new ParameterizedTypeReference<List<Match>>() { 
                                }); 
 
                assertEquals(HttpStatus.CREATED, response.getStatusCode()); 
                List<Match> matches = response.getBody(); 
                assertNotNull(matches); 
                assertEquals(16, matches.size()); 
                verifyMatchProperties(matches); 
        } 
 
        @Test 
        void createMatches_NoKnockoutStage_Failure() throws Exception { 
                String url = "/tournaments/" + tournament.getId() + 
                                "/events/" + event.getId() + 
                                "/knockoutStage/1/matches"; 
 
                ResponseEntity<String> response = restTemplate.exchange( 
                                createUrl(url), 
                                HttpMethod.POST, 
                                new HttpEntity<>(null, createHeaders(adminToken)), 
                                String.class); 
 
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); 
        } 

        private void verifyMatchProperties(List<Match> matches) { 
                Set<String> uniquePairings = new HashSet<>(); 
 
                for (Match match : matches) { 
                        assertNotNull(match.getPlayer1()); 
                        assertNotNull(match.getPlayer2()); 
                        assertNotNull(match.getKnockoutStage()); 
                        assertEquals(event.getId(), match.getEvent().getId()); 
 
                        // Verify no duplicate pairings 
                        String pairing = Math.min(match.getPlayer1().getId(), match.getPlayer2().getId()) + 
                                        "-" + Math.max(match.getPlayer1().getId(), match.getPlayer2().getId()); 
                        assertTrue(uniquePairings.add(pairing), "No duplicate pairings should exist"); 
 
                        // Verify seeding order (higher ranked player should be player1) 
                        assertTrue(match.getPlayer1().getId() < match.getPlayer2().getId(), 
                                        "Higher ranked player should be player1"); 
                }
        }
}
