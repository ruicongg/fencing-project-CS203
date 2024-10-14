package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Optional;

import org.fencing.demo.player.*;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PlayerIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private  PlayerRepository players;

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @AfterEach
    void tearDown() {
        // clear the database after each test
        players.deleteAll();
        users.deleteAll();
    }

    @Test
    public void getPlayers_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/players");
        Player player = new Player("user", passwordEncoder.encode("userPass"), "user@example.com", Role.USER);
        players.save(player);

        ResponseEntity<Player[]> result = restTemplate.getForEntity(uri, Player[].class);
        Player[] players = result.getBody();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(players);
        assertEquals(1, players.length);
    }


}

