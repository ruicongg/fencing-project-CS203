package org.fencing.demo.player;


// import org.fencing.demo.tournaments.Tournament;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
// import java.util.HashSet;
import java.util.Set;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player extends User implements Comparable<Player>{
    private int elo;

    private final int STARTING_ELO = 1700;

    // @ManyToMany
    // @JoinTable(
    //     name = "player_tournament",
    //     joinColumns = @JoinColumn(name = "player_id"),
    //     inverseJoinColumns = @JoinColumn(name = "tournament_id")
    // )
    // private Set<Tournament> tournaments = new HashSet<>();

    // // ! need to implement logic to update matches when player is updated
    // @OneToMany(mappedBy = "winner", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Match> wonMatches = new HashSet<>();

    // @OneToMany(mappedBy = "loser", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Match> lostMatches = new HashSet<>();


    @OneToMany(mappedBy = "player1")
    @JsonIgnore
    private Set<Match> matchesAsPlayer1;

    @OneToMany(mappedBy = "player2")
    @JsonIgnore
    private Set<Match> matchesAsPlayer2;

    @OneToMany(mappedBy = "player")
    @JsonIgnore
    Set<PlayerRank> playerRanks;


    public Player(String username, String password, String email) {
        super(username, password, email);
        this.elo = STARTING_ELO;
    }

    @Override
    public int compareTo(Player otherPlayer) {
        // Sort in descending order of ELO
        return Integer.compare(otherPlayer.elo, this.elo); // Higher ELO comes first
    }

    @Override
    public String toString() {
        return super.toString() + elo;
    }

}
