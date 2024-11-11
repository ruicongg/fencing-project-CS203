package org.fencing.demo.match;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

//import java.util.Set;

@RestController
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }


    @GetMapping("/tournaments/{tournamentId}/events/{eventId}/knockoutStage/{knockoutStageId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public List<Match> getAllMatchesForKnockoutStageByKnockoutStageId(@PathVariable Long knockoutStageId) {
        return matchService.getAllMatchesForKnockoutStageByKnockoutStageId(knockoutStageId);
    }

    @GetMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage/{groupStageId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public List<Match> getAllMatchesForGroupStageByGroupStageId(@PathVariable Long groupStageId) {
        return matchService.getAllMatchesForGroupStageByGroupStageId(groupStageId);
    }

    @GetMapping("/tournaments/{tournamentId}/events/{eventId}/match/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public Match getMatch(@PathVariable Long matchId) {
        return matchService.getMatch(matchId);
    }

    // @GetMapping("/upcoming-matches")
    // public List<Match> getUpcomingMatches() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String username = authentication.getName();
    //     return matchService.getMatchesScheduledForToday(username);
    // }

    @PutMapping("/tournaments/{tournamentId}/events/{eventId}/match/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public Match updateMatch(@PathVariable Long eventId, @PathVariable Long matchId, @RequestBody Match match) {
        return matchService.updateMatch(eventId, matchId, match);
    }

    @DeleteMapping("/tournaments/{tournamentId}/events/{eventId}/match/{matchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMatch(@PathVariable Long eventId, @PathVariable Long matchId) {
        matchService.deleteMatch(eventId, matchId);
    }

    
}
