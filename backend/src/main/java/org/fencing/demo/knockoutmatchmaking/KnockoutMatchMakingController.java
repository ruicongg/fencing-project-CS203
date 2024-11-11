package org.fencing.demo.knockoutmatchmaking;

import java.util.List;

import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.match.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

@RestController
public class KnockoutMatchMakingController {

    @Autowired
    private KnockoutMatchMakingService knockoutMatchMakingService;

    @PostMapping("/tournaments/{tournamentId}/events/{eventId}/knockoutStage/{knockoutStageId}/matches")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Match> addMatchesForKnockoutStage(@NotNull@PathVariable Long eventId) {
        return knockoutMatchMakingService.createMatchesInKnockoutStage(eventId);
    }

    // POST: Add a new KnockoutStage for a specific event (Admin Only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public KnockoutStage addKnockoutStage(@NotNull@PathVariable Long eventId) {
        return knockoutMatchMakingService.createNextKnockoutStage(eventId);
    }
}
