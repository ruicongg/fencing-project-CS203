package org.fencing.demo.match;

import java.util.List;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.groupstage.GroupStageNotFoundException;
import org.fencing.demo.groupstage.GroupStageRepository;
import org.fencing.demo.knockoutstage.KnockoutStageNotFoundException;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.playerrank.PlayerRank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final KnockoutStageRepository knockoutStageRepository;
    private final GroupStageRepository groupStageRepository;
    private final PlayerRepository playerRepository;

    public MatchServiceImpl(MatchRepository matchRepository, EventRepository eventRepository, 
    KnockoutStageRepository knockoutStageRepository, GroupStageRepository groupStageRepository,
    PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.knockoutStageRepository = knockoutStageRepository;
        this.groupStageRepository = groupStageRepository;
        this.playerRepository = playerRepository;
    }




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

    // @Override
    // public List<Match> getMatchesScheduledForToday(String username) {
    //     // Retrieve the player by username
    //     // Player player = playerRepository.findByUsername(username)
    //     //         .orElseThrow(() -> new PlayerNotFoundException(username));
    //     Optional<Player> playerOpt = playerRepository.findByUsername(username);
    //     Player player = playerOpt.orElseThrow(() -> new PlayerNotFoundException(username));

    //     LocalDate today = LocalDate.now();

    //     // Retrieve all matches involving the player by their ID
    //     List<Match> matches = matchRepository.findMatchesByPlayerId(player.getId());

    //     // Filter matches based on the date of the associated event
    //     return matches.stream()
    //             .filter(match -> match.getEvent() != null && match.getEvent().getStartDate() != null)
    //             .filter(match -> match.getEvent().getStartDate().toLocalDate().isEqual(today)) // Only matches for today
    //             .collect(Collectors.toList());
    // }

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
        
        player1Rank.updateAfterMatch(newMatch.getPlayer1Score(), newMatch.getPlayer2Score(), player2Rank);
        player2Rank.updateAfterMatch(newMatch.getPlayer2Score(), newMatch.getPlayer1Score(), player1Rank);
        
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
