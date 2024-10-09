package org.fencing.demo.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.TreeMap;

import org.fencing.demo.match.Match;
import org.fencing.demo.matchMaking.BeforeGroupStage;
import org.fencing.demo.matchMaking.WithinGroupSort;
import org.fencing.demo.player.Player;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournaments.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private WeaponType weapon;

    // for sorting after
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlayerRank> rankings = new TreeSet<>();

    //public TreeSet<Player> EloRank;
    //for sorting first when go to group stage
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
    
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    //@JsonIgnore // To prevent circular references during serialization
    private List<GroupStage> groupStages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnockoutStage> knockoutStages = new ArrayList<>();

    //HOMEWORKKKKK FOR JL
    //includes creating matches
    public Set<Match> createRoundsForGroupStages() {
        Set<Match> allMatchesForGroup = new HashSet<>();
        //sort by elo ranks return grp num to playerRanks
        TreeMap<Integer, Set<PlayerRank>> groups = BeforeGroupStage.sortByELO(rankings);
        //within groups to sort
        TreeMap<Integer, Set<Match>> groupMatches = WithinGroupSort.groupMatchMakingAlgorithm(groups, this);
        for(Integer i:groups.keySet()){
            GroupStage grpStage = new GroupStage();
            grpStage.setPlayers(groups.get(i));
            grpStage.setMatches(groupMatches.get(i));
            // add all the matches to return
            allMatchesForGroup.addAll(groupMatches.get(i));
            grpStage.setEvent(this);
            groupStages.add(grpStage);
        }

        return allMatchesForGroup;
    }

    public Set<Match> createRoundForKnockoutStage(KnockoutStage knockoutStage) {
        
        List<Player> players = new ArrayList<>();
        int roundNum = knockoutStages.size();

        if (roundNum == 1) {
            // For the first round, convert PlayerRank set to a list of Players
            List<PlayerRank> playerRankList = new ArrayList<>(rankings);
            playerRankList.sort(Comparator.comparing(PlayerRank::getScore));

        } else {
            KnockoutStage previousRound = knockoutStages.get(roundNum - 1 - 1);
            Set<Match> previousMatches = previousRound.getMatches();
            for (Match match : previousMatches) {
                players.add(match.getWinner()); // Get the winner of each match

            }
        }

        roundNum++; // Increment the round number
        // KnockoutStage nextKnockoutStage = new KnockoutStage();
        Set<Match> nextRound = createMatches(players, knockoutStage);
        knockoutStage.setEvent(this);
        knockoutStage.setMatches(nextRound);
        knockoutStage.setRoundNum(roundNum);
        knockoutStages.add(knockoutStage);
        return nextRound; // Create matches for the next round
    }

    // Method to create matches for both first and subsequent rounds
    private Set<Match> createMatches(List<Player> players, KnockoutStage nextKnockoutStage) {
        Set<Match> matches = new LinkedHashSet<>();
        int n = players.size();
        for (int i = 0; i < n / 2; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(n - 1 - i);

            // Create a match between the two players
            Match match = new Match();
            match.setPlayer1(player1);
            match.setPlayer2(player2);
            match.setEvent(this);
            match.setKnockoutStage(nextKnockoutStage);
            matches.add(match);
        }

        return matches;
    }

    public List<Player> convertToPlayerList(Set<PlayerRank> rankings) {
        List<Player> players = new ArrayList<>();
        for (PlayerRank playerRank : rankings) {
            players.add(playerRank.getPlayer()); // Extract the Player object from PlayerRank
        }
        return players;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  // Use the unique `id` field
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id;  // Use only `id` for equality comparison
    }

}
