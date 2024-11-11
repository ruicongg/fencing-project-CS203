package org.fencing.demo.knockoutstage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournaments/{tournamentId}/events/{eventId}/knockoutStage")
public class KnockoutStageController {

    private final KnockoutStageService knockoutStageService;

    public KnockoutStageController(KnockoutStageService knockoutStageService) {
        this.knockoutStageService = knockoutStageService;
    }

    
    // GET: Get a specific KnockoutStage by ID (Accessible by anyone)
    @GetMapping("/{knockoutStageId}")
    @ResponseStatus(HttpStatus.OK)
    public KnockoutStage getKnockoutStage(@PathVariable Long knockoutStageId) {
        return knockoutStageService.getKnockoutStage(knockoutStageId);
    }

    // there isn't a put method for knockout stage because you can't change the event and you can't change the matches
    
    // DELETE: Remove a KnockoutStage from a specific event (Admin Only)
    @DeleteMapping("/{knockoutStageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteKnockoutStage(@PathVariable Long eventId, @PathVariable Long knockoutStageId) {
        if (knockoutStageService.getKnockoutStage(knockoutStageId).getEvent().getId() != eventId) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        knockoutStageService.deleteKnockoutStage(eventId, knockoutStageId);
        return ResponseEntity.noContent().build();
    }

}
