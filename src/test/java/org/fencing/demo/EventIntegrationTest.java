package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.player.PlayerRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository; // Assuming this is your user repository

    @Autowired
    private BCryptPasswordEncoder encoder;

    private Tournament tournament;

    private User adminUser;

    private final String baseUrl = "http://localhost:";

    @BeforeEach
    void setUp() {
        adminUser = createValidAdminUser(encoder);
        userRepository.save(adminUser);

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

        // Create the URI for adding an event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        // Create a new event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));


        // Send the POST request
        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                        .postForEntity(uri, event, Event.class);

        // Verify the result
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(event.getGender(), result.getBody().getGender());
        assertEquals(event.getWeapon(), result.getBody().getWeapon());
    }

    // Get All Events by Tournament ID - Success
    @Test
    public void getAllEventsByTournamentId_Success() throws Exception {

        // Create and save an event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));
        event.setTournament(tournament);
        eventRepository.save(event);

        // Create the URI for retrieving events
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        // Send the GET request
        ResponseEntity<Event[]> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                            .getForEntity(uri, Event[].class);

        // Verify the result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().length);
    }

    // Get Event by ID - Success
    @Test
    public void getEventById_Success() throws Exception {
        // Create and save a tournament and an event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));
        event.setTournament(tournament);
        event = eventRepository.save(event);

        // Create the URI for retrieving the event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        // Send the GET request
        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                            .getForEntity(uri, Event.class);

        // Verify the result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(event.getId(), result.getBody().getId());
    }

    // Update Event - Success
    @Test
    @Transactional
    public void updateEvent_Success() throws Exception {
        // Create and save a tournament and an event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));
        event.setTournament(tournament);
        event = eventRepository.save(event);
        
        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        // Create the URI for updating the event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        // Send the PUT request
        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                        .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), Event.class);

        // Verify the result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("FEMALE", result.getBody().getGender());
        assertEquals("EPEE", result.getBody().getWeapon());
    }

    // Delete Event - Success
    @Test
    public void deleteEvent_Success() throws Exception {
        // Create and save a tournament and an event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));
        event.setTournament(tournament);
        event = eventRepository.save(event);

        // Create the URI for deleting the event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        // Send the DELETE request
        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                        .exchange(uri, HttpMethod.DELETE, null, Void.class);

        // Verify the result
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertFalse(eventRepository.findById(event.getId()).isPresent());
    }

    // Helper methods for creating valid entities
    private Tournament createValidTournament() {
        return Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.now().plusDays(1))
                .registrationEndDate(LocalDate.now().plusDays(30))
                .tournamentStartDate(LocalDate.now().plusDays(60))
                .tournamentEndDate(LocalDate.now().plusDays(65))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
    }

    public User createValidAdminUser(BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(encoder.encode("strongpassword123"));  // Encode the password
        user.setEmail("validuser@example.com");  // Set a valid email
        user.setRole(Role.ADMIN);  // Use the correct enum value for Role
        return user;
    }
}

