package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.*;
import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.groupstage.GroupStageRepository;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.playerrank.PlayerRank;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private MatchRepository matchRepository;

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
    private PasswordEncoder passwordEncoder;

    private Event event;

    private Tournament tournament;

    private GroupStage groupStage;
    
    private KnockoutStage knockoutStage;

    private User adminUser;

    private User regularUser;

    private final String baseUrl = "http://localhost:";

    // @BeforeEach
    // void setUp() {
        
    //     adminUser = createValidAdminUser(encoder);
    //     if (!userRepository.existsByUsername(adminUser.getUsername())) {
    //         userRepository.save(adminUser);
    //     }

    //     tournament = createValidTournament();
    //     tournamentRepository.save(tournament);

    //     regularUser = createValidRegularUser(encoder);
    //     if (!userRepository.existsByUsername(regularUser.getUsername())) {
    //         userRepository.save(regularUser);
    //     }

    //     event = createValidEvent(tournament, new HashSet<>());
    //     eventRepository.save(event);

    //     // Now create players after event is saved
    //     Set<PlayerRank> players = createPlayers(event);
    //     event.setRankings(players);  // Update event rankings
    //     eventRepository.save(event);

    //     groupStage = createValidGroupStage(event, new ArrayList<>(players));
    //     groupStageRepository.save(groupStage);

    //     knockoutStage = createValidKnockoutStage(event);
    //     knockoutStageRepository.save(knockoutStage);

    // }

    @BeforeEach
    void setUp() {

        eventRepository.deleteAll();
        tournamentRepository.deleteAll();
        groupStageRepository.deleteAll();
        knockoutStageRepository.deleteAll();
        userRepository.deleteAll();
        playerRepository.deleteAll();
        matchRepository.deleteAll();

        userRepository.deleteAll();
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        userRepository.save(adminUser);

        regularUser = new User("user", passwordEncoder.encode("userPass"), "user@example.com", Role.USER);
        userRepository.save(regularUser);

        tournamentRepository.deleteAll();
        tournament = createValidTournament();
        tournamentRepository.save(tournament);

        eventRepository.deleteAll();
        event = createValidEvent(tournament);
        eventRepository.save(event);

        groupStageRepository.deleteAll();
        groupStage = createValidGroupStage(event);
        groupStageRepository.save(groupStage);

        playerRepository.deleteAll();
        addPlayersToEvent(event, groupStage);
        groupStageRepository.save(groupStage);

        knockoutStageRepository.deleteAll();
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
        playerRepository.deleteAll();
        matchRepository.deleteAll();
    }

    @Test //passed!
     public void addInitialMatchForGroupStage_Success() throws Exception {

        groupStageRepository.deleteAll();

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/matches");


        ResponseEntity<List<Match>> result = restTemplate.withBasicAuth("admin", "adminPass")
                                                    .exchange(uri, 
                                                        HttpMethod.POST, 
                                                        null, 
                                                        new ParameterizedTypeReference<List<Match>>() {});
        //System.out.println(result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().size() > 0);
    }

    @Test // passed
    public void addMatchesForKnockoutStage_Success() throws Exception {
        
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + knockoutStage.getId() + "/matches");

        ResponseEntity<List<Match>> result = restTemplate.withBasicAuth("admin", "adminPass")
            .exchange(uri, 
                     HttpMethod.POST, 
                     null, 
                     new ParameterizedTypeReference<List<Match>>() {});

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().size() > 0);
    }

    // @Test
    // public void addInitialMatchForGroupStage_NullGroupStageFound_Failure() throws Exception {

    //     URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/matches");

    //     // When: Sending a POST request to add matches for a non-existent group stage
    //     ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
    //                                                 .postForEntity(uri, null, String.class);

    //     // Then: Assert that the request fails with an appropriate error message
    //     assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    //     assertTrue(result.getBody().contains("No groupStage found for event"));
    // }

    // @Test
    // public void addMatchesForKnockoutStage_NullKnockoutStage_Failure() throws Exception {

    //     URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/null/matches");

    //     // When: Sending a POST request to add matches for a non-existent knockout stage
    //     ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
    //                                                 .postForEntity(uri, null, String.class);

    //     // Then: Assert that the request fails with an appropriate error message
    //     assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    //     assertTrue(result.getBody().contains("No KnockoutStage found for event"));
    // }

    @Test // passed
    public void addInitialMatchForGroupStage_EventNotFound_Failure() throws Exception {
        long nonExistentEventId = 999L;

        URI uri = new URI(baseUrl + port + "/tournaments/1/events/" + nonExistentEventId + "/groupStage/matches");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                                                    .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test // passed
    public void addMatchesForKnockoutStage_EventNotFound_Failure() throws Exception {
        long nonExistentEventId = 999L;
        
        URI uri = new URI(baseUrl + port + "/tournaments/1/events/" + nonExistentEventId + "/knockoutStage/1/matches");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                                                    .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
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

    public GroupStage createValidGroupStage(Event event) {
        return GroupStage.builder()
            .event(event)                                                  
            .matches(new ArrayList<Match>())                   
            .allMatchesCompleted(false)                   
            .build();
    }

    public KnockoutStage createValidKnockoutStage(Event event) {
        return KnockoutStage.builder()
            .event(event)                                
            .matches(new ArrayList<>())                   
            .build();
    }

    private Event addPlayersToEvent(Event event, GroupStage groupStage) {
        SortedSet<PlayerRank> playerRanks = event.getRankings();
        for (int i = 1; i <= 8; i++) {
            Player player = createValidPlayer(i); 
            playerRepository.save(player);
            PlayerRank playerRank = createPlayerRank(player, event);
            playerRanks.add(playerRank);
        }
        event.setRankings(playerRanks);
        return eventRepository.save(event);
    }

    private PlayerRank createPlayerRank(Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        player.getPlayerRanks().add(playerRank);
        return playerRank;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setId((long) id);
        player.setElo(1200 + (id * 100));
        player.setEmail("player"+id+"@email.com");
        player.setUsername("player"+id);
        player.setPassword(passwordEncoder.encode("playerPass"));
        player.setRole(Role.USER);
        
        return playerRepository.save(player);
    }

    // public User createValidAdminUser(BCryptPasswordEncoder encoder) {
    //     User user = new User();
    //     user.setUsername("admin");
    //     user.setPassword(encoder.encode("strongpassword123"));  // Encode the password
    //     user.setEmail("admin@example.com");  // Set a valid email
    //     user.setRole(Role.ADMIN);  // Use the correct enum value for Role
    //     return user;
    // }

    // public User createValidRegularUser(BCryptPasswordEncoder encoder) {
    //     User user = new User();
    //     user.setUsername("user");
    //     user.setPassword(encoder.encode("strongpassword456"));  // Encode the password
    //     user.setEmail("validuser@example.com");  // Set a valid email
    //     user.setRole(Role.USER);  // Use the correct enum value for Role
    //     return user;
    // }
}
