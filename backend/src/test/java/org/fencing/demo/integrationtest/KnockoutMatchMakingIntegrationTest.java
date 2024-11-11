package org.fencing.demo.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.fencing.demo.knockoutmatchmaking.KnockoutMatchMakingService;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class KnockoutMatchMakingIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private KnockoutMatchMakingService knockoutMatchMakingService;

    @Test
    void createKnockoutStageAndMatches_SmallEvent() {
        // Setup 25 players
        int[] elos = new int[25];
        for (int i = 0; i < 25; i++) {
            elos[i] = 1000 + i;
        }
        List<Player> players = setUpWithPlayersInEvent(elos);

        // Create knockout stage
        KnockoutStage stage = knockoutMatchMakingService.createNextKnockoutStage(event.getId());

        // Create matches
        List<Match> matches = knockoutMatchMakingService.createMatchesInKnockoutStage(event.getId());

        // Verify match count (25 players -> 7 byes, 18 players in 9 matches)
        assertEquals(9, matches.size());
    }

    @Test
    void createKnockoutStageAndMatches_LargeEvent() {
        // Setup 100 players
        int[] elos = new int[100];
        for (int i = 0; i < 100; i++) {
            elos[i] = 1000 + i;
        }
        List<Player> players = setUpWithPlayersInEvent(elos);

        // Create knockout stage
        KnockoutStage stage = knockoutMatchMakingService.createNextKnockoutStage(event.getId());

        // Create matches
        List<Match> matches = knockoutMatchMakingService.createMatchesInKnockoutStage(event.getId());

        // 100 players -> 80 advance -> 16 matches (32 players in matches, 48 byes)
        assertEquals(16, matches.size());
    }

    @Test
    void createMultipleKnockoutStages() {
        // Setup 32 players
        int[] elos = new int[32];
        for (int i = 0; i < 32; i++) {
            elos[i] = 1000 + i;
        }
        List<Player> players = setUpWithPlayersInEvent(elos);

        // First round: 32 players -> 16 matches
        KnockoutStage firstStage = knockoutMatchMakingService.createNextKnockoutStage(event.getId());
        event.getKnockoutStages().add(firstStage); // Add stage to event
        eventRepository.save(event); // Save the updated event

        List<Match> firstRoundMatches = knockoutMatchMakingService.createMatchesInKnockoutStage(event.getId());
        assertEquals(16, firstRoundMatches.size());

        // Set winners for first round
        firstRoundMatches.forEach(match -> {
            match.setPlayer1Score(15);
            match.setPlayer2Score(10);
            match.setFinished(true);
        });
        matchRepository.saveAll(firstRoundMatches);
        firstStage.setMatches(firstRoundMatches); // Link matches to stage
        knockoutStageRepository.save(firstStage); // Save updated stage

        // Second round
        KnockoutStage secondStage = knockoutMatchMakingService.createNextKnockoutStage(event.getId());
        event.getKnockoutStages().add(secondStage); // Add second stage to event
        eventRepository.save(event); // Save the updated event

        List<Match> secondRoundMatches = knockoutMatchMakingService.createMatchesInKnockoutStage(event.getId());
        assertEquals(8, secondRoundMatches.size());
    }

    @Test
    void throwsExceptionWhenNoStageExists() {
        // Setup players but don't create stage
        int[] elos = new int[8];
        for (int i = 0; i < 8; i++) {
            elos[i] = 1000 + i;
        }
        List<Player> players = setUpWithPlayersInEvent(elos);

        // Clear any existing knockout stages
        event.setKnockoutStages(new ArrayList<>());
        eventRepository.save(event);

        assertThrows(IllegalArgumentException.class,
                () -> knockoutMatchMakingService.createMatchesInKnockoutStage(event.getId()));
    }
}
