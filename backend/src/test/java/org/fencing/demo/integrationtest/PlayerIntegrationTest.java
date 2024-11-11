package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Optional;

import org.fencing.demo.player.Player;
import org.fencing.demo.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PlayerIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() {
        // Create an admin user
        super.setUp();
    }

    

    @Test
    public void getPlayers_Success() throws Exception {

        URI uri = createUrl("/players");

        ResponseEntity<Player[]> response = restTemplate.getForEntity(uri, Player[].class);
        Player[] players = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(players);
        assertEquals(playerUser.getId(), players[0].getId());
        assertEquals(1, players.length);
    }

    @Test
    public void getPlayer_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/players/999");

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addPlayer_Success() throws Exception {
        Player player = new Player("user69", "password", "user69@example.com", Role.USER);
        URI uri = createUrl("/players");

        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass").postForEntity(uri, player,
                Player.class);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(player.getUsername(), result.getBody().getUsername());

    }

    @Test
    public void addPlayer_Failure_Same_Username() throws Exception {
        Player player = new Player("user", "password", "user@example.com", Role.USER);
        URI uri = createUrl("/players");

        HttpEntity<Player> request = new HttpEntity<>(player, createHeaders(adminToken));
        ResponseEntity<String> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    public void updatePlayer_Success() throws Exception {

        Long id = playerUser.getId();
        URI uri = createUrl("/players/" + id);
        Player newPlayer = new Player("user3", "password3", "user3@example.com", Role.USER);

        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newPlayer), Player.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(newPlayer.getUsername(), result.getBody().getUsername());
    }

    @Test
    public void updatePlayer_Failure_InvalidId() throws Exception {
        URI uri = createUrl("/players/999");
        Player newPlayer = new Player("user2", "password", "user@example.com", Role.USER);
        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newPlayer), Player.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deletePlayer_Success() throws Exception {
        Long id = playerUser.getId();
        URI uri = createUrl("/players/" + id);

        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(200, result.getStatusCode().value());
        Optional<Player> emptyValue = Optional.empty();
        assertEquals(emptyValue, playerRepository.findById(playerUser.getId()));
    }

    @Test
    public void deletePlayer_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/players/999");
        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(404, result.getStatusCode().value());
    }


}
