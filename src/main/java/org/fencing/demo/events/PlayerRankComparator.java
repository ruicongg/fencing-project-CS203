package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankComparator implements Comparator<PlayerRank> {

    @Override
    public int compare(PlayerRank p1, PlayerRank p2) {

        if (p1.winCount != p2.winCount) {
            return Integer.compare(p2.winCount, p1.winCount);
        }
        
        if (p1.lossCount != p2.lossCount) {
            return Integer.compare(p1.lossCount, p2.lossCount);
        }
        
        return Integer.compare(p1.pointDiff, p2.pointDiff);
    }
}
