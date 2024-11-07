// package org.fencing.demo;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.util.List;
// import java.util.Set;
// import java.util.TreeMap;
// import java.util.TreeSet;

// import org.fencing.demo.events.Event;
// import org.fencing.demo.events.PlayerRank;
// import org.fencing.demo.events.PlayerRankEloComparator;
// import org.fencing.demo.match.Match;
// import org.fencing.demo.matchMaking.BeforeGroupStage;
// import org.fencing.demo.matchMaking.WithinGroupSort;
// import org.fencing.demo.player.Player;
// import org.junit.jupiter.api.Test;

// public class WithinGroupSortTest {
//     @Test
//     public void test_WithinGroupSort_Nicegrps() {
//         // Test with number of players that perfectly divides into groups
//         Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankEloComparator());

        
//         // Create 8 mock players with different ELOs
//         Player p1 = new Player();
//         p1.setElo(1200);
//         PlayerRank pr1 = new PlayerRank();
//         pr1.setPlayer(p1);
//         pr1.setId(1);

//         //create player 2
//         Player p2 = new Player();
//         p2.setElo(1300);
//         PlayerRank pr2 = new PlayerRank();
//         pr2.setPlayer(p2);
//         pr2.setId(2);

//         //create player 3
//         Player p3 = new Player();
//         p3.setElo(1250);
//         PlayerRank pr3 = new PlayerRank();
//         pr3.setPlayer(p3);
//         pr3.setId(3);

//         //create player 4
//         Player p4 = new Player();
//         p4.setElo(1350);
//         PlayerRank pr4 = new PlayerRank();
//         pr4.setPlayer(p4);
//         pr4.setId(4);
        
//         //create player 5
//         Player p5 = new Player();
//         p5.setElo(1400);
//         PlayerRank pr5 = new PlayerRank();
//         pr5.setPlayer(p5);
//         pr5.setId(5);

//         //create player 6
//         Player p6 = new Player();
//         p6.setElo(1500);
//         PlayerRank pr6 = new PlayerRank();
//         pr6.setPlayer(p6);
//         pr6.setId(6);

//         //create player 7
//         Player p7 = new Player();
//         p7.setElo(1800);
//         PlayerRank pr7 = new PlayerRank();
//         pr7.setPlayer(p7);
//         pr7.setId(7);

//         //create player 8
//         Player p8 = new Player();
//         p8.setElo(1600);
//         PlayerRank pr8 = new PlayerRank();
//         pr8.setPlayer(p8);
//         pr8.setId(8);

//         rankings.add(pr1);
//         rankings.add(pr2);
//         rankings.add(pr3);
//         rankings.add(pr4);
//         rankings.add(pr5);
//         rankings.add(pr6);
//         rankings.add(pr7);
//         rankings.add(pr8);

//         TreeMap<Integer, List<PlayerRank>> result = BeforeGroupStage.sortByELO(rankings);

//         Event currentEvent = new Event();
        
//         TreeMap<Integer, List<Match>> grpMatches = WithinGroupSort.groupMatchMakingAlgorithm(result, currentEvent);
        
//         //Test the number of groups (should be 2 groups, each with 4 players)
//         assertEquals(2, grpMatches.size(), "There should be 2 groups");

//         //Check if the groups have the correct number of players
//         for (Integer group : grpMatches.keySet()) {
//             assertEquals(6, grpMatches.get(group).size(), "Each group should have 6 matches");
//         }

//     }
// }
