package org.fencing.demo.stages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table; 
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "knockout_stage")
public class KnockoutStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; 

    @OneToMany(mappedBy = "knockoutStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Match> matches; 

    private int roundNum;

    public Set<Match> createOrAdvanceRound() {
        
        List<Player> players = new ArrayList<>();

        if (roundNum == 0) {
            // For the first round, convert PlayerRank set to a list of Players
            Set<PlayerRank> playerRanks = event.getRankings();
            List<PlayerRank> playerRankList = new ArrayList<>(playerRanks);
            playerRankList.sort(Comparator.comparing(PlayerRank::getScore));
            players = convertToPlayerList(playerRanks);

        } else {
            KnockoutStage previousRound = event.getKnockoutStages().get(roundNum - 1 -1);
            Set<Match> previousMatches = previousRound.getMatches();
            for (Match match : previousMatches) {
                players.add(match.getWinner()); // Get the winner of each match

            }
        }

        roundNum++; // Increment the round number
        KnockoutStage nextKnockoutStage = new KnockoutStage();
        Set<Match> nextRound = createMatches(players, roundNum, nextKnockoutStage);
        nextKnockoutStage.setEvent(event);
        nextKnockoutStage.setMatches(nextRound);
        nextKnockoutStage.setRoundNum(roundNum);
        event.getKnockoutStages().add(nextKnockoutStage);
        return nextRound; // Create matches for the next round
    }

    // Method to create matches for both first and subsequent rounds
    private Set<Match> createMatches(List<Player> players, int roundNumber, KnockoutStage nextKnockoutStage) {
        matches = new LinkedHashSet<>();
        int n = players.size();
        for (int i = 0; i < n / 2; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(n - 1 - i);

            // Create a match between the two players
            Match match = new Match();
            match.setPlayer1(player1);
            match.setPlayer2(player2);
            match.setEvent(this.event);
            match.setKnockoutStage(nextKnockoutStage);
            matches.add(match);
        }

        return matches;
    }

    // // Access matches for a specific round
    // public Match getMatch(int round) {
    //     return matches.get(round);
    // }

    public Set<Match> getMatches() {
        return matches;
    }

    // Get the current round number
    public int getRoundNum() {
        return roundNum;
    }

    public List<Player> convertToPlayerList(Set<PlayerRank> rankings) {
        List<Player> players = new ArrayList<>();
        for (PlayerRank playerRank : rankings) {
            players.add(playerRank.getPlayer()); // Extract the Player object from PlayerRank
        }
        return players;
    }
}
