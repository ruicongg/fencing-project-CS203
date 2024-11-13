package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.events.Gender;
import org.fencing.demo.events.WeaponType;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerNotFoundException;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.playerrank.*;
import org.fencing.demo.tournament.Tournament;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerRankServiceTest {

    @Mock
    private PlayerRankRepository playerRankRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private PlayerRankServiceImpl playerRankService;

    @Test
    void getAllPlayerRanksForEvent_eventExists_returnsPlayerRanks() {
        // Arrange
        Event event = createValidEvent();
        Player player1 = createValidPlayer(1);
        Player player2 = createValidPlayer(2);
        PlayerRank playerRank1 = createPlayerRank(1, player1, event);
        PlayerRank playerRank2 = createPlayerRank(2, player2, event);

        // Use the actual `event` instance here to match the expected return type
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(playerRankRepository.findByEventId(event.getId())).thenReturn(Arrays.asList(playerRank1, playerRank2));

        // Act
        List<PlayerRank> result = playerRankService.getAllPlayerRanksForEvent(event.getId());

        // Assert
        assertEquals(2, result.size());
        assertEquals(playerRank1, result.get(0));
        assertEquals(playerRank2, result.get(1));
    }

    @Test
    void getAllPlayerRanksForEvent_eventDoesNotExist_throwsEventNotFoundException() {
        // Arrange
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> playerRankService.getAllPlayerRanksForEvent(eventId));
    }

    @Test
    void getAllPlayerRanksForPlayer_playerExists_returnsPlayerRanks() {
        // Arrange
        String username = "testUser";
        Event event = createValidEvent();
        Player player = createValidPlayer(1);
        PlayerRank playerRank1 = createPlayerRank(1, player, event);
        PlayerRank playerRank2 = createPlayerRank(2, player, event);

        when(playerRepository.findByUsername(username)).thenReturn(Collections.singletonList(player));
        when(playerRankRepository.findByPlayerId(player.getId())).thenReturn(Arrays.asList(playerRank1, playerRank2));

        // Act
        List<PlayerRank> result = playerRankService.getAllPlayerRanksForPlayer(username);

        // Assert
        assertEquals(2, result.size());
        assertEquals(playerRank1, result.get(0));
        assertEquals(playerRank2, result.get(1));
    }

    @Test
    void getAllPlayerRanksForPlayer_playerDoesNotExist_throwsPlayerNotFoundException() {
        // Arrange
        String username = "unknownUser";
        when(playerRepository.findByUsername(username)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(PlayerNotFoundException.class, () -> playerRankService.getAllPlayerRanksForPlayer(username));
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
        event.setTournament(createValidTournament());
        return event;
    }

    private Tournament createValidTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Spring Championship");
        return tournament;
    }

    private Player createValidPlayer(int id) {
        Player player = new Player();
        player.setId((long) id);
        player.setUsername("Player" + id);
        player.setElo(1700);
        return player;
    }

    private PlayerRank createPlayerRank(int id, Player player, Event event) {
        // Adjusted to match the actual constructor of `PlayerRank`
        return new PlayerRank((long) id, player, event, 0, 0, 0, 0);
    }
}
