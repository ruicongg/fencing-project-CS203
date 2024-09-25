package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankComparator implements Comparator<PlayerRank> {
    public int compare(PlayerRank p1, PlayerRank p2) {
        // First compare by higher win count
        if (p1.winCount != p2.winCount) {
            return Integer.compare(p2.winCount, p1.winCount); // Descending order of wins
        }

        // If win count is the same, compare by lower loss count
        if (p1.lossCount != p2.lossCount) {
            return Integer.compare(p1.lossCount, p2.lossCount); // Ascending order of losses
        }

        // If both win and loss counts are the same, compare by higher score
        return Integer.compare(p2.score, p1.score); // Descending order of score
    }
}
