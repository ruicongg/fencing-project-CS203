package org.fencing.demo.match;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.GroupStageRepository;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.stages.KnockoutStageNotFoundException;
import org.fencing.demo.stages.KnockoutStageRepository;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final EventRepository eventRepository;
    private final KnockoutStageRepository knockoutStageRepository;

    public MatchServiceImpl(MatchRepository matchRepository, EventRepository eventRepository, 
    KnockoutStageRepository knockoutStageRepository) {
        this.matchRepository = matchRepository;
        this.eventRepository = eventRepository;
        this.knockoutStageRepository = knockoutStageRepository;
        // this.groupStageRepository = groupStageRepository;
    }

    // public Match addMatch(Long eventId, Match match){
    //     if (eventId == null || match == null) {
    //         throw new IllegalArgumentException("Event ID and Match cannot be null");
    //     }
    //     return eventRepository.findById(eventId).map(event -> {
    //         match.setEvent(event);
    //         return matchRepository.save(match);
    //     }).orElseThrow(() -> new EventNotFoundException(eventId));
    // }

    
    @Override
    @Transactional
    public List<Match> addMatchesforAllGroupStages(Long eventId) {
        if(eventId == null){
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        Event event = eventRepository.findById(eventId).get();
        List<GroupStage> groupStages = event.getGroupStages();
        if (groupStages.isEmpty()) {
            throw new IllegalStateException("No groupStage found for event " + eventId);
        }
        //event.createRoundsForGroupStages() return Set of all groupMatches under a single event
        return matchRepository.saveAll(event.createRoundsForGroupStages());
    }

    @Override
    @Transactional
    public List<Match> addMatchesforKnockoutStage(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID and Match cannot be null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        Event event = eventRepository.findById(eventId).get();
        List<KnockoutStage> knockoutStages = event.getKnockoutStages();
        if (knockoutStages.isEmpty()) {
            throw new IllegalStateException("No KnockoutStage found for event " + eventId);
        }
        KnockoutStage knockoutStage = knockoutStages.get(knockoutStages.size() - 1);
        return matchRepository.saveAll(event.createRoundForKnockoutStage(knockoutStage));
    }

    // @Override
    // public List<Match> getAllMatchesByEventId(Long eventId) {
    //     if (eventId == null) {
    //         throw new IllegalArgumentException("Event ID cannot be null");
    //     }
    //     if (!eventRepository.existsById(eventId)) {
    //         throw new EventNotFoundException(eventId);
    //     }
    //     return matchRepository.findByEventId(eventId);
    // }

    public Set<Match> getAllMatchesForKnockoutStageByKnockoutStageId(Long knockoutStageId) {
        if (knockoutStageId == null) {
            throw new IllegalArgumentException("Knockout Stage ID cannot be null");
        }
        if (!knockoutStageRepository.existsById(knockoutStageId)) {
            throw new KnockoutStageNotFoundException(knockoutStageId);
        }
        return knockoutStageRepository.findById(knockoutStageId).get().getMatches();
    }

    @Override
    public Match getMatch(Long matchId) {
        if (matchId == null){
            throw new IllegalArgumentException("Match ID cannot be null");
        }
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
    }

    @Override
    @Transactional
    public Match updateMatch(Long eventId, Long matchId, Match newMatch) {
        if (eventId == null || matchId == null || newMatch == null) {
            throw new IllegalArgumentException("Event ID, Match ID and updated Match cannot be null");
        }
        Match existingMatch = matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));
        if (!existingMatch.getEvent().equals(newMatch.getEvent())) {
            throw new IllegalArgumentException("Event cannot be changed");
        }
        existingMatch.setPlayer1(newMatch.getPlayer1());
        existingMatch.setPlayer2(newMatch.getPlayer2());
        existingMatch.setPlayer1Score(newMatch.getPlayer1Score());
        existingMatch.setPlayer2Score(newMatch.getPlayer2Score());
        existingMatch.setEvent(newMatch.getEvent());
        return matchRepository.save(existingMatch);
        
    }

    @Override
    @Transactional
    public void deleteMatch(Long eventId, Long matchId) {
        if (eventId == null || matchId == null) {
            throw new IllegalArgumentException("Event ID and Match ID cannot be null");
        }
        matchRepository.deleteByEventIdAndId(eventId, matchId);
    }
}