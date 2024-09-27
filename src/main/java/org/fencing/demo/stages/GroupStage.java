package org.fencing.demo.stages;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;

import java.util.Set;
import java.util.TreeSet;

@Entity
public class GroupStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @MapsId
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToMany
    public TreeSet<PlayerRank> rankings;

    @OneToMany
    public Set<Match> matches;

    // public GroupStage(TreeSet<PlayerRank> rankings){
    //     // TreeMap<PlayerRank, PlayerRank> pairings = permutation(rankings);
        
    //     // for (PlayerRank pr : pairings.keySet()) {
    //     //     //add getplayer for PlayerRank
    //     //     // add the fields for match
    //     //     // Match newMatch = new Match();
    //     //     // matches.add(newMatch);
    //     // }
        
    // }

    // public static TreeMap<PlayerRank, PlayerRank> permutation(TreeSet<PlayerRank> rankings){

    //     TreeMap<PlayerRank, PlayerRank> result = new TreeMap<>();

    //     for (PlayerRank playerRank : rankings) {
    //         // Inner loop: Iterate through elements that come after 'first'
    //         Iterator<PlayerRank> it = rankings.tailSet(playerRank, false).iterator(); // Start after 'first'
    //         while (it.hasNext()) {
    //             PlayerRank nextPlayerRank = it.next();
    //             result.put(playerRank, nextPlayerRank);
    //         }
    //     }

    //     return result;
    // }
}
