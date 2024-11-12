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

    @BeforeEach
    void setUp() {
        super.setUp();
    }


    @Test
    public void getTournaments_Success() throws Exception {
        URI uri = createUrl("/tournaments");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Tournament[]> result = restTemplate.exchange(uri, HttpMethod.GET, request, Tournament[].class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().length);

    }

    @Test
    public void getTournament_ValidTournamentId_Success() throws Exception {

        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.GET, request, Tournament.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(tournament.getName(), result.getBody().getName());
    }

    @Test
    public void getTournament_InvalidTournamentId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/999");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.GET, request, Tournament.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addTournament_AdminUser_Success() throws Exception {
        URI uri = createUrl("/tournaments");

        HttpEntity<Tournament> request = new HttpEntity<>(tournament, createHeaders(adminToken));
        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.POST, request, Tournament.class);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(tournament.getName(), result.getBody().getName());
    }

    @Test
    public void addTournament_RegularUser_Failure() throws Exception {
        URI uri = createUrl("/tournaments");

        HttpEntity<Tournament> request = new HttpEntity<>(tournament, createHeaders(userToken));
        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.POST, request, Tournament.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void addTournament_InvalidDates_Failure() throws Exception {
        URI uri = createUrl("/tournaments");
        tournament.setTournamentEndDate(tournament.getTournamentStartDate().minusDays(1)); // Invalid end date

        HttpEntity<Tournament> request = new HttpEntity<>(tournament, createHeaders(adminToken));
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(400, result.getStatusCode().value());
        assertTrue(result.getBody().contains("Tournament end date must be after start date"));
    }

    @Test
    public void addTournament_NullName_Failure() throws Exception {
        URI uri = createUrl("/tournaments");
        tournament.setName(null);

        HttpEntity<Tournament> request = new HttpEntity<>(tournament, createHeaders(adminToken));
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        assertEquals(400, result.getStatusCode().value());
        assertTrue(result.getBody().contains("Tournament name cannot be null"));
    }

    @Test
    public void updateTournament_AdminUser_Success() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        tournament.setName("Updated Spring Open");
        HttpEntity<Tournament> request = new HttpEntity<>(tournament, createHeaders(adminToken));

        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.PUT, request, Tournament.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Updated Spring Open", result.getBody().getName());
    }

    @Test
    public void updateTournament_RegularUser_Failure() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        tournament.setName("Updated Spring Open");
        HttpEntity<Tournament> request = new HttpEntity<>(tournament, createHeaders(userToken));

        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.PUT, request, Tournament.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void updateTournament_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/999");
        HttpEntity<Tournament> request = new HttpEntity<>(tournament, createHeaders(adminToken));

        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.PUT, request, Tournament.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteTournament_AdminUser_Success() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(204, result.getStatusCode().value());
        assertFalse(tournamentRepository.existsById(id));
    }

    @Test
    public void deleteTournament_RegularUser_Failure() throws Exception {
        Long id = tournament.getId();
        URI uri = createUrl("/tournaments/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userToken));
        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(403, result.getStatusCode().value());
        assertTrue(tournamentRepository.existsById(id));
    }

    @Test
    public void deleteTournament_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/999");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }

}
