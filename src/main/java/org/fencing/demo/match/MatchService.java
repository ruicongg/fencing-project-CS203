package org.fencing.demo.match;

import java.util.List;

public interface MatchService {

    Match addMatch(Long tournamentId, Match match);

    List<Match> getAllMatchesByTournamentId(Long tournamentId);

    Match updateMatch(Long tournamentId, Long matchId, Match newMatch);

    void deleteMatch(Long tournamentId, Long matchId);
}
