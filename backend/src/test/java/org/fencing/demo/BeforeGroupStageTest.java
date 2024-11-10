package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankEloComparator;
import org.fencing.demo.matchmaking.BeforeGroupStage;
import org.fencing.demo.player.Player;
import org.junit.jupiter.api.Test;

public class BeforeGroupStageTest {

    @Test
    public void testSortByELO_NullInput() {
        // Test null rankings input
        TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(null);
        assertNull(result, "Should return null when input is null");
    }

    @Test
    public void testSortByELO_EmptySet() {
        // Test empty rankings set
        Set<PlayerRank> emptySet = new HashSet<>();
        TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(emptySet);
        assertNull(result, "Should return null when there are no players");
    }

    @Test
    public void testSortByELO_Perfect4() {
        // Test with number of players that perfectly divides into groups
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        
        // Create 8 mock players with different ELOs
        Player p1 = new Player();
        p1.setElo(1200);
        PlayerRank pr1 = new PlayerRank();
        pr1.setPlayer(p1);
        // pr1.setId(1);

        //create player 2
        Player p2 = new Player();
        p2.setElo(1300);
        PlayerRank pr2 = new PlayerRank();
        pr2.setPlayer(p2);
        // pr2.setId(2);

        //create player 3
        Player p3 = new Player();
        p3.setElo(1250);
        PlayerRank pr3 = new PlayerRank();
        pr3.setPlayer(p3);
        // pr3.setId(3);

        //create player 4
        Player p4 = new Player();
        p4.setElo(1350);
        PlayerRank pr4 = new PlayerRank();
        pr4.setPlayer(p4);
        // pr4.setId(4);
        
        //create player 5
        Player p5 = new Player();
        p5.setElo(1400);
        PlayerRank pr5 = new PlayerRank();
        pr5.setPlayer(p5);
        // pr5.setId(5);

        //create player 6
        Player p6 = new Player();
        p6.setElo(1500);
        PlayerRank pr6 = new PlayerRank();
        pr6.setPlayer(p6);
        // pr6.setId(6);

        //create player 7
        Player p7 = new Player();
        p7.setElo(1800);
        PlayerRank pr7 = new PlayerRank();
        pr7.setPlayer(p7);
        // pr7.setId(7);

        //create player 8
        Player p8 = new Player();
        p8.setElo(1600);
        PlayerRank pr8 = new PlayerRank();
        pr8.setPlayer(p8);
        // pr8.setId(8);

        rankings.add(pr1);
        rankings.add(pr2);
        rankings.add(pr3);
        rankings.add(pr4);
        rankings.add(pr5);
        rankings.add(pr6);
        rankings.add(pr7);
        rankings.add(pr8);

        TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);
        
        //Test the number of groups (should be 2 groups, each with 4 players)
        assertEquals(2, result.size(), "There should be 2 groups for 8 players");

