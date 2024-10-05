package org.fencing.demo.events;

import java.time.LocalDateTime;
import java.util.Set;

import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournaments.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private WeaponType weapon;

    // for sorting after
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // To avoid circular references
    private Set<PlayerRank> rankings;

    //public TreeSet<Player> EloRank;
    //for sorting first when go to group stage
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
    
    // @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    // @JsonIgnore // To prevent circular references during serialization
    // private GroupStage GroupStages;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore // To prevent circular references during serialization
    private KnockoutStage knockoutStage;

}
