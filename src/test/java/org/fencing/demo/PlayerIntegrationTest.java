package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Optional;

import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.fencing.demo.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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

    private User adminUser;

    @BeforeEach
    void setUp() {
        // Create an admin user
        users.deleteAll();
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        users.save(adminUser);
    }
    
    @AfterEach
    void tearDown() {
        // clear the database after each test
        players.deleteAll();
        users.deleteAll();
    }

    //     @Test
    // public void getPlayers_Success() throws Exception {
    //     URI uri = new URI(baseUrl + port + "/players");
    //     players.save(new Player("user2", "pasword", "user@example.com", Role.USER));

    //     // Continue with the original test logic

    //     ResponseEntity<Player[]> response = restTemplate.getForEntity(uri, Player[].class);
    //     Player[] players = response.getBody();

    //     assertEquals(200, response.getStatusCode().value());
    //     assertNotNull(players);
    //     assertEquals(1, players.length);
    // }

    @Test
    public void getPlayer_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/players");
        Player player = new Player("user2", "pasword", "user@example.com", Role.USER);

        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass").getForEntity(uri, Player.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(player.getId(), result.getBody().getId());
    }

    @Test
    public void addPlayer_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/players");
        Player player = new Player("user2", "pasword", "user@example.com", Role.USER);

        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass").postForEntity(uri, player, Player.class);
          
        assertEquals(player.getId(), result.getBody().getId());
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    public void updatePlayer_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/players");
        Player player = new Player("user2", "pasword", "user@example.com", Role.USER);
        Player newPlayer = new Player("user3", "password", "user3@example.com", Role.USER);

        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newPlayer), Player.class);
        
        assertEquals(newPlayer.getId(), result.getBody().getId()); 
        assertEquals(200, result.getStatusCode().value()); 
    }


}

