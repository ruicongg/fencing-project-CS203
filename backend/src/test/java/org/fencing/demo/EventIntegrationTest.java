package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Collections;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.EventServiceImpl;
import org.fencing.demo.events.EventService;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankComparator;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.player.PlayerNotFoundException;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.user.UserRepository;
import org.hibernate.Hibernate;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.security.core.GrantedAuthority;
import java.util.Date;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository; // Assuming this is your user repository

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Tournament tournament;
    private Event event;
    private User adminUser;
    private User regularUser;
    private String adminToken;
    private String userToken;
    private Player player;
    private final String baseUrl = "http://localhost:";

    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY") != null
            ? System.getenv("JWT_SECRET_KEY")
            : "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @BeforeEach
    void setUp() {
        eventRepository.findAll().forEach(event -> {
            // Clear matches first
            Event eventWithMatches = eventRepository.findByIdWithMatches(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
            eventWithMatches.getMatches().clear();
            eventRepository.save(eventWithMatches);
    
            // Clear stages
            Event eventWithGroups = eventRepository.findByIdWithGroupStages(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
            Event eventWithKnockouts = eventRepository.findByIdWithKnockoutStages(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            eventWithGroups.getGroupStages().clear();
            eventWithKnockouts.getKnockoutStages().clear();
            
            eventRepository.save(eventWithGroups);
            eventRepository.save(eventWithKnockouts);
    
            // Clear rankings last
            Event eventWithRankings = eventRepository.findByIdWithRankings(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
            eventWithRankings.getRankings().clear();
            eventRepository.save(eventWithRankings);
        });
    
        eventRepository.deleteAll();
        playerRepository.deleteAll();
        tournamentRepository.deleteAll();

        userRepository.deleteAll(); // delete admin initializer

        // Create an admin user
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        userRepository.save(adminUser);
        adminToken = generateToken(adminUser);
        // Create a regular user
        regularUser = new User("user", passwordEncoder.encode("userPass"), "user@example.com", Role.USER);
        userRepository.save(regularUser);
        userToken = generateToken(regularUser);

        tournament = createValidTournament();
        tournamentRepository.save(tournament);
    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
        tournamentRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Add Event - Success
    @Test
    public void addEvent_Success() throws Exception {
        Event event = createValidEvent(tournament);
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(event.getGender(), response.getBody().getGender());
        assertEquals(event.getWeapon(), response.getBody().getWeapon());
    }

    @Test
    public void addEvent_ForbiddenForRegularUser_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(userToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void addEvent_NullTournamentId_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        URI uri = new URI(baseUrl + port + "/tournaments/null/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addEvent_NullEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addEvent_MissingRequiredFields_Failure() throws Exception {
        Event event = new Event();
        event.setGender(Gender.MALE);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addEvent_NonExistentTournamentId_Failure() throws Exception {
        Event event = createValidEvent(tournament);

        Long nonExistentTournamentId = 999L;
        URI uri = new URI(baseUrl + port + "/tournaments/" + nonExistentTournamentId + "/events");

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Get All Events by Tournament ID - Success
    @Test
    public void getAllEventsByTournamentId_Success() throws Exception {
        Event event1 = createValidEvent(tournament);
        Event event2 = createValidEvent(tournament);
        eventRepository.save(event1);
        eventRepository.save(event2);
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<Event[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, Event[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length);
    }

    // Get Event by ID - Success
    @Test
    public void getEventById_Success() throws Exception {
        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.GET, request, Event.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(event.getId(), response.getBody().getId());
    }

    @Test
    public void getAllEventsByTournamentId_RegularUser_Success() throws Exception {
        Event event = createValidEvent(tournament);
        eventRepository.save(event);
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userToken));
        ResponseEntity<Event[]> response = restTemplate.exchange(uri, HttpMethod.GET, request, Event[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
    }

    @Test
    public void addEvent_StartDateBeforeTournamentStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        Event event = createValidEvent(tournament);
        event.setStartDate(LocalDateTime.now().plusDays(24));

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Event start date cannt be earlier than Tournament start date"));
    }

    @Test
    public void addEvent_EndDateBeforeStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        Event event = createValidEvent(tournament);
        event.setEndDate(LocalDateTime.now().plusDays(24));

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Event end date must be after start date"));
    }

    @Test
    public void addEvent_NonExistentTournament_ThrowsTournamentNotFoundException() throws Exception {
        Event event = createValidEvent(tournament);
        Long nonExistentId = 999L;

        URI uri = new URI(baseUrl + port + "/tournaments/" + nonExistentId + "/events");
        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Update Event - Success
    @Test // event successfully created and persisted, but when updating, everything
          // becomes null? eg. id=0, startDate=null
    public void updateEvent_Success() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();

        tournament.getEvents().add(event);
        tournamentRepository.save(tournament);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.PUT, request, Event.class);

        // assertEquals(HttpStatus.OK, response.getStatusCode());
        // assertEquals("smth", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Gender.FEMALE, response.getBody().getGender());
        assertEquals(WeaponType.EPEE, response.getBody().getWeapon());
    }

    @Test
    public void updateEvent_ForbiddenForRegularUser_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();

        tournament.getEvents().add(event);
        tournamentRepository.save(tournament);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(userToken));
        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.PUT, request, Event.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void updateEvent_NullIdsOrEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/null");

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test // internal server error 500
    public void updateEvent_EndDateBeforeStartDate_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setEndDate(LocalDateTime.now().plusDays(24)); // End date before start date
        // System.out.println("in repository
        // "+tournamentRepository.findById(tournament_id));
        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertTrue(response.getBody().contains("Event end date must be after start date"));
    }

    @Test
    public void updateEvent_NonExistentEvent_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        Long nonExistentId = 9999L;
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + nonExistentId);

        HttpEntity<Event> request = new HttpEntity<>(event, createHeaders(adminToken));
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateEvent_StartDateBeforeTournamentStart_ThrowsIllegalArgumentException() throws Exception {
        Event event = createValidEvent(tournament);
        event = eventRepository.save(event);

        Event updatedEvent = createValidEvent(tournament);
        updatedEvent.setStartDate(tournament.getTournamentStartDate().atStartOfDay().minusDays(1));

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());
        HttpEntity<Event> request = new HttpEntity<>(updatedEvent, createHeaders(adminToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Event start date cannot be earlier than Tournament start date"));
    }

    // Delete Event - Success
    @Test
    public void deleteEvent_Success() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();

        tournament.getEvents().add(event);
        tournamentRepository.save(tournament);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(eventRepository.findById(event.getId()).isPresent());
    }

    @Test
    public void deleteEvent_ForbiddenForRegularUser_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();

        tournament.getEvents().add(event);
        tournamentRepository.save(tournament);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(userToken));
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // Expecting 403 Forbidden
    }

    @Test
    public void addPlayerToEvent_UserAddingSelf_Success() throws Exception {

        // Create and save test user with proper role
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        // Create authentication token
        String token = generateToken(testPlayer);

        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players/" + testPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(token));

        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_UserAddingOther_Forbidden() throws Exception {

        // Create and save test user with proper role
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        // Create authentication token
        String token = generateToken(testPlayer);

        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players/" + testPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(userToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_AdminAddingOther_Success() throws Exception {

        // Create and save test user with proper role
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        // Create authentication token
        String token = generateToken(testPlayer);

        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players/" + testPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(adminToken));

        ResponseEntity<Event> response = restTemplate.exchange(uri, HttpMethod.POST, request, Event.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_NullEventId_ThrowsIllegalArgumentException() throws Exception {

        // Create and save test user with proper role
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        // Create authentication token
        String token = generateToken(testPlayer);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/null/players/"
                + testPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(token));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addPlayerToEvent_PlayerGenderMismatch_ThrowsIllegalArgumentException() throws Exception {

        // Create and save test user with proper role
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        // Create authentication token
        String token = generateToken(testPlayer);

        Event event = createValidEvent(tournament);
        event.setGender(Gender.FEMALE);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/players/" + testPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(token));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST,
                request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Player's gender does not match"));
    }

    @Test
    public void addPlayerToEvent_OutsideRegistrationPeriod_ThrowsIllegalStateException() throws Exception {

        // Create and save test user with proper role
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        // Create authentication token
        String token = generateToken(testPlayer);

        Tournament newTournament = Tournament.builder()
                .name("Fall Championship")
                .registrationStartDate(LocalDate.now().plusDays(1))
                .registrationEndDate(LocalDate.now().plusDays(20))
                .tournamentStartDate(LocalDate.now().plusDays(25))
                .tournamentEndDate(LocalDate.now().plusDays(30))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
        tournamentRepository.save(newTournament);

        Event event = createValidEvent(newTournament);
        eventRepository.save(event);

        URI uri = new URI(
                baseUrl + port + "/tournaments/" + newTournament.getId() + "/events/" + event.getId() + "/players/" +
                        testPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(token));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST,
                request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Registration is not open"));
    }

    @Test
    public void removePlayerFromEvent_PlayerNotInEvent_ThrowsPlayerNotFoundException() throws Exception {
        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        // Create and save player
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() +
                "/events/" + event.getId() + "/players");
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(userToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void adminRemovesPlayerFromEvent_NonAdminUser_ThrowsAccessDeniedException() throws Exception {

        // Create and save test user with proper role
        Player testPlayer = new Player(
                "testPlayer",
                passwordEncoder.encode("password"),
                "test@example.com",
                Role.USER,
                Gender.MALE);
        playerRepository.save(testPlayer);

        // Create authentication token
        String token = generateToken(testPlayer);

        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId()
                + "/players/" + testPlayer.getUsername());
        HttpEntity<Void> request = new HttpEntity<>(null, createHeaders(userToken));

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Helper methods for creating valid entities
    private Tournament createValidTournament() {
        return Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.now())
                .registrationEndDate(LocalDate.now().plusDays(20))
                .tournamentStartDate(LocalDate.now().plusDays(25))
                .tournamentEndDate(LocalDate.now().plusDays(30))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
    }

    private Event createValidEvent(Tournament tournament) {
        Event event = Event.builder()
                .tournament(tournament)
                .gender(Gender.MALE)
                .weapon(WeaponType.FOIL)
                .startDate(LocalDateTime.now().plusDays(25))
                .endDate(LocalDateTime.now().plusDays(26))
                .rankings(new TreeSet<>(new PlayerRankComparator()))
                .groupStages(new ArrayList<>())
                .knockoutStages(new ArrayList<>())
                .matches(new ArrayList<>())
                .build();

        // Initialize stages with proper event reference
        GroupStage groupStage = new GroupStage();
        groupStage.setEvent(event);
        groupStage.setMatches(new ArrayList<>());
        KnockoutStage knockoutStage = new KnockoutStage();
        knockoutStage.setEvent(event);
        knockoutStage.setMatches(new ArrayList<>());

        event.setGroupStages(List.of(groupStage));
        event.setKnockoutStages(List.of(knockoutStage));

        return event;
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authorities = List.of("ROLE_" + user.getRole().name());
        claims.put("roles", authorities);

        return Jwts.builder()
                .setClaims(claims)
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
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}