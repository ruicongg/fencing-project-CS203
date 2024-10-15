package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankComparator;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.GroupStageRepository;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.stages.KnockoutStageRepository;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;
import org.fencing.demo.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GroupStageRepository groupStageRepository;

    @Autowired
    private KnockoutStageRepository knockoutStageRepository;

    @Autowired
    private UserRepository userRepository; // Assuming this is your user repository

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private Event event;

    private Tournament tournament;

    private GroupStage groupStage;
    
    private KnockoutStage knockoutStage;

    private User adminUser;

    private User regularUser;

    private final String baseUrl = "http://localhost:";

    @BeforeEach
    void setUp() {
        
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

        Set<PlayerRank> players = createPlayers(event);

        event = createValidEvent(tournament, players);
        eventRepository.save(event);

        groupStage = createValidGroupStage(event, new ArrayList<>(players));
        groupStageRepository.save(groupStage);

        knockoutStage = createValidKnockoutStage(event);
        knockoutStageRepository.save(knockoutStage);

    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
        tournamentRepository.deleteAll();
        groupStageRepository.deleteAll();
        knockoutStageRepository.deleteAll();
        userRepository.deleteAll();
        // entityManager.flush();
    }

    @Test
    @Transactional
    public void addInitialMatchForGroupStage_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/matches");

        // When: Sending a POST request to add matches for the group stage
        ResponseEntity<Match[]> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                                    .postForEntity(uri, null, Match[].class);

        // Then: Assert that matches are created successfully
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().length > 0);
    }

    @Test
    @Transactional
    public void addMatchesForKnockoutStage_Success() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + knockoutStage.getId() + "/matches");

        // When: Sending a POST request to add matches for the knockout stage
        ResponseEntity<Match[]> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                                    .postForEntity(uri, null, Match[].class);

        // Then: Assert that matches are created successfully
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().length > 0);
    }

    @Test
    @Transactional
    public void addInitialMatchForGroupStage_NoGroupStageFound_Failure() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/matches");

        // When: Sending a POST request to add matches for a non-existent group stage
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                                    .postForEntity(uri, null, String.class);

        // Then: Assert that the request fails with an appropriate error message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody().contains("No groupStage found for event"));
    }

    @Test
    @Transactional
    public void addMatchesForKnockoutStage_NoKnockoutStageFound_Failure() throws Exception {

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/1/matches");

        // When: Sending a POST request to add matches for a non-existent knockout stage
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                                    .postForEntity(uri, null, String.class);

        // Then: Assert that the request fails with an appropriate error message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody().contains("No KnockoutStage found for event"));
    }

    @Test
    @Transactional
    public void addInitialMatchForGroupStage_EventNotFound_Failure() throws Exception {
        // Given: Event ID does not exist
        long nonExistentEventId = 999L;

        URI uri = new URI(baseUrl + port + "/tournaments/1/events/" + nonExistentEventId + "/groupStage/matches");

        // When: Sending a POST request with a non-existent event ID
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                                    .postForEntity(uri, null, String.class);

        // Then: Assert that the request fails with a 404 Not Found status
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().contains("Could not find Event"));
    }

    @Test
    @Transactional
    public void addMatchesForKnockoutStage_EventNotFound_Failure() throws Exception {
        // Given: Event ID does not exist
        long nonExistentEventId = 999L;
        
        URI uri = new URI(baseUrl + port + "/tournaments/1/events/" + nonExistentEventId + "/knockoutStage/1/matches");

        // When: Sending a POST request with a non-existent event ID
        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "strongpassword123")
                                                    .postForEntity(uri, null, String.class);

        // Then: Assert that the request fails with a 404 Not Found status
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody().contains("Could not find Event"));
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

    private Event createValidEvent(Tournament tournament, Set<PlayerRank> players) {
        return Event.builder()
            .startDate(LocalDateTime.now().plusDays(10))  // Set a start date in the future
            .endDate(LocalDateTime.now().plusDays(11))    // Set an end date after the start date
            .gender(Gender.MALE)                          // Set a valid gender
            .weapon(WeaponType.FOIL)                      // Set a valid weapon type
            .tournament(tournament)                       // Set the related tournament
            .rankings(players)  // Initialize rankings set
            .groupStages(new ArrayList<>())                      // Initialize group stages list
            .knockoutStages(new ArrayList<>())                   // Initialize knockout stages list
            .build();
    }

    public GroupStage createValidGroupStage(Event event, List<PlayerRank> players) {
        return GroupStage.builder()
            .event(event)                                 // Set the related event
            .players(players)                             // Set the list of players in the group
            .matches(new ArrayList<>())                   // Initialize matches list as empty
            .allMatchesCompleted(false)                   // Set initial state of match completion
            .build();
    }

    public KnockoutStage createValidKnockoutStage(Event event) {
        return KnockoutStage.builder()
            .event(event)                                 // Set the related event
            .matches(new ArrayList<>())                   // Initialize matches list as empty
            .build();
    }

    private Set<PlayerRank> createPlayers(Event event) {
        Set<PlayerRank> players = new TreeSet<>(new PlayerRankComparator());
        for (int i = 1; i <= 8; i++) {
            Player player = createValidPlayer(i); 
            PlayerRank playerRank = createPlayerRank(i, player, event);
            players.add(playerRank);
        }
        return players;
    }

    private PlayerRank createPlayerRank(int id, Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setId((long) id);
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        return playerRank;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setId((long) id);
        player.setUsername("Player" + id);
        player.setElo(1700);
        player.setEmail("player"+id+"@email.com");
        player.setUsername("player"+id);
        player .setPassword("123456");
        playerRepository.save(player);
        return player;
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
