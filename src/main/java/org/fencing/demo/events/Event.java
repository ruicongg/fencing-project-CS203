package org.fencing.demo.events;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.Set;

import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournaments.Tournament;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
    @OneToOne
    public TreeSet<PlayerRank> rankings;

    //public TreeSet<Player> EloRank;
    //for sorting first when go to group stage
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    public Tournament tournament;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<GroupStage> GroupStages;

    @OneToOne
    public KnockoutStage knockoutStage;

}
