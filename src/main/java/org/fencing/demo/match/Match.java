package org.fencing.demo.match;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

import org.fencing.demo.events.Event;
import org.fencing.demo.player.Player;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    // Optional ManyToOne relationship with GroupStage
    @ManyToOne
    @JoinColumn(name = "group_stage_id")
    @JsonIgnore
    private GroupStage groupStage;

    // Optional ManyToOne relationship with KnockoutStage
    @ManyToOne
    @JoinColumn(name = "knockout_stage_id")
    private KnockoutStage knockoutStage;
    
    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private Player player2;

    private int player1Score;
    private int player2Score;


    //added - tbc
    //private boolean matchFinished;


    public Player getWinner(){
        return player1Score > player2Score ? player1 : player2;
    }

    //need the number of k value in player
    // add the number of matches played in player
    // public void updateEloRanking(){
    //     if(matchFinished){
    //         int elo1 = this.player1.getElo();
    //         int elo2 = this.player2.getElo();


    //     }
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;
        Match match = (Match) o;
        return id == match.id; // Use the ID for equality check
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash based on ID
    }

}