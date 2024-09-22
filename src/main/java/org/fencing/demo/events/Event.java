package org.fencing.demo.events;

import java.time.LocalDate;
import java.util.TreeSet;
import java.util.Set;

import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournaments.Tournament;


public class Event {

    private long id;

    public LocalDate startDate;

    public LocalDate endDate;

    public Gender gender;

    public WeaponType weapon;

    // for sorting after
    public TreeSet<PlayerRank> rankings;

    //public TreeSet<Player> EloRank;
    //for sorting first when go to group stage

    public Tournament tournament;

    public Set<GroupStage> GroupStages;

    public KnockoutStage knockoutStage;

}
