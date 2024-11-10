package org.fencing.demo.groupmatchmaking;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fencing.demo.events.PlayerRank;

//ELO sort
public class BeforeGroupStage {
    
    // return the grp number with the set of PlayerRanks
    public static TreeMap<Integer, List<PlayerRank>> sortByELO(Set<PlayerRank> rankings){
        //Check if event and rankings exist
        if (rankings == null) {
            return null;
        }
        
        //stores the group number and the playerRanks inside
        TreeMap<Integer, List<PlayerRank>> resultMatches = new TreeMap<>();

        //sort players according to Elo
        TreeSet<PlayerRank> players = new TreeSet<>();


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

        int grpSize = 4;

        // Find the best group size with the smallest remainder
        for (Integer factor : factorRemainder.keySet()) {
            //if it is perfect division then break the loop
            if (factorRemainder.get(factor) == 0) {
                grpSize = factor;
                break;
            }
            // Otherwise, pick the factor with the smallest remainder
            if (factorRemainder.get(factor) < factorRemainder.get(grpSize) && grpSize != 7) {
                grpSize = factor;
            }
        }

        int numGroups = playerNum / grpSize;
        

        // Initialize the groups
        for (int i = 1; i <= numGroups; i++) {
            resultMatches.put(i, new ArrayList<PlayerRank>());
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