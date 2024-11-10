package org.fencing.demo.matchmaking;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankEloComparator;
import org.fencing.demo.groupmatchmaking.GroupDistributionService;
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

    private Set<PlayerRank> rankings;

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

    private Set<PlayerRank> createPlayersWithElos(int[] elos) {
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());
        for (int elo : elos) {
            Player player = new Player();
            player.setElo(elo);
            PlayerRank rank = new PlayerRank();
            rank.setPlayer(player);
            rankings.add(rank);
        }
        return rankings;
    }

    @Test
    void distributePlayersIntoGroups_NullInput_ReturnsNull() {
        Map<Integer, List<PlayerRank>> result = groupDistributionService.distributePlayersIntoGroups(null);
        assertNull(result, "Should return null when input is null");
    }

    @Test
    void distributePlayersIntoGroups_EmptySet_ReturnsNull() {
        Set<PlayerRank> emptySet = new HashSet<>();
        Map<Integer, List<PlayerRank>> result = groupDistributionService.distributePlayersIntoGroups(emptySet);
        assertNull(result, "Should return null when there are no players");
    }

    @Test
    void distributePlayersIntoGroups_EightPlayers_TwoEqualGroups() {
        int[] elos = {1200, 1300, 1250, 1350, 1400, 1500, 1800, 1600};
        Set<PlayerRank> rankings = createPlayersWithElos(elos);
        
        Map<Integer, List<PlayerRank>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);
        
        assertNotNull(groups);
        assertEquals(2, groups.size(), "Should create 2 groups for 8 players");
        assertEquals(4, groups.get(0).size(), "First group should have 4 players");
        assertEquals(4, groups.get(1).size(), "Second group should have 4 players");
        
        verifyGroupDistribution(groups);
    }

    @Test
    void distributePlayersIntoGroups_TenPlayers_TwoEqualGroups() {
        int[] elos = {1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150};
        Set<PlayerRank> rankings = createPlayersWithElos(elos);
        
        Map<Integer, List<PlayerRank>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);
        
        assertEquals(2, groups.size(), "Should create 2 groups for 10 players");
        assertEquals(5, groups.get(0).size(), "First group should have 5 players");
        assertEquals(5, groups.get(1).size(), "Second group should have 5 players");
    }

    @Test
    void distributePlayersIntoGroups_EighteenPlayers_ThreeEqualGroups() {
        int[] elos = {1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150,
                      1221, 1331, 1441, 1505, 1670, 1234, 1501, 1360};
        Set<PlayerRank> rankings = createPlayersWithElos(elos);
        
        Map<Integer, List<PlayerRank>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);
        
        assertEquals(3, groups.size(), "Should create 3 groups for 18 players");
        for (List<PlayerRank> group : groups.values()) {
            assertEquals(6, group.size(), "Each group should have 6 players");
        }
    }

    @Test
    void distributePlayersIntoGroups_FourteenPlayers_TwoEqualGroups() {
        int[] elos = {1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150,
                      1221, 1331, 1441, 1501};
        Set<PlayerRank> rankings = createPlayersWithElos(elos);
        
        Map<Integer, List<PlayerRank>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);
        
        assertEquals(2, groups.size(), "Should create 2 groups for 14 players");
        assertEquals(7, groups.get(0).size(), "First group should have 7 players");
        assertEquals(7, groups.get(1).size(), "Second group should have 7 players");
    }

    @Test
    void distributePlayersIntoGroups_NinePlayers_UnequalGroups() {
        int[] elos = {1200, 1300, 1250, 1350, 1400, 1500, 1800, 1600, 2000};
        Set<PlayerRank> rankings = createPlayersWithElos(elos);
        
        Map<Integer, List<PlayerRank>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);
        
        assertEquals(2, groups.size(), "Should create 2 groups for 9 players");
        assertEquals(5, groups.get(0).size(), "First group should have 5 players");
        assertEquals(4, groups.get(1).size(), "Second group should have 4 players");
    }

    private void verifyGroupDistribution(Map<Integer, List<PlayerRank>> groups) {
        List<PlayerRank> group0 = groups.get(0);
        List<PlayerRank> group1 = groups.get(1);
        
        assertEquals(1200, group0.get(0).getPlayer().getElo());
        assertEquals(1300, group1.get(0).getPlayer().getElo());
        assertEquals(1250, group0.get(1).getPlayer().getElo());
        assertEquals(1350, group1.get(1).getPlayer().getElo());
        assertEquals(1400, group0.get(2).getPlayer().getElo());
        assertEquals(1500, group1.get(2).getPlayer().getElo());
        assertEquals(1600, group0.get(3).getPlayer().getElo());
        assertEquals(1800, group1.get(3).getPlayer().getElo());
    }

}
