package org.fencing.demo.events;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.Set;

import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournaments.Tournament;
import org.fencing.demo.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    private long id;

    public LocalDateTime startDate;

    public LocalDateTime endDate;

    public Gender gender;

    public WeaponType weapon;

    // for sorting after
    public TreeSet<PlayerRank> rankings;

    public TreeSet<Player> players;
    //for sorting first when go to group stage

    @ManyToOne
    public Tournament tournament;
    
    @OneToMany
    public Set<GroupStage> GroupStages;

    public KnockoutStage knockoutStage;

    public Event(LocalDateTime startDate, LocalDateTime endDate, Gender gender,
    WeaponType weapon, Tournament tournament){
        this.tournament = tournament;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gender = gender;
        this.weapon = weapon;
    }

    public void addPlayer(Player p){
        players.add(p);
        PlayerRank pr = new PlayerRank(p);
        rankings.add(pr);
    }

}
