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
class KnockoutStageIntegrationTest {

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
    public void getKnockoutStage_ValidKnockoutStageId_Success() throws Exception {
        KnockoutStage knockoutStage = createValidKnockoutStage();
        Long id = knockoutStageRepository.save(knockoutStage).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

        ResponseEntity<KnockoutStage> result = restTemplate.getForEntity(uri, KnockoutStage.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(knockoutStage.getId(), result.getBody().getId());
    }

    @Test
    public void getKnockoutStage_InvalidKnockoutStageId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/999");

        ResponseEntity<KnockoutStage> result = restTemplate.getForEntity(uri, KnockoutStage.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addKnockoutStage_AdminUser_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage");

        KnockoutStage knockoutStage = createValidKnockoutStage();

        HttpEntity<KnockoutStage> request = new HttpEntity<>(knockoutStage, createHeaders(adminToken));
        ResponseEntity<KnockoutStage> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, KnockoutStage.class);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody().getId());
    }

    @Test
    public void addKnockoutStage_RegularUser_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage");

        KnockoutStage knockoutStage = createValidKnockoutStage();

        HttpEntity<KnockoutStage> request = new HttpEntity<>(knockoutStage, createHeaders(userToken));
        ResponseEntity<KnockoutStage> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, KnockoutStage.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateKnockoutStage_AdminUser_Success() throws Exception {
        KnockoutStage knockoutStage = createValidKnockoutStage();
        Long id = knockoutStageRepository.save(knockoutStage).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);
        Event newEvent = Event.builder()
                .startDate(LocalDateTime.now().plusDays(13))
                .endDate(LocalDateTime.now().plusDays(17))
                .gender(Gender.FEMALE)
                .weapon(WeaponType.EPEE)
                .tournament(tournament)
                .build();
        
        long newEventId = eventRepository.save(newEvent).getId();
        knockoutStage.setEvent(newEvent);
        KnockoutStage updatedKnockoutStage = knockoutStage;

        HttpEntity<KnockoutStage> request = new HttpEntity<>(updatedKnockoutStage, createHeaders(adminToken));
        ResponseEntity<KnockoutStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, KnockoutStage.class);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(updatedKnockoutStage.getEvent().getId(), newEventId);
    }
    // @Test
    // public void updateKnockoutStage_AdminUser_Success() throws Exception {
    //     KnockoutStage knockoutStage = createValidKnockoutStage();
    //     Long id = knockoutStageRepository.save(knockoutStage).getId();
    //     URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

    //     List<Match> updatedMatches = new ArrayList<>();
    //     updatedMatches.add(Match.builder()
    //             .player1(player1)
    //             .player2(player2)
    //             .player1Score(15)
    //             .player2Score(10)
    //             .knockoutStage(knockoutStage)  // Ensure knockoutStage is set here
    //             .event(event)  // Make sure event is associated with the match
    //             .build());

    //     knockoutStage.setMatches(updatedMatches);
    //     KnockoutStage updatedKnockoutStage = knockoutStage;

    //     HttpEntity<KnockoutStage> request = new HttpEntity<>(updatedKnockoutStage, createHeaders(adminToken));
    //     ResponseEntity<KnockoutStage> result = restTemplate
    //             .exchange(uri, HttpMethod.PUT, request, KnockoutStage.class);
        
    //     assertEquals(200, result.getStatusCode().value());
    //     assertEquals(updatedKnockoutStage.getId(), result.getBody().getId());
    // }

    @Test
    public void updateKnockoutStage_RegularUser_Failure() throws Exception {
        KnockoutStage knockoutStage = createValidKnockoutStage();
        Long id = knockoutStageRepository.save(knockoutStage).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

        KnockoutStage updatedKnockoutStage = KnockoutStage.builder()
                .id(id)
                .event(event)
                .build();

        HttpEntity<KnockoutStage> request = new HttpEntity<>(updatedKnockoutStage, createHeaders(userToken));
        ResponseEntity<KnockoutStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, KnockoutStage.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateKnockoutStage_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/999");

        KnockoutStage updatedKnockoutStage = KnockoutStage.builder()
                .id(999L)
                .event(event)
                .build();

        HttpEntity<KnockoutStage> request = new HttpEntity<>(updatedKnockoutStage, createHeaders(adminToken));
        ResponseEntity<KnockoutStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, KnockoutStage.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteKnockoutStage_AdminUser_Success() throws Exception {
        KnockoutStage knockoutStage = createValidKnockoutStage();
        Long id = knockoutStageRepository.save(knockoutStage).getId();
        URI uri = URI.create(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(204, result.getStatusCode().value());
        assertFalse(knockoutStageRepository.existsById(id));
    }

    @Test
    public void deleteKnockoutStage_RegularUser_Failure() throws Exception {
        KnockoutStage knockoutStage = createValidKnockoutStage();
        Long id = knockoutStageRepository.save(knockoutStage).getId();
        URI uri = URI.create(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(403, result.getStatusCode().value());
        assertTrue(knockoutStageRepository.existsById(id));
    }

    @Test
    public void deleteKnockoutStage_InvalidId_Failure() throws Exception {
        URI uri = URI.create(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/999");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }

    private KnockoutStage createValidKnockoutStage() {
        return KnockoutStage.builder()
                .event(event)
                .build();
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
