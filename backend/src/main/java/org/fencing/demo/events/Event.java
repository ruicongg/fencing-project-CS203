package org.fencing.demo.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
// import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

import org.fencing.demo.match.Match;
import org.fencing.demo.matchMaking.BeforeGroupStage;
import org.fencing.demo.matchMaking.WithinGroupSort;
import org.fencing.demo.player.Player;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournament.Tournament;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

    @NotNull(message = "Event start date cannot be null")
    @Future(message = "Event start date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime startDate;

    @NotNull(message = "Event start date cannot be null")
    @Future(message = "Event start date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime endDate;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Weapon is required")
    @Enumerated(EnumType.STRING)
    private WeaponType weapon;

    // for sorting after
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PlayerRank> rankings = new TreeSet<>(new PlayerRankComparator());

    // public TreeSet<Player> EloRank;
    // for sorting first when go to group stage
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<GroupStage> groupStages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<KnockoutStage> knockoutStages = new ArrayList<>();

    private String status;

    //includes creating matches

    public List<Match> createRoundsForGroupStages() {
        System.out.println("all the player ranks in event.java = " + rankings);
        List<Match> allMatchesForGroup = new ArrayList<>();
        //sort by elo ranks return grp num to playerRanks
        TreeMap<Integer, List<PlayerRank>> groups = BeforeGroupStage.sortByELO(rankings);
        System.out.println("\n\n");
        System.out.println("BeforeGroupStage.sortByELO in Event.java" + groups);

        //within groups to sort
        TreeMap<Integer, List<Match>> groupMatches = WithinGroupSort.groupMatchMakingAlgorithm(groups, this);

        System.out.println("\n\n");
        System.out.println("WithinGroupSort.groupMatchMakingAlgorithm in Event.java" + groups);
        System.out.println("\n\n");

        for(Integer i:groups.keySet()){
            GroupStage grpStage = new GroupStage();
            grpStage.setPlayers(groups.get(i));
            grpStage.setMatches(groupMatches.get(i));
            // add all the matches to return
            allMatchesForGroup.addAll(groupMatches.get(i));
            grpStage.setEvent(this);
            groupStages.add(grpStage);
        }

        return groupMatches.get((int)currGrpStage.getId());
    }

    //public List<GroupStage> 

    public List<Match> getMatchesForKnockoutStage(KnockoutStage knockoutStage) {

        List<Player> players = new ArrayList<>();
        int roundNum = knockoutStages.indexOf(knockoutStage);

        if (roundNum == 0) {
            // For the first round, convert PlayerRank set to a list of Players
            players = convertToPlayerList(rankings);

        } else {
            KnockoutStage previousRound = knockoutStages.get(roundNum - 1);
            List<Match> previousMatches = previousRound.getMatches();
            for (Match match : previousMatches) {
                players.add(match.getWinner()); // Get the winner of each match

            }
        }

        List<Match> nextRound = createMatches(players, knockoutStage);
        return nextRound; // Create matches for the next round
    }

    // Method to create matches for both first and subsequent rounds
    private List<Match> createMatches(List<Player> players, KnockoutStage knockoutStage) {
        List<Match> matches = new ArrayList<>();
        int n = players.size();

        for (int i = 0; i < n / 2; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(n - 1 - i);
            // System.out.println(player1);
            // System.out.println(player2);

            // Create a match between the two players
            Match match = new Match();
            match.setPlayer1(player1);
            match.setPlayer2(player2);
            match.setEvent(this);
            match.setKnockoutStage(knockoutStage);
            matches.add(match);
        }
        return matches;
    }

    public List<Player> convertToPlayerList(Set<PlayerRank> rankings) {
        List<PlayerRank> playerRankList = new ArrayList<>(rankings);
        playerRankList.sort(Comparator.comparing(PlayerRank::getScore));

        List<Player> players = new ArrayList<>();
        for (PlayerRank playerRank : playerRankList) {
            players.add(playerRank.getPlayer()); // Extract the Player object from PlayerRank
        }
        return players;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", rankingsCount=" + rankings.size() + // Just print the count or IDs
                '}';
    }

}
