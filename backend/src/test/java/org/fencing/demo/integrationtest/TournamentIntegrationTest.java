package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.fencing.demo.tournament.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TournamentIntegrationTest extends BaseIntegrationTest {

    // @LocalServerPort
    // private int port;

    // private final String baseUrl = "http://localhost:";

    // @Autowired
    // private TestRestTemplate restTemplate;

    // @Autowired
    // private TournamentRepository tournamentRepository;

    // @Autowired
    // private UserRepository userRepository;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    // private User adminUser;
    // private User regularUser;

    @BeforeEach
    void setUp() {
        super.setUp();
    }


    @Test
    public void getTournaments_Success() throws Exception {
        URI uri = createUrl("/tournaments");

        ResponseEntity<Tournament[]> result = restTemplate.getForEntity(uri, Tournament[].class);
        Tournament[] tournaments = result.getBody();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, tournaments.length);

    }

    @Test
    public void getTournament_ValidTournamentId_Success() throws Exception {

        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        ResponseEntity<Tournament> result = restTemplate.getForEntity(uri, Tournament.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(tournament.getName(), result.getBody().getName());
    }

    @Test
    public void getTournament_InvalidTournamentId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/999");

        ResponseEntity<Tournament> result = restTemplate.getForEntity(uri, Tournament.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addTournament_AdminUser_Success() throws Exception {
        URI uri = createUrl("/tournaments");

        ResponseEntity<Tournament> result = restTemplate
            .withBasicAuth("admin", "adminPass")
            .postForEntity(uri, tournament, Tournament.class);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(tournament.getName(), result.getBody().getName());
    }

    @Test
    public void addTournament_RegularUser_Failure() throws Exception {
        URI uri = createUrl("/tournaments");

        ResponseEntity<Tournament> result = restTemplate
            .withBasicAuth("user", "userPass")
            .postForEntity(uri, tournament, Tournament.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void addTournament_InvalidDates_Failure() throws Exception {
        URI uri = createUrl("/tournaments");
        tournament.setTournamentEndDate(tournament.getTournamentStartDate().minusDays(1)); // Invalid end date

        ResponseEntity<String> result = restTemplate
            .withBasicAuth("admin", "adminPass")
            .postForEntity(uri, tournament, String.class);

        assertEquals(400, result.getStatusCode().value());
        assertTrue(result.getBody().contains("Tournament end date must be after start date"));
    }

    @Test
    public void addTournament_NullName_Failure() throws Exception {
        URI uri = createUrl("/tournaments");
        tournament.setName(null);

        ResponseEntity<String> result = restTemplate
            .withBasicAuth("admin", "adminPass")
            .postForEntity(uri, tournament, String.class);

        assertEquals(400, result.getStatusCode().value());
        assertTrue(result.getBody().contains("Tournament name cannot be null"));
    }

    @Test
    public void updateTournament_AdminUser_Success() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        tournament.setName("Updated Spring Open");
        HttpEntity<Tournament> requestEntity = new HttpEntity<>(tournament);

        ResponseEntity<Tournament> result = restTemplate
            .withBasicAuth("admin", "adminPass")
            .exchange(uri, HttpMethod.PUT, requestEntity, Tournament.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Updated Spring Open", result.getBody().getName());
    }

    @Test
    public void updateTournament_RegularUser_Failure() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        tournament.setName("Updated Spring Open");
        HttpEntity<Tournament> requestEntity = new HttpEntity<>(tournament);

        ResponseEntity<Tournament> result = restTemplate
            .withBasicAuth("user", "userPass")
            .exchange(uri, HttpMethod.PUT, requestEntity, Tournament.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateTournament_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/999");
        HttpEntity<Tournament> requestEntity = new HttpEntity<>(tournament);

        ResponseEntity<Tournament> result = restTemplate
            .withBasicAuth("admin", "adminPass")
            .exchange(uri, HttpMethod.PUT, requestEntity, Tournament.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteTournament_AdminUser_Success() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        ResponseEntity<Void> result = restTemplate
            .withBasicAuth("admin", "adminPass")
            .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(204, result.getStatusCode().value());
        assertFalse(tournamentRepository.existsById(id));
    }

    @Test
    public void deleteTournament_RegularUser_Failure() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        ResponseEntity<Void> result = restTemplate
            .withBasicAuth("user", "userPass")
            .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(403, result.getStatusCode().value());
        assertTrue(tournamentRepository.existsById(id));
    }

    @Test
    public void deleteTournament_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/999");

        ResponseEntity<Void> result = restTemplate
            .withBasicAuth("admin", "adminPass")
            .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }

}
