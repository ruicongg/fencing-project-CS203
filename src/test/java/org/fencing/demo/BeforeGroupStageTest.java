package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.HashSet;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankEloComparator;
import org.fencing.demo.matchMaking.BeforeGroupStage;
import org.fencing.demo.player.Player;
import org.junit.jupiter.api.Test;

public class BeforeGroupStageTest {

    @Test
    public void testSortByELO_NullInput() {
        // Test null rankings input
        TreeMap<Integer, Set<PlayerRank>> result = BeforeGroupStage.sortByELO(null);
        assertNull(result, "Should return null when input is null");
    }

    @Test
    public void testSortByELO_EmptySet() {
        // Test empty rankings set
        Set<PlayerRank> emptySet = new HashSet<>();
        TreeMap<Integer, Set<PlayerRank>> result = BeforeGroupStage.sortByELO(emptySet);
        assertNull(result, "Should return null when there are no players");
    }

    @Test
    public void testSortByELO_PerfectDivision() {
        // Test with number of players that perfectly divides into groups
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        
        // Create 8 mock players with different ELOs
        Player p1 = new Player("Player 1", "abcdef", "One@gmail.com");
        p1.setElo(1200);
        PlayerRank pr1 = new PlayerRank();
        pr1.setPlayer(p1);

        //create player 2
        Player p2 = new Player("Player 2", "12345", "Two@gmail.com");
        p2.setElo(1300);
        PlayerRank pr2 = new PlayerRank();
        pr2.setPlayer(p2);

        //create player 3
        Player p3 = new Player("Player 3", "smu", "Three@gmail.com");
        p3.setElo(1250);
        PlayerRank pr3 = new PlayerRank();
        pr3.setPlayer(p3);

        //create player 4
        Player p4 = new Player("Player 4", "CS203", "Four@gmail.com");
        p4.setElo(1350);
        PlayerRank pr4 = new PlayerRank();
        pr4.setPlayer(p4);
        
        //create player 5
        Player p5 = new Player("Player 5", "abcd", "Five@gmail.com");
        p5.setElo(1400);
        PlayerRank pr5 = new PlayerRank();
        pr5.setPlayer(p5);

        //create player 6
        Player p6 = new Player("Player 6", "abcd", "Six@gmail.com");
        p6.setElo(1500);
        PlayerRank pr6 = new PlayerRank();
        pr6.setPlayer(p6);

        //create player 7
        Player p7 = new Player("Player 7", "abcd", "Seven@gmail.com");
        p7.setElo(1800);
        PlayerRank pr7 = new PlayerRank();
        pr7.setPlayer(p7);

        //create player 8
        Player p8 = new Player("Player 8", "abcd", "Eight@gmail.com");
        p8.setElo(1600);
        PlayerRank pr8 = new PlayerRank();
        pr8.setPlayer(p8);

        rankings.add(pr1);
        rankings.add(pr2);
        rankings.add(pr3);
        rankings.add(pr4);
        rankings.add(pr5);
        rankings.add(pr6);
        rankings.add(pr7);
        rankings.add(pr8);

        TreeMap<Integer, Set<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);
        
        //Test the number of groups (should be 2 groups, each with 4 players)
        assertEquals(2, result.size(), "There should be 2 groups for 8 players");

        // Check if the groups have the correct number of players
        // for (Integer group : result.keySet()) {
        //     assertEquals(4, result.get(group).size(), "Each group should have 4 players");
        // }
    }

    @Test
    public void testSortByELO_WithRemainder() {
        // Test with a number of players that does not perfectly divide into groups
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        // Create 8 mock players with different ELOs
        Player p1 = new Player("Player 1", "abcdef", "One@gmail.com");
        p1.setElo(1200);
        PlayerRank pr1 = new PlayerRank();
        pr1.setPlayer(p1);

        //create player 2
        Player p2 = new Player("Player 2", "12345", "Two@gmail.com");
        p2.setElo(1200);
        PlayerRank pr2 = new PlayerRank();
        pr2.setPlayer(p2);

        //create player 3
        Player p3 = new Player("Player 3", "smu", "Three@gmail.com");
        p3.setElo(1250);
        PlayerRank pr3 = new PlayerRank();
        pr3.setPlayer(p3);

        //create player 4
        Player p4 = new Player("Player 4", "CS203", "Four@gmail.com");
        p4.setElo(1350);
        PlayerRank pr4 = new PlayerRank();
        pr4.setPlayer(p4);
        
        //create player 5
        Player p5 = new Player("Player 5", "abcd", "Five@gmail.com");
        p5.setElo(1400);
        PlayerRank pr5 = new PlayerRank();
        pr5.setPlayer(p5);

        //create player 6
        Player p6 = new Player("Player 6", "abcd", "Six@gmail.com");
        p6.setElo(1500);
        PlayerRank pr6 = new PlayerRank();
        pr6.setPlayer(p6);

        //create player 7
        Player p7 = new Player("Player 7", "abcd", "Seven@gmail.com");
        p7.setElo(1800);
        PlayerRank pr7 = new PlayerRank();
        pr7.setPlayer(p7);

        //create player 8
        Player p8 = new Player("Player 8", "abcd", "Eight@gmail.com");
        p8.setElo(1600);
        PlayerRank pr8 = new PlayerRank();
        pr8.setPlayer(p8);

        //create player 9
        Player p9 = new Player("Player 9", "abcd", "Nine@gmail.com");
        p9.setElo(2000);
        PlayerRank pr9 = new PlayerRank();
        pr9.setPlayer(p9);

        rankings.add(pr1);
        rankings.add(pr2);
        rankings.add(pr3);
        rankings.add(pr4);
        rankings.add(pr5);
        rankings.add(pr6);
        rankings.add(pr7);
        rankings.add(pr8);
        rankings.add(pr9);

        TreeMap<Integer, Set<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);

        // Test the number of groups (should be 2 groups of 5 players each)
        assertEquals(2, result.size(), "There should be 2 groups for 9 players");

        // Check if the groups have the correct number of players
        // for (Integer group : result.keySet()) {
        //     assertTrue(result.get(group).size() >= 4 && result.get(group).size() <= 5, 
        //                "Groups should be balanced, 4-5 players per group");
        // }
    }

    // @Test
    // public void testSortByELO_SortedByELO() {
    //     // Test if players are sorted by their ELO properly
    //     Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

    //     // // Create 4 mock players with different ELOs
    //     // rankings.add(new PlayerRank(new Player("Player 1"), 1200));
    //     // rankings.add(new PlayerRank(new Player("Player 2"), 1300));
    //     // rankings.add(new PlayerRank(new Player("Player 3"), 1250));
    //     // rankings.add(new PlayerRank(new Player("Player 4"), 1100));

    //     TreeMap<Integer, Set<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);
        
    //     // Verify that players are assigned to the correct group and sorted by ELO
    //     int[] expectedELOs = {1300, 1250, 1200, 1100};
    //     int index = 0;
    //     for (Set<PlayerRank> group : result.values()) {
    //         for (PlayerRank playerRank : group) {
    //             assertEquals(expectedELOs[index], playerRank.getPlayer().getElo(), "Players should be sorted by ELO");
    //             index++;
    //         }
    //     }
    // }
}