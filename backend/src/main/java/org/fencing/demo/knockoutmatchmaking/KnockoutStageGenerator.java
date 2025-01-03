package org.fencing.demo.knockoutmatchmaking;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fencing.demo.events.Event;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.springframework.stereotype.Component;

@Component
public class KnockoutStageGenerator {

    private final KnockoutAdvancementCalculator calculator;

    public KnockoutStageGenerator() {
        this.calculator = new KnockoutAdvancementCalculator();
    }

    public List<Match> generateInitialKnockoutMatches(KnockoutStage knockoutStage, Event event) {
        Set<PlayerRank> rankings = event.getRankings();
        int totalPlayers = rankings.size();

        // Calculate how many players should advance
        int playersAdvancing = calculator.calculateNumberOfPlayersAdvancing(totalPlayers);
        int numberOfByes = calculator.calculateNumberOfByes(playersAdvancing);

        // Get the top ranked players
        List<Player> qualifiedPlayers = getPlayersAdvancing(playersAdvancing, event);

        // Create matches, accounting for byes
        return createMatchesWithByes(qualifiedPlayers, numberOfByes, knockoutStage, event);
    }

    public List<Match> generateNextKnockoutMatches(KnockoutStage previousStage, KnockoutStage currentStage,
            Event event) {
        List<Match> matches = previousStage.getMatches();
        List<Player> winners = getPreviousRoundWinners(matches);
        return createMatches(winners, currentStage, event);
    }

    private int largestPowerOf2LessThan(int number) {
        int power = 1;
        while (power * 2 <= number) {
            power *= 2;
        }
        return power;
    }

    private List<Player> getPlayersAdvancing(int numberOfPlayersAdvancing, Event event) {

        SortedSet<PlayerRank> rankings = event.getRankings();
        return rankings.stream()
                .sorted()
                .limit(numberOfPlayersAdvancing)
                .map(PlayerRank::getPlayer)
                .collect(Collectors.toList());
    }

    // Create matches pairing highest ranked with lowest ranked
    private List<Match> createMatches(List<Player> players, KnockoutStage knockoutStage, Event event) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < players.size() / 2; i++) {
            matches.add(createMatch(players.get(i), players.get(players.size() - 1 - i), knockoutStage, event));
        }
        return matches;
    }

    private Match createMatch(Player topSeed, Player bottomSeed, KnockoutStage knockoutStage, Event event) {
        Match match = new Match();
        match.setPlayer1(topSeed);
        match.setPlayer2(bottomSeed);
        match.setEvent(event);
        match.setKnockoutStage(knockoutStage);
        return match;
    }

    private List<Player> getPreviousRoundWinners(List<Match> previousMatches) {
        List<Player> winners = new ArrayList<>();
        for (Match match : previousMatches) {
            winners.add(match.getWinner());
        }
        return winners;
    }

    private List<Match> createMatchesWithByes(List<Player> players, int numberOfByes, KnockoutStage knockoutStage, Event event) {
        List<Match> matches = new ArrayList<>();
        int totalPlayers = players.size();
        int matchesNeeded = (totalPlayers - numberOfByes) / 2;

        // Top ranked players get byes
        List<Player> playersWithMatches = players.subList(numberOfByes, totalPlayers);

        // Create matches for remaining players
        for (int i = 0; i < matchesNeeded; i++) {
            Player topSeed = playersWithMatches.get(i);
            Player bottomSeed = playersWithMatches.get(playersWithMatches.size() - 1 - i);
            matches.add(createMatch(topSeed, bottomSeed, knockoutStage, event));
        }

        return matches;
    }

}
