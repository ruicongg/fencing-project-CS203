package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.EventServiceImpl;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.player.PlayerNotFoundException;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentNotFoundException;
import org.fencing.demo.tournament.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

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

    @Test
    public void addEvent_ValidEvent_ReturnsSavedEvent() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(validTournament));
        when(eventRepository.save(any(Event.class))).thenReturn(validEvent);

        Event result = eventService.addEvent(1L, validEvent);

        assertNotNull(result);
        assertEquals("Spring Championship", result.getTournament().getName());
        verify(eventRepository, times(1)).save(validEvent);
    }

    @Test
    public void addEvent_NullTournamentId_ThrowsIllegalArgumentException() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

        assertThrows(IllegalArgumentException.class, () -> eventService.addEvent(null, validEvent));
    }

    @Test
    public void addEvent_TournamentNotFound_ThrowsTournamentNotFoundException() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> eventService.addEvent(1L, validEvent));
    }

    @Test
    public void getAllEventsByTournamentId_ValidId_ReturnsListOfEvents() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

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
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

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
    public void updateEvent_ExistingEvent_ReturnsUpdatedEvent() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

        Event newEvent = createValidEvent(validTournament);
        newEvent.setGender(Gender.FEMALE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(validEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(validEvent);

        Event updatedEvent = eventService.updateEvent(tournamentId, eventId, newEvent);

        assertNotNull(updatedEvent);
        assertEquals(Gender.FEMALE, updatedEvent.getGender());
    }

    @Test
    public void updateEvent_InvalidEventDates_ThrowsIllegalArgumentException() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

        Event newEvent = createValidEvent(validTournament);
        newEvent.setStartDate(LocalDateTime.now().minusDays(1)); // Invalid start date

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(validEvent));

        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(1L, 1L, newEvent));
    }

    @Test
    public void updateEvent_TournamentChanged_ThrowsIllegalArgumentException() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

        Long newTournamentId = 2L;
        Tournament newTournament = createValidTournament();
        newTournament.setId(newTournamentId);

        Event newEvent = createValidEvent(validTournament);
        newEvent.setTournament(newTournament);
        // System.out.println(validEvent);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(validEvent));

        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(1L, 1L, newEvent));
    }

    @Test
    public void deleteEvent_ValidIds_SuccessfullyDeletesEvent() {
        Long tournamentId = 1L;
        Tournament validTournament = createValidTournament();
        validTournament.setId(tournamentId);

        Long eventId = 1L;
        Event validEvent = createValidEvent(validTournament);
        validEvent.setId(eventId);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(validEvent));

        eventService.deleteEvent(1L, 1L);

        verify(eventRepository, times(1)).deleteByTournamentIdAndId(1L, 1L);
    }

    @Test
    public void deleteEvent_NonExistingEvent_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L, 1L));

        verify(eventRepository, never()).deleteByTournamentIdAndId(anyLong(), anyLong());
    }

    @Test
    public void addPlayerToEvent_ValidUsernameAndEvent_ReturnsUpdatedEvent() {
        Long eventId = 1L;
        Event event = createValidEvent(createValidTournament());
        Player player = createValidPlayer();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(playerRepository.findByUsername(player.getUsername())).thenReturn(List.of(player));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updatedEvent = eventService.addPlayerToEvent(eventId, player.getUsername());

        assertNotNull(updatedEvent);
        assertEquals(1, updatedEvent.getRankings().size());
        verify(eventRepository).save(event);
    }

    @Test
    public void addPlayerToEvent_PlayerNotFound_ThrowsPlayerNotFoundException() {
        Long eventId = 1L;
        String username = "nonexistentUser";
        Event event = createValidEvent(createValidTournament());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(playerRepository.findByUsername(username)).thenReturn(List.of());

        assertThrows(PlayerNotFoundException.class, () -> eventService.addPlayerToEvent(eventId, username));
    }

    @Test
    public void addPlayerToEvent_InvalidEvent_ThrowsEventNotFoundException() {
        Player player = createValidPlayer();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.addPlayerToEvent(1L, "Player1"));
    }

    @Test
    public void removePlayerFromEvent_Success() {
        Long eventId = 1L;
        Tournament tournament = createValidTournament();
        Event event = createValidEvent(tournament);
        Player player = createValidPlayer();
        PlayerRank playerRank = new PlayerRank();
        playerRank.setPlayer(player);
        playerRank.setEvent(event);
        event.getRankings().add(playerRank);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updatedEvent = eventService.removePlayerFromEvent(eventId, player.getUsername());

        assertTrue(updatedEvent.getRankings().isEmpty());
        verify(eventRepository).save(event);
    }

    @Test
    public void removePlayerFromEvent_OutsideRegistrationPeriod_ThrowsIllegalStateException() {
        Long eventId = 1L;
        Tournament tournament = createValidTournament();
        tournament.setRegistrationEndDate(LocalDate.now().minusDays(1));
        Event event = createValidEvent(tournament);
        Player player = createValidPlayer();
        PlayerRank playerRank = new PlayerRank();
        playerRank.setPlayer(player);
        playerRank.setEvent(event);
        event.getRankings().add(playerRank);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalStateException.class,
                () -> eventService.removePlayerFromEvent(eventId, player.getUsername()));

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void removePlayerFromEvent_PlayerNotInEvent_ThrowsIllegalStateException() {
        Long eventId = 1L;
        Tournament tournament = createValidTournament();
        Event event = createValidEvent(tournament);
        Player player = createValidPlayer();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalStateException.class,
                () -> eventService.removePlayerFromEvent(eventId, player.getUsername()));

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void adminRemovesPlayerFromEvent_Success() {
        Long eventId = 1L;
        Tournament tournament = createValidTournament();
        Event event = createValidEvent(tournament);
        Player player = createValidPlayer();
        PlayerRank playerRank = new PlayerRank();
        playerRank.setPlayer(player);
        playerRank.setEvent(event);
        event.getRankings().add(playerRank);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updatedEvent = eventService.adminRemovesPlayerFromEvent(eventId, player.getUsername());

        assertTrue(updatedEvent.getRankings().isEmpty());
        verify(eventRepository).save(event);
    }

    @Test
    public void adminRemovesPlayerFromEvent_PlayerNotInEvent_ThrowsIllegalStateException() {
        Long eventId = 1L;
        Tournament tournament = createValidTournament();
        Event event = createValidEvent(tournament);
        Player player = createValidPlayer();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalStateException.class,
                () -> eventService.adminRemovesPlayerFromEvent(eventId, player.getUsername()));

        verify(eventRepository, never()).save(any(Event.class));
    }

    private Tournament createValidTournament() {
        return Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.now())
                .registrationEndDate(LocalDate.now().plusDays(20))
                .tournamentStartDate(LocalDate.now().plusDays(25))
                .tournamentEndDate(LocalDate.now().plusDays(30))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
    }

    private Event createValidEvent(Tournament tournament) {
        return Event.builder()
                .tournament(tournament)
                .gender(Gender.MALE)
                .weapon(WeaponType.FOIL)
                .startDate(LocalDateTime.now().plusDays(25))
                .endDate(LocalDateTime.now().plusDays(26))
                .build();
    }

    private Player createValidPlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setUsername("Player1");
        player.setEmail("player1@example.com");
        player.setPassword("securePassword1");
        player.setGender(Gender.MALE);
        player.setElo(1700);
        player.setMatchesAsPlayer1(new HashSet<>());
        player.setMatchesAsPlayer2(new HashSet<>());
        return player;
    }
}
