package com.example.demo.tournaments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.player.Player;
import com.example.demo.match.Match;
import com.example.demo.tournamentPlayer.TournamentPlayer;;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tournaments")

public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_id_seq")
    @SequenceGenerator(name = "tournament_id_seq", sequenceName = "tournament_id_seq", allocationSize = 1)
    private long id;

    private String name;

    private LocalDate date;

    private List<Match> matches;

    //modify to show ranking??
    //create sorter to sort aft each round
    private List<TournamentPlayer> participants;
}
