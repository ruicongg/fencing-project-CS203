package org.fencing.demo.matchMaking;

import org.fencing.demo.player.Player;

public class Pair {
    Player p1;
    Player p2;

    public Pair(Player p1, Player p2){
        this.p1 = p1;
        this.p2 = p2;
    }

    public Player getPlayer1(){
        return p1;
    }

    public Player getPlayer2(){
        return p2;
    }
}
