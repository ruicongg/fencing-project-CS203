package org.fencing.demo.matchMaking;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.events.PlayerRank;

public class WithinGroupSort {

    public static TreeSet<Pair> permutation(Set<Player> players) {

        TreeSet<Pair> result = new TreeSet<>();
        Player[] playerArr = players.toArray(new Player[0]);

        for(int i = 0; i < playerArr.length - 1; i++){
            for(int r = i + 1; r < playerArr.length; r++){
                result.add(new Pair(playerArr[i], playerArr[r]));
            }
        }
        return result;
    }
    
    // public TreeMap<Integer, Match> groupMatchMakingAlgorithm(){
    //     TreeMap<Integer, Match> resultMap = new TreeMap<>();
    //     for(int i : resultMap.keySet()){
    //         HashSet<Match> matches = new HashSet<>();
    //         TreeSet<Pair> pairings = permutation(resultMap.get(i));
    //         for(Pair p : pairings){
    //             Match current = new Match();
    //             current.setEvent(event);
    //             current.setPlayer1(p.getPlayer1());
    //             current.setPlayer2(p.getPlayer2());
    //             matches.add(current);
    //         }

    //         groupMatches.put(i, matches);
    //     }
    // }
}
