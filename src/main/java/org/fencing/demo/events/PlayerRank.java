package org.fencing.demo.events;

import org.fencing.demo.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "Player rank")
public class PlayerRank {
    public Player player;

    public int scoreDiff;

    public int winCount;

    public int lossCount;

    @ManyToOne
    public Event event;

}
