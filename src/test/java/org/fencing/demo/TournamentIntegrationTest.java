package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.HashSet;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TournamentIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @AfterEach
    void tearDown() {
        tournamentRepository.deleteAll();
    }

    @Test
    public void getTournaments_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments");
        tournamentRepository.save(createValidTournament());

        ResponseEntity<Tournament[]> result = restTemplate.getForEntity(uri, Tournament[].class);
        Tournament[] tournaments = result.getBody();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, tournaments.length);
    }

    @Test
    public void getTournament_ValidTournamentId_Success() throws Exception {
        Tournament tournament = createValidTournament();
        Long id = tournamentRepository.save(tournament).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + id);

        ResponseEntity<Tournament> result = restTemplate.getForEntity(uri, Tournament.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(tournament.getName(), result.getBody().getName());
    }

    @Test
    public void getTournament_InvalidTournamentId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/999");

        ResponseEntity<Tournament> result = restTemplate.getForEntity(uri, Tournament.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addTournament_Success() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments");
        Tournament tournament = createValidTournament();

        ResponseEntity<Tournament> result = restTemplate.postForEntity(uri, tournament, Tournament.class);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(tournament.getName(), result.getBody().getName());
    }

    @Test
    public void updateTournament_Success() throws Exception {
        Tournament tournament = createValidTournament();
        Long id = tournamentRepository.save(tournament).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + id);

        tournament.setName("Updated Spring Open");
        HttpEntity<Tournament> requestEntity = new HttpEntity<>(tournament);

        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Tournament.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Updated Spring Open", result.getBody().getName());
    }

    @Test
    public void updateTournament_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/999");
        Tournament tournament = createValidTournament();
        HttpEntity<Tournament> requestEntity = new HttpEntity<>(tournament);

        ResponseEntity<Tournament> result = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, Tournament.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void deleteTournament_Success() throws Exception {
        Tournament tournament = createValidTournament();
        Long id = tournamentRepository.save(tournament).getId();
        URI uri = new URI(baseUrl + port + "/tournaments/" + id);

        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(204, result.getStatusCode().value());
        assertFalse(tournamentRepository.existsById(id));
    }

    @Test
    public void deleteTournament_InvalidId_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + "/tournaments/999");

        ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }

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
}
