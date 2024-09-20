package com.example.demo.player;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "password")
@NoArgsConstructor
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username; //Need Unique Usernames, throw error if username taken
    private String password; // Consider hashing passwords for security , Need Min length etc
    private String email; 
    private int elo;

    public Player(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
        this.elo = 1700; //Arbitary value
    }

    // @ManyToMany
    // @JoinTable(
    //     name = "Tournament_player",
    //     joinColumns = @JoinColumn(name = "Player_id"),
    //     inverseJoinColumns = @JoinColumn(name = "Tournament_id")
    // )
}

