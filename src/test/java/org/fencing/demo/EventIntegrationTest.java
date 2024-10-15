package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.TreeSet;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.PlayerRankComparator;
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

import jakarta.persistence.EntityManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository; // Assuming this is your user repository

    @Autowired
    private BCryptPasswordEncoder encoder;

    private Tournament tournament;

    private User adminUser;

    private User regularUser;

    private final String baseUrl = "http://localhost:";

    @BeforeEach
    void setUp() {

        eventRepository.deleteAll();
        tournamentRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
        
        adminUser = createValidAdminUser(encoder);
        if (!userRepository.existsByUsername(adminUser.getUsername())) {
            userRepository.save(adminUser);
        }

        tournament = createValidTournament();
        tournamentRepository.save(tournament);

        regularUser = createValidRegularUser(encoder);
        if (!userRepository.existsByUsername(regularUser.getUsername())) {
            userRepository.save(regularUser);
        }
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

    @Test
    public void addEvent_ForbiddenForRegularUser_Failure() throws Exception {
        // Create the URI for adding an event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        // Create a new event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));

        // Send the POST request with regular user credentials
        ResponseEntity<Event> result = restTemplate.withBasicAuth("user", "strongpassword456")
                                            .postForEntity(uri, event, Event.class);

        // Verify the result
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  // Expecting 403 Forbidden
    }

    @Test
    public void addEvent_NullTournamentId_Failure() throws Exception {
        // Create the URI with a null tournament ID
        URI uri = new URI(baseUrl + port + "/tournaments/null/events");

        // Create a new event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));

        // Send the POST request
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .postForEntity(uri, event, String.class);

        // Verify the result (expecting 404 Not Found or 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_NullEvent_Failure() throws Exception {
        // Create the URI for adding an event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        // Send the POST request with a null event
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .postForEntity(uri, null, String.class);

        // Verify the result (expecting 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_MissingRequiredFields_Failure() throws Exception {
        // Create the URI for adding an event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        // Create an event with missing required fields
        Event event = new Event();
        event.setGender(Gender.MALE);  // No weapon, startDate, or endDate

        // Send the POST request
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .postForEntity(uri, event, String.class);

        // Verify the result (expecting 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_NonExistentTournamentId_Failure() throws Exception {
        // Use a non-existent tournament ID
        Long nonExistentTournamentId = 999L;  // Assume this ID doesn't exist
        URI uri = new URI(baseUrl + port + "/tournaments/" + nonExistentTournamentId + "/events");

        // Create a new event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));

        // Send the POST request
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .postForEntity(uri, event, String.class);

        // Verify the result (expecting 404 Not Found)
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
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

    @Test
    public void getAllEventsByTournamentId_RegularUser_Success() throws Exception {
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

        // Send the GET request with regular user credentials
        ResponseEntity<Event[]> result = restTemplate.withBasicAuth("user", "strongpassword456")
                                                .getForEntity(uri, Event[].class);

        // Verify the result
        assertEquals(HttpStatus.OK, result.getStatusCode());  // Expecting 200 OK
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().length);
    }

    @Test
    public void addEvent_StartDateBeforeTournamentStartDate_Failure() throws Exception {
        // Set tournament start date
        tournament.setTournamentStartDate(LocalDate.of(2025, 1, 10)); 
        tournamentRepository.save(tournament);

        // Create the URI for adding an event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        // Create an event where the start date is before the tournament start date
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 5, 10, 0)); // Earlier than tournament start date
        event.setEndDate(LocalDateTime.of(2025, 1, 11, 18, 0));

        // Send the POST request
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .postForEntity(uri, event, String.class);

        // Verify the result (expecting 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Event start date cannt be earlier than Tournament start date"));
    }

    @Test
    public void addEvent_EndDateBeforeStartDate_Failure() throws Exception {
        // Set tournament start date
        tournament.setTournamentStartDate(LocalDate.of(2025, 1, 10)); 
        tournamentRepository.save(tournament);

        // Create the URI for adding an event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        // Create an event where the end date is before the start date
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 12, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 11, 18, 0)); // End date is earlier than start date

        // Send the POST request
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .postForEntity(uri, event, String.class);

        // Verify the result (expecting 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Event end date must be after start date"));
    }

    // Update Event - Success
    @Test
    public void updateEvent_Success() throws Exception {
        // Create and save a tournament and an event
        tournament.setTournamentStartDate(LocalDate.of(2025, 1, 1));
        tournamentRepository.save(tournament);

        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 10, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 11, 18, 0));
        event.setTournament(tournament);

        // Create the URI for updating the event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        // Update event details
        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        // Send the PUT request
        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), Event.class);
        System.out.println("Event being sent: " + event);
        // Verify the result
        // assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Event end date must be after start date", result.getBody());
        assertEquals(Gender.FEMALE, result.getBody().getGender());
        assertEquals(WeaponType.EPEE, result.getBody().getWeapon());
    }

    @Test
    public void updateEvent_ForbiddenForRegularUser_Failure() throws Exception {
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

        // Send the PUT request with regular user credentials
        ResponseEntity<Event> result = restTemplate.withBasicAuth("user", "strongpassword456")
                                            .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), Event.class);

        // Verify the result
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  // Expecting 403 Forbidden
    }

    @Test
    public void updateEvent_NullIdsOrEvent_Failure() throws Exception {
        // Create the URI with null event ID
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/null");

        // Try sending a PUT request with null event
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(null), String.class);

        // Verify the result (expecting 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void updateEvent_StartDateBeforeTournamentStartDate_Failure() throws Exception {
        // Set the tournament start date
        tournament.setTournamentStartDate(LocalDate.of(2025, 1, 10));
        tournamentRepository.save(tournament);

        // Create and save an event
        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 10, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 11, 18, 0));
        event.setTournament(tournament);
        event = eventRepository.save(event);

        // Try updating with a start date earlier than the tournament start date
        event.setStartDate(LocalDateTime.of(2025, 1, 5, 10, 0));

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        // Send the PUT request
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), String.class);
        
        // Verify the result (expecting 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        // assertEquals("Event end date must be after start date", result.getBody());
        assertTrue(result.getBody().contains("Event start date cannt be earlier than Tournament start date"));
    }

    @Test
    public void updateEvent_EndDateBeforeStartDate_Failure() throws Exception {
        // Create and save an event
        tournament.setTournamentStartDate(LocalDate.of(2025, 1, 1));
        tournamentRepository.save(tournament);

        Event event = new Event();
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setStartDate(LocalDateTime.of(2025, 1, 10, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 11, 18, 0));
        event.setTournament(tournament);
        event = eventRepository.save(event);

        // Update event details where end date is before start date
        event.setEndDate(LocalDateTime.of(2025, 1, 9, 18, 0));  // End date before start date

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        // Send the PUT request
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), String.class);

        // Verify the result (expecting 400 Bad Request)
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        // assertEquals("Event end date must be after start date", result.getBody());
        assertTrue(result.getBody().contains("Event end date must be after start date"));
    }

    @Test
    public void updateEvent_NonExistentEvent_Failure() throws Exception {
        // Create a tournament
        tournament.setTournamentStartDate(LocalDate.of(2025, 1, 1));
        tournamentRepository.save(tournament);

        // Try to update a non-existent event
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/9999");

        // Send the PUT request
        Event newEvent = new Event();
        newEvent.setGender(Gender.MALE);
        newEvent.setWeapon(WeaponType.FOIL);
        newEvent.setStartDate(LocalDateTime.of(2025, 1, 10, 10, 0));
        newEvent.setEndDate(LocalDateTime.of(2025, 1, 11, 18, 0));

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(newEvent), String.class);

        // Verify the result (expecting 404 Not Found)
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
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

    @Test
    public void deleteEvent_ForbiddenForRegularUser_Failure() throws Exception {
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

        // Send the DELETE request with regular user credentials
        ResponseEntity<Void> result = restTemplate.withBasicAuth("user", "strongpassword456")
                                            .exchange(uri, HttpMethod.DELETE, null, Void.class);

        // Verify the result
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  // Expecting 403 Forbidden
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
        user.setEmail("admin@example.com");  // Set a valid email
        user.setRole(Role.ADMIN);  // Use the correct enum value for Role
        return user;
    }

    public User createValidRegularUser(BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setUsername("user");
        user.setPassword(encoder.encode("strongpassword456"));  // Encode the password
        user.setEmail("validuser@example.com");  // Set a valid email
        user.setRole(Role.USER);  // Use the correct enum value for Role
        return user;
    }
}

