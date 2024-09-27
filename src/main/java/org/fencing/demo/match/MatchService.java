package org.fencing.demo.match;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MatchService {

    // Match addMatchforGroupStage(Long knockoutstageId, Match match);

    List<Match> addMatchesforKnockoutStage(Long eventId);

    // List<Match> getAllMatchesByEventId(Long eventId);

    Map<Integer, Set<Match>> getAllMatchesForKnockoutStageByEventId(Long eventId);

    Set<Match> getAllMatchesForKnockoutStageRound(Long eventId, int round);

    Match getMatch(Long id);

    Match updateMatch(Long eventId, Long matchId, Match newMatch);

    void deleteMatch(Long eventId, Long matchId);
}
