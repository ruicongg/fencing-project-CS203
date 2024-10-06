package org.fencing.demo.match;

import java.util.List;
import java.util.Set;

public interface MatchService {

    List<Match> addMatchesforGroupStage(Long groupstageId);

    List<Match> addMatchesforKnockoutStage(Long eventId);

    // List<Match> getAllMatchesByEventId(Long eventId);

   Set<Match> getAllMatchesForKnockoutStageByKnockoutStageId(Long knockoutStageId);

    Match getMatch(Long id);

    Match updateMatch(Long eventId, Long matchId, Match newMatch);

    void deleteMatch(Long eventId, Long matchId);
}
