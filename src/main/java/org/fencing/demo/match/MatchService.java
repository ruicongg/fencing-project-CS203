package org.fencing.demo.match;

import java.util.List;
import java.util.Set;

public interface MatchService {

    List<Match> addMatchesforAllGroupStages(Long eventId);

    List<Match> addMatchesforKnockoutStage(Long eventId);

    // List<Match> getAllMatchesByEventId(Long eventId);

    List<Match> getAllMatchesForKnockoutStageByKnockoutStageId(Long knockoutStageId);

    Set<Match> getAllMatchesForGroupStageByGroupStageId(Long groupStageId);

    Match getMatch(Long id);

    Match updateMatch(Long eventId, Long matchId, Match newMatch);

    void deleteMatch(Long eventId, Long matchId);
}
