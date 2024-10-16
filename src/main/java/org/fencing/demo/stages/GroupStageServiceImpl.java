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
            System.out.println("Event ID: " + eventId);
            System.out.println("GroupStage ID: " + groupStageId);
            System.out.println("Updated GroupStage: " + newGroupStage);
            throw new IllegalArgumentException("Event ID, GroupStage ID and updated GroupStage cannot be null");
        }
        GroupStage existingGroupStage = groupStageRepository.findById(groupStageId)
                                                .orElseThrow(() -> new GroupStageNotFoundException(groupStageId));
        // if (!existingGroupStage.getMatches().equals(newGroupStage.getMatches())) {
        //     throw new IllegalArgumentException("No changes can be made to the group stage");
        // }
        System.out.println("Existing GroupStage: " + existingGroupStage);
        if (existingGroupStage.getMatches().size() != newGroupStage.getMatches().size()
            || !existingGroupStage.getMatches().containsAll(newGroupStage.getMatches())
            || !newGroupStage.getMatches().containsAll(existingGroupStage.getMatches())) {
            throw new IllegalArgumentException("No changes can made to the group stage");
        }
        // System.out.println("Existing GroupStage: " + existingGroupStage);
        if (existingGroupStage.getEvent().getId() != (newGroupStage.getEvent().getId())) {

            throw new IllegalArgumentException("Event cannot be changed");
        }
        existingGroupStage.getMatches().clear();
        System.out.println("New GroupStage: " + newGroupStage);
        System.out.println("Matches: " + newGroupStage.getMatches());
        
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
