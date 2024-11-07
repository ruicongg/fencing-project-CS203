package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankEloComparator implements Comparator<PlayerRank>{
    
    @Override
    public int compare(PlayerRank p1, PlayerRank p2) {
        int eloComparison = Integer.compare(p2.getPlayer().getElo(), p1.getPlayer().getElo()); // Sort by Elo in descending order
        if (eloComparison == 0) {
            return Long.compare(p1.getPlayer().getId(), p2.getPlayer().getId()); // Sort by ID in ascending order if Elo is the same
        }
        return eloComparison;
    }
}
