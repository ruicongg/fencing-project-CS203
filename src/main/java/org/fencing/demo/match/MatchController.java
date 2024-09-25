package org.fencing.demo.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/tournaments/{tournamentId}/events/{eventId}/matches")
    @ResponseStatus(HttpStatus.CREATED)
    public Match addMatch(@PathVariable Long eventId, @RequestBody Match match) {
        return matchService.addMatch(eventId, match);
    }

    @GetMapping("/tournaments/{tournamentId}/events/{eventId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public List<Match> getAllMatchesByEventId(@PathVariable Long eventId) {
        return matchService.getAllMatchesByEventId(eventId);
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