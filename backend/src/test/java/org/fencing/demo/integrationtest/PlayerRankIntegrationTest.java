package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.fencing.demo.events.Event;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
@ActiveProfiles("test")
public class PlayerRankIntegrationTest extends BaseIntegrationTest {

    @Test
    void getAllPlayerRanksForEvent_Success() {
        // Setup players with different Elos
        int[] elos = {1200, 1300, 1400, 1500};
        setUpWithPlayersInEvent(elos);

        String url = "/tournaments/" + tournament.getId() + 
                    "/events/" + event.getId() + 
                    "/playerRanks";

        ResponseEntity<List<PlayerRank>> response = restTemplate.exchange(
            createUrl(url),
            HttpMethod.GET,
            new HttpEntity<>(null, createHeaders(userToken)),
            new ParameterizedTypeReference<List<PlayerRank>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerRank> playerRanks = response.getBody();
        assertNotNull(playerRanks);
        assertEquals(4, playerRanks.size());
        
        // Verify ranks are ordered correctly (should be in same order as initial Elos)
        for (int i = 0; i < playerRanks.size(); i++) {
            assertEquals(elos[i], playerRanks.get(i).getPlayer().getElo());
        }
    }

    @Test
    void getAllPlayerRanksForEvent_EventNotFound() {
        String url = "/tournaments/" + tournament.getId() + 
                    "/events/999999/playerRanks";

        ResponseEntity<String> response = restTemplate.exchange(
            createUrl(url),
            HttpMethod.GET,
            new HttpEntity<>(null, createHeaders(userToken)),
            String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllPlayerRanksForPlayer_Success() {
        // Setup player with ranks in multiple events
        int[] elos = {1200};
        List<Player> players = setUpWithPlayersInEvent(elos);
        Player testPlayer = players.get(0);

        // Create another event and add the same player
        Event event2 = eventRepository.save(createValidEvent(tournament));
        PlayerRank rank2 = new PlayerRank();
        rank2.setEvent(event2);
        rank2.setPlayer(testPlayer);
        event2.getRankings().add(rank2);
        eventRepository.save(event2);

        String url = "/player/" + testPlayer.getUsername() + "/playerRanks";

        ResponseEntity<List<PlayerRank>> response = restTemplate.exchange(
            createUrl(url),
            HttpMethod.GET,
            new HttpEntity<>(null, createHeaders(userToken)),
            new ParameterizedTypeReference<List<PlayerRank>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PlayerRank> playerRanks = response.getBody();
        assertNotNull(playerRanks);
        assertEquals(2, playerRanks.size());
        
        // Verify all ranks belong to the same player
        for (PlayerRank rank : playerRanks) {
            assertEquals(testPlayer.getId(), rank.getPlayer().getId());
        }
    }

    @Test
    void getAllPlayerRanksForPlayer_PlayerNotFound() {
        String url = "/player/nonexistentplayer/playerRanks";

        ResponseEntity<String> response = restTemplate.exchange(
            createUrl(url),
            HttpMethod.GET,
            new HttpEntity<>(null, createHeaders(userToken)),
            String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}