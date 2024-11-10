package org.fencing.demo.matchmaking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.fencing.demo.match.Match;

@RestController
public class MatchMakingController {
    
    @Autowired
    private MatchMakingService matchMakingService;  

    @PostMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage/matches")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Match> addInitialMatchForGroupStage(@PathVariable Long eventId) {
        return matchMakingService.createGroupStagesAndMatches(eventId);
    }


}
