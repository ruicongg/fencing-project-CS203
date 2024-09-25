package org.fencing.demo.stages;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.events.Event;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.util.TreeSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Iterator;

public class GroupStage {
    
    @ManyToMany
    public TreeSet<PlayerRank> rankings;

    public Set<Match> matches;

    @ManyToOne
    public Event event;


    public GroupStage(TreeSet<PlayerRank> rankings, Event event){
        this.rankings = rankings;
        this.event = event;
        this.matches = new HashSet<Match>();

        TreeMap<PlayerRank, PlayerRank> pairings = permutation(rankings);
        
        for (PlayerRank pr : pairings.keySet()) {
            Match newMatch = new Match(event, pr.getPlayer(), pairings.get(pr).getPlayer());
            matches.add(newMatch);
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
