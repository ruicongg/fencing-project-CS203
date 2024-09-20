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

    @PostMapping("/tournaments/{tournamentId}/matches")
    @ResponseStatus(HttpStatus.CREATED)
    public Match addMatch(@PathVariable Long tournamentId, @RequestBody Match match) {
        return matchService.addMatch(tournamentId, match);
    }

    @GetMapping("/tournaments/{tournamentId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public List<Match> getAllMatchesByTournamentId(@PathVariable Long tournamentId) {
        return matchService.getAllMatchesByTournamentId(tournamentId);
    }

    @PutMapping("/tournaments/{tournamentId}/match/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    public Match updateMatch(@PathVariable Long tournamentId, @PathVariable Long matchId, @RequestBody Match match) {
        return matchService.updateMatch(tournamentId, matchId, match);
    }

    @DeleteMapping("/tournaments/{tournamentId}/match/{matchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMatch(@PathVariable Long tournamentId, @PathVariable Long matchId) {
        matchService.deleteMatch(tournamentId, matchId);
    }
}