package org.fencing.demo.stages;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;

import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table; 

import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.HashSet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "groupStage")
public class GroupStage {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public TreeMap<Integer, Set<Player>> playerGroups;

    public TreeMap<Integer, Set<Match>> groupMatches;

    private boolean allMatchesCompleted;

    public void eloSort(){
        // Check if event and rankings exist
        if (getEvent() == null || getEvent().getRankings() == null) {
            return;
        }

        Set<PlayerRank> rankings = getEvent().getRankings();
        Set<Player> players = new TreeSet<>();

        // Add players from rankings to the TreeSet (automatically sorted)
        for (PlayerRank pr : rankings) {
            players.add(pr.getPlayer());
        }

        int playerNum = players.size();
        if (playerNum == 0) {
            return; // No players to sort
        }

        // Map to store possible group sizes and remainders
        TreeMap<Integer, Integer> factorRemainder = new TreeMap<>();

        // Find the best group size by checking divisors between 4 and 7
        for (int i = 4; i <= 7; i++) {
            int remainder = playerNum % i;
            factorRemainder.put(i, remainder);
        }

        int bestFactor = 4;
        int grpSize = 0;

        // Find the best group size with the smallest remainder
        for (Integer factor : factorRemainder.keySet()) {
            if (factorRemainder.get(factor) == 0) {
                grpSize = factor;
                break;
            }
            // Otherwise, pick the factor with the smallest remainder
            if (factorRemainder.get(factor) > factorRemainder.get(bestFactor)) {
                bestFactor = factor;
            }
        }

        if (grpSize == 0) {
            grpSize = bestFactor; // Use the best factor if no perfect division
        }

        int numGroups = playerNum / grpSize;
        int remainder = playerNum % grpSize;

        // If there's a remainder, we may need one more group
        if (remainder != 0) {
            numGroups++;
        }

        // Initialize the groups
        for (int i = 1; i <= numGroups; i++) {
            playerGroups.put(i, new HashSet<>());
        }

        // Distribute players across the groups in a round-robin fashion
        int currentGrp = 1;
        for (Player p : players) {
            playerGroups.get(currentGrp).add(p);
            currentGrp++;
            if (currentGrp > numGroups) {
                currentGrp = 1;
            }
        }

    }

    // to move methods here
    public static TreeSet<Pair> permutation(Set<Player> players) {

        TreeSet<Pair> result = new TreeSet<>();
        Player[] playerArr = players.toArray(new Player[0]);

        for(int i = 0; i < playerArr.length - 1; i++){
            for(int r = i + 1; r < playerArr.length; r++){
                result.add(new Pair(playerArr[i], playerArr[r]));
            }
        }
        return result;
    }
    
    public void groupMatchMakingAlgorithm(){
        for(int i : playerGroups.keySet()){
            HashSet<Match> matches = new HashSet<>();
            TreeSet<Pair> pairings = permutation(playerGroups.get(i));
            for(Pair p : pairings){
                Match current = new Match();
                current.setEvent(event);
                current.setPlayer1(p.getPlayer1());
                current.setPlayer2(p.getPlayer2());
                matches.add(current);
            }

            groupMatches.put(i, matches);
        }
    }
    


}
