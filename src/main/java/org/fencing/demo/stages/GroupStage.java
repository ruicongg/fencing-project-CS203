package org.fencing.demo.stages;

import org.fencing.demo.events.Event;
import org.fencing.demo.match.Match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

// import org.fencing.demo.player.Player;
import org.fencing.demo.events.PlayerRank;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.List;
//import java.util.Set;
// import java.util.TreeSet;
// import java.util.TreeMap;
// import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "groupStage")
@ToString(exclude = "matches")
public class GroupStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // @OneToMany
    // private List<PlayerRank> players;

    @OneToMany(mappedBy = "groupStage", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Match> matches;
    
    private boolean allMatchesCompleted;

    // @Override
    // public String toString() {
    //     return "GroupStage(id=" + id + ", event=" + (event != null ? event.getId() : null) + ", matchesCount=" + (matches != null ? matches.size() : 0) + ")";
    // }
}
