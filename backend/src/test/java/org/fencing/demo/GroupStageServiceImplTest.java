package org.fencing.demo;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.GroupStageNotFoundException;
import org.fencing.demo.stages.GroupStageRepository;
import org.fencing.demo.stages.GroupStageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupStageServiceImplTest {

    private GroupStageRepository groupStageRepository;
    private EventRepository eventRepository;
    private GroupStageServiceImpl groupStageService;

    @BeforeEach
    void setUp() {
        groupStageRepository = mock(GroupStageRepository.class);
        eventRepository = mock(EventRepository.class);
        groupStageService = new GroupStageServiceImpl(groupStageRepository, eventRepository);
    }

    @Test
    void addGroupStage_eventExists_shouldAddGroupStage() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setGroupStages(new ArrayList<>());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(groupStageRepository.save(any(GroupStage.class))).thenAnswer(i -> i.getArguments()[0]);

        GroupStage groupStage = groupStageService.addGroupStage(eventId);

        assertNotNull(groupStage);
        assertEquals(event, groupStage.getEvent());
        assertTrue(event.getGroupStages().contains(groupStage));
        verify(groupStageRepository).save(groupStage);
    }

    @Test
    void addGroupStage_eventDoesNotExist_shouldThrowException() {
        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> groupStageService.addGroupStage(eventId));
    }

    @Test
    void getGroupStage_groupStageExists_shouldReturnGroupStage() {
        Long groupStageId = 1L;
        GroupStage groupStage = new GroupStage();
        groupStage.setId(groupStageId);

        when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.of(groupStage));

        GroupStage result = groupStageService.getGroupStage(groupStageId);

        assertEquals(groupStage, result);
    }

    @Test
    void getGroupStage_groupStageDoesNotExist_shouldThrowException() {
        Long groupStageId = 1L;

        when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.empty());

        assertThrows(GroupStageNotFoundException.class, () -> groupStageService.getGroupStage(groupStageId));
    }

    // @Test
    // void updateGroupStage_validUpdate_shouldReturnUpdatedGroupStage() {
    //     Long eventId = 1L;
    //     Long groupStageId = 1L;
    //     GroupStage existingGroupStage = new GroupStage();
    //     existingGroupStage.setId(groupStageId);
    //     Event event = new Event();
    //     existingGroupStage.setEvent(event);
    //     ArrayList<Match> matches = new ArrayList<>();
    //     existingGroupStage.setMatches(matches);
    //     existingGroupStage.setAllMatchesCompleted(false);


    //     when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.of(existingGroupStage));
    //     when(groupStageRepository.save(existingGroupStage)).thenReturn(existingGroupStage);

    //     existingGroupStage.setAllMatchesCompleted(true);

    //     GroupStage result = groupStageService.updateGroupStage(eventId, groupStageId, existingGroupStage);

    //     assertEquals(existingGroupStage, result);
    //     assertTrue(existingGroupStage.isAllMatchesCompleted());
    // }

    @Test
    void updateGroupStage_noChanges_shouldThrowException() {
        Long eventId = 1L;
        Long groupStageId = 1L;
        GroupStage existingGroupStage = new GroupStage();
        existingGroupStage.setId(groupStageId);
        existingGroupStage.setEvent(new Event());
        ArrayList<Match> matches = new ArrayList<>();
        matches.add(new Match());
        existingGroupStage.setMatches(matches);

        GroupStage newGroupStage = new GroupStage();
        newGroupStage.setEvent(new Event());
        matches.add(new Match());
        existingGroupStage.setMatches(matches);
        newGroupStage.setMatches(matches);

        when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.of(existingGroupStage));

        assertThrows(IllegalArgumentException.class, () -> groupStageService.updateGroupStage(eventId, groupStageId, newGroupStage));
    }

    @Test
    void deleteGroupStage_validIds_shouldDeleteGroupStage() {
        Long eventId = 1L;
        Long groupStageId = 1L;
        Event event = new Event();
        event.setId(eventId);
        GroupStage groupStage = new GroupStage();
        groupStage.setId(groupStageId);
        
        event.getGroupStages().add(groupStage);

        when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.of(groupStage));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        groupStageService.deleteGroupStage(eventId, groupStageId);

        assertFalse(event.getGroupStages().contains(groupStage));
        verify(groupStageRepository).delete(groupStage);
    }

    @Test
    void deleteGroupStage_groupStageDoesNotExist_shouldThrowException() {
        Long eventId = 1L;
        Long groupStageId = 1L;

        when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.empty());

        assertThrows(GroupStageNotFoundException.class, () -> groupStageService.deleteGroupStage(eventId, groupStageId));
    }

    @Test
    void deleteGroupStage_eventDoesNotExist_shouldThrowException() {
        Long eventId = 1L;
        Long groupStageId = 1L;
        GroupStage groupStage = new GroupStage();
        groupStage.setId(groupStageId);

        when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.of(groupStage));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> groupStageService.deleteGroupStage(eventId, groupStageId));
    }
}
