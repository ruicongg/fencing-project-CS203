package org.fencing.demo.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // @PostMapping("/tournaments/{tournamentId}/events/{eventId}/group/*/matches")
    // @ResponseStatus(HttpStatus.CREATED)
    // public Match addMatchforGroupStage(@PathVariable Long eventId, @RequestBody Match match) {
    //     return matchService.addMatchforGroupStage(eventId, match);
    // }

    @PostMapping("/tournaments/{tournamentId}/events/{eventId}/knockoutStage/{knockoutStageId}/matches")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Match> addMatchforKnockoutStage(@PathVariable Long eventId) {
        return matchService.addMatchesforKnockoutStage(eventId);
    }

    @GetMapping("/tournaments/{tournamentId}/events/{eventId}/knockoutStage/{knockoutStageId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public Set<Match> getAllMatchesForKnockoutStageByKnockoutStageId(@PathVariable Long knockoutStageId) {
        return matchService.getAllMatchesForKnockoutStageByKnockoutStageId(knockoutStageId);
    }

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