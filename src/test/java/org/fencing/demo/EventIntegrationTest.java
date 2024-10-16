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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

    private Tournament tournament;

    private User adminUser;

    private User regularUser;

    private final String baseUrl = "http://localhost:";

    @BeforeEach
    void setUp() {
        // adminUser = createValidAdminUser(encoder);
        // userRepository.save(adminUser);
    
        // regularUser = createValidRegularUser(encoder);
        // userRepository.save(regularUser);

        // Create an admin user
        userRepository.deleteAll();
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        userRepository.save(adminUser);

        // Create a regular user
        regularUser = new User("user", passwordEncoder.encode("userPass"), "user@example.com", Role.USER);
        userRepository.save(regularUser);
        
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

        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "adminPass")
                                        .postForEntity(uri, event, Event.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(event.getGender(), result.getBody().getGender());
        assertEquals(event.getWeapon(), result.getBody().getWeapon());
    }

    @Test
    public void addEvent_ForbiddenForRegularUser_Failure() throws Exception {
        Event event = createValidEvent(tournament);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<Event> result = restTemplate.withBasicAuth("user", "userPass")
                                            .postForEntity(uri, event, Event.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  
    }

    @Test
    public void addEvent_NullTournamentId_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        
        URI uri = new URI(baseUrl + port + "/tournaments/null/events");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_NullEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_MissingRequiredFields_Failure() throws Exception {
        Event event = new Event();
        event.setGender(Gender.MALE);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void addEvent_NonExistentTournamentId_Failure() throws Exception {
        Event event = createValidEvent(tournament);

        Long nonExistentTournamentId = 999L;  
        URI uri = new URI(baseUrl + port + "/tournaments/" + nonExistentTournamentId + "/events");
        
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    // Get All Events by Tournament ID - Success
    @Test
    public void getAllEventsByTournamentId_Success() throws Exception {
        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<Event[]> result = restTemplate.withBasicAuth("admin", "adminPass")
                                            .getForEntity(uri, Event[].class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().length);
    }

    // Get Event by ID - Success
    @Test
    public void getEventById_Success() throws Exception {
        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId());

        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "adminPass")
                                            .getForEntity(uri, Event.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(event.getId(), result.getBody().getId());
    }

    @Test
    public void getAllEventsByTournamentId_RegularUser_Success() throws Exception {
        Event event = createValidEvent(tournament);
        eventRepository.save(event);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        ResponseEntity<Event[]> result = restTemplate.withBasicAuth("user", "userPass")
                                                .getForEntity(uri, Event[].class);
            // assertEquals("smth", result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());  
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().length);
    }

    @Test
    public void addEvent_StartDateBeforeTournamentStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        Event event = createValidEvent(tournament);
        event.setStartDate(LocalDateTime.now().plusDays(24));

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Event start date cannt be earlier than Tournament start date"));
    }

    @Test
    public void addEvent_EndDateBeforeStartDate_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events");

        Event event = createValidEvent(tournament);
        event.setEndDate(LocalDateTime.now().plusDays(24));

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .postForEntity(uri, event, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Event end date must be after start date"));
    }

    // Update Event - Success
    @Test // event successfully created and persisted, but when updating, everything becomes null? eg. id=0, startDate=null
    public void updateEvent_Success() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();
        
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        // ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "adminPass")
        //                                     .getForEntity(uri, Event.class);

        ResponseEntity<Event> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), Event.class);

        // assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("smth", result.getBody());
        assertEquals(Gender.FEMALE, result.getBody().getGender());
        assertEquals(WeaponType.EPEE, result.getBody().getWeapon());
    }

    @Test
    public void updateEvent_ForbiddenForRegularUser_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);
        
        event.setGender(Gender.FEMALE);
        event.setWeapon(WeaponType.EPEE);

        ResponseEntity<Event> result = restTemplate.withBasicAuth("user", "userPass")
                                            .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), Event.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  
    }

    @Test
    public void updateEvent_NullIdsOrEvent_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/null");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test // internal server error 500
    public void updateEvent_StartDateBeforeTournamentStartDate_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        LocalDate tournamentStartDate = tournament.getTournamentStartDate();
        tournamentStartDate = tournamentStartDate.minusDays(2);
        event.setStartDate(tournamentStartDate.atStartOfDay());
        
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), String.class);

        // assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("smth", result.getBody());
        assertTrue(result.getBody().contains("Event start date cannt be earlier than Tournament start date"));
    }

    @Test // internal server error 500
    public void updateEvent_EndDateBeforeStartDate_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        event.setEndDate(LocalDateTime.now().plusDays(24));  // End date before start date

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().contains("Event end date must be after start date"));
    }

    @Test
    public void updateEvent_NonExistentEvent_Failure() throws Exception {
        Event event = createValidEvent(tournament);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/9999");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(event), String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }


    // Delete Event - Success
    @Test
    public void deleteEvent_Success() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        ResponseEntity<Void> result = restTemplate.withBasicAuth("admin", "adminPass")
                                        .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertFalse(eventRepository.findById(event.getId()).isPresent());
    }

    @Test
    public void deleteEvent_ForbiddenForRegularUser_Failure() throws Exception {
        Event event = createValidEvent(tournament);
        long id = eventRepository.save(event).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + id);

        ResponseEntity<Void> result = restTemplate.withBasicAuth("user", "userPass")
                                            .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  // Expecting 403 Forbidden
    }

    // Helper methods for creating valid entities
    private Tournament createValidTournament() {
        return Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.now().plusDays(1))
                .registrationEndDate(LocalDate.now().plusDays(20))
                .tournamentStartDate(LocalDate.now().plusDays(25))
                .tournamentEndDate(LocalDate.now().plusDays(30))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
    }

    private Event createValidEvent(Tournament tournament) {
        return Event.builder()
        .tournament(tournament)
        .gender(Gender.MALE)
        .weapon(WeaponType.FOIL)  
        .startDate(LocalDateTime.now().plusDays(25))  
        .endDate(LocalDateTime.now().plusDays(26))
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

