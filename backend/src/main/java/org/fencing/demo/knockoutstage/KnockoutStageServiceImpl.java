package org.fencing.demo.knockoutstage;

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


    public KnockoutStage getKnockoutStage(Long knockoutStageId){
        if (knockoutStageId == null) {
            throw new IllegalArgumentException("Event ID and KnockoutStage cannot be null");
        }
        
        return knockoutStageRepository.findById(knockoutStageId)
                .orElseThrow(() -> new KnockoutStageNotFoundException(knockoutStageId));
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
