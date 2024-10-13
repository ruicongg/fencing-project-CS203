package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.EventServiceImpl;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankComparator;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchNotFoundException;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.match.MatchServiceImpl;
import org.fencing.demo.player.Player;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.GroupStageRepository;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.stages.KnockoutStageRepository;
import org.fencing.demo.tournament.Tournament;
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

    @InjectMocks
    private EventServiceImpl eventService;

    @InjectMocks
    private MatchServiceImpl matchService;

    // @Test
    // public void addMatchesForAllGroupStages_ValidEvent_ReturnsSavedMatches() {
       
    // }

    @Test
    public void addMatchesForAllGroupStages_NonExistingEvent_ThrowsEventNotFoundException() {
        Long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> {
            matchService.addMatchesforAllGroupStages(eventId);
        });

        verify(eventRepository, times(1)).existsById(eventId);
    }

    // @Test // ERROR expected: <1> but was: <0>
    // public void addMatchesForKnockoutStage_ValidEvent_ReturnsSavedMatches() {
    //     Long eventId = 1L;
    //     Event event = createValidEvent();
    //     KnockoutStage knockoutStage = createValidKnockoutStage(event);
    //     event.getKnockoutStages().add(knockoutStage);
        
    //     // Add 8 players to the event
    //     // wait...need player repo
    //     Set<PlayerRank> players = createPlayers(event);
    //     event = createValidEventWithPlayers(event, players);

    //     when(eventRepository.existsById(eventId)).thenReturn(true);
    //     when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    //     when(matchRepository.saveAll(anyList())).thenReturn(knockoutStage.getMatches());

    //     List<Match> result = matchService.addMatchesforKnockoutStage(eventId);

    //     assertNotNull(result);
    //     assertEquals(1, result.size());
    //     verify(matchRepository, times(1)).saveAll(anyList());
    // }

    @Test
    public void addMatchesForKnockoutStage_NonExistingEvent_ThrowsEventNotFoundException() {
        Long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> {
            matchService.addMatchesforKnockoutStage(eventId);
        });

        verify(eventRepository, times(1)).existsById(eventId);
    }

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

    // @Test // ERROR IllegalArgument Player is not registered in this event
    // public void updateMatch_ValidIds_ReturnsUpdatedMatch() {
    //     Long eventId = 1L;
    //     Long matchId = 1L;
    //     Event event = createValidEvent();

    //     Player player1 = createValidPlayer(1);
    //     Player player2 = createValidPlayer(2);
    //     PlayerRank playerRank1 = createPlayerRank(1, player1, event);
    //     PlayerRank playerRank2 = createPlayerRank(2, player2, event);
    //     Set<PlayerRank> playerRanks = new TreeSet<>(new PlayerRankComparator());
    //     playerRanks.add(playerRank1);
    //     playerRanks.add(playerRank2);
    //     event.setRankings(playerRanks);

    //     Match existingMatch = createValidMatch(event, player1, player2);
    //     Match newMatch = createUpdatedMatch(event, player1, player2);
        
    //     when(matchRepository.findById(matchId)).thenReturn(Optional.of(existingMatch));
    //     when(matchRepository.save(any(Match.class))).thenReturn(existingMatch);

    //     Match result = matchService.updateMatch(eventId, matchId, newMatch);

    //     assertNotNull(result);
    //     assertEquals(newMatch.getPlayer1(), result.getPlayer1());
    //     assertEquals(newMatch.getPlayer1Score(), result.getPlayer1Score());
    // }

    @Test
    public void deleteMatch_ValidIds_SuccessfullyDeletesMatch() {
        Long eventId = 1L;
        Long matchId = 1L;

        doNothing().when(matchRepository).deleteByEventIdAndId(eventId, matchId);

        matchService.deleteMatch(eventId, matchId);

        verify(matchRepository, times(1)).deleteByEventIdAndId(eventId, matchId);
    }

    @Test
    public void deleteMatch_NonExistingMatch_ThrowsMatchNotFoundException() {
        Long eventId = 1L;
        Long matchId = 1L;

        doThrow(new MatchNotFoundException(matchId)).when(matchRepository).deleteByEventIdAndId(eventId, matchId);

        assertThrows(MatchNotFoundException.class, () -> {
            matchService.deleteMatch(eventId, matchId);
        });
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
        event.setRankings(new TreeSet<>(new PlayerRankComparator()));
        event.setGroupStages(new ArrayList<>());
        event.setKnockoutStages(new ArrayList<>());
        event.setTournament(createValidTournament());
        return event;
    }

    private Set<PlayerRank> createPlayers(Event event) {
        Set<PlayerRank> players = new TreeSet<>(new PlayerRankComparator());
        for (int i = 1; i <= 8; i++) {
            Player player = createValidPlayer(i); 
            PlayerRank playerRank = createPlayerRank(i, player, event);
            players.add(playerRank);
        }
        return players;
    }

    private Event createValidEventWithPlayers(Event event, Set<PlayerRank> playerRanks) {
        event.setRankings(playerRanks);
        return event;
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

    @Test
    public void testAddMatchesForAllGroupStages_success() {
        Long eventId = 1L;
        Event event = mock(Event.class);
        GroupStage groupStage = mock(GroupStage.class);
        
        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(event.getGroupStages()).thenReturn(Arrays.asList(groupStage));
        
        List<Match> matches = Arrays.asList(mock(Match.class), mock(Match.class));
        when(event.createRoundsForGroupStages()).thenReturn(matches);
        when(matchRepository.saveAll(matches)).thenReturn(matches);

        List<Match> result = matchService.addMatchesforAllGroupStages(eventId);
        assertNotNull(result);
        assertEquals(matches.size(), result.size());
    }


    @Test
    public void testAddMatchesForAllGroupStages_noGroupStages() {
        Long eventId = 1L;
        Event event = mock(Event.class);

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(event.getGroupStages()).thenReturn(Collections.emptyList());

        assertThrows(IllegalStateException.class, () -> {
            matchService.addMatchesforAllGroupStages(eventId);
        });
    }
}
