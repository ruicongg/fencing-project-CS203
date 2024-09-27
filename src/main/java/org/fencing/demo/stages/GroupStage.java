package org.fencing.demo.stages;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;

import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Iterator;

public class GroupStage {
    
    public TreeSet<PlayerRank> rankings;

    public Set<Match> matches;

    public GroupStage(TreeSet<PlayerRank> rankings){
        TreeMap<PlayerRank, PlayerRank> pairings = permutation(rankings);
        
        for (PlayerRank pr : pairings.keySet()) {
            //add getplayer for PlayerRank
            // add the fields for match
            // Match newMatch = new Match();
            // matches.add(newMatch);
        }
        
    }

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
}
