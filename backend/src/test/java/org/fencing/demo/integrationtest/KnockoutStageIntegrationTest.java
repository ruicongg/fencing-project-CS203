package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.fencing.demo.events.Gender;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.player.Player;
import org.fencing.demo.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

class KnockoutStageIntegrationTest extends BaseIntegrationTest {

    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        super.setUp();

        // Initialize players for the matches
        player1 = new Player("player1", passwordEncoder.encode("password1"), "player1@example.com", Role.USER,
                Gender.MALE);
        player1.setElo(1700);
        playerRepository.save(player1);

        player2 = new Player("player2", passwordEncoder.encode("password2"), "player2@example.com", Role.USER,
                Gender.MALE);
        player2.setElo(1700);
        playerRepository.save(player2);

    }

    @Test
    public void getKnockoutStage_ValidKnockoutStageId_Success() throws Exception {
        Long id = knockoutStage.getId();
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

        ResponseEntity<KnockoutStage> result = restTemplate.getForEntity(uri, KnockoutStage.class);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(knockoutStage.getId(), result.getBody().getId());
    }

    @Test
    public void getKnockoutStage_InvalidKnockoutStageId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/999");

        ResponseEntity<KnockoutStage> result = restTemplate.getForEntity(uri, KnockoutStage.class);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void addKnockoutStage_RegularUser_Failure() throws Exception {
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage");

        HttpEntity<KnockoutStage> request = new HttpEntity<>(knockoutStage, createHeaders(userToken));
        ResponseEntity<KnockoutStage> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, KnockoutStage.class);

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    public void deleteKnockoutStage_AdminUser_Success() throws Exception {
        Long id = knockoutStage.getId();
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(204, result.getStatusCode().value());
        assertFalse(knockoutStageRepository.existsById(id));
    }

    @Test
    public void deleteKnockoutStage_RegularUser_Failure() throws Exception {
        Long id = knockoutStage.getId();
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/" + id);

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(userToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(403, result.getStatusCode().value());
        assertTrue(knockoutStageRepository.existsById(id));
    }

    @Test
    public void deleteKnockoutStage_InvalidId_Failure() throws Exception {
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage/999");

        HttpEntity<Void> request = new HttpEntity<>(createHeaders(adminToken));
        ResponseEntity<Void> result = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, Void.class);

        assertEquals(404, result.getStatusCode().value());
    }

}
