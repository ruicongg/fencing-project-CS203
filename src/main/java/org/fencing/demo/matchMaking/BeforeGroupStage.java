package org.fencing.demo.matchMaking;

import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.HashSet;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankEloComparator;

//ELO sort
public class BeforeGroupStage {
    
    // return the grp number with the set of PlayerRanks
    public static TreeMap<Integer, Set<PlayerRank>> sortByELO(Set<PlayerRank> rankings){
        //Check if event and rankings exist
        if (rankings == null) {
            return null;
        }
    
        TreeMap<Integer, Set<PlayerRank>> resultMatches = new TreeMap<>();

        Set<PlayerRank> players = new TreeSet<>(new PlayerRankEloComparator());

        // Add players from rankings to the TreeSet (automatically sorted)
        for (PlayerRank pr : rankings) {
            players.add(pr);
        }

        int playerNum = players.size();
        if (playerNum == 0) {
            return null; // No players to sort
        }

        // Map to store possible group sizes and remainders
        TreeMap<Integer, Integer> factorRemainder = new TreeMap<>();

        // Find the best group size by checking divisors between 4 and 7
        for (int i = 4; i <= 7; i++) {
            int remainder = playerNum % i;
            factorRemainder.put(i, remainder);
        }

        int bestFactor = 4;
        int grpSize = 0;

        // Find the best group size with the smallest remainder
        for (Integer factor : factorRemainder.keySet()) {
            if (factorRemainder.get(factor) == 0) {
                grpSize = factor;
                break;
            }
            // Otherwise, pick the factor with the smallest remainder
            if (factorRemainder.get(factor) > factorRemainder.get(bestFactor)) {
                bestFactor = factor;
            }
        }

        if (grpSize == 0) {
            grpSize = bestFactor; // Use the best factor if no perfect division
        }

        int numGroups = playerNum / grpSize;
        int remainder = playerNum % grpSize;

        // If there's a remainder, we need one more group
        if (remainder != 0) {
            numGroups++;
        }

        // Initialize the groups
        for (int i = 1; i <= numGroups; i++) {
            resultMatches.put(i, new HashSet<>());
        }

        // Distribute players across the groups in a round-robin fashion
        int currentGrp = 1;
        for (PlayerRank p : players) {
            resultMatches.get(currentGrp).add(p);
            currentGrp++;
            if (currentGrp > numGroups) {
                currentGrp = 1;
            }

        }
        return resultMatches;
    }
}
