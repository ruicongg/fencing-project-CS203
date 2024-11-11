package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AftEventIntegrationTest {

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
    private GroupStage groupStage;
    private KnockoutStage knockoutStage;
    private String adminToken;
    private String userToken;
    private Match grpMatch;
    private Match knockoutMatch;

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
    private MatchRepository matchRepository;

    @Autowired
    private GroupStageRepository groupStageRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // @Autowired
    // private AfterEventRepository afterEventRepository;

    @BeforeEach
    void setUp() {
        // Clear repositories in a consistent order to avoid conflicts
        matchRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
        groupStageRepository.deleteAll();
        knockoutStageRepository.deleteAll();
        eventRepository.deleteAll();
        tournamentRepository.deleteAll();

        // Save users
        adminUser = new User("admin", passwordEncoder.encode("adminPass"), "admin@example.com", Role.ADMIN);
        userRepository.save(adminUser);
        regularUser = new User("user", passwordEncoder.encode("userPass"), "user@example.com", Role.USER);
        userRepository.save(regularUser);

        // Save players
        player1 = createValidPlayer(1);
        player2 = createValidPlayer(2);
        playerRepository.save(player1);
        playerRepository.save(player2);

        // Create and save tournament, event, group stage, and knockout stage
        tournament = createValidTournament();
        tournamentRepository.save(tournament);
        event = createValidEvent(tournament);
        tournament.getEvents().add(event);
        eventRepository.save(event);

        groupStage = createValidGroupStage(event);
        event.getGroupStages().add(groupStage);
        groupStageRepository.save(groupStage);

        knockoutStage = createValidKnockoutStage(event);
        event.getKnockoutStages().add(knockoutStage);
        knockoutStageRepository.save(knockoutStage);

        // Save PlayerRanks (only after groupStage is created)
        PlayerRank playerRank1 = createPlayerRank(player1, event, groupStage);
        PlayerRank playerRank2 = createPlayerRank(player2, event, groupStage);
        

        // Create and save matches
        grpMatch = createValidGroupMatch();
        groupStage.getMatches().add(grpMatch);
        matchRepository.save(grpMatch);
        groupStageRepository.save(groupStage);

        knockoutMatch = createValidKnockoutMatch();
        knockoutStage.getMatches().add(knockoutMatch);
        matchRepository.save(knockoutMatch);
        knockoutStageRepository.save(knockoutStage);

        playerRepository.save(player1);
        playerRepository.save(player2);

        playerRank1.updateAfterMatch(grpMatch.getPlayer1Score(), grpMatch.getPlayer2Score(), playerRank2);
        playerRank2.updateAfterMatch(grpMatch.getPlayer2Score(), grpMatch.getPlayer1Score(), playerRank1);

        playerRank1.updateAfterMatch(knockoutMatch.getPlayer1Score(), knockoutMatch.getPlayer2Score(), playerRank2);
        playerRank2.updateAfterMatch(knockoutMatch.getPlayer2Score(), knockoutMatch.getPlayer1Score(), playerRank1);

        event.getRankings().add(playerRank1);
        event.getRankings().add(playerRank2);
        eventRepository.save(event);

        player1.getPlayerRanks().add(playerRank1);
        player2.getPlayerRanks().add(playerRank2);
        playerRepository.save(player1);
        playerRepository.save(player2);

        System.out.println();
        System.out.println("player1ranks: " + player1.getPlayerRanks());
        System.out.println("player2ranks: " + player2.getPlayerRanks());
        System.out.println();
    }

    // @AfterEach
    // void tearDown(){
    //     playerRepository.deleteAll();
    //     userRepository.deleteAll();
    //     matchRepository.deleteAll();
    //     groupStageRepository.deleteAll();
    //     knockoutStageRepository.deleteAll();
    //     eventRepository.deleteAll();
    //     tournamentRepository.deleteAll();
        
    // }



    @Test
    public void endEvent_Success() throws Exception {
        // Arrange
        // Create a URI for the endEvent endpoint
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/elo");

        System.out.println();
        for (Player player : playerRepository.findAll()) {
            System.out.println("Players at the start: " + player.getElo());
        }

          // Act
        ResponseEntity<List<Player>> response = restTemplate.withBasicAuth("admin", "adminPass")
        .exchange(uri, HttpMethod.PUT, null, new ParameterizedTypeReference<List<Player>>() {});

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected OK status");

        // Get the list of players from the response
        List<Player> updatedPlayers = response.getBody();

        if (updatedPlayers != null) {
            System.out.println("ELOs at the end:");
            for (Player player : updatedPlayers) {
                System.out.println("Player " + player.getId() + ": " + player.getElo());
            }
        }
    }


    @Test
    //passed
    public void endEvent_InvalidEventId_Failure()  throws Exception {
        Long invalidId = -1L; // Use an invalid ID
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + invalidId + "/elo");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    // //Need check
    //need seperate set up
    @Test
    public void endEvent_IncompleteMatches_Failure() throws Exception {
        grpMatch.setPlayer1Score(0);
        grpMatch.setPlayer2Score(0);
        matchRepository.save(grpMatch);
        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/elo");

        ResponseEntity<String> result = restTemplate.withBasicAuth("admin", "adminPass")
                .exchange(uri, HttpMethod.PUT, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }



    @Test
    public void endEvent_ForbiddenForRegularUser_Failure() throws Exception {
        Event event = createValidEvent(tournament);

        URI uri = new URI(baseUrl + port + "/tournaments/" + tournament.getId() + "/events" + event.getId() + "/elo");

        ResponseEntity<String> result = restTemplate.withBasicAuth("user", "userPass")
                                            .exchange(uri, HttpMethod.PUT, null, String.class);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());  
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
            //.players(new ArrayList<PlayerRank>())                            
            .matches(new ArrayList<Match>())                   
            .allMatchesCompleted(true)                   
            .build();
    }

    public KnockoutStage createValidKnockoutStage(Event event) {
        return KnockoutStage.builder()
            .event(event)                                
            .matches(new ArrayList<>())                   
            .build();
    }

    private PlayerRank createPlayerRank(Player player, Event event, GroupStage groupStage) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        //playerRank.setGroupStage(groupStage);
        playerRank.initializeTempElo();
        return playerRank;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setEmail("player" + id + "@email.com");
        player.setUsername("player" + id + "_" + System.currentTimeMillis());  // Ensures unique username per player
        player.setPassword(passwordEncoder.encode("playerPass"));
        player.setElo(1500);
        player.setReached2400(false);
        player.setRole(Role.USER);
        player.setMatchesAsPlayer1(new HashSet<Match>());
        player.setMatchesAsPlayer2(new HashSet<Match>());
        player.setPlayerRanks(new HashSet<PlayerRank>());
        return player;
    }

    private Match createValidGroupMatch(){
        Match match = new Match();
        match.setGroupStage(groupStage);
        match.setEvent(event);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setPlayer1Score(4);
        match.setPlayer2Score(7);

        player1.getMatchesAsPlayer1().add(match);
        player2.getMatchesAsPlayer2().add(match);

        return match;

    }


    private Match createValidKnockoutMatch(){
        Match match = new Match();
        match.setKnockoutStage(knockoutStage);
        match.setEvent(event);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setPlayer1Score(4);
        match.setPlayer2Score(7);

        player1.getMatchesAsPlayer1().add(match);
        player2.getMatchesAsPlayer2().add(match);

        return match;

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

