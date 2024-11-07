package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.stages.KnockoutStageRepository;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.fencing.demo.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GroupIntegrationTest {
     @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    private User adminUser;
    private User regularUser;
    private Tournament tournament;
    private Event event;
    private Player player1;
    private Player player2;
    private String adminToken;
    private String userToken;

    @Autowired
    private KnockoutStageRepository knockoutStageRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Create an admin user
        userRepository.deleteAll();
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        userRepository.save(adminUser);

        // Create a regular user
        regularUser = new User("user", passwordEncoder.encode("userPass"), "user@example.com", Role.USER);
        userRepository.save(regularUser);

        adminToken = "Bearer " + generateToken(adminUser);
        userToken = "Bearer " + generateToken(regularUser);

        // Initialize players for the matches
        player1 = new Player("player1", passwordEncoder.encode("password1"), "player1@example.com", Role.USER);
        player1.setElo(1700);
        playerRepository.save(player1);

        player2 = new Player("player2", passwordEncoder.encode("password2"), "player2@example.com", Role.USER);
        player2.setElo(1700);
        playerRepository.save(player2);

        // Initialize and save a Tournament object
        tournament = Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.now().plusDays(1))
                .registrationEndDate(LocalDate.now().plusDays(10))
                .tournamentStartDate(LocalDate.now().plusDays(15))
                .tournamentEndDate(LocalDate.now().plusDays(20))
                .venue("Olympic Stadium")
                .build();
        tournament = tournamentRepository.save(tournament);

        // Initialize and save an Event object linked to the tournament
        event = Event.builder()
                .startDate(LocalDateTime.now().plusDays(16))
                .endDate(LocalDateTime.now().plusDays(17))
                .gender(Gender.MALE)
                .weapon(WeaponType.FOIL)
                .tournament(tournament)
                .build();
        event = eventRepository.save(event);
    }

    @AfterEach
    void tearDown() {
        // Clear the database after each test
        knockoutStageRepository.deleteAll();
        eventRepository.deleteAll();
        tournamentRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
    }

@Test
public void addInitialGrpStages_ValidEventId_ReturnsListOfGroupStages() throws Exception {
    URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage");

    ResponseEntity<List<GroupStage>> result = restTemplate.exchange(
            uri,
            HttpMethod.POST,
            null,
            new ParameterizedTypeReference<List<GroupStage>>() {}
    );

    assertEquals(201, result.getStatusCode().value());

    List<GroupStage> groupStages = result.getBody();
    assertNotNull(groupStages);
    assertFalse(groupStages.isEmpty());

    // Further assertions can check specific properties in groupStages, like event ID or player count.
    GroupStage firstGroupStage = groupStages.get(0);
    assertEquals(event.getId(), firstGroupStage.getEvent().getId());
    assertNotNull(firstGroupStage.getPlayers());
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
