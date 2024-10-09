package org.fencing.demo.match;

import java.util.List;

public interface MatchService {

    // Match addMatchforGroupStage(Long knockoutstageId, Match match);

    List<Match> addMatchesforKnockoutStage(Long eventId);

    // List<Match> getAllMatchesByEventId(Long eventId);

    List<Match> getAllMatchesForKnockoutStageByKnockoutStageId(Long knockoutStageId);

    Match getMatch(Long id);

    Match updateMatch(Long eventId, Long matchId, Match newMatch);

    void deleteMatch(Long eventId, Long matchId);
}
