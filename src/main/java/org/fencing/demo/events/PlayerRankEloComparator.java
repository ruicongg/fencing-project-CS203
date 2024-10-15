package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankEloComparator implements Comparator<PlayerRank>{
    
    public int compare(PlayerRank p1, PlayerRank p2) {
        return Integer.compare(p1.getPlayer().getElo(), p2.getPlayer().getElo());
    }
}
