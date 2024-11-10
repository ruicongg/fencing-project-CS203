package org.fencing.demo.player;

import java.util.Objects;
import java.util.Set;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

// import org.fencing.demo.tournaments.Tournament;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player extends User implements Comparable<Player>{
    private int elo;

    private static int STARTING_ELO = 1700;  

    @OneToMany(mappedBy = "player1")
    @JsonIgnore
    private Set<Match> matchesAsPlayer1;

    @OneToMany(mappedBy = "player2")
    @JsonIgnore
    private Set<Match> matchesAsPlayer2;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<PlayerRank> playerRanks;

    private boolean reached2400;


    public Player(String username, String password, String email, Role role) {
        super(username, password, email, role);
        this.elo = STARTING_ELO;
    }

    @Override
    public int compareTo(Player otherPlayer) {
        // Sort in descending order of ELO
        if (this.elo != otherPlayer.elo) {
            return Integer.compare(otherPlayer.elo, this.elo); // Higher ELO comes first
        }
        return Long.compare(this.getId(), otherPlayer.getId()); // If ELO is the same, compare by ID
        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(getId(), player.getId());  // Compare based on ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());  // Hash based on ID
    }

    @Override
    public String toString() {
        return super.toString() + elo;
    }

}
