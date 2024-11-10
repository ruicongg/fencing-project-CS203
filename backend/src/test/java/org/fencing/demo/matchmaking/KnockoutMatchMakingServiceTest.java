package org.fencing.demo.matchmaking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.knockoutmatchmaking.KnockoutMatchMakingService;
import org.fencing.demo.knockoutmatchmaking.KnockoutMatchMakingServiceImpl;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.fencing.demo.knockoutmatchmaking.KnockoutStageGenerator;

@ExtendWith(MockitoExtension.class)
public class KnockoutMatchMakingServiceTest {
    
    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private KnockoutStageRepository knockoutStageRepository;
    
    @Mock
    private MatchRepository matchRepository;

    private KnockoutMatchMakingService knockoutMatchMakingService;

    @Mock
    private KnockoutStageGenerator knockoutStageGenerator;
    
    @BeforeEach
    void setUp() {
        knockoutMatchMakingService = new KnockoutMatchMakingServiceImpl(
            eventRepository, 
            knockoutStageRepository, 
            matchRepository,
            knockoutStageGenerator
        );
    }
    
    @Test
    void createNextKnockoutStage_ValidEvent_ReturnsSavedKnockoutStage() {
        Long eventId = 1L;
        Event event = new Event();
        KnockoutStage knockoutStage = new KnockoutStage();
        knockoutStage.setEvent(event);
        
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(knockoutStageRepository.save(any(KnockoutStage.class))).thenReturn(knockoutStage);
        
        KnockoutStage result = knockoutMatchMakingService.createNextKnockoutStage(eventId);
        
        assertNotNull(result);
        verify(knockoutStageRepository, times(1)).save(any(KnockoutStage.class));
    }
    
    @Test
    void createNextKnockoutStage_NullEventId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            knockoutMatchMakingService.createNextKnockoutStage(null);
        });
    }
    
    @Test
    void createNextKnockoutStage_NonExistentEvent_ThrowsEventNotFoundException() {
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        
        assertThrows(EventNotFoundException.class, () -> {
            knockoutMatchMakingService.createNextKnockoutStage(eventId);
        });
        
        verify(knockoutStageRepository, never()).save(any(KnockoutStage.class));
    }
    
    @Test
    void createMatchesInKnockoutStage_ValidEvent_ReturnsSavedMatches() {
        Long eventId = 1L;
        Event event = createValidEvent();
        KnockoutStage knockoutStage = createValidKnockoutStage(event);
        event.getKnockoutStages().add(knockoutStage);
        Set<PlayerRank> players = createPlayers(event);
        event.getRankings().addAll(players);
        
        List<Match> expectedMatches = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            expectedMatches.add(new Match());
        }
        
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(matchRepository.saveAll(anyList())).thenReturn(expectedMatches);
        
        List<Match> result = knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);
        
        assertNotNull(result);
        assertEquals(expectedMatches.size(), result.size());
        verify(matchRepository).saveAll(anyList());
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
    void createMatchesInKnockoutStage_NoKnockoutStages_ThrowsIllegalStateException() {
        Long eventId = 1L;
        Event event = createValidEvent(); // This creates an event with empty knockout stages
        
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        
        assertThrows(IllegalStateException.class, () -> {
            knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);
        });
        
        verify(matchRepository, never()).saveAll(anyList());
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
    
    private Set<PlayerRank> createPlayers(Event event) {
        Set<PlayerRank> players = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
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
}
