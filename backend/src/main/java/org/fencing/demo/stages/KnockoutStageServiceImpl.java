package org.fencing.demo.stages;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.springframework.stereotype.Service;
import org.fencing.demo.match.Match;
import java.util.List;
@Service
public class KnockoutStageServiceImpl implements KnockoutStageService{
    private final KnockoutStageRepository knockoutStageRepository;
    private final EventRepository eventRepository;

    public KnockoutStageServiceImpl(KnockoutStageRepository knockoutStageRepository, EventRepository eventRepository) {
        this.knockoutStageRepository = knockoutStageRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public KnockoutStage addKnockoutStage(Long eventId){
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        KnockoutStage knockoutStage = new KnockoutStage();
        return eventRepository.findById(eventId).map(event -> {
            knockoutStage.setEvent(event);
            event.getKnockoutStages().add(knockoutStage);
            return knockoutStageRepository.save(knockoutStage);
        }).orElseThrow(() -> new EventNotFoundException(eventId));
    }

    @Override
    public KnockoutStage getKnockoutStage(Long knockoutStageId){
        if (knockoutStageId == null) {
            throw new IllegalArgumentException("Event ID and KnockoutStage cannot be null");
        }
        
        return knockoutStageRepository.findById(knockoutStageId)
            .orElseThrow(() -> new KnockoutStageNotFoundException(knockoutStageId));
    }

    @Override
    public List<KnockoutStage> getAllKnockoutStagesByEventId(Long eventId){
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        return knockoutStageRepository.findAllByEventId(eventId);
    }

    @Override
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

    @Override
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
