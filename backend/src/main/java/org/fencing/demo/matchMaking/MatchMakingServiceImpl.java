package org.fencing.demo.matchmaking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.groupstage.GroupStage;
import org.fencing.demo.groupstage.GroupStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Service
@Transactional
public class MatchMakingServiceImpl implements MatchMakingService {

    private final GroupStageRepository groupStageRepository;
    private final EventRepository eventRepository;
    private final MatchRepository matchRepository;
    private final GroupDistributionService groupDistributionService;
    private final GroupMatchGenerator groupMatchGenerator;

    public MatchMakingServiceImpl(GroupStageRepository groupStageRepository, EventRepository eventRepository,
            MatchRepository matchRepository, GroupDistributionService groupDistributionService,
            GroupMatchGenerator groupMatchGenerator) {
        this.groupStageRepository = groupStageRepository;
        this.eventRepository = eventRepository;
        this.matchRepository = matchRepository;
        this.groupDistributionService = groupDistributionService;
        this.groupMatchGenerator = groupMatchGenerator;
    }

    // @Override
    // public List<GroupStage> createGroupStages(@NotNull Long eventId){

    // return eventRepository.findById(eventId).map(event -> {
    // List<GroupStage> grpStages = new ArrayList<>();
    // Map<Integer, List<PlayerRank>> groups =
    // groupDistributionService.distributePlayersIntoGroups(event.getRankings());
    // for(Integer i : groups.keySet()){
    // GroupStage grpStage = new GroupStage();
    // grpStage.setEvent(event);
    // grpStages.add(grpStage);
    // }
    // return groupStageRepository.saveAll(grpStages);
    // }).orElseThrow(() -> new EventNotFoundException(eventId));

    // }

    // // this should assume that group stages are already created
    // @Override
    // public List<Match> addMatchesForGroupStages(@NotNull Long eventId) {
    // Event event = eventRepository.findById(eventId).orElseThrow(() -> new
    // EventNotFoundException(eventId));
    // List<GroupStage> groupStages = event.getGroupStages();
    // if (groupStages.isEmpty()) {
    // throw new IllegalArgumentException(
    // "No groupStage found for event " + eventId + "Please create group stages
    // first");
    // }
    // List<Match> allMatches = new ArrayList<>();
    // for (int i = 0; i < groupStages.size(); i++) {
    // Map<Integer, List<Match>> matches = generateMatchesForGroupStage(event,
    // groupStages.get(i));
    // allMatches.addAll(matches.get(i));
    // }
    // return matchRepository.saveAll(allMatches);
    // }

    // private Map<Integer, List<Match>> generateMatchesForGroupStage(Event event,
    // GroupStage groupStage) {
    // Map<Integer, List<PlayerRank>> groups =
    // groupDistributionService.distributePlayersIntoGroups(event.getRankings());
    // return groupMatchGenerator.generateGroupMatches(groups, event);
    // }

    @Override
    public List<Match> createGroupStagesAndMatches(@NotNull Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        // Distribute players into groups
        Map<Integer, List<PlayerRank>> groups = groupDistributionService
                .distributePlayersIntoGroups(event.getRankings());

        // Create and save group stages
        List<GroupStage> groupStages = groups.keySet().stream()
                .map(groupNumber -> {
                    GroupStage stage = new GroupStage();
                    stage.setEvent(event);
                    return stage;
                })
                .collect(Collectors.toList());
        groupStages = groupStageRepository.saveAll(groupStages);

        // Generate and save matches for each group
        List<Match> allMatches = new ArrayList<>();
        Map<Integer, List<Match>> groupMatches = groupMatchGenerator.generateGroupMatches(groups, event);

        for (int i = 0; i < groupStages.size(); i++) {
            List<Match> matches = groupMatches.get(i);
            allMatches.addAll(matches);
        }

        return matchRepository.saveAll(allMatches);

    }

    @Override
    public List<Match> addMatchesForKnockoutStage(Long eventId) {
        return null;
    }
}
