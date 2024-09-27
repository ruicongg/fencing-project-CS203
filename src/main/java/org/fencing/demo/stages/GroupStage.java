package org.fencing.demo.stages;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.events.Event;

import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;

import java.util.TreeSet;
import java.util.Set;
import java.util.HashSet;


public class GroupStage {
    
    @OneToMany
    public TreeSet<PlayerRank> rankings;

    public Set<Match> matches;

    @ManyToOne
    public Event event;


    public GroupStage(TreeSet<PlayerRank> rankings, Event event, Set<Match> matches){
        this.rankings = rankings;
        this.event = event;
        this.matches = new HashSet<Match>();

        this.matches = matches;
        
    }

    

}
