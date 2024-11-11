package org.fencing.demo.knockoutmatchmaking;

import java.util.*;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@Service
@Transactional
public class KnockoutMatchMakingServiceImpl implements KnockoutMatchMakingService {

    private final EventRepository eventRepository;
    private final KnockoutStageRepository knockoutStageRepository;
    private final MatchRepository matchRepository;
    private final KnockoutStageGenerator knockoutStageGenerator;

    public KnockoutMatchMakingServiceImpl(EventRepository eventRepository,
            KnockoutStageRepository knockoutStageRepository,
            MatchRepository matchRepository,
            KnockoutStageGenerator knockoutStageGenerator) {
        this.eventRepository = eventRepository;
        this.knockoutStageRepository = knockoutStageRepository;
        this.matchRepository = matchRepository;
        this.knockoutStageGenerator = knockoutStageGenerator;
    }

    @Override
    public KnockoutStage createNextKnockoutStage(@NotNull Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        KnockoutStage knockoutStage = new KnockoutStage();
        knockoutStage.setEvent(event);
        return knockoutStageRepository.save(knockoutStage);
    }

    @Override
    public List<Match> createMatchesInKnockoutStage(@NotNull Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        List<KnockoutStage> knockoutStages = event.getKnockoutStages();

        if (knockoutStages == null || knockoutStages.isEmpty()) {
            throw new IllegalArgumentException("No KnockoutStage found for event " + eventId);
        }

        // Get the current stage (last one created)
        KnockoutStage currentStage = knockoutStages.get(knockoutStages.size() - 1);
        List<Match> matches;

        // Check if there are any existing stages with matches
        boolean hasExistingStagesWithMatches = knockoutStages.stream()
                .limit(knockoutStages.size() - 1) // Exclude current stage
                .anyMatch(stage -> stage.getMatches() != null && !stage.getMatches().isEmpty());

        // If no existing stages with matches, this is the initial stage
        if (!hasExistingStagesWithMatches) {
            matches = knockoutStageGenerator.generateInitialKnockoutMatches(currentStage, event);
        } else {
            // Get the previous stage and generate matches based on winners
            KnockoutStage previousStage = knockoutStages.get(knockoutStages.size() - 2);
            matches = knockoutStageGenerator.generateNextKnockoutMatches(previousStage, currentStage, event);
        }

        // Maintain bidirectional relationship
        matches.forEach(match -> {
            match.setKnockoutStage(currentStage);
        });
        currentStage.getMatches().clear(); // Clear existing matches
        currentStage.getMatches().addAll(matches); // Add new matches

        // Save everything
        knockoutStageRepository.save(currentStage);
        return matchRepository.saveAll(matches);
    }

    private KnockoutStage generateKnockoutStage(Event event) {
        KnockoutStage knockoutStage = new KnockoutStage();
        knockoutStage.setEvent(event);
        return knockoutStageRepository.save(knockoutStage);
    }

}
