package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankEloComparator implements Comparator<PlayerRank>{
    
    public int compare(PlayerRank p1, PlayerRank p2) {
        int eloCompare = Integer.compare(p1.getPlayer().getElo(), p2.getPlayer().getElo());
        if (eloCompare != 0) {
            return eloCompare;
        } else {
            //else compare by username
            return p1.getPlayer().getUsername().compareTo(p2.getPlayer().getUsername());
        }
    }
}