        //Check if the groups have the correct number of players
        for (Integer group : result.keySet()) {
            assertEquals(4, result.get(group).size(), "Each group should have 4 players");
        }
    }


    @Test
    public void testSortByELO_Perfect5(){
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        int[] elos = {1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150};

        for(int i = 0; i < 10; i++){
            Player p = new Player();
            p.setElo(elos[i]);
            PlayerRank pr = new PlayerRank();
            pr.setPlayer(p);
            rankings.add(pr);
        }

        TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);
        
        //Test the number of groups (should be 2 groups, each with 4 players)
        assertEquals(2, result.size(), "There should be 2 groups for 8 players");

        //Check if the groups have the correct number of players
        for (Integer group : result.keySet()) {
            assertEquals(5, result.get(group).size(), "Each group should have 4 players");
        }

    }

    @Test
    public void testSortByELO_Perfect6(){
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        int[] elos = {1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150,
        1221, 1331, 1441, 1505, 1670, 1234, 1501, 1360};

        for(int i = 0; i < 18; i++){
            Player p = new Player();
            p.setElo(elos[i]);
            PlayerRank pr = new PlayerRank();
            pr.setPlayer(p);
            rankings.add(pr);
        }

        TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);
        
        //Test the number of groups (should be 2 groups, each with 4 players)
        assertEquals(3, result.size(), "There should be 2 groups for 8 players");

        //Check if the groups have the correct number of players
        for (Integer group : result.keySet()) {
            assertEquals(6, result.get(group).size(), "Each group should have 4 players");
        }

    }


    @Test
    public void testSortByELO_Perfect7(){
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        int[] elos = {1200, 1400, 1900, 2000, 1500, 1300, 1600, 1700, 1000, 1150,
        1221, 1331, 1441, 1501};

        for(int i = 0; i < 14; i++){
            Player p = new Player();
            p.setElo(elos[i]);
            PlayerRank pr = new PlayerRank();
            pr.setPlayer(p);
            rankings.add(pr);
        }

        TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);
        
        //Test the number of groups (should be 2 groups, each with 4 players)
        assertEquals(2, result.size(), "There should be 2 groups for 8 players");

        //Check if the groups have the correct number of players
        for (Integer group : result.keySet()) {
            assertEquals(7, result.get(group).size(), "Each group should have 4 players");
        }

    }


    @Test
    public void testSortByELO_WithRemainder() {
        // Test with a number of players that does not perfectly divide into groups
        Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        // Create 9 mock players with different ELOs
        Player p1 = new Player();
        p1.setElo(1200);
        p1.setEmail("player1@gmail.com");
        PlayerRank pr1 = new PlayerRank();
        pr1.setPlayer(p1);
        pr1.setId(1);

        //create player 2
        Player p2 = new Player();
        p2.setElo(1300);
        PlayerRank pr2 = new PlayerRank();
        pr2.setPlayer(p2);
        pr2.setId(2);

        //create player 3
        Player p3 = new Player();
        p3.setElo(1250);
        PlayerRank pr3 = new PlayerRank();
        pr3.setPlayer(p3);
        pr3.setId(3);

        //create player 4
        Player p4 = new Player();
        p4.setElo(1350);
        PlayerRank pr4 = new PlayerRank();
        pr4.setPlayer(p4);
        pr4.setId(4);
        
        //create player 5
        Player p5 = new Player();
        p5.setElo(1400);
        PlayerRank pr5 = new PlayerRank();
        pr5.setPlayer(p5);
        pr5.setId(5);

        //create player 6
        Player p6 = new Player();
        p6.setElo(1500);
        PlayerRank pr6 = new PlayerRank();
        pr6.setPlayer(p6);
        pr6.setId(6);

        //create player 7
        Player p7 = new Player();
        p7.setElo(1800);
        PlayerRank pr7 = new PlayerRank();
        pr7.setPlayer(p7);
        pr7.setId(7);

        //create player 8
        Player p8 = new Player();
        p8.setElo(1600);
        PlayerRank pr8 = new PlayerRank();
        pr8.setPlayer(p8);
        pr8.setId(8);

        //create player 9
        Player p9 = new Player();
        p9.setElo(2000);
        PlayerRank pr9 = new PlayerRank();
        pr9.setPlayer(p9);
        pr9.setId(9);

        rankings.add(pr1);
        rankings.add(pr2);
        rankings.add(pr3);
        rankings.add(pr4);
        rankings.add(pr5);
        rankings.add(pr6);
        rankings.add(pr7);
        rankings.add(pr8);
        rankings.add(pr9);

        TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);

        // Test the number of groups (should be 2 groups of 5 players each)
        assertEquals(2, result.size(), "There should be 2 groups for 9 players");

        //Check if the groups have the correct number of players
        // for (Integer group : result.keySet()) {
        //     assertTrue(result.get(group).size() >= 4 && result.get(group).size() <= 5, 
        //                "Groups should be balanced, 4-5 players per group");
        // }
                assertTrue(result.get(1).size() == 5 && result.get(2).size() == 4, 
                           "Groups should be balanced, 4-5 players per group");
    }


}
