package org.fencing.demo.stages;

import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;

public class KnockoutStageServiceImpl implements KnockoutStageService{
    private final KnockoutStageRepository knockoutStageRepository;
    private final EventRepository eventRepository;

    public KnockoutStageServiceImpl(KnockoutStageRepository knockoutStageRepository, EventRepository eventRepository) {
        this.knockoutStageRepository = knockoutStageRepository;
        this.eventRepository = eventRepository;
    }

    public KnockoutStage addKnockoutStage(Long eventId, KnockoutStage knockoutStage){
        if (eventId == null || knockoutStage == null) {
            throw new IllegalArgumentException("Event ID and KnockoutStage cannot be null");
        }
        return eventRepository.findById(eventId).map(event -> {
            knockoutStage.setEvent(event);
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
        knockoutStageRepository.deleteByEventIdAndId(eventId, knockoutStageId);
    }
}
