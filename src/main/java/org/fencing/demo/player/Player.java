package org.fencing.demo.player;

import org.fencing.demo.tournaments.Tournament;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "password")
@NoArgsConstructor
public class Player {

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

    @ManyToMany
    @JoinTable(
        name = "Tournament_player",
        joinColumns = @JoinColumn(name = "Player_id"),
        inverseJoinColumns = @JoinColumn(name = "Tournament_id")
    )
    private Set<Tournament> tournaments = new HashSet<>();

    public Player(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.elo = STARTING_ELO;
    }

}
