package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.knockoutstage.KnockoutStageNotFoundException;
import org.fencing.demo.knockoutstage.KnockoutStageRepository;
import org.fencing.demo.knockoutstage.KnockoutStageServiceImpl;
import org.fencing.demo.tournament.Tournament;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KnockoutStageServiceTest {

    @Mock
    private KnockoutStageRepository knockoutStageRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private KnockoutStageServiceImpl knockoutStageService;

    @Test
    public void addKnockoutStage_ValidEvent_ReturnsSavedKnockoutStage() {
        Long eventId = 1L;
        Event event = createValidEvent();
        KnockoutStage knockoutStage = new KnockoutStage();
        knockoutStage.setEvent(event);  // Ensure the event is set before saving
    
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(knockoutStageRepository.save(any(KnockoutStage.class))).thenReturn(knockoutStage);  // Expect a KnockoutStage with an event

        KnockoutStage result = knockoutStageService.addKnockoutStage(eventId);
    
        assertNotNull(result);
        verify(knockoutStageRepository, times(1)).save(any(KnockoutStage.class));  // Verify that a knockoutStage with an event is saved
    }

    @Test
    public void addKnockoutStage_NullEventId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            knockoutStageService.addKnockoutStage(null);
        });
    }

    @Test
    public void addKnockoutStage_NonExistentEvent_ThrowsEventNotFoundException() {
        Long eventId = 1L;
        // when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            knockoutStageService.addKnockoutStage(eventId);
        });

        verify(knockoutStageRepository, never()).save(any(KnockoutStage.class));
    }

    @Test
    public void getKnockoutStage_ValidId_ReturnsKnockoutStage() {
        Long knockoutStageId = 1L;
        KnockoutStage knockoutStage = new KnockoutStage();
        when(knockoutStageRepository.findById(knockoutStageId)).thenReturn(Optional.of(knockoutStage));

        KnockoutStage result = knockoutStageService.getKnockoutStage(knockoutStageId);

        assertNotNull(result);
        assertEquals(knockoutStage, result);
        verify(knockoutStageRepository, times(1)).findById(knockoutStageId);
    }

    @Test
    public void getKnockoutStage_NullId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            knockoutStageService.getKnockoutStage(null);
        });
    }

    @Test
    public void getKnockoutStage_NonExistentId_ThrowsKnockoutStageNotFoundException() {
        Long knockoutStageId = 1L;
        when(knockoutStageRepository.findById(knockoutStageId)).thenReturn(Optional.empty());

        assertThrows(KnockoutStageNotFoundException.class, () -> {
            knockoutStageService.getKnockoutStage(knockoutStageId);
        });

        verify(knockoutStageRepository, times(1)).findById(knockoutStageId);
    }

    @Test
    public void updateKnockoutStage_ValidInput_UpdatesAndReturnsKnockoutStage() {
        Long eventId = 1L;
        Long knockoutStageId = 1L;
        Event event = createValidEvent();
        KnockoutStage existingKnockoutStage = createValidKnockoutStage(event);
        KnockoutStage newKnockoutStage = new KnockoutStage();
        newKnockoutStage.setEvent(event);
        newKnockoutStage.setMatches(new ArrayList<>());

        when(knockoutStageRepository.findById(knockoutStageId)).thenReturn(Optional.of(existingKnockoutStage));
        when(knockoutStageRepository.save(existingKnockoutStage)).thenReturn(existingKnockoutStage);

        KnockoutStage updatedKnockoutStage = knockoutStageService.updateKnockoutStage(eventId, knockoutStageId, newKnockoutStage);

        assertNotNull(updatedKnockoutStage);
        verify(knockoutStageRepository, times(1)).save(existingKnockoutStage);
    }

    @Test
    public void updateKnockoutStage_NonExistentId_ThrowsKnockoutStageNotFoundException() {
        Long eventId = 1L;
        Long knockoutStageId = 1L;
        KnockoutStage newKnockoutStage = new KnockoutStage();

        when(knockoutStageRepository.findById(knockoutStageId)).thenReturn(Optional.empty());

        assertThrows(KnockoutStageNotFoundException.class, () -> {
            knockoutStageService.updateKnockoutStage(eventId, knockoutStageId, newKnockoutStage);
        });
    }

    @Test
    public void deleteKnockoutStage_ValidId_DeletesKnockoutStage() {
        Long eventId = 1L;
        Long knockoutStageId = 1L;
        Event event = createValidEvent();
        KnockoutStage knockoutStage = createValidKnockoutStage(event);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(knockoutStageRepository.findById(knockoutStageId)).thenReturn(Optional.of(knockoutStage));

        knockoutStageService.deleteKnockoutStage(eventId, knockoutStageId);

        verify(knockoutStageRepository, times(1)).delete(knockoutStage);
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void deleteKnockoutStage_NonExistentId_ThrowsKnockoutStageNotFoundException() {
        Long eventId = 1L;
        Long knockoutStageId = 1L;
        
        when(knockoutStageRepository.findById(knockoutStageId)).thenReturn(Optional.empty());

        assertThrows(KnockoutStageNotFoundException.class, () -> {
            knockoutStageService.deleteKnockoutStage(eventId, knockoutStageId);
        });
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setRankings(new TreeSet<>());
        event.setGroupStages(new ArrayList<>());
        event.setKnockoutStages(new ArrayList<>());
        event.setTournament(createValidTournament()); // Assuming there's a method to create a valid tournament
        return event;
    }

    private Tournament createValidTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Spring Championship");
        return tournament;
    }

    private KnockoutStage createValidKnockoutStage(Event event) {
        return KnockoutStage.builder()
            .id(1L)
            .event(event)
            .matches(new ArrayList<>())  // Initialize an empty list for matches
            .build();
    }
}
