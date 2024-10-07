package org.fencing.demo.events;
import java.util.Objects;

import org.fencing.demo.player.Player;

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
    private Player player; 

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;

    public int score;

    public int winCount;

    public int lossCount;

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

    public void updateAfterMatch(int pointsWon, int pointsOpponent) {
        if (pointsWon > pointsOpponent) {
            winCount++;
            score += (pointsWon * 5); // 5 points for each win
        } else {
            lossCount++;
        }
        score -= pointsOpponent; // Deduct opponent's points
    }

}
