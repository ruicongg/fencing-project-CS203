package org.fencing.demo.events;

import java.util.Comparator;

public class PlayerRankEloComparator implements Comparator<PlayerRank>{
    
    @Override
    public int compare(PlayerRank pr1, PlayerRank pr2){
        int p1Elo = pr1.getPlayer().getElo();
        int p2Elo = pr2.getPlayer().getElo();

        return p1Elo - p2Elo;
    }


}
