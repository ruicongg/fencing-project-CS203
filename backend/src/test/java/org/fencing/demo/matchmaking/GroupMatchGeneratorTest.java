package org.fencing.demo.matchmaking;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.groupmatchmaking.GroupMatchGenerator;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
        
        int numberOfGroups = 2;
        int groupSize = 4;
        for (int i = 0; i < numberOfGroups; i++) { 
            List<PlayerRank> group = new ArrayList<>();
            for (int j = 0; j < groupSize; j++) {
                Player player = new Player();
                player.setId((long) (i * groupSize + j));
                PlayerRank rank = new PlayerRank();
                rank.setPlayer(player);
                group.add(rank);
            }
            groups.put(i, group);
        }

        for (int i = 0; i < numberOfGroups; i++) {
            GroupStage stage = new GroupStage();
            stage.setEvent(event);
            event.getGroupStages().add(stage);
        }

    }

    @Test
    void testGenerateGroupMatches_success() {
        Map<Integer, List<Match>> result = groupMatchGenerator.generateGroupMatches(groups, event);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        List<Match> groupMatches = result.get(0);
        // For 4 players, we should have 6 matches (4C2 = 6)
        assertEquals(6, groupMatches.size());
        
        for (Match match : groupMatches) {
            assertNotNull(match.getPlayer1());
            assertNotNull(match.getPlayer2());
            assertNotEquals(match.getPlayer1().getId(), match.getPlayer2().getId());
            assertEquals(event, match.getEvent());
        }
    }
}
