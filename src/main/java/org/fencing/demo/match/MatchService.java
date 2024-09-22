package org.fencing.demo.match;

import java.util.List;

public interface MatchService {

    Match addMatch(Long eventId, Match match);

    List<Match> getAllMatchesByEventId(Long eventId);

    Match getMatch(Long id);

    Match updateMatch(Long eventId, Long matchId, Match newMatch);

    void deleteMatch(Long eventId, Long matchId);
}
