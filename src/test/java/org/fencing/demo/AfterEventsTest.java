package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.AfterEvent.AfterEvent;
import org.fencing.demo.AfterEvent.AfterEventRepository;
import org.fencing.demo.AfterEvent.AfterEventsService;
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

    @InjectMocks
    private AfterEvent afterEvents;

   @Mock
    private PlayerRepository playerRepository;

    @Mock
    private AfterEventRepository afterEventsRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private AfterEventsService afterEventsService;

    @Mock
    private MatchRepository matchRepository;

    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event();
        // Initialize your Event object as needed for the tests
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

        // Call the method
        afterEventsService.updatePlayerEloAfterEvent(event);

        // Verify the player's Elo was updated
        verify(playerRepository, times(1)).save(player);
        assertEquals(2400, player.getElo());
    }


    @Test
    public void updatePlayerEloAfterEvent_NotAllGrpMatchesComplete_ThrowsException() {
        // Set up the event with incomplete matches
        event.setGroupStages(Collections.singletonList(createIncompleteGroupStage()));
        event.setKnockoutStages(Collections.singletonList(createCompleteKnockoutStage()));

        assertThrows(RuntimeException.class, () -> {
            afterEventsService.updatePlayerEloAfterEvent(event);
        });
    }

    @Test
    public void updatePlayerEloAfterEvent_NotAllKnockoutMatchesComplete_ThrowsException() {
        // Set up the event with incomplete matches
        event.setGroupStages(Collections.singletonList(createIncompleteGroupStage()));
        event.setKnockoutStages(Collections.singletonList(createCompleteKnockoutStage()));

        assertThrows(RuntimeException.class, () -> {
            afterEventsService.updatePlayerEloAfterEvent(event);
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

    private KnockoutStage createInCompleteKnockoutStage() {
        KnockoutStage knockoutStage = new KnockoutStage();
        List<Match> matches = new ArrayList<>();
        Match match = createIncompleteMatch();
        matches.add(match);
        knockoutStage.setMatches(matches);
        return knockoutStage;
    }

    @Test
    public void getAfterEventByEventId_ValidId_ReturnsAfterEvent() {
        Long eventId = 1L;
        AfterEvent afterEvent = new AfterEvent();
        
        when(afterEventsRepository.findById(eventId)).thenReturn(Optional.of(afterEvent));

        AfterEvent result = afterEventsService.getAfterEventByEventId(eventId);

        assertEquals(afterEvent, result);
    }

    @Test
    public void getAfterEventByEventId_InvalidId_ThrowsException() {
        Long eventId = 1L;

        when(afterEventsRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            afterEventsService.getAfterEventByEventId(eventId);
        });
    }

    @Test
    public void getTournamentById_ValidId_ReturnsTournament() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        Tournament result = afterEventsService.getTournamentById(tournamentId);

        assertEquals(tournament, result);
    }

    @Test
    public void getTournamentById_InvalidId_ThrowsException() {
        Long tournamentId = 1L;

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            afterEventsService.getTournamentById(tournamentId);
        });
    }

    private Match createCompleteMatch(){
        Match match = new Match();
        match.setPlayer1(new Player());
        match.setPlayer2(new Player());
        match.setPlayer1Score(9);
        match.setPlayer2Score(3);

        return match;
    }

    private Match createIncompleteMatch(){
        Match match = new Match();
        match.setPlayer1(new Player());
        match.setPlayer2(new Player());
        return match;
    }
}
