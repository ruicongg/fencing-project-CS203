// ! This is a simple example of a Player class

package org.fencing.demo.player;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import org.fencing.demo.tournaments.Tournament;

@Table(name = "players")
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_id_seq")
    private long id;

    private String name;

    private int elo;

    // @ManyToMany
    // @JoinTable(
    //     name = "Tournament_player",
    //     joinColumns = @JoinColumn(name = "Player_id"),
    //     inverseJoinColumns = @JoinColumn(name = "Tournament_id")
    // )
    
    // see the tournaments participating in
    private List<Tournament> tournaments;

}
