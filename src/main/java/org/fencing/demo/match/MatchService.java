package org.fencing.demo.match;

import java.util.List;

public interface MatchService {

    List<Match> addMatchesforAllGroupStages(Long eventId);

    List<Match> addMatchesforKnockoutStage(Long eventId);

    // List<Match> getAllMatchesByEventId(Long eventId);

    List<Match> getAllMatchesForKnockoutStageByKnockoutStageId(Long knockoutStageId);

    List<Match> getAllMatchesForGroupStageByGroupStageId(Long groupStageId);

    Match getMatch(Long id);

    Match updateMatch(Long eventId, Long matchId, Match newMatch);

    void deleteMatch(Long eventId, Long matchId);
}
