package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.fencing.demo.events.*;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournament.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AfterEventsTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event();
        event.setId(1L); // Set an ID for the event to use in tests
    }

    @Test
    public void updatePlayerEloAfterEvent_AllMatchesComplete_UpdatesPlayerElo() {
        // Set up the event with complete matches and rankings
        Set<PlayerRank> rankings = new HashSet<>();
        Player player = new Player();
        player.setElo(2300);
        PlayerRank playerRank = new PlayerRank();
        playerRank.setPlayer(player);
        playerRank.setTempElo(2400);
        rankings.add(playerRank);

        event.setRankings(rankings);
        event.setGroupStages(Collections.singletonList(createCompleteGroupStage()));
        event.setKnockoutStages(Collections.singletonList(createCompleteKnockoutStage()));

        // Mock repository behavior
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        // Call the method
        eventService.updatePlayerEloAfterEvent(event.getId());

        // Verify the player's Elo was updated
        verify(playerRepository, times(1)).save(player);
        assertEquals(2400, player.getElo());
    }

    @Test
    public void updatePlayerEloAfterEvent_NotAllMatchesComplete_ThrowsException() {
        // Set up the event with incomplete matches
        event.setGroupStages(Collections.singletonList(createIncompleteGroupStage()));
        event.setKnockoutStages(Collections.singletonList(createCompleteKnockoutStage()));

        // Mock repository behavior
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        // Assert that an exception is thrown
        assertThrows(RuntimeException.class, () -> {
            eventService.updatePlayerEloAfterEvent(event.getId());
        });
    }

    @Test
    public void updatePlayerEloAfterEvent_EventNotFound_ThrowsException() {
        // Mock repository behavior to return empty for the event
        when(eventRepository.findById(event.getId())).thenReturn(Optional.empty());

        // Assert that an exception is thrown
        assertThrows(EventNotFoundException.class, () -> {
            eventService.updatePlayerEloAfterEvent(event.getId());
        });
    }

    // Helper methods to create stages for tests
    private GroupStage createCompleteGroupStage() {
        GroupStage groupStage = new GroupStage();
        List<Match> matches = new ArrayList<>();
        Match match = createCompleteMatch();
        matches.add(match);
        groupStage.setMatches(matches);
        return groupStage;
    }

    private GroupStage createIncompleteGroupStage() {
        GroupStage groupStage = new GroupStage();
        List<Match> matches = new ArrayList<>();
        Match match = createIncompleteMatch();
        matches.add(match);
        groupStage.setMatches(matches);
        return groupStage;
    }

    private KnockoutStage createCompleteKnockoutStage() {
        KnockoutStage knockoutStage = new KnockoutStage();
        List<Match> matches = new ArrayList<>();
        Match match = createCompleteMatch();
        matches.add(match);
        knockoutStage.setMatches(matches);
        return knockoutStage;
    }

    private Match createCompleteMatch() {
        Match match = new Match();
        match.setPlayer1(new Player());
        match.setPlayer2(new Player());
        match.setPlayer1Score(9);
        match.setPlayer2Score(3);
        return match;
    }

    private Match createIncompleteMatch() {
        Match match = new Match();
        match.setPlayer1(new Player());
        match.setPlayer2(new Player());
        return match;
    }
}
