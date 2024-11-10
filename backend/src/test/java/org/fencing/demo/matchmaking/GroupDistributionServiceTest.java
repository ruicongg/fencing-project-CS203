package org.fencing.demo.matchmaking;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankEloComparator;
import org.fencing.demo.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupDistributionServiceTest {

    @InjectMocks
    private GroupDistributionService groupDistributionService;

    private Event event;
    private Set<PlayerRank> rankings;

    @BeforeEach
    void setUp() {
        // Create Event
        event = new Event();
        event.setId(1L);

    }

    @Test
    void testDistributePlayersIntoGroups_success() {
        rankings = new TreeSet<>(new PlayerRankEloComparator());
        for (int i = 0; i < 8; i++) {
            Player player = new Player();
            player.setElo(1000 + i * 100);
            PlayerRank rank = new PlayerRank();
            rank.setPlayer(player);
            rankings.add(rank);
        }
        
        Map<Integer, List<PlayerRank>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        assertNotNull(groups);
        assertEquals(2, groups.size());
        assertTrue(groups.get(0).size() == 4 && groups.get(1).size() == 4);

        List<PlayerRank> group0 = groups.get(0);
        List<PlayerRank> group1 = groups.get(1);

        // lowest ELO player is in group 0
        assertEquals(1000, group0.get(0).getPlayer().getElo());
        // everything in between
        assertEquals(1100, group1.get(0).getPlayer().getElo());
        assertEquals(1200, group0.get(1).getPlayer().getElo());
        assertEquals(1300, group1.get(1).getPlayer().getElo());
        assertEquals(1400, group0.get(2).getPlayer().getElo());
        assertEquals(1500, group1.get(2).getPlayer().getElo());
        assertEquals(1600, group0.get(3).getPlayer().getElo());

        // highest ELO player is in group 1
        assertEquals(1700, group1.get(3).getPlayer().getElo());
    }
}
