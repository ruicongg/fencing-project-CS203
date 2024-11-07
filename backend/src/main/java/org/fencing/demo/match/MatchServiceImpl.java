package org.fencing.demo.match;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.Optional;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.GroupStageNotFoundException;
import org.fencing.demo.stages.GroupStageRepository;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.stages.KnockoutStageNotFoundException;
import org.fencing.demo.stages.KnockoutStageRepository;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerNotFoundException;
import org.fencing.demo.player.PlayerRepository;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final EventRepository eventRepository;
    private final KnockoutStageRepository knockoutStageRepository;
    private final GroupStageRepository groupStageRepository;
    private final PlayerRepository playerRepository;

    public MatchServiceImpl(MatchRepository matchRepository, EventRepository eventRepository, 
    KnockoutStageRepository knockoutStageRepository, GroupStageRepository groupStageRepository,
    PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.eventRepository = eventRepository;
        this.knockoutStageRepository = knockoutStageRepository;
        this.groupStageRepository = groupStageRepository;
        this.playerRepository = playerRepository;
    }

    // public Match addMatch(Long eventId, Match match){
    //     if (eventId == null || match == null) {
    //         throw new IllegalArgumentException("Event ID and Match cannot be null");
    //     }
    //     return eventRepository.findById(eventId).map(event -> {
    //         match.setEvent(event);
    //         return matchRepository.save(match);
    //     }).orElseThrow(() -> new EventNotFoundException(eventId));
    // }

    
    @Override
    @Transactional
    public List<Match> addMatchesforGroupStages(Long eventId) {
        List<Match> allMatches = new ArrayList<>();
        if(eventId == null){
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        Event event = eventRepository.findById(eventId).get();
        List<GroupStage> groupStages = event.getGroupStages();
        System.out.println("Group Stages: " + groupStages);
        if (groupStages.isEmpty()) {
            throw new IllegalStateException("No groupStage found for event " + eventId);
        }
        for(int i = 0; i < groupStages.size(); i++){
            List<Match> matches = event.createRoundsForGroupStages(groupStages.get(i));
            allMatches.addAll(matches);
        }
        // for (int i = 0; i < allMatches.size(); i++) {
        //     System.out.println(allMatches.get(i));
        // }

        return matchRepository.saveAll(allMatches);
        
    }


    // @Override
    // @Transactional
    // public List<Match> addMatchesforAllGroupStages(Long eventId) {
    //     if(eventId == null){
    //         throw new IllegalArgumentException("Event ID cannot be null");
    //     }
    //     if (!eventRepository.existsById(eventId)) {
    //         throw new EventNotFoundException(eventId);
    //     }
    //     Event event = eventRepository.findById(eventId).get();
    //     List<GroupStage> groupStages = event.getGroupStages();
    //     System.out.println("Group Stages: " + groupStages);
    //     if (groupStages.isEmpty()) {
    //         throw new IllegalStateException("No groupStage found for event " + eventId);
    //     }
    //     //event.createRoundsForGroupStages() return Set of all groupMatches under a single event
    //     return matchRepository.saveAll(event.createRoundsForGroupStages());
    // }


    @Override
    @Transactional
    public List<Match> addMatchesforKnockoutStage(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }

        Event event = eventRepository.findById(eventId).get();
        List<KnockoutStage> knockoutStages = event.getKnockoutStages();

        if (knockoutStages == null || knockoutStages.isEmpty()) {
            throw new IllegalStateException("No KnockoutStage found for event " + eventId);
        }
        
        KnockoutStage knockoutStage = knockoutStages.get(knockoutStages.size() - 1);
        List<Match> knockoutStageMatches = event.getMatchesForKnockoutStage(knockoutStage);

        knockoutStage.getMatches().addAll(knockoutStageMatches);

        return matchRepository.saveAll(knockoutStageMatches);
    }

    // @Override
    // public List<Match> getAllMatchesByEventId(Long eventId) {
    //     if (eventId == null) {
    //         throw new IllegalArgumentException("Event ID cannot be null");
    //     }
    //     if (!eventRepository.existsById(eventId)) {
    //         throw new EventNotFoundException(eventId);
    //     }
    //     return matchRepository.findByEventId(eventId);
    // }

    public List<Match> getAllMatchesForKnockoutStageByKnockoutStageId(Long knockoutStageId) {
        if (knockoutStageId == null) {
            throw new IllegalArgumentException("Knockout Stage ID cannot be null");
        }
        if (!knockoutStageRepository.existsById(knockoutStageId)) {
            throw new KnockoutStageNotFoundException(knockoutStageId);
        }
        return knockoutStageRepository.findById(knockoutStageId).get().getMatches();
    }

    public List<Match> getAllMatchesForGroupStageByGroupStageId(Long groupStageId) {
        if (groupStageId == null) {
            throw new IllegalArgumentException("Group Stage ID cannot be null");
        }
        if (!groupStageRepository.existsById(groupStageId)) {
            throw new GroupStageNotFoundException(groupStageId);
        }
        return groupStageRepository.findById(groupStageId).get().getMatches();
    }

    @Override
    public Match getMatch(Long matchId) {
        if (matchId == null){
            throw new IllegalArgumentException("Match ID cannot be null");
        }
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
    }

    @Override
    public List<Match> getMatchesScheduledForToday(String username) {
        // Retrieve the player by username
        // Player player = playerRepository.findByUsername(username)
        //         .orElseThrow(() -> new PlayerNotFoundException(username));
        Optional<Player> playerOpt = playerRepository.findByUsername(username);
        Player player = playerOpt.orElseThrow(() -> new PlayerNotFoundException(username));

        LocalDate today = LocalDate.now();

        // Retrieve all matches involving the player by their ID
        List<Match> matches = matchRepository.findMatchesByPlayerId(player.getId());

        // Filter matches based on the date of the associated event
        return matches.stream()
                .filter(match -> match.getEvent() != null && match.getEvent().getStartDate() != null)
                .filter(match -> match.getEvent().getStartDate().toLocalDate().isEqual(today)) // Only matches for today
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Match updateMatch(Long eventId, Long matchId, Match newMatch) {
        if (eventId == null || matchId == null || newMatch == null) {
            throw new IllegalArgumentException("Event ID, Match ID and updated Match cannot be null");
        }
        Match existingMatch = matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));
        
        if (existingMatch.getEvent().getId() != eventId) {
            throw new IllegalArgumentException("Event cannot be changed");
        }

        Event event = existingMatch.getEvent();

        // Fetch PlayerRank for Player 1 and Player 2
        PlayerRank player1Rank = event.getRankings().stream()
                                    .filter(rank -> rank.getPlayer().equals(newMatch.getPlayer1()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Player 1 is not registered in this event"));

        PlayerRank player2Rank = event.getRankings().stream()
                                    .filter(rank -> rank.getPlayer().equals(newMatch.getPlayer2()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Player 2 is not registered in this event"));
        
        existingMatch.setPlayer1(newMatch.getPlayer1());
        existingMatch.setPlayer2(newMatch.getPlayer2());
        existingMatch.setPlayer1Score(newMatch.getPlayer1Score());
        existingMatch.setPlayer2Score(newMatch.getPlayer2Score());
        if (player1Rank == null || player2Rank == null){
            throw new IllegalArgumentException("Player is not registered in this event");
        }

        System.out.println("the tempELO of player1 b4 match" + player1Rank.getTempElo());
        System.out.println("the tempELO of player2 b4 match" + player2Rank.getTempElo());
        System.out.println("\n\n");
        
        player1Rank.updateAfterMatch(newMatch.getPlayer1Score(), newMatch.getPlayer2Score(), player2Rank);
        player2Rank.updateAfterMatch(newMatch.getPlayer2Score(), newMatch.getPlayer1Score(), player1Rank);
        
        System.out.println("the tempELO of player1 aft match" + player1Rank.getTempElo());
        System.out.println("the tempELO of player2 aft match" + player2Rank.getTempElo());

        return matchRepository.save(existingMatch);
    }

    @Override
    @Transactional
    public void deleteMatch(Long eventId, Long matchId) {
        if (eventId == null || matchId == null) {
            throw new IllegalArgumentException("Event ID and Match ID cannot be null");
        }
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new MatchNotFoundException(matchId));
        
        if (match.getEvent() == null || match.getEvent().getId() != eventId) {
            throw new IllegalArgumentException("Match does not belong to the specified event");
        }
        
        matchRepository.delete(match);
    }
}
