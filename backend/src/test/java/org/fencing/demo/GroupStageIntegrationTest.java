package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.swing.GroupLayout.Group;

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
import org.fencing.demo.stages.GroupStageRepository;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.stages.KnockoutStageRepository;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.fencing.demo.user.UserRepository;
import org.fencing.demo.match.MatchRepository;
import java.util.*;
import org.fencing.demo.events.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GroupStageIntegrationTest {

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
    private GroupStageRepository grpStageRepository;

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

    @Autowired
    private MatchRepository matchRepository;

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
        playerRepository.deleteAll();
        player1 = new Player("player1", passwordEncoder.encode("password1"), "player1@example.com", Role.USER, Gender.MALE);
        player1.setElo(1700);
        playerRepository.save(player1);

        player2 = new Player("player2", passwordEncoder.encode("password2"), "player2@example.com", Role.USER, Gender.MALE);
        player2.setElo(1700);
        playerRepository.save(player2);

        // Initialize and save a Tournament object
        tournamentRepository.deleteAll();
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
        eventRepository.deleteAll();
        event = Event.builder()
                .startDate(LocalDateTime.now().plusDays(16))
                .endDate(LocalDateTime.now().plusDays(17))
                .gender(Gender.MALE)
                .weapon(WeaponType.FOIL)
                .tournament(tournament)
                .build();

        event.getRankings().add(createPlayerRank(player1, event));
        event.getRankings().add(createPlayerRank(player2, event));
        event = eventRepository.save(event);
    }

    @AfterEach
    void tearDown() {
        // Clear the database after each test
        grpStageRepository.deleteAll();
        eventRepository.deleteAll();
        tournamentRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getKnockoutStage_ValidKnockoutStageId_Success() throws Exception {
        GroupStage grpStage = createValidGroupStage();
        Long id = grpStageRepository.save(grpStage).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        ResponseEntity<GroupStage> result = restTemplate.getForEntity(uri, GroupStage.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(grpStage.getId(), result.getBody().getId());
    }

    @Test
    public void getGroupStage_InvalidGroupStageId_Failure() throws Exception {
        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/999");

        ResponseEntity<GroupStage> result = restTemplate.getForEntity(uri, GroupStage.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addGroupStage_AdminUser_Success() throws Exception {
        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage");

        GroupStage groupStage = createValidGroupStage();
        System.out.println("Initial event: " + groupStage.getEvent());

        HttpEntity<GroupStage> request = new HttpEntity<>(groupStage, createHeaders(adminToken));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
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

        GroupStage groupStage = createValidGroupStage();

        HttpEntity<GroupStage> request = new HttpEntity<>(groupStage, createHeaders(userToken));
        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, GroupStage.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateGroupStage_AdminUser_Success() throws Exception {
        // Create a valid GroupStage
        GroupStage groupStage = createValidGroupStage();

        // Save the initial GroupStage and get its ID
        Long id = grpStageRepository.save(groupStage).getId();

        // System.out.println("Before adding match:"+groupStage);
        // System.out.println("GroupStage ID:" + groupStage.getId());
        // System.out.println("Matches:" + groupStage.getMatches());
        // Create the URI for the PUT request
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        // Add a new match to the GroupStage
        Match newMatch = Match.builder()
                .player1(player1)
                .player2(player2)
                .player1Score(15)
                .player2Score(10)
                .event(event) // Make sure event is associated with the match
                .groupStage(groupStage)
                .build();
        matchRepository.save(newMatch);
        groupStage.getMatches().add(newMatch);

        // Create the HTTP request with the updated GroupStage
        HttpEntity<GroupStage> request = new HttpEntity<>(groupStage, createHeaders(adminToken));

        // Execute the PUT request

        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, GroupStage.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(groupStage.getId(), result.getBody().getId());
        // Assert the results
        // assertEquals("smtth", result.getBody());
    }

    @Test
    public void updateGroupStage_RegularUser_Failure() throws Exception {
        GroupStage groupStage = createValidGroupStage();
        Long id = grpStageRepository.save(groupStage).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        GroupStage updatedGroupStage = GroupStage.builder()
                .id(id)
                .event(event)
                .build();

        HttpEntity<GroupStage> request = new HttpEntity<>(updatedGroupStage, createHeaders(userToken));
        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, GroupStage.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateGroupStage_InvalidId_Failure() throws Exception {
        URI uri = new URI(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/999");

        GroupStage updateGroupStage = GroupStage.builder()
                .id(999L)
                .event(event)
                .build();

        HttpEntity<GroupStage> request = new HttpEntity<>(updateGroupStage, createHeaders(adminToken));
        ResponseEntity<GroupStage> result = restTemplate
                .exchange(uri, HttpMethod.PUT, request, GroupStage.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteGroupStage_AdminUser_Success() throws Exception {
        // Create and save a valid GroupStage
        GroupStage groupStage = createValidGroupStage();
        Long id = grpStageRepository.save(groupStage).getId();

        // Construct the URI for the DELETE request
        URI uri = URI.create(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        // Log the ID of the GroupStage to be deleted
        // System.out.println("Deleting GroupStage with ID: " + id);

        // Create the HTTP request entity with headers
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));

        // Execute the DELETE request
        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);
        // Log the response status
        // System.out.println("Response Status: " + result.getStatusCode());

        // Assert the response status code and database state
        assertEquals(204, result.getStatusCode().value());
        // Verify that the GroupStage has been deleted
    }

    @Test
    public void deleteGroupStage_RegularUser_Failure() throws Exception {
        GroupStage groupStage = createValidGroupStage();
        Long id = grpStageRepository.save(groupStage).getId();
        URI uri = URI.create(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/groupStage/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(403, result.getStatusCode().value());
        assertTrue(grpStageRepository.existsById(id));
    }

    @Test
    public void deleteGroupStage_InvalidId_Failure() throws Exception {
        Long id = 999L;
        assertFalse(grpStageRepository.existsById(id));
        URI uri = URI.create(
                baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/999");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }

    private GroupStage createValidGroupStage() {
        return GroupStage.builder()
                .event(event)
                .matches(new ArrayList<Match>())
                .build();
    }

    private PlayerRank createPlayerRank(Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        return playerRank;
    }

    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY") != null 
        ? System.getenv("JWT_SECRET_KEY") 
        : "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

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
