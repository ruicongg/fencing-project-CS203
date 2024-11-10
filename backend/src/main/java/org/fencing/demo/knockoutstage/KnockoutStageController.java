package org.fencing.demo.knockoutstage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // POST: Add a new KnockoutStage for a specific event (Admin Only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public KnockoutStage addKnockoutStage(@PathVariable Long eventId) {
        return knockoutStageService.addKnockoutStage(eventId);
    }
    
    // GET: Get a specific KnockoutStage by ID (Accessible by anyone)
    @GetMapping("/{knockoutStageId}")
    @ResponseStatus(HttpStatus.OK)
    public KnockoutStage getKnockoutStage(@PathVariable Long knockoutStageId) {
        return knockoutStageService.getKnockoutStage(knockoutStageId);
    }

    // PUT: Update an existing KnockoutStage for a specific event (Admin Only)
    @PutMapping("/{knockoutStageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KnockoutStage> updateKnockoutStage(@PathVariable Long eventId, @PathVariable Long knockoutStageId, @RequestBody KnockoutStage knockoutStage) {
        if (knockoutStageService.getKnockoutStage(knockoutStageId).getEvent().getId() != eventId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Event mismatch check
        }

        KnockoutStage updatedKnockoutStage = knockoutStageService.updateKnockoutStage(eventId, knockoutStageId, knockoutStage);
        return ResponseEntity.ok(updatedKnockoutStage);
    }

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
