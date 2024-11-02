package org.fencing.demo.player;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.events.Gender;
import org.fencing.demo.user.Role;
import org.fencing.demo.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player extends User implements Comparable<Player>{
    private int elo;
    private org.fencing.demo.events.Gender gender;

    private static int STARTING_ELO = 1700;  


    @OneToMany(mappedBy = "player1")
    @JsonIgnore
    private Set<Match> matchesAsPlayer1;

    @OneToMany(mappedBy = "player2")
    @JsonIgnore
    private Set<Match> matchesAsPlayer2;

    @OneToMany(mappedBy = "player")
    @JsonIgnore
    Set<PlayerRank> playerRanks;


    public Player(String username, String password, String email, Role role, Gender gender) {
        super(username, password, email, role);
        this.elo = STARTING_ELO;
        this.gender = gender;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public int compareTo(Player otherPlayer) {
        // Sort in descending order of ELO
        return Integer.compare(otherPlayer.elo, this.elo); // Higher ELO comes first
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
