package org.fencing.demo.matchmaking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Stream;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.knockoutmatchmaking.KnockoutMatchMakingService;
import org.fencing.demo.knockoutmatchmaking.KnockoutMatchMakingServiceImpl;
import org.fencing.demo.knockoutmatchmaking.KnockoutStageGenerator;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KnockoutMatchMakingServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KnockoutStageRepository knockoutStageRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private KnockoutStageGenerator knockoutStageGenerator;

    private KnockoutMatchMakingService knockoutMatchMakingService;

    @BeforeEach
    void setUp() {
        knockoutMatchMakingService = new KnockoutMatchMakingServiceImpl(
                eventRepository,
                knockoutStageRepository,
                matchRepository,
                knockoutStageGenerator);
    }

    @Test
    void createMatchesInKnockoutStage_SmallEvent_AllPlayersAdvance() {
        // Setup 25 players - all should advance, 7 byes to reach 32
        Long eventId = 1L;
        Event event = createValidEvent();
        Set<PlayerRank> players = createPlayers(event, 25);
        event.getRankings().addAll(players);

        // Create initial knockout stage
        KnockoutStage knockoutStage = createValidKnockoutStage(event);
        event.getKnockoutStages().add(knockoutStage);

        List<Match> expectedMatches = new ArrayList<>();
        // Should create 9 matches (25-7 byes = 18 players, so 9 matches)
        for (int i = 0; i < 9; i++) {
            expectedMatches.add(new Match());
        }

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(knockoutStageRepository.save(any())).thenReturn(new KnockoutStage());
        when(knockoutStageGenerator.generateInitialKnockoutMatches(any(), any())).thenReturn(expectedMatches);
        when(matchRepository.saveAll(anyList())).thenReturn(expectedMatches);

        List<Match> result = knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);

        assertEquals(9, result.size());
        verify(knockoutStageGenerator).generateInitialKnockoutMatches(any(), eq(event));
    }

    @Test
    void createMatchesInKnockoutStage_MediumEvent_WithCut() {
        // Setup 45 players
        Long eventId = 1L;
        Event event = createValidEvent();
        Set<PlayerRank> players = createPlayers(event, 45);
        event.getRankings().addAll(players);
        
        // Create initial knockout stage
        KnockoutStage knockoutStage = createValidKnockoutStage(event);
        event.getKnockoutStages().add(knockoutStage);

        List<Match> expectedMatches = new ArrayList<>();
        // 45 players -> 36 after 20% cut
        // Need to reach 32, so 4 matches needed (8 players)
        // 28 byes + 4 matches = 32 players
        for (int i = 0; i < 4; i++) {
            expectedMatches.add(new Match());
        }

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(knockoutStageGenerator.generateInitialKnockoutMatches(any(), any())).thenReturn(expectedMatches);
        when(matchRepository.saveAll(anyList())).thenReturn(expectedMatches);

        List<Match> result = knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);

        assertEquals(4, result.size());
        verify(knockoutStageGenerator).generateInitialKnockoutMatches(any(), eq(event));
    }

    @Test
    void createMatchesInKnockoutStage_LargeEvent_WithCut() {
        // Setup 240 players
        Long eventId = 1L;
        Event event = createValidEvent();
        Set<PlayerRank> players = createPlayers(event, 240);
        event.getRankings().addAll(players);
        
        // Create initial knockout stage
        KnockoutStage knockoutStage = createValidKnockoutStage(event);
        event.getKnockoutStages().add(knockoutStage);

        List<Match> expectedMatches = new ArrayList<>();
        // 240 -> 192 after 20% cut
        // Need to reach 128, so 64 matches needed
        // 64 byes + 64 matches = 128 players next round
        for (int i = 0; i < 64; i++) {
            expectedMatches.add(new Match());
        }

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(knockoutStageGenerator.generateInitialKnockoutMatches(any(), any())).thenReturn(expectedMatches);
        when(matchRepository.saveAll(anyList())).thenReturn(expectedMatches);

        List<Match> result = knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);

        assertEquals(64, result.size());
        verify(knockoutStageGenerator).generateInitialKnockoutMatches(any(), eq(event));
    }

    @Test
    void createMatchesInKnockoutStage_NonExistentEvent_ThrowsEventNotFoundException() {
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);
        });

        verify(matchRepository, never()).saveAll(anyList());
    }

    @Test
    void createMatchesInKnockoutStage_NoKnockoutStages_ThrowsIllegalArgumentException() {
        // Setup
        Long eventId = 1L;
        Event event = new Event();

        // Test both null and empty list cases
        Stream.of(null, new ArrayList<KnockoutStage>()).forEach(stages -> {
            event.setKnockoutStages(stages);
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            // Execute & Verify
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);
            });

            assertEquals("No KnockoutStage found for event " + eventId, exception.getMessage());
            verify(matchRepository, never()).saveAll(anyList());
        });
    }

    private Set<PlayerRank> createPlayers(Event event, int count) {
        Set<PlayerRank> players = new TreeSet<>();
        for (int i = 1; i <= count; i++) {
            Player player = new Player();
            player.setId((long) i);
            player.setUsername("Player " + i);

            PlayerRank playerRank = new PlayerRank();
            playerRank.setPlayer(player);
            playerRank.setEvent(event);
            playerRank.setScore(1000 + i);

            players.add(playerRank);
        }
        return players;
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setKnockoutStages(new ArrayList<>());
        event.setRankings(new TreeSet<>());
        return event;
    }

    private KnockoutStage createValidKnockoutStage(Event event) {
        KnockoutStage knockoutStage = new KnockoutStage();
        knockoutStage.setEvent(event);
        knockoutStage.setMatches(new ArrayList<>());
        return knockoutStage;
    }
}
