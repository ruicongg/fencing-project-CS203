package org.fencing.demo.events;

import org.fencing.demo.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Player rank")
public class PlayerRank {
    public Player player;

    public int scoreDiff;

    public int winCount;

    public int lossCount;

    @ManyToOne
    public Event event;

    public PlayerRank(Player p){
        this.player = p;
        this.scoreDiff = 0;
        this.winCount = 0;
        this.lossCount = 0;
    }

}
