package org.fencing.demo.matchMaking;

import org.fencing.demo.events.PlayerRank;

public class Pair {
    PlayerRank p1;
    PlayerRank p2;

    public Pair(PlayerRank p1, PlayerRank p2){
        this.p1 = p1;
        this.p2 = p2;
    }

    public PlayerRank getPlayerRank1(){
        return p1;
    }

    public PlayerRank getPlayerRank2(){
        return p2;
    }
}
