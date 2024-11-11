package org.fencing.demo.groupstage;

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

<<<<<<< HEAD:backend/src/main/java/org/fencing/demo/stages/GroupStageServiceImpl.java
    @Override
    public List<GroupStage> addInitialGroupStages(Long eventId){
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID and Group Stage cannot be null");
        }
        return eventRepository.findById(eventId).map(event -> {
            System.out.println("Event found");
            System.out.println("Event: " + event);
            List<GroupStage> grpStages = new ArrayList<>();
            Map<Integer, List<PlayerRank>> groups = BeforeGroupStage.sortByELO(event.getRankings());
            System.out.println("Print groups map: " + groups);
            for(Integer i : groups.keySet()){
                GroupStage grpStage = new GroupStage();
                grpStage.setEvent(event);
                grpStages.add(grpStage);
            }
            return groupStageRepository.saveAll(grpStages);
        }).orElseThrow(() -> new EventNotFoundException(eventId));
        
    }

    @Override
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
    
    @Override
=======
>>>>>>> f216da0ed79be91e3584c0a1a621baa34ab802c7:backend/src/main/java/org/fencing/demo/groupstage/GroupStageServiceImpl.java
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
