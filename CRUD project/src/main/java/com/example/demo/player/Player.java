package com.example.demo.player;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.example.demo.tournaments.Tournament;

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
