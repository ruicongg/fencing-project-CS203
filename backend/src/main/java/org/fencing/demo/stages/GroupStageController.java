package org.fencing.demo.stages;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupStageController {
    private final GroupStageService groupStageService;

    public GroupStageController(GroupStageService groupStageService) {
        this.groupStageService = groupStageService;
    }

    @PostMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage")
    @ResponseStatus(HttpStatus.CREATED)
    public List<GroupStage> addGroupStage(@PathVariable Long eventId) {
        System.out.println("Controller is running");
        return groupStageService.addInitialGroupStages(eventId);
    }

    @GetMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage/{groupStageId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupStage getGroupStage(@PathVariable Long groupStageId) {
        return groupStageService.getGroupStage(groupStageId);
    }

    @PutMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage/{groupStageId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupStage updateGroupStage(@PathVariable Long eventId, @PathVariable Long groupStageId, @RequestBody GroupStage groupStage) {
        return groupStageService.updateGroupStage(eventId, groupStageId, groupStage);
    }

    @DeleteMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage/{groupStageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupStage(@PathVariable Long eventId, @PathVariable Long groupStageId) {
        groupStageService.deleteGroupStage(eventId, groupStageId);
    }
}
