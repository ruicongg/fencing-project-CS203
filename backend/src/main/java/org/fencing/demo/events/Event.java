package org.fencing.demo.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
// import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.tournament.Tournament;
import org.springframework.format.annotation.DateTimeFormat;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Event start date cannot be null")
    @Future(message = "Event start date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime startDate;

    @NotNull(message = "Event start date cannot be null")
    @Future(message = "Event start date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime endDate;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Weapon is required")
    @Enumerated(EnumType.STRING)
    private WeaponType weapon;

    // for sorting after
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private TreeSet<PlayerRank> rankings = new TreeSet<>();

    // public TreeSet<Player> EloRank;
    // for sorting first when go to group stage
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupStage> groupStages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<KnockoutStage> knockoutStages = new ArrayList<>();

    // //added this
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Match> matches = new ArrayList<>();

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", rankingsCount=" + rankings.size() + // Just print the count or IDs
                '}';
    }

}
