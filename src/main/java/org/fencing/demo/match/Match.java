package org.fencing.demo.match;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.fencing.demo.tournaments.Tournament;
import org.fencing.demo.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "matches")
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "match_id_seq", sequenceName = "match_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    // @ManyToOne
    // @JoinColumn(name = "winner_id", nullable = false)
    // private Player winner;

    // @ManyToOne
    // @JoinColumn(name = "loser_id", nullable = false)
    // private Player loser;

    private int winnerScore;
    private int loserScore;
}