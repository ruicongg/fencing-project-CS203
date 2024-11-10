package org.fencing.demo.matchmaking;

import java.util.List;

import org.fencing.demo.match.Match;

public interface MatchMakingService {
    
    List<Match> createGroupStagesAndMatches(Long eventId);

    List<Match> addMatchesForKnockoutStage(Long eventId);
}
