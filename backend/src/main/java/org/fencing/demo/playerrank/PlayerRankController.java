package org.fencing.demo.playerrank;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerRankController {
    private PlayerRankService playerRankService;

    public PlayerRankController(PlayerRankService playerRankService) {
        this.playerRankService = playerRankService;
    }

    @GetMapping("/tournaments/{tournamentId}/events/{eventId}/playerRanks")
    @ResponseStatus(HttpStatus.OK)
    public List<PlayerRank> getAllPlayerRanksForEvent(@PathVariable Long eventId) {
        return playerRankService.getAllPlayerRanksForEvent(eventId);
    }

    @GetMapping("player/{username}/playerRanks")
    @ResponseStatus(HttpStatus.OK)
    public List<PlayerRank> getAllPlayerRanksForPlayer(@PathVariable String username) {
        return playerRankService.getAllPlayerRanksForPlayer(username);
    }
}
