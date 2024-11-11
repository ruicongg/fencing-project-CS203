package org.fencing.demo.playerrank;

import java.util.Objects;

import org.fencing.demo.events.Event;
import org.fencing.demo.player.Player;

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
@Table(name = "player_rank", uniqueConstraints = @UniqueConstraint(columnNames = { "player_id", "event_id" }))
public class PlayerRank implements Comparable<PlayerRank> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private int score;

    private int winCount;

    private int lossCount;

    private int tempElo;

    // Update tempElo after player is set
    public void initializeTempElo() {
        if (player != null) {
            this.tempElo = player.getElo();
        }
    }

    @Override
    public int compareTo(PlayerRank other) {
        
        // First compare by higher win count
        if (this.getWinCount() != other.getWinCount()) {
            return Integer.compare(other.getWinCount(), this.getWinCount()); // Descending order of wins
        }

        // If win count is the same, compare by lower loss count
        if (this.getLossCount() != other.getLossCount()) {
            return Integer.compare(this.getLossCount(), other.getLossCount()); // Ascending order of losses
        }

        // If both win and loss counts are the same, compare by higher score
        if (this.getScore() != other.getScore()) {
            return Integer.compare(other.getScore(), this.getScore()); // Descending order of score
        }

        // As a final tiebreaker, compare by Player ID to ensure no two PlayerRanks are considered equal unless they are for the same player
        return Long.compare(this.getPlayer().getId(), other.getPlayer().getId()); 

    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use the unique `id` field
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PlayerRank that = (PlayerRank) o;
        return id == that.id; // Use only `id` for equality comparison
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
