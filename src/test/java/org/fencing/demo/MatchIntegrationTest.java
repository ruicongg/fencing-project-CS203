package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.fencing.demo.match.MatchRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private MatchRepository matchRepository;

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

    @BeforeEach
    void setUp() {
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

        // groupStage.setPlayers(new ArrayList<>(event.getRankings()));
        groupStageRepository.save(groupStage);

        knockoutStageRepository.deleteAll();
        knockoutStage = createValidKnockoutStage(event);
        knockoutStageRepository.save(knockoutStage);

        // event.getGroupStages().add(groupStage);
        // event.getKnockoutStages().add(knockoutStage);
        // eventRepository.save(event);
    }


    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
        tournamentRepository.deleteAll();
        groupStageRepository.deleteAll();
        knockoutStageRepository.deleteAll();
        matchRepository.deleteAll();
        userRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void addInitialGroupStageMatches_ShouldSucceed() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/groupStage/matches");

        ResponseEntity<Match[]> response = restTemplate.withBasicAuth("admin", "adminPass")
                                                    .postForEntity(uri, null, Match[].class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    public void addKnockoutStageMatches_ShouldSucceed() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + knockoutStage.getId() + "/matches");

        ResponseEntity<Match[]> response = restTemplate.withBasicAuth("admin", "adminPass")
                                                    .postForEntity(uri, null, Match[].class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    public void addInitialGroupStageMatches_WithNonExistentEvent_ShouldFail() throws Exception {
        long nonExistentEventId = 999L;

        URI uri = new URI(baseUrl + port + "/tournaments/1/events/" + nonExistentEventId + "/groupStage/matches");

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
                                                    .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addKnockoutStageMatches_WithNonExistentEvent_ShouldFail() throws Exception {
        long nonExistentEventId = 999L;
        
        URI uri = new URI(baseUrl + port + "/tournaments/1/events/" + nonExistentEventId + "/knockoutStage/1/matches");

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
                                                    .postForEntity(uri, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // @Test
    // public void addMatchesForKnockoutStage_NoKnockoutStage() throws Exception {
    //     Long invalidKnockoutStageId = 9999L;

    //     URI uri = new URI(baseUrl + port + "/tournaments/1/events/" + event.getId() + "/knockoutStage/" + invalidKnockoutStageId + "/matches");

    //     // Call the endpoint for an event without knockout stages
    //     ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
    //             .postForEntity(uri, null, String.class);

    //     // Assert the response status is BAD_REQUEST
    //     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    //     // Assert the error message
    //     String errorMessage = response.getBody();
    //     assertTrue(errorMessage.contains("No KnockoutStage found for event"));
    // }

    // @Test // consider removing the null check for eventid bc null would throw invalid parameter error (expected type Long)
    // public void addMatchesForKnockoutStage_NullEventId() throws Exception {
    //     URI uri = new URI(baseUrl + port + "/tournaments/1/events/null/knockoutStage/1/matches");

    //     // Call the endpoint with null event ID
    //     ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
    //             .postForEntity(uri, null, String.class);

    //     // Assert the response status is BAD_REQUEST
    //     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    //     // Assert the error message
    //     String errorMessage = response.getBody();
    //     assertEquals("smth", response.getBody());
    //     assertTrue(errorMessage.contains("Event ID cannot be null"));
    // }

    @Test
    public void getAllGroupStageMatches_ShouldSucceed() throws Exception {
        List<PlayerRank> players = new ArrayList<>(event.getRankings());
        
        Match groupMatch1 = createGroupStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(groupMatch1);

        Match groupMatch2 = createGroupStageMatch(event, players.get(2).getPlayer(), players.get(3).getPlayer());
        matchRepository.save(groupMatch2);

        groupStage.getMatches().clear();
        groupStage.getMatches().add(groupMatch1);
        groupStage.getMatches().add(groupMatch2);
        groupStageRepository.save(groupStage);

        URI uri = new URI("/tournaments/1/events/" + event.getId() + "/groupStage/" + groupStage.getId() + "/matches");

        ResponseEntity<Match[]> response = restTemplate.withBasicAuth("admin", "adminPass")
                .getForEntity(uri, Match[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Match[] matches = response.getBody();
        assertNotNull(matches);
        assertEquals(2, matches.length);
    }

    @Test
    public void getAllKnockoutStageMatches_ShouldSucceed() throws Exception {
        List<PlayerRank> players = new ArrayList<>(event.getRankings());
        
        Match knockoutMatch1 = createKnockoutStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(knockoutMatch1);

        Match knockoutMatch2 = createKnockoutStageMatch(event, players.get(2).getPlayer(), players.get(3).getPlayer());
        matchRepository.save(knockoutMatch2);

        knockoutStage.getMatches().clear();
        knockoutStage.getMatches().add(knockoutMatch1);
        knockoutStage.getMatches().add(knockoutMatch2);
        knockoutStageRepository.save(knockoutStage);

        URI uri = new URI("/tournaments/1/events/" + event.getId() + "/knockoutStage/" + knockoutStage.getId() + "/matches");

        ResponseEntity<Match[]> response = restTemplate.withBasicAuth("admin", "adminPass")
                .getForEntity(uri, Match[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Match[] matches = response.getBody();
        assertNotNull(matches);
        assertEquals(2, matches.length);
    }

    // @Test
    // public void getAllMatchesForKnockoutStage_KnockoutStageNotFound() throws Exception {
    //     Long invalidKnockoutStageId = 9999L;

    //     URI uri = new URI("/tournaments/1/events/" + event.getId() + "/knockoutStage/" + invalidKnockoutStageId + "/matches");

    //     // Call the endpoint with an invalid knockout stage ID
    //     ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
    //             .getForEntity(uri, String.class);

    //     // Assert the response status is NOT_FOUND
    //     assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    //     // Assert the error message
    //     String errorMessage = result.getBody();
    //     assertTrue(errorMessage.contains("Knockout stage not found"));
    // }
    
    // @Test
    // public void getAllMatchesForGroupStage_NullGroupStageId() throws Exception {
    //     URI uri = new URI("/tournaments/1/events/" + event.getId() + "/groupStage/null/matches");

    //     ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
    //             .getForEntity(uri, String.class);

    //     assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
 
    //     String errorMessage = result.getBody();
    //     assertTrue(errorMessage.contains("Invalid parameter: knockoutStageId. Expected type: Long"));
    // }

    // @Test
    // public void getAllMatchesForKnockoutStage_NullKnockoutStageId() throws Exception {
    //     URI uri = new URI("/tournaments/1/events/" + event.getId() + "/knockoutStage/null/matches");

    //     ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
    //             .getForEntity(uri, String.class);

    //     // Assert the response status is BAD_REQUEST
    //     assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
 
    //     // Assert the error message
    //     String errorMessage = result.getBody();
    //     assertTrue(errorMessage.contains("Invalid parameter: knockoutStageId. Expected type: Long"));
    // }


    @Test
    public void updateMatch_ShouldSucceed() throws Exception {
        List<PlayerRank> players = new ArrayList<>(event.getRankings());
        
        Match match = createKnockoutStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(match);

        match.setPlayer1Score(7);
        match.setPlayer2Score(6);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/match/" + match.getId());

        ResponseEntity<Match> response = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(match), Match.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Match updatedMatch = response.getBody();
        assertNotNull(updatedMatch);
        assertEquals(7, updatedMatch.getPlayer1Score());
        assertEquals(6, updatedMatch.getPlayer2Score());
    }

    @Test
    public void updateMatch_WithNonExistentMatch_ShouldFail() throws Exception {
        List<PlayerRank> players = new ArrayList<>(event.getRankings());

        Match match = createKnockoutStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(match);

        Long nonExistentMatchId = 9999L;

        URI uri = new URI("/tournaments/1/events/" + event.getId() + "/match/" + nonExistentMatchId);

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(match), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void updateMatch_WithEventMismatch_ShouldFail() throws Exception {
        Event anotherEvent = createValidEvent(tournament);
        eventRepository.save(anotherEvent);

        List<PlayerRank> players = new ArrayList<>(event.getRankings());

        Match match = createKnockoutStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(match);

        URI uri = new URI("/tournaments/1/events/" + anotherEvent.getId() + "/match/" + match.getId());

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(match), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String errorMessage = response.getBody();
        assertTrue(errorMessage.contains("Event cannot be changed"));
    }

    @Test
    public void updateMatch_WithPlayerNotInEvent_ShouldFail() throws Exception {
        List<PlayerRank> players = new ArrayList<>(event.getRankings());

        Match match = createKnockoutStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(match);

        Player playerNotInEvent = createValidPlayer(99);
        match.setPlayer1(playerNotInEvent);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/match/" + match.getId());

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, new HttpEntity<>(match), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("is not registered in this event"));
    }

    @Test
    public void deleteMatch_ShouldSucceed() throws Exception {
        List<PlayerRank> players = new ArrayList<>(event.getRankings());

        Match match = createKnockoutStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(match);

        URI uri = new URI("/tournaments/1/events/" + event.getId() + "/match/" + match.getId());

        ResponseEntity<Void> response = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(matchRepository.existsById(match.getId()));
    }

    @Test
    public void deleteMatch_WithNonExistentMatch_ShouldFail() throws Exception {
        Long nonExistentMatchId = 9999L;

        URI uri = new URI("/tournaments/1/events/" + event.getId() + "/match/" + nonExistentMatchId);

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getMatch_ShouldSucceed() throws Exception {
        List<PlayerRank> players = new ArrayList<>(event.getRankings());

        Match match = createKnockoutStageMatch(event, players.get(0).getPlayer(), players.get(1).getPlayer());
        matchRepository.save(match);

        URI uri = new URI("/tournaments/1/events/" + event.getId() + "/match/" + match.getId());

        ResponseEntity<Match> response = restTemplate.withBasicAuth("admin", "adminPass")
                .getForEntity(uri, Match.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Match retrievedMatch = response.getBody();
        assertNotNull(retrievedMatch);
        assertEquals(match.getPlayer1(), retrievedMatch.getPlayer1());
        assertEquals(match.getPlayer2(), retrievedMatch.getPlayer2());
    }

    @Test
    public void getMatch_WithNonExistentMatch_ShouldFail() throws Exception {
        Long nonExistentMatchId = 9999L;

        URI uri = new URI("/tournaments/1/events/" + event.getId() + "/match/" + nonExistentMatchId);

        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "adminPass")
                .getForEntity(uri, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
            // .players(new ArrayList<PlayerRank>())                            
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
        for (int i = 1; i <= 8; i++) {
            Player player = createValidPlayer(i); 
            PlayerRank playerRank = createPlayerRank(player, event, groupStage);
            event.getRankings().add(playerRank);
        }

        return eventRepository.save(event);
    }

    private PlayerRank createPlayerRank(Player player, Event event, GroupStage groupStage) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        playerRank.setGroupStage(groupStage);
        return playerRank;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setEmail("player"+id+"@email.com");
        player.setUsername("player"+id);
        player.setPassword(passwordEncoder.encode("playerPass"));
        player.setRole(Role.USER);
        return playerRepository.save(player);
    }

    private Match createGroupStageMatch(Event event, Player player1, Player player2) {
        Match match = Match.builder()
            .event(event)
            .player1(player1)
            .player2(player2)
            .build();
        match.setGroupStage(groupStage);
        return match;
    }

    private Match createKnockoutStageMatch(Event event, Player player1, Player player2) {
        Match match = Match.builder()
            .event(event)
            .player1(player1)
            .player2(player2)
            .build();
        match.setKnockoutStage(knockoutStage);
        return match;
    }
}
