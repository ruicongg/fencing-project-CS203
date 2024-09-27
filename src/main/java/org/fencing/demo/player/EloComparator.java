package org.fencing.demo.player;

import java.util.Comparator;

public class EloComparator implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        return Integer.compare(p1.getElo(), p2.getElo());
    }
    
}

