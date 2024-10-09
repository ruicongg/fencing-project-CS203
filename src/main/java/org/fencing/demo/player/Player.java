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


@Entity
@Getter
@Setter
@ToString(exclude = "password")
@NoArgsConstructor
@Table(name = "players")
public class Player implements Comparable<Player>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Username is required")
    @Column(unique = true)
    // ! might need to implement unique logic in service as well
    private String username; 
    @NotNull(message = "Password is required")
    private String password; // Consider hashing passwords for security , Need Min length etc
    @Email(message = "Email should be valid")
    private String email;
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
    private Set<Match> matchesAsPlayer1;

    @OneToMany(mappedBy = "player2")
    private Set<Match> matchesAsPlayer2;

    @OneToMany(mappedBy = "player")
    Set<PlayerRank> playerRanks;


    public Player(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.elo = STARTING_ELO;
    }

    @Override
    public int compareTo(Player otherPlayer) {
        // Sort in descending order of ELO
        return Integer.compare(otherPlayer.elo, this.elo); // Higher ELO comes first
    }

}
