package com.example.demo.match;

import jakarta.persistence.*;
import com.example.demo.tournaments.Tournament;
import com.example.demo.player.Player;

@Entity
public class Match {
    
    private Tournament tournament;
    // in position 0 is winner followed by loss
    private Player[] winLoss;
    // in postion 0 is the higher score
    private int[] sortedScores;
    

}
