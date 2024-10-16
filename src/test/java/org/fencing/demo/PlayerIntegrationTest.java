package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.Date;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

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

    private String adminToken;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Create an admin user
        players.deleteAll();
        users.deleteAll();
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        users.save(adminUser);

        adminToken = "Bearer " + generateToken(adminUser);
    }
    
    @AfterEach
    void tearDown() {
        // clear the database after each test
        players.deleteAll();
        users.deleteAll();
    }

    @Test
    public void getPlayers_Success() throws Exception {
        players.save(new Player("user2", "password", "user@example.com", Role.USER));
        URI uri = new URI(baseUrl + port + "/players");

        ResponseEntity<Player[]> response = restTemplate.getForEntity(uri, Player[].class);
        Player[] players = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(players);
        assertEquals(1, players.length);
    }

    @Test
    public void getPlayer_Success() throws Exception {
        Player player = new Player("user2", "password", "user@example.com", Role.USER);
        Long id = players.save(player).getId();
        URI uri = new URI(baseUrl + port + "/players/" + id);

        ResponseEntity<Player> result = restTemplate.getForEntity(uri, Player.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(player.getId(), result.getBody().getId());
    }

    @Test
    public void getPlayer_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/players/1");

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        assertEquals(404, result.getStatusCode().value());
    }


    @Test
    public void addPlayer_Success() throws Exception {
        Player player = new Player("user69", "password", "user69@example.com", Role.USER);
        URI uri = new URI(baseUrl + port + "/players");

        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass").postForEntity(uri, player, Player.class);
        
        assertEquals(201, result.getStatusCode().value());
        assertEquals(player.getUsername(), result.getBody().getUsername());
        
    }

    @Test
    public void addPlayer_Failure_Sameusername() throws Exception {
        Player player = new Player("user69", "password", "user69@example.com", Role.USER);
        players.save(player);
        Player player2 = new Player("user69", "password", "user70@example.com", Role.USER);
        URI uri = new URI(baseUrl + port + "/players");

        HttpEntity<Player> request = new HttpEntity<>(player2, createHeaders(adminToken));
        ResponseEntity<String> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(409, result.getStatusCode().value());
    }

    @Test
    public void updatePlayer_Success() throws Exception {
        Player player =  players.save(new Player("user2", "password", "user@example.com", Role.USER));
        Long id = player.getId().longValue();
        URI uri = new URI(baseUrl + port + "/players/" + id);
        Player newPlayer = new Player("user3", "password3", "user3@example.com", Role.USER);
        

        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newPlayer), Player.class);
        
        assertEquals(200, result.getStatusCode().value()); 
        assertEquals(newPlayer.getUsername(), result.getBody().getUsername()); 
    }

    @Test
    public void updatePlayer_Failure_InvalidId() throws Exception {
        URI uri = new URI(baseUrl + port + "/players/1");
        Player newPlayer = new Player("user2", "password", "user@example.com", Role.USER);
        ResponseEntity<Player> result = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newPlayer), Player.class);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deletePlayer_Success() throws Exception {
        Player player = new Player("user2", "password", "user@example.com", Role.USER);
        players.save(player);
        Long id = player.getId();
        URI uri = new URI(baseUrl + port + "/players/" + id);

        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(200, result.getStatusCode().value());
        Optional<Player> emptyValue = Optional.empty();
        assertEquals(emptyValue, players.findById(player.getId()));
    }

    @Test
	public void deletePlayer_InvalidId_Failure() throws Exception {
		URI uri = new URI(baseUrl + port + "/players/1");
		ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "adminPass")
		.exchange(uri, HttpMethod.DELETE, null, Void.class);
		assertEquals(404, result.getStatusCode().value());
    }

    private static final String SECRET_KEY = "okLzUXUdbiclWJtW5hXRabO10nXGqWdCFQodkuPpnKI=";

    private String generateToken(User user) {
        return Jwts
            .builder()
            .setSubject(user.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }

}

