package org.fencing.demo.stages;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupStageServiceImpl implements GroupStageService{
    
    private final GroupStageRepository groupStageRepository;
    private final EventRepository eventRepository;


    public GroupStageServiceImpl(GroupStageRepository groupStageRepository, EventRepository eventRepository) {
        this.groupStageRepository = groupStageRepository;
        this.eventRepository = eventRepository;
    }

    public GroupStage addGroupStage(Long eventId){
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID and Group Stage cannot be null");
        }
        GroupStage grpStage = new GroupStage();
        return eventRepository.findById(eventId).map(event -> {
            grpStage.setEvent(event);
            event.getGroupStages().add(grpStage);
            return groupStageRepository.save(grpStage);
        }).orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public GroupStage getGroupStage(Long groupStageId){
        if (groupStageId == null) {
            throw new IllegalArgumentException("Event ID and GroupStage cannot be null");
        }
        
        return groupStageRepository.findById(groupStageId)
                .orElseThrow(() -> new GroupStageNotFoundException(groupStageId));
    }

    public GroupStage updateGroupStage(Long eventId, Long groupStageId, GroupStage newGroupStage){
        if (eventId == null || groupStageId == null || newGroupStage == null) {
            throw new IllegalArgumentException("Event ID, GroupStage ID and updated GroupStage cannot be null");
        }
        System.out.println("WORKING");
        System.out.println("OG EVENT:"+eventRepository.findById(eventId));
        System.out.println("NEW EVENT"+newGroupStage.getEvent());
        GroupStage existingGroupStage = groupStageRepository.findById(groupStageId)
                                                .orElseThrow(() -> new GroupStageNotFoundException(groupStageId));
        if (!existingGroupStage.getEvent().equals(newGroupStage.getEvent())) {
            throw new IllegalArgumentException("Event cannot be changed");
        }
        existingGroupStage.getMatches().clear();
        existingGroupStage.getMatches().addAll(newGroupStage.getMatches());
        existingGroupStage.setAllMatchesCompleted(newGroupStage.isAllMatchesCompleted());
        return groupStageRepository.save(existingGroupStage);
    }

    public void deleteGroupStage(Long eventId, Long groupStageId){
       if (eventId == null || groupStageId == null) {
            throw new IllegalArgumentException("Event ID and KnockoutStage ID cannot be null");
        }
        GroupStage groupStage = groupStageRepository.findById(groupStageId)
            .orElseThrow(() -> new GroupStageNotFoundException(groupStageId));
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        event.getGroupStages().remove(groupStage);
        
        groupStageRepository.delete(groupStage);
    }



}
