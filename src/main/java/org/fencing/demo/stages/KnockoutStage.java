package org.fencing.demo.stages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table; 
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_id")
    private Event event; 

    @Builder.Default
    private Map<Integer, Set<Match>> matches = new LinkedHashMap<>(); // Store matches by round number

    @Builder.Default
    private int currentRound = 0; // Keep track of the current round

    public Set<Match> createOrAdvanceRound() {
        
        List<Player> players = new ArrayList<>();

        if (currentRound == 0) {
            // For the first round, convert PlayerRank set to a list of Players
            Set<PlayerRank> playerRanks = event.getRankings();
            List<PlayerRank> playerRankList = new ArrayList<>(playerRanks);
            playerRankList.sort(Comparator.comparing(PlayerRank::getScore));
            players = convertToPlayerList(playerRanks);

        } else {
            Set<Match> previousMatches = getMatchesForRound(currentRound);
            for (Match match : previousMatches) {
                players.add(match.getWinner()); // Get the winner of each match

            }
        }

        currentRound++; // Increment the round number
        return createMatches(players, currentRound); // Create matches for the next round
    }

    // Method to create matches for both first and subsequent rounds
    private Set<Match> createMatches(List<Player> players, int roundNumber) {
        Set<Match> roundMatches = new LinkedHashSet<>();
        int n = players.size();
        for (int i = 0; i < n / 2; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(n - 1 - i);

            // Create a match between the two players
            Match match = new Match();
            match.setPlayer1(player1);
            match.setPlayer2(player2);
            match.setEvent(this.event);
            roundMatches.add(match);
        }
        matches.put(roundNumber, roundMatches); // Store the matches in the map by round
        return roundMatches;
    }

    // Access matches for a specific round
    public Set<Match> getMatchesForRound(int round) {
        return matches.get(round);
    }

    // Get the current round number
    public int getCurrentRound() {
        return currentRound;
    }

    public List<Player> convertToPlayerList(Set<PlayerRank> rankings) {
        List<Player> players = new ArrayList<>();
        for (PlayerRank playerRank : rankings) {
            players.add(playerRank.getPlayer()); // Extract the Player object from PlayerRank
        }
        return players;
    }
}
