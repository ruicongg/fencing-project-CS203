package org.fencing.demo.match;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.fencing.demo.tournaments.*;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public MatchServiceImpl(MatchRepository matchRepository, TournamentRepository tournamentRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    @Transactional
    public Match addMatch(Long tournamentId, Match match) {
        if (tournamentId == null || match == null) {
            throw new IllegalArgumentException("Tournament ID and Match cannot be null");
        }
        return tournamentRepository.findById(tournamentId).map(tournament -> {
            match.setTournament(tournament);
            return matchRepository.save(match);
        }).orElseThrow(() -> new TournamentNotFoundException(tournamentId));
    }

    @Override
    public List<Match> getAllMatchesByTournamentId(Long tournamentId) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new TournamentNotFoundException(tournamentId);
        }
        return matchRepository.findByTournamentId(tournamentId);
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
    @Transactional
    public Match updateMatch(Long tournamentId, Long matchId, Match newMatch) {
        if (tournamentId == null || matchId == null || newMatch == null) {
            throw new IllegalArgumentException("Tournament ID, Match ID and updated Match cannot be null");
        }
        Match existingMatch = matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));
        if (!existingMatch.getTournament().equals(newMatch.getTournament())) {
            throw new IllegalArgumentException("Tournament cannot be changed");
        }
        existingMatch.setWinner(newMatch.getWinner());
        existingMatch.setLoser(newMatch.getLoser());
        existingMatch.setLoserScore(newMatch.getLoserScore());
        existingMatch.setWinnerScore(newMatch.getWinnerScore());
        return matchRepository.save(existingMatch);
        
    }

    @Override
    @Transactional
    public void deleteMatch(Long tournamentId, Long matchId) {
        if (tournamentId == null || matchId == null) {
            throw new IllegalArgumentException("Tournament ID and Match ID cannot be null");
        }
        matchRepository.deleteByTournamentIdAndId(tournamentId, matchId);
    }
}