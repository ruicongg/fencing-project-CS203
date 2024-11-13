package org.fencing.demo.groupmatchmaking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.groupstage.GroupStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.player.Player;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Service
@Transactional
public class GroupMatchMakingServiceImpl implements GroupMatchMakingService {

    private final GroupStageRepository groupStageRepository;
    private final EventRepository eventRepository;
    private final MatchRepository matchRepository;
    private final GroupDistributionService groupDistributionService;
    private final GroupMatchGenerator groupMatchGenerator;

    private static final int MIN_PLAYERS_GROUP_STAGE = 8;

    public GroupMatchMakingServiceImpl(GroupStageRepository groupStageRepository, EventRepository eventRepository,
            MatchRepository matchRepository, GroupDistributionService groupDistributionService,
                GroupMatchGenerator groupMatchGenerator) {
        this.groupStageRepository = groupStageRepository;
        this.eventRepository = eventRepository;
        this.matchRepository = matchRepository;
        this.groupDistributionService = groupDistributionService;
        this.groupMatchGenerator = groupMatchGenerator;
    }

    @Override
    public List<GroupStage> createGroupStages(@NotNull Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        validatePlayersForGroupStage(event);
        
        if (event.getGroupStages() != null && !event.getGroupStages().isEmpty()) {
            throw new IllegalArgumentException("GroupStages already exist for event " + eventId);
        }
        Map<Integer, List<Player>> groups = groupDistributionService
                .distributePlayersIntoGroups(event.getRankings());

        // Create and save group stages
        List<GroupStage> groupStages = groups.keySet().stream()
                .map(groupNumber -> {
                    GroupStage stage = new GroupStage();
                    stage.setEvent(event);
                    return stage;
                })
                .collect(Collectors.toList());
        return groupStageRepository.saveAll(groupStages);
    }

    @Override
    public List<Match> createMatchesInGroupStages(@NotNull Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        Map<Integer, List<Player>> groups = groupDistributionService
                .distributePlayersIntoGroups(event.getRankings());
        List<GroupStage> groupStages = event.getGroupStages();
        if (groupStages == null || groupStages.isEmpty()) {
            throw new IllegalArgumentException("No GroupStage found for event " + eventId);
        }
        // Generate and save matches for each group
        List<Match> allMatches = new ArrayList<>();
        Map<Integer, List<Match>> groupMatches = groupMatchGenerator.generateGroupMatches(groups, event);

        for (int i = 0; i < groupStages.size(); i++) {
            List<Match> matches = groupMatches.get(i);
            allMatches.addAll(matches);
        }

        return matchRepository.saveAll(allMatches);
    }

    private void validatePlayersForGroupStage(Event event) {
        int registeredPlayers = event.getRankings().size();
        if (registeredPlayers < MIN_PLAYERS_GROUP_STAGE) {
            throw new IllegalStateException(
                String.format("Cannot generate group stages: Insufficient number of registered players. Minimum %d players required, but only %d registered.", 
                    MIN_PLAYERS_GROUP_STAGE, 
                    registeredPlayers)
            );
        }
    }
}
