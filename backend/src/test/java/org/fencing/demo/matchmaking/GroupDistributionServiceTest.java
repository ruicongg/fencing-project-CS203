package org.fencing.demo.matchmaking;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.fencing.demo.groupmatchmaking.GroupDistributionService;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupDistributionServiceTest {

    @InjectMocks
    private GroupDistributionService groupDistributionService;

    private TreeSet<PlayerRank> rankings;

    @Test
    void testDistributePlayersIntoGroups_success() {

        rankings = createPlayersWithElos(new int[] { 1700, 1600, 1500, 1400, 1300, 1200, 1100, 1000 });
        Map<Integer, List<Player>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        List<Player> group0 = groups.get(0);
        List<Player> group1 = groups.get(1);
        // highest ELO player is in group 1
        assertEquals(1700, group0.get(0).getElo());

        // everything in between
        assertEquals(1600, group1.get(0).getElo());
        assertEquals(1500, group0.get(1).getElo());
        assertEquals(1400, group1.get(1).getElo());
        assertEquals(1300, group0.get(2).getElo());
        assertEquals(1200, group1.get(2).getElo());
        assertEquals(1100, group0.get(3).getElo());
        // lowest ELO player is in group 0
        assertEquals(1000, group1.get(3).getElo());

    }

    private TreeSet<PlayerRank> createPlayersWithElos(int[] elos) {
        TreeSet<PlayerRank> rankings = new TreeSet<>();
        for (int elo : elos) {
            Player player = new Player();
            player.setId((long) elo);
            player.setElo(elo);
            PlayerRank rank = new PlayerRank();
            rank.setPlayer(player);
            rankings.add(rank);
        }
        return rankings;
    }

    @Test
    void distributePlayersIntoGroups_EmptySet_ThrowsIllegalArgumentException() {
        TreeSet<PlayerRank> emptySet = new TreeSet<>();
        assertThrows(IllegalArgumentException.class,
                () -> groupDistributionService.distributePlayersIntoGroups(emptySet));
    }

    @Test
    void distributePlayersIntoGroups_EightPlayers_TwoEqualGroups() {
        int[] elos = { 1200, 1300, 1250, 1350, 1400, 1500, 1800, 1600 };
        TreeSet<PlayerRank> rankings = createPlayersWithElos(elos);

        Map<Integer, List<Player>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        assertNotNull(groups);
        assertEquals(2, groups.size(), "Should create 2 groups for 8 players");
        assertEquals(4, groups.get(0).size(), "First group should have 4 players");
        assertEquals(4, groups.get(1).size(), "Second group should have 4 players");

    }

    @Test
    void distributePlayersIntoGroups_TenPlayers_TwoEqualGroups() {
        int[] elos = { 1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150 };
        TreeSet<PlayerRank> rankings = createPlayersWithElos(elos);

        Map<Integer, List<Player>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        assertEquals(2, groups.size(), "Should create 2 groups for 10 players");
        assertEquals(5, groups.get(0).size(), "First group should have 5 players");
        assertEquals(5, groups.get(1).size(), "Second group should have 5 players");
    }

    @Test
    void distributePlayersIntoGroups_EighteenPlayers_ThreeEqualGroups() {
        int[] elos = { 1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150,
                1221, 1331, 1441, 1505, 1670, 1234, 1501, 1360 };
        TreeSet<PlayerRank> rankings = createPlayersWithElos(elos);

        Map<Integer, List<Player>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        assertEquals(3, groups.size(), "Should create 3 groups for 18 players");
        for (List<Player> group : groups.values()) {
            assertEquals(6, group.size(), "Each group should have 6 players");
        }
    }

    @Test
    void distributePlayersIntoGroups_FourteenPlayers_TwoEqualGroups() {
        int[] elos = { 1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150,
                1221, 1331, 1441, 1501 };
        TreeSet<PlayerRank> rankings = createPlayersWithElos(elos);

        Map<Integer, List<Player>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        assertEquals(2, groups.size(), "Should create 2 groups for 14 players");
        assertEquals(7, groups.get(0).size(), "First group should have 7 players");
        assertEquals(7, groups.get(1).size(), "Second group should have 7 players");
    }

    @Test
    void distributePlayersIntoGroups_NinePlayers_UnequalGroups() {
        int[] elos = { 1200, 1300, 1250, 1350, 1400, 1500, 1800, 1600, 2000 };
        TreeSet<PlayerRank> rankings = createPlayersWithElos(elos);

        Map<Integer, List<Player>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        assertEquals(2, groups.size(), "Should create 2 groups for 9 players");
        assertEquals(5, groups.get(0).size(), "First group should have 5 players");
        assertEquals(4, groups.get(1).size(), "Second group should have 4 players");
    }

    @Test
    void distributePlayersIntoGroups_SeventeenPlayers_UnevenGroups() {
        int[] elos = new int[17];
        for (int i = 0; i < 17; i++) {
            elos[i] = 1200 + (i * 100);
        }
        TreeSet<PlayerRank> rankings = createPlayersWithElos(elos);

        Map<Integer, List<Player>> groups = groupDistributionService.distributePlayersIntoGroups(rankings);

        assertEquals(4, groups.size(), "Should create 4 groups for 17 players");
        assertEquals(5, groups.get(0).size(), "First group should have 5 players");
        assertTrue(groups.get(1).size() == 4 &&
                groups.get(2).size() == 4 &&
                groups.get(3).size() == 4,
                "Other groups should have 4 players each");
    }

}
