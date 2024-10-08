package org.fencing.demo.matchMaking;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;

import org.fencing.demo.match.Match;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.Event;

public class WithinGroupSort {

    public static TreeSet<Pair> permutation(Set<PlayerRank> playerRanks) {

        TreeSet<Pair> result = new TreeSet<>();
        PlayerRank[] playerArr = playerRanks.toArray(new PlayerRank[0]);

        for(int i = 0; i < playerArr.length - 1; i++){
            for(int r = i + 1; r < playerArr.length; r++){
                result.add(new Pair(playerArr[i], playerArr[r]));
            }
        }
        return result;
    }
    
    public TreeMap<Integer, Set<Match>> groupMatchMakingAlgorithm(Map<Integer, Set<PlayerRank>> groups, Event event){
        TreeMap<Integer, Set<Match>> resultMap = new TreeMap<>();
        for(int i : groups.keySet()){
            HashSet<Match> matches = new HashSet<>();
            TreeSet<Pair> pairings = permutation(groups.get(i));
            for(Pair p : pairings){
                Match current = new Match();
                current.setEvent(event);
                current.setPlayer1(p.getPlayerRank1().getPlayer());
                current.setPlayer2(p.getPlayerRank2().getPlayer());
                matches.add(current);
            }

            resultMap.put(i, matches);
        }

        return resultMap;
    }
}
