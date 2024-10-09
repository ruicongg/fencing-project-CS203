package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankComparator implements Comparator<PlayerRank> {
    public int compare(PlayerRank p1, PlayerRank p2) {
        // First compare by higher win count
        if (p1.getWinCount() != p2.getWinCount()) {
            return Integer.compare(p2.getWinCount(), p1.getWinCount()); // Descending order of wins
        }

        // If win count is the same, compare by lower loss count
        if (p1.getLossCount() != p2.getLossCount()) {
            return Integer.compare(p1.getLossCount(), p2.getLossCount()); // Ascending order of losses
        }

        // If both win and loss counts are the same, compare by higher score
        return Integer.compare(p2.getScore(), p1.getScore()); // Descending order of score
    }
}
