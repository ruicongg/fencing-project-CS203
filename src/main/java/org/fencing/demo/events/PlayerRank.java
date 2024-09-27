package org.fencing.demo.events;
import org.fencing.demo.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name = "player rank")
public class PlayerRank {
    @OneToOne
    @JoinColumn(name = "player_id")
    public Player player;

    public int score;

    public int winCount;

    public int lossCount;

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
