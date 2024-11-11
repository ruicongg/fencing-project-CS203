package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;

import org.fencing.demo.knockoutstage.KnockoutStage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class MatchMakingIT extends BaseIntegrationTest {
    
    @Test
    public void addKnockoutStage_AdminUser_Success() throws Exception {
        URI uri = createUrl("/tournaments/" + tournament.getId() + "/events/" + event.getId() + "/knockoutStage");


        HttpEntity<KnockoutStage> request = new HttpEntity<>(knockoutStage, createHeaders(adminToken));
        ResponseEntity<KnockoutStage> result = restTemplate
                .exchange(uri, HttpMethod.POST, request, KnockoutStage.class);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody().getId());
    }
}
