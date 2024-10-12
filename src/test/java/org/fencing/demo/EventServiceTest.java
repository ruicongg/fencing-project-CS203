package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.EventServiceImpl;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.PlayerRankComparator;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentNotFoundException;
import org.fencing.demo.tournament.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    
    @Mock
    private EventRepository eventRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event validEvent;
    private Tournament validTournament;
    
    @BeforeEach
    void setUp() {
        validEvent = createValidEvent();
        validTournament = createValidTournament();
    }

    @Test
    public void addEvent_ValidEvent_ReturnsSavedEvent() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(validTournament));
        when(eventRepository.save(any(Event.class))).thenReturn(validEvent);

        Event result = eventService.addEvent(1L, validEvent);

        assertNotNull(result);
        assertEquals("Spring Championship", result.getTournament().getName());
        verify(eventRepository, times(1)).save(validEvent);
    }

    @Test
    public void addEvent_NullTournamentId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventService.addEvent(null, validEvent));
    }

    @Test
    public void addEvent_TournamentNotFound_ThrowsTournamentNotFoundException() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> eventService.addEvent(1L, validEvent));
    }

    @Test
    public void getAllEventsByTournamentId_ValidId_ReturnsListOfEvents() {
        when(tournamentRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.findByTournamentId(1L)).thenReturn(List.of(validEvent));

        List<Event> events = eventService.getAllEventsByTournamentId(1L);

        assertEquals(1, events.size());
        verify(eventRepository, times(1)).findByTournamentId(1L);
    }

    @Test
    public void getAllEventsByTournamentId_TournamentNotFound_ThrowsTournamentNotFoundException() {
        when(tournamentRepository.existsById(1L)).thenReturn(false);

        assertThrows(TournamentNotFoundException.class, () -> eventService.getAllEventsByTournamentId(1L));
    }

    @Test
    public void getEvent_ValidId_ReturnsEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));

        Event result = eventService.getEvent(1L);

        assertNotNull(result);
        assertEquals(validEvent.getId(), result.getId());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    public void getEvent_NonExistingId_ThrowsEventNotFoundException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEvent(1L));
    }

    @Test
    public void updateEvent_ValidEvent_ReturnsUpdatedEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(validEvent);

        validEvent.setGender(Gender.FEMALE);
        validEvent.setWeapon(WeaponType.SABER);

        Event updatedEvent = eventService.updateEvent(1L, 1L, validEvent);

        assertNotNull(updatedEvent);
        assertEquals(Gender.FEMALE, updatedEvent.getGender());
        assertEquals(WeaponType.SABER, updatedEvent.getWeapon());
        verify(eventRepository, times(1)).save(validEvent);
    }

    @Test
    public void updateEvent_InvalidEventDates_ThrowsIllegalArgumentException() {
        Event invalidEvent = createValidEvent();
        invalidEvent.setStartDate(LocalDateTime.now().minusDays(1)); // Invalid start date

        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));

        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(1L, 1L, invalidEvent));
    }

    @Test
    public void updateEvent_TournamentChanged_ThrowsIllegalArgumentException() {
        Event newEvent = createValidEvent();
        Tournament anotherTournament = createValidTournament();
        anotherTournament.setId(2L);
        newEvent.setTournament(anotherTournament);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));

        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(1L, 1L, newEvent));
    }

    @Test
    public void deleteEvent_ValidIds_SuccessfullyDeletesEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));
    
        eventService.deleteEvent(1L, 1L);
    
        verify(eventRepository, times(1)).deleteByTournamentIdAndId(1L, 1L);
    }

    @Test
    public void deleteEvent_NonExistingEvent_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L, 1L));

        // No interaction with delete method should occur
        verify(eventRepository, never()).deleteByTournamentIdAndId(anyLong(), anyLong());
    }


    @Test
    public void addPlayerToEvent_ValidIds_ReturnsUpdatedEvent() {
        Player player = createValidPlayer();
        
        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(eventRepository.save(any(Event.class))).thenReturn(validEvent);

        Event updatedEvent = eventService.addPlayerToEvent(1L, 1L);

        assertEquals(1, updatedEvent.getRankings().size());
        verify(eventRepository, times(1)).save(validEvent);
    }

    @Test
    public void addPlayerToEvent_InvalidEvent_ThrowsEventNotFoundException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.addPlayerToEvent(1L, 1L));
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setStartDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        event.setEndDate(LocalDateTime.of(2025, 1, 2, 18, 0));
        event.setGender(Gender.MALE);
        event.setWeapon(WeaponType.FOIL);
        event.setRankings(new TreeSet<>(new PlayerRankComparator()));
        event.setGroupStages(new ArrayList<>());
        event.setKnockoutStages(new ArrayList<>());
        event.setTournament(createValidTournament());
        return event;
    }

    private Tournament createValidTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Spring Championship");
        return tournament;
    }

    private Player createValidPlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("Player1");
        player.setEmail("player1@example.com");
        player.setPassword("securePassword1");
        player.setElo(1700);
        player.setMatchesAsPlayer1(new HashSet<>());
        player.setMatchesAsPlayer2(new HashSet<>());
        return player;
    }
}

