package org.fencing.demo.stages;

import org.fencing.demo.events.Event;
import org.fencing.demo.match.Match;
// import org.fencing.demo.player.Player;
import org.fencing.demo.player.Player;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table; 

import java.util.Set;
// import java.util.TreeSet;
// import java.util.TreeMap;
// import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "groupStage")
public class GroupStage {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    
    private Set<Player> players;

    @OneToMany(mappedBy = "groupStage", cascade = CascadeType.ALL)
    private Set<Match> matches;
    
    private boolean allMatchesCompleted;

}
