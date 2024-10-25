package org.fencing.demo.events;
import java.util.Objects;

import org.fencing.demo.matchMaking.EloCalculator;
import org.fencing.demo.player.Player;
import org.fencing.demo.stages.GroupStage;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "player_rank", uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "event_id"}))
public class PlayerRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonIgnore
    private Player player; 

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    @ManyToOne
    @JoinColumn(name = "group_stage_id")
    private GroupStage groupStage;

    private int score;

    private int tempElo;

    private int winCount;

    private int lossCount;

    // Update tempElo after player is set
    public void initializeTempElo() {
        if (player != null) {
            this.tempElo = player.getElo();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  // Use the unique `id` field
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerRank that = (PlayerRank) o;
        return id == that.id;  // Use only `id` for equality comparison
    }

    public void updateAfterMatch(int pointsWon, int pointsOpponent, PlayerRank opponent) {
        if (pointsWon > pointsOpponent) {
            winCount++;
            score += (pointsWon * 5); // 5 points for each win
            tempElo = EloCalculator.changeTempElo(this, opponent, true);
        } else {
            lossCount++;
            tempElo = EloCalculator.changeTempElo(this, opponent, false);
        }
        score -= pointsOpponent; // Deduct opponent's points
    }

}
