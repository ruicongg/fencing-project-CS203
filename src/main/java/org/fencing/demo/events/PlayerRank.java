package org.fencing.demo.events;
import org.fencing.demo.player.Player;
import org.fencing.demo.stages.GroupStage;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private Event event;

    @OneToMany
    private GroupStage groupStage;

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
