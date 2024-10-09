package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankEloComparator implements Comparator<PlayerRank>{
    
    public int compare(PlayerRank p1, PlayerRank p2) {
            return p1.getPlayer().compareTo(p2.getPlayer());
    }
}
