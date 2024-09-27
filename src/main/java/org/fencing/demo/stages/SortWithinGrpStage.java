package org.fencing.demo.stages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.events.Event;

//call before creating grp stage so can pass in the matches
// I think maybe can add back into grp stage
public class SortWithinGrpStage {

    public static TreeMap<PlayerRank, PlayerRank> permutation(TreeSet<PlayerRank> rankings){

        TreeMap<PlayerRank, PlayerRank> result = new TreeMap<>();

        for (PlayerRank playerRank : rankings) {
            // Inner loop: Iterate through elements that come after 'first'
            Iterator<PlayerRank> it = rankings.tailSet(playerRank, false).iterator(); // Start after 'first'
            while (it.hasNext()) {
                PlayerRank nextPlayerRank = it.next();
                result.put(playerRank, nextPlayerRank);
            }
        }

        return result;
    }

    public static HashSet<Match> groupMatchMakingAlgorithm(TreeSet<PlayerRank> rankings, Event event){
        TreeMap<PlayerRank, PlayerRank> pairings = permutation(rankings);

        HashSet<Match> results = new HashSet<>();

        for (PlayerRank pr : pairings.keySet()) {
            Match newMatch = new Match(event, pr.getPlayer(), pairings.get(pr).getPlayer());
            results.add(newMatch);
        }

        return results;
    }
}
