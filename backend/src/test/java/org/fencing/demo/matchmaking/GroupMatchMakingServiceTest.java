package org.fencing.demo.matchmaking; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.groupmatchmaking.GroupDistributionService;
import org.fencing.demo.groupmatchmaking.GroupMatchGenerator;
import org.fencing.demo.groupmatchmaking.GroupMatchMakingService;
import org.fencing.demo.groupmatchmaking.GroupMatchMakingServiceImpl;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.groupstage.GroupStageRepository;
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
public class GroupMatchMakingServiceTest { 
 
    @Mock 
    private GroupStageRepository groupStageRepository; 
 
    @Mock 
    private EventRepository eventRepository; 
 
    @Mock 
    private MatchRepository matchRepository; 
 
    @Mock 
    private GroupDistributionService groupDistributionService; 
 
    @Mock 
    private GroupMatchGenerator groupMatchGenerator; 
 
    private GroupMatchMakingService groupMatchMakingService; 
 
    @BeforeEach 
    void setUp() { 
        groupMatchMakingService = new GroupMatchMakingServiceImpl( 
                groupStageRepository, 
                eventRepository, 
                matchRepository, 
                groupDistributionService, 
                groupMatchGenerator); 
    } 
 
    @Test 
    void createGroupStages_Success() { 
        // Arrange 
        Long eventId = 1L; 
        Event event = new Event(); 
        event.setId(eventId); 
 
        // Create at least 8 player rankings 
        SortedSet<PlayerRank> rankings = new TreeSet<>(); 
        for (int i = 0; i < 8; i++) { 
            Player player = new Player(); 
            player.setId((long) i); 
            PlayerRank rank = new PlayerRank(); 
            rank.setPlayer(player); 
            rank.setTempElo(1000 + i); 
            rankings.add(rank); 
        } 
        event.setRankings(rankings); 
 
        Map<Integer, List<Player>> mockGroups = new HashMap<>(); 
        mockGroups.put(0, Arrays.asList(new Player(), new Player())); 
        mockGroups.put(1, Arrays.asList(new Player(), new Player())); 
 
        List<GroupStage> expectedGroupStages = Arrays.asList( 
                new GroupStage(), 
                new GroupStage()); 
 
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event)); 
        when(groupDistributionService.distributePlayersIntoGroups(rankings)).thenReturn(mockGroups); 
        when(groupStageRepository.saveAll(any())).thenReturn(expectedGroupStages); 
 
        // Act 
        List<GroupStage> result = groupMatchMakingService.createGroupStages(eventId); 
 
        // Assert 
        assertNotNull(result); 
        assertEquals(2, result.size()); 
    } 
 
    @Test 
    void createMatchesInGroupStages_Success() { 
        // Arrange 
        Long eventId = 1L; 
        Event event = new Event(); 
        event.setId(eventId); 
 
        SortedSet<PlayerRank> rankings = new TreeSet<>(); 
        event.setRankings(rankings); 
 
        List<GroupStage> groupStages = Arrays.asList( 
                new GroupStage(), 
                new GroupStage()); 
        event.setGroupStages(groupStages); 
 
        Map<Integer, List<Player>> mockGroups = new HashMap<>(); 
        mockGroups.put(0, Arrays.asList(new Player(), new Player())); 
        mockGroups.put(1, Arrays.asList(new Player(), new Player())); 
 
        Map<Integer, List<Match>> mockGroupMatches = new HashMap<>(); 
        mockGroupMatches.put(0, Arrays.asList(new Match(), new Match())); 
        mockGroupMatches.put(1,Arrays.asList(new Match(), new Match())); 
        List<Match> expectedMatches = Arrays.asList( 
            new Match(), new Match(), new Match(), new Match()); 

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event)); 
    when(groupDistributionService.distributePlayersIntoGroups(rankings)).thenReturn(mockGroups); 
    when(groupMatchGenerator.generateGroupMatches(mockGroups, event)).thenReturn(mockGroupMatches); 
    when(matchRepository.saveAll(any())).thenReturn(expectedMatches); 

    // Act 
    List<Match> result = groupMatchMakingService.createMatchesInGroupStages(eventId); 

    // Assert 
    assertNotNull(result); 
    assertEquals(4, result.size()); 
} 

@Test 
void createGroupStages_ThrowsException_WhenGroupStagesAlreadyExist() { 
    // Arrange 
    Long eventId = 1L; 
    Event event = new Event(); 
    event.setId(eventId); 

    // Add minimum required players 
    SortedSet<PlayerRank> rankings = new TreeSet<>(); 
    for (int i = 0; i < 8; i++) { 
        Player player = new Player(); 
        player.setId((long) i); 
        PlayerRank rank = new PlayerRank(); 
        rank.setPlayer(player); 
        rank.setTempElo(1000 + i); 
        rankings.add(rank); 
    } 
    event.setRankings(rankings); 

    event.setGroupStages(Arrays.asList(new GroupStage())); 

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event)); 

    // Act & Assert 
    assertThrows(IllegalArgumentException.class, () -> { 
        groupMatchMakingService.createGroupStages(eventId); 
    }); 
} 

@Test 
void createMatchesInGroupStages_ThrowsException_WhenNoGroupStagesExist() { 
    // Arrange 
    Long eventId = 1L; 
    Event event = new Event(); 
    event.setId(eventId); 
    event.setGroupStages(null); 

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event)); 

    // Act & Assert 
    assertThrows(IllegalArgumentException.class, () -> { 
        groupMatchMakingService.createMatchesInGroupStages(eventId); 
    }); 
} 

}
