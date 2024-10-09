package org.fencing.demo.stages;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class KnockoutStageServiceImpl implements KnockoutStageService{
    private final KnockoutStageRepository knockoutStageRepository;
    private final EventRepository eventRepository;

    public KnockoutStageServiceImpl(KnockoutStageRepository knockoutStageRepository, EventRepository eventRepository) {
        this.knockoutStageRepository = knockoutStageRepository;
        this.eventRepository = eventRepository;
    }

    public KnockoutStage addKnockoutStage(Long eventId){
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID and KnockoutStage cannot be null");
        }
        KnockoutStage knockoutStage = new KnockoutStage();
        return eventRepository.findById(eventId).map(event -> {
            knockoutStage.setEvent(event);
            event.getKnockoutStages().add(knockoutStage);
            return knockoutStageRepository.save(knockoutStage);
        }).orElseThrow(() -> new EventNotFoundException(eventId));
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
        if (!existingKnockoutStage.getEvent().equals(newKnockoutStage.getEvent())) {
            throw new IllegalArgumentException("Event cannot be changed");
        }
        existingKnockoutStage.setMatches(newKnockoutStage.getMatches());
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
