package org.fencing.demo.knockoutmatchmaking;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Service
@Transactional
public class KnockoutMatchMakingServiceImpl implements KnockoutMatchMakingService {

    private final EventRepository eventRepository;
    private final KnockoutStageRepository knockoutStageRepository;
    private final MatchRepository matchRepository;


    public KnockoutMatchMakingServiceImpl(EventRepository eventRepository,
            KnockoutStageRepository knockoutStageRepository,
            MatchRepository matchRepository) {
        this.eventRepository = eventRepository;
        this.knockoutStageRepository = knockoutStageRepository;
        this.matchRepository = matchRepository;

    }

    @Override
    public KnockoutStage createNextKnockoutStage(@NotNull Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        KnockoutStage knockoutStage = new KnockoutStage();
        knockoutStage.setEvent(event);
        return knockoutStageRepository.save(knockoutStage);
    }

    @Override
    public List<Match> createMatchesInKnockoutStage(@NotNull Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        List<KnockoutStage> knockoutStages = event.getKnockoutStages();

        if (knockoutStages == null || knockoutStages.isEmpty()) {
            throw new IllegalArgumentException("No KnockoutStage found for event " + eventId);
        }

        List<Match> matches = new ArrayList<>();
        if (knockoutStages.size() == 0) {
            matches = generateInitialKnockoutMatches(event);
        } else {
            
        }

        // KnockoutStage knockoutStage = knockoutStages.get(knockoutStages.size() - 1);
        // List<Match> knockoutStageMatches = knockoutMatchGenerator
        //         .generateMatchesForKnockoutStage(knockoutStage, knockoutStages, event.getRankings());
        // knockoutStage.getMatches().addAll(matches);

        return matchRepository.saveAll(matches);

    }


    private List<Match> generateInitialKnockoutMatches(Event event) {
        
        return null;
    }

    // public List<Match> generateMatchesForKnockoutStage(KnockoutStage knockoutStage, 
    //         List<KnockoutStage> knockoutStages, Set<PlayerRank> rankings) {
        
    //     List<Player> players = new ArrayList<>();
    //     int roundNum = knockoutStages.indexOf(knockoutStage);

    //     if (roundNum == 0) {
    //         players = convertToPlayerList(rankings);
    //     } else {
    //         KnockoutStage previousRound = knockoutStages.get(roundNum - 1);
    //         players = getPreviousRoundWinners(previousRound.getMatches());
    //     }

    //     return createMatches(players, knockoutStage);
    // }

    // private List<Player> getPreviousRoundWinners(List<Match> previousMatches) {
    //     List<Player> winners = new ArrayList<>();
    //     for (Match match : previousMatches) {
    //         winners.add(match.getWinner());
    //     }
    //     return winners;
    // }

    // private List<Match> createMatches(Map<Integer, Player> players, KnockoutStage knockoutStage) {
    //     List<Match> matches = new ArrayList<>();
    //     int n = players.size();

    //     for (int i = 0; i < n / 2; i++) {
    //         Player player1 = players.get(i);
    //         Player player2 = players.get(n - 1 - i);
    //         matches.add(createMatch(player1, player2, knockoutStage));
    //     }
    //     return matches;
    // }

    // private Match createMatch(Player player1, Player player2, KnockoutStage knockoutStage) {
    //     Match match = new Match();
    //     match.setPlayer1(player1);
    //     match.setPlayer2(player2);
    //     match.setEvent(knockoutStage.getEvent());
    //     match.setKnockoutStage(knockoutStage);
    //     return match;
    // }

    // private List<Player> convertToPlayerList(Set<PlayerRank> rankings) {
    //     List<PlayerRank> playerRankList = new ArrayList<>(rankings);
    //     playerRankList.sort(Comparator.comparing(PlayerRank::getScore));

    //     List<Player> players = new ArrayList<>();
    //     for (PlayerRank playerRank : playerRankList) {
    //         players.add(playerRank.getPlayer());
    //     }
    //     return players;
    // }
}
