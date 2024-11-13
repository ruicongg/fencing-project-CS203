package org.fencing.demo.groupstage;

import java.util.List;

import org.fencing.demo.events.*;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.groupmatchmaking.*;
import org.springframework.stereotype.Service;

@Service
public class GroupStageServiceImpl implements GroupStageService{
    
    private final GroupStageRepository groupStageRepository;
    private final EventRepository eventRepository;


    public GroupStageServiceImpl(GroupStageRepository groupStageRepository, EventRepository eventRepository) {
        this.groupStageRepository = groupStageRepository;
        this.eventRepository = eventRepository;
    }

    public GroupStage getGroupStage(Long groupStageId){
        if (groupStageId == null) {
            throw new IllegalArgumentException("Event ID and GroupStage cannot be null");
        }
        
        return groupStageRepository.findById(groupStageId)
                .orElseThrow(() -> new GroupStageNotFoundException(groupStageId));
    }

    @Override
    public List<GroupStage> getAllGroupStagesByEventId(Long eventId){
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        return groupStageRepository.findAllByEventId(eventId);
    }

    @Override
    public GroupStage updateGroupStage(Long eventId, Long groupStageId, GroupStage newGroupStage){
        if (eventId == null || groupStageId == null || newGroupStage == null) {
            throw new IllegalArgumentException("Event ID, GroupStage ID and updated GroupStage cannot be null");
        }
        GroupStage existingGroupStage = groupStageRepository.findById(groupStageId)
                                                .orElseThrow(() -> new GroupStageNotFoundException(groupStageId));
        if (existingGroupStage.getMatches().equals(newGroupStage.getMatches())) {
            throw new IllegalArgumentException("No changes can made to the group stage");
        }
        // System.out.println("Existing GroupStage: " + existingGroupStage);
        if (existingGroupStage.getEvent().getId() != (newGroupStage.getEvent().getId())) {

            throw new IllegalArgumentException("Event cannot be changed");
        }
        existingGroupStage.getMatches().clear();
        
        existingGroupStage.setAllMatchesCompleted(newGroupStage.isAllMatchesCompleted());
        return groupStageRepository.save(existingGroupStage);
    }

    @Override
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
