package org.fencing.demo.matchMaking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.fencing.demo.match.Match;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.Event;

public class WithinGroupSort {

    public static List<Pair> permutation(List<PlayerRank> playerRanks) {

        List<Pair> result = new ArrayList<>();
        PlayerRank[] playerArr = playerRanks.toArray(new PlayerRank[0]);

        for(int i = 0; i < playerArr.length - 1; i++){
            for(int r = i + 1; r < playerArr.length; r++){
                result.add(new Pair(playerArr[i], playerArr[r]));
            }
        }
        return result;
    }
    
    public static TreeMap<Integer, List<Match>> groupMatchMakingAlgorithm(Map<Integer, List<PlayerRank>> groups, Event event){
        TreeMap<Integer, List<Match>> resultMap = new TreeMap<>();
        for(int i : groups.keySet()){
            List<Match> matches = new ArrayList<>();
            List<Pair> pairings = permutation(groups.get(i));
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
