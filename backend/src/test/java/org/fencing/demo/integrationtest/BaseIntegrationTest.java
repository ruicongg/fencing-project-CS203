package org.fencing.demo.integrationtest;

import java.net.URI;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @LocalServerPort
    protected int port;
    protected final String baseUrl = "http://localhost:";

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected TournamentRepository tournamentRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected PlayerRepository playerRepository;
    @Autowired
    protected EventRepository eventRepository;
    @Autowired
    protected MatchRepository matchRepository;
    @Autowired
    protected GroupStageRepository groupStageRepository;
    @Autowired
    protected KnockoutStageRepository knockoutStageRepository;

    protected User adminUser;
    protected User regularUser;
    protected Player playerUser;

    protected String adminToken;
    protected String userToken;
    protected String playerToken;

    protected Event event;
    protected Tournament tournament;
    protected GroupStage groupStage;
    protected KnockoutStage knockoutStage;
    @BeforeEach
    void setUp() {

        userRepository.deleteAll();
        adminUser = userRepository.save(createValidAdminUser());
        regularUser = userRepository.save(createValidUser());
        playerUser = playerRepository.save(createValidPlayer());
        tournament = tournamentRepository.save(createValidTournament());
        event = eventRepository.save(createValidEvent(tournament));
        groupStage = groupStageRepository.save(createValidEmptyGroupStage(event));
        knockoutStage = knockoutStageRepository.save(createValidEmptyKnockoutStage(event));
        adminToken = "Bearer " + generateToken(adminUser);
        userToken = "Bearer " + generateToken(regularUser);
        playerToken = "Bearer " + generateToken(playerUser);
    }


    protected List<Player> setUpWithPlayersInEvent(int[] uniqueElos) {
        List<Player> players = new ArrayList<>();
        SortedSet<PlayerRank> rankings = event.getRankings();
        for (int elo : uniqueElos) {
            Player player = new Player("player" + elo, passwordEncoder.encode("playerPass"),
                    "player" + elo + "@example.com", Role.USER, Gender.MALE);
            player.setElo(elo);
            playerRepository.save(player);
            players.add(player);
            PlayerRank rank = createPlayerRank(player, event);
            rankings.add(rank);
        }
        event.setRankings(rankings);
        event = eventRepository.save(event);
        return players;
    }


    @AfterEach
    void tearDown() {
        tournamentRepository.deleteAll();
        userRepository.deleteAll();
        eventRepository.deleteAll();
        knockoutStageRepository.deleteAll();
        groupStageRepository.deleteAll();
        matchRepository.deleteAll();
    }

    protected HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }


    protected URI createUrl(String endpoint) {
        return URI.create(baseUrl + port + endpoint);
    }


    // security 
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


    // Helper method to create everything
    private User createValidAdminUser() {
        return User.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminPass"))
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();
    }

    private User createValidUser() {
        return User.builder()
                .username("user")
                .password(passwordEncoder.encode("userPass"))
                .email("user@example.com")
                .role(Role.USER)
                .build();
    }

    private Player createValidPlayer() {
        return new Player("player", passwordEncoder.encode("playerPass"), "player@example.com", Role.USER, Gender.MALE);
    }

    private Tournament createValidTournament() {
        return Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.now())
                .registrationEndDate(LocalDate.now().plusDays(30))
                .tournamentStartDate(LocalDate.now().plusDays(60))
                .tournamentEndDate(LocalDate.now().plusDays(90))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
    }

    protected Event createValidEvent(Tournament tournament) {
        return Event.builder()
                .tournament(tournament)
                .gender(Gender.MALE)
                .weapon(WeaponType.FOIL)
                .startDate(LocalDateTime.now().plusDays(70))
                .endDate(LocalDateTime.now().plusDays(80))
                .build();
    }

    private GroupStage createValidEmptyGroupStage(Event event) {
        return GroupStage.builder()
                .event(event)
                .matches(new ArrayList<Match>())
                .allMatchesCompleted(false)
                .build();
    }

    private KnockoutStage createValidEmptyKnockoutStage(Event event) {
        return KnockoutStage.builder()
                .event(event)
                .matches(new ArrayList<>())
                .build();
    }

    private PlayerRank createPlayerRank(Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        return playerRank;
    }

}
