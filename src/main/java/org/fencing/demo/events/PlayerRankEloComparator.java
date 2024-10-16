package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankEloComparator implements Comparator<PlayerRank>{
    
    public int compare(PlayerRank p1, PlayerRank p2) {
        // First, compare based on Elo
        int eloComparison = Integer.compare(p1.getPlayer().getElo(), p2.getPlayer().getElo());
        
        //If the Elo ratings are equal, compare based on Player ID
        if (eloComparison == 0 && p1.getPlayer().getUsername() != null && p2.getPlayer().getUsername() != null) {
            return p1.getPlayer().getUsername().compareTo(p2.getPlayer().getUsername());
        }
        
        // Return the Elo comparison result if they are not equal
        return eloComparison;
    }
}
