package org.fencing.demo.matchmaking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.player.Player;
import org.fencing.demo.stages.GroupStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.fencing.demo.match.Match;

@ExtendWith(MockitoExtension.class)
public class GroupMatchGeneratorTest {
    
    @InjectMocks
    private GroupMatchGenerator groupMatchGenerator;

    private Event event;
    private Map<Integer, List<PlayerRank>> groups;

    @BeforeEach
    void setUp() {
        event = new Event();
        groups = new TreeMap<>();
        
        // Create 4 players for one group
        List<PlayerRank> group0 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            player.setId((long) i);
            
            PlayerRank rank = new PlayerRank();
            rank.setPlayer(player);
            group0.add(rank);
        }
        groups.put(0, group0);
         
        // Set up group stages in event
        List<GroupStage> groupStages = new ArrayList<>();
        GroupStage stage = new GroupStage();
        stage.setEvent(event);
        groupStages.add(stage);
        event.setGroupStages(groupStages);
    }

    @Test
    void testGenerateGroupMatches_success() {
        Map<Integer, List<Match>> result = groupMatchGenerator.generateGroupMatches(groups, event);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        List<Match> groupMatches = result.get(0);
        // For 4 players, we should have 6 matches (4C2 = 6)
        assertEquals(6, groupMatches.size());
        
        // Verify each match has correct setup
        for (Match match : groupMatches) {
            assertNotNull(match.getPlayer1());
            assertNotNull(match.getPlayer2());
            assertNotEquals(match.getPlayer1().getId(), match.getPlayer2().getId());
            assertEquals(event, match.getEvent());
        }
    }
}