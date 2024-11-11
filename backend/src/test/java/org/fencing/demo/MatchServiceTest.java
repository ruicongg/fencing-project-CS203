package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.EventServiceImpl;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.groupstage.GroupStageRepository;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.knockoutstage.KnockoutStageNotFoundException;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchNotFoundException;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.match.MatchServiceImpl;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.playerrank.PlayerRank;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KnockoutStageRepository knockoutStageRepository;

    @Mock
    private GroupStageRepository groupStageRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @InjectMocks
    private MatchServiceImpl matchService;


    @Test
    public void getMatch_ValidId_ReturnsMatch() {
        Event event = createValidEvent();
        Long matchId = 1L;
        Player player1 = createValidPlayer(1);
        Player player2 = createValidPlayer(2);
        Match match = createValidMatch(event, player1, player2);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        Match result = matchService.getMatch(matchId);

        assertNotNull(result);
        assertEquals(matchId, result.getId());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void getMatch_NonExistingId_ThrowsMatchNotFoundException() {
        Long matchId = 1L;

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(MatchNotFoundException.class, () -> {
            matchService.getMatch(matchId);
        });

        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void getMatch_NullMatchId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.getMatch(null);
        });
    }

    @Test
    public void getAllMatchesForKnockoutStage_NullKnockoutStageId_ThrowsIllegalArgumentException() {
        // Test case where knockoutStageId is null
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.getAllMatchesForKnockoutStageByKnockoutStageId(null);
        });
    }

    @Test
    public void getAllMatchesForKnockoutStage_NonExistingKnockoutStage_ThrowsKnockoutStageNotFoundException() {
        Long knockoutStageId = 1L;

        // Mock knockoutStageRepository to return false for existsById
        when(knockoutStageRepository.existsById(knockoutStageId)).thenReturn(false);

        // Verify that KnockoutStageNotFoundException is thrown
        assertThrows(KnockoutStageNotFoundException.class, () -> {
            matchService.getAllMatchesForKnockoutStageByKnockoutStageId(knockoutStageId);
        });

        // Verify that knockoutStageRepository was called to check for existence
        verify(knockoutStageRepository, times(1)).existsById(knockoutStageId);
    }

    @Test
    public void getAllMatchesForKnockoutStage_ValidKnockoutStageId_ReturnsMatches() {
        Long knockoutStageId = 1L;
        Event event = createValidEvent();

        // Create a valid KnockoutStage with matches
        KnockoutStage knockoutStage = createValidKnockoutStage(event);
        List<Match> expectedMatches = knockoutStage.getMatches();

        // Mock knockoutStageRepository behavior
        when(knockoutStageRepository.existsById(knockoutStageId)).thenReturn(true);
        when(knockoutStageRepository.findById(knockoutStageId)).thenReturn(Optional.of(knockoutStage));

        // Call the service method
        List<Match> result = matchService.getAllMatchesForKnockoutStageByKnockoutStageId(knockoutStageId);

        // Verify the result
        assertNotNull(result);
        assertEquals(expectedMatches.size(), result.size());

        // Verify repository interactions
        verify(knockoutStageRepository, times(1)).existsById(knockoutStageId);
        verify(knockoutStageRepository, times(1)).findById(knockoutStageId);
    }

    @Test
    public void getAllMatches_ReturnsMatchesList() {
        Event event = createValidEvent();
        Player player1 = createValidPlayer(1);
        Player player2 = createValidPlayer(2);
        Player player3 = createValidPlayer(3);
        Player player4 = createValidPlayer(4);

        // Arrange: Create a list of matches
        Match match1 = createValidMatch(event, player1, player2); 
        Match match2 = createValidMatch(event, player3, player4);
        List<Match> matches = Arrays.asList(match1, match2);

        // Mock repository response
        when(matchRepository.findAll()).thenReturn(matches);

        // Act
        List<Match> result = matchService.getAllMatches();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(matchRepository, times(1)).findAll();
    }

    @Test
    public void getAllMatches_ReturnsEmptyList_WhenNoMatchesFound() {
        // Arrange: Mock repository to return an empty list
        when(matchRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Match> result = matchService.getAllMatches();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(matchRepository, times(1)).findAll();
    }

    @Test // ERROR IllegalArgument Player is not registered in this event
    public void updateMatch_ValidIds_ReturnsUpdatedMatch() {
        Long eventId = 1L;
        Long matchId = 1L;
        Event event = createValidEvent();

        Player player1 = createValidPlayer(1);
        Player player2 = createValidPlayer(2);
        PlayerRank playerRank1 = mock(PlayerRank.class); // Mock PlayerRank
        PlayerRank playerRank2 = mock(PlayerRank.class); // Mock PlayerRank

        // Mock the getPlayer() method of PlayerRank to return valid players
        when(playerRank1.getPlayer()).thenReturn(player1);
        when(playerRank2.getPlayer()).thenReturn(player2);

        event.getRankings().add(playerRank1);
        event.getRankings().add(playerRank2);

        Match existingMatch = createValidMatch(event, player1, player2);
        Match newMatch = createUpdatedMatch(event, player1, player2);
        
        // Mocking repository behavior
        when(matchRepository.findById(matchId)).thenReturn(Optional.of(existingMatch));
        when(matchRepository.save(any(Match.class))).thenReturn(existingMatch);

        // Correct stubbing: match the actual arguments passed to updateAfterMatch
        doNothing().when(playerRank1).updateAfterMatch(eq(20), eq(18), eq(playerRank2));
        doNothing().when(playerRank2).updateAfterMatch(eq(18), eq(20), eq(playerRank1));
        
        // Perform the actual update
        Match result = matchService.updateMatch(eventId, matchId, newMatch);

        // Assert results
        assertNotNull(result);
        assertEquals(newMatch.getPlayer1(), result.getPlayer1());
        assertEquals(newMatch.getPlayer1Score(), result.getPlayer1Score());

        // Verify repository interactions
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).save(existingMatch);
    }



    @Test
    public void updateMatch_NonExistingMatch_ThrowsMatchNotFoundException() {
        Long eventId = 1L;
        Long matchId = 1L;
        Match newMatch = createUpdatedMatch(createValidEvent(), createValidPlayer(1), createValidPlayer(2));

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(MatchNotFoundException.class, () -> {
            matchService.updateMatch(eventId, matchId, newMatch);
        });

        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void updateMatch_EventMismatch_ThrowsIllegalArgumentException() {
        Long eventId = 1L;
        Long matchId = 1L;

        Event originalEvent = createValidEvent();
        Event differentEvent = createValidEvent();
        differentEvent.setId(2L);

        Player player1 = createValidPlayer(1);
        Player player2 = createValidPlayer(2);

        Match existingMatch = createValidMatch(originalEvent, player1, player2);
        Match newMatch = createUpdatedMatch(differentEvent, player1, player2);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(existingMatch));

        assertThrows(IllegalArgumentException.class, () -> {
            matchService.updateMatch(eventId, matchId, newMatch);
        });

        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void updateMatch_PlayerNotInEvent_ThrowsIllegalArgumentException() {
        Long eventId = 1L;
        Long matchId = 1L;

        Event event = createValidEvent();
        Player player1 = createValidPlayer(1);
        Player player2 = createValidPlayer(2);

        PlayerRank playerRank1 = createPlayerRank(1, player1, event);
        event.getRankings().add(playerRank1);

        Match existingMatch = createValidMatch(event, player1, player2);
        Match newMatch = createUpdatedMatch(event, player1, player2);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(existingMatch));

        assertThrows(IllegalArgumentException.class, () -> {
            matchService.updateMatch(eventId, matchId, newMatch);
        });

        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void deleteMatch_ValidIds_SuccessfullyDeletesMatch() {
        Long eventId = 1L;
        Long matchId = 1L;
        Event event = createValidEvent();
        Match match = createValidMatch(event, createValidPlayer(1), createValidPlayer(2));

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        doNothing().when(matchRepository).delete(match);

        matchService.deleteMatch(eventId, matchId);

        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).delete(match);
    }

    @Test
    public void deleteMatch_NonExistingMatch_ThrowsMatchNotFoundException() {
        Long eventId = 1L;
        Long matchId = 1L;

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(MatchNotFoundException.class, () -> {
            matchService.deleteMatch(eventId, matchId);
        });

        verify(matchRepository, times(1)).findById(matchId);
    }

    // Helper methods to create valid entities
    private Tournament createValidTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Spring Championship");
        return tournament;
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setGroupStages(new ArrayList<>());
        event.setKnockoutStages(new ArrayList<>());
        event.setTournament(createValidTournament());
        return event;
    }

    private Set<PlayerRank> createPlayers(Event event) {
        Set<PlayerRank> players = new TreeSet<>();
        for (int i = 1; i <= 8; i++) {
            Player player = createValidPlayer(i); 
            PlayerRank playerRank = createPlayerRank(i, player, event);
            players.add(playerRank);
        }
        return players;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setId((long) id);
        player.setUsername("Player" + id);
        player.setElo(1700);
        return player;
    }

    private PlayerRank createPlayerRank(int id, Player player, Event event) {
        PlayerRank playerRank = new PlayerRank();
        playerRank.setId((long) id);
        playerRank.setEvent(event);
        playerRank.setPlayer(player);
        return playerRank;
    }

    private Match createValidMatch(Event event, Player player1, Player player2) {
        return Match.builder()
                .id(1L)
                .player1(player1)
                .player2(player2)
                .event(event)
                .player1Score(15)
                .player2Score(10)
                .build();
    }

    private Match createUpdatedMatch(Event event, Player player1, Player player2) {
        return Match.builder()
                .id(1L)
                .player1(player1)
                .player2(player2)
                .event(event)
                .player1Score(20)
                .player2Score(18)
                .build();
    }

    // private GroupStage createValidGroupStage(Event event) {
    //     return GroupStage.builder()
    //         .id(1L)
    //         .event(event)
    //         .players(new HashSet<>()) // assuming PlayerRank set is initialized
    //         .matches(new HashSet<>()) // Initialize an empty set for matches
    //         .allMatchesCompleted(false)
    //         .build();
    // }

    // private KnockoutStage createValidKnockoutStage(Event event) {
    //     return KnockoutStage.builder()
    //         .id(1L)
    //         .event(event)
    //         .matches(new ArrayList<>())  // Initialize an empty list for matches
    //         .build();
    // }

    // @Test
    // public void testAddMatchesForAllGroupStages_success() {
    //     Long eventId = 1L;
    //     Event event = mock(Event.class);
    //     GroupStage groupStage = mock(GroupStage.class);
        
    //     when(eventRepository.existsById(eventId)).thenReturn(true);
    //     when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    //     when(event.getGroupStages()).thenReturn(Arrays.asList(groupStage));
        
    //     List<Match> matches = Arrays.asList(mock(Match.class), mock(Match.class));
    //     when(event.createRoundsForGroupStages()).thenReturn(matches);
    //     when(matchRepository.saveAll(matches)).thenReturn(matches);

    //     List<Match> result = matchService.addMatchesforGroupStages(eventId);
    //     assertNotNull(result);
    //     assertEquals(matches.size(), result.size());
    // }


    // @Test
    // public void testAddMatchesForAllGroupStages_noGroupStages() {
    //     Long eventId = 1L;
    //     Event event = mock(Event.class);

    //     when(eventRepository.existsById(eventId)).thenReturn(true);
    //     when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    //     when(event.getGroupStages()).thenReturn(Collections.emptyList());

    //     assertThrows(IllegalStateException.class, () -> {
    //         matchService.addMatchesforAllGroupStages(eventId);
    //     });
    // }
    private KnockoutStage createValidKnockoutStage(Event event) {
        return KnockoutStage.builder()
            .id(1L)
            .event(event)
            .build();
    }
}
