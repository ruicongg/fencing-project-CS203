package org.fencing.demo.events;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.Set;

import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournaments.Tournament;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    //public TreeSet<Player> EloRank;
    //for sorting first when go to group stage

    @ManyToOne
    public Tournament tournament;
    
    @OneToMany
    public Set<GroupStage> GroupStages;

    public KnockoutStage knockoutStage;

}
