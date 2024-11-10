package org.fencing.demo.knockoutstage;

import java.util.List;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.match.Match;
import org.springframework.stereotype.Service;

@Service
public class KnockoutStageServiceImpl implements KnockoutStageService{
    private final KnockoutStageRepository knockoutStageRepository;
    private final EventRepository eventRepository;

    public KnockoutStageServiceImpl(KnockoutStageRepository knockoutStageRepository, EventRepository eventRepository) {
        this.knockoutStageRepository = knockoutStageRepository;
        this.eventRepository = eventRepository;
    }


    public KnockoutStage getKnockoutStage(Long knockoutStageId){
        if (knockoutStageId == null) {
            throw new IllegalArgumentException("Event ID and KnockoutStage cannot be null");
        }
        
        return knockoutStageRepository.findById(knockoutStageId)
                .orElseThrow(() -> new KnockoutStageNotFoundException(knockoutStageId));
    }

    public KnockoutStage updateKnockoutStage(Long eventId, Long knockoutStageId, KnockoutStage newKnockoutStage){
        if (eventId == null || knockoutStageId == null || newKnockoutStage == null) {
            throw new IllegalArgumentException("Event ID, KnockoutStage ID and updated KnockoutStage cannot be null");
        }
        KnockoutStage existingKnockoutStage = knockoutStageRepository.findById(knockoutStageId)
                                                .orElseThrow(() -> new KnockoutStageNotFoundException(knockoutStageId));
        List<Match> existingMatches = existingKnockoutStage.getMatches();
        List<Match> newMatches = newKnockoutStage.getMatches();
        if (existingMatches.size() != newMatches.size() || 
            !existingMatches.containsAll(newMatches) || 
            !newMatches.containsAll(existingMatches)) {
            throw new IllegalArgumentException("Matches cannot be changed");
        }
        existingKnockoutStage.setEvent(newKnockoutStage.getEvent());
        return knockoutStageRepository.save(existingKnockoutStage);
    }

    public void deleteKnockoutStage(Long eventId, Long knockoutStageId){
        if (eventId == null || knockoutStageId == null) {
            throw new IllegalArgumentException("Event ID and KnockoutStage ID cannot be null");
        }
        KnockoutStage knockoutStage = knockoutStageRepository.findById(knockoutStageId)
            .orElseThrow(() -> new KnockoutStageNotFoundException(knockoutStageId));
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        event.getKnockoutStages().remove(knockoutStage);
        
        knockoutStageRepository.delete(knockoutStage);
    }
}
