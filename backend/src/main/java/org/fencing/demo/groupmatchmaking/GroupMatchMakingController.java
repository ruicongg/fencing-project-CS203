package org.fencing.demo.groupmatchmaking;

import java.util.List;

import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.match.Match;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupMatchMakingController {

    private final GroupMatchMakingService matchMakingService;

    public GroupMatchMakingController(GroupMatchMakingService matchMakingService) {
        this.matchMakingService = matchMakingService;
    }

    @PostMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage")
    @ResponseStatus(HttpStatus.CREATED)
    public List<GroupStage> addGroupStage(@PathVariable Long eventId) {
        return matchMakingService.createGroupStages(eventId);
    }

    @PostMapping("/tournaments/{tournamentId}/events/{eventId}/groupStage/matches")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Match> addInitialMatchForGroupStage(@PathVariable Long eventId) {
        return matchMakingService.createMatchesInGroupStages(eventId);
    }

   

}
