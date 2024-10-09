package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.tournament.TournamentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.HashSet;
import org.fencing.demo.tournament.TournamentNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {
    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;


    @Test
    public void addTournamentAndShouldReturnSavedTournament() {
        Tournament tournament = Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.of(2023, 1, 1))
                .registrationEndDate(LocalDate.of(2023, 1, 31))
                .tournamentStartDate(LocalDate.of(2023, 2, 15))
                .tournamentEndDate(LocalDate.of(2023, 2, 20))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();

        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Tournament result = tournamentService.addTournament(tournament);

        assertNotNull(result);
        assertEquals("Spring Championship", result.getName());
        verify(tournamentRepository, times(1)).save(tournament);
    }
    // ! do i need to test duplicate tournament name?

    @Test
    public void getMissingTournamentAndShouldReturnNotFound() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.getTournament(1L);
        });

        assertEquals("Could not find Tournament 1", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(1L);
    }
    

    @Test
    public void updateTournamentAndShouldReturnUpdatedTournament() {
        // Arrange
        Long tournamentId = 1L;
        Tournament existingTournament = Tournament.builder()
                .id(tournamentId)
                .name("Old Name")
                .tournamentStartDate(LocalDate.of(2023, 2, 15))
                .tournamentEndDate(LocalDate.of(2023, 2, 20))
                .registrationStartDate(LocalDate.of(2023, 1, 1))
                .registrationEndDate(LocalDate.of(2023, 1, 31))
                .venue("Old Venue")
                .build();

        Tournament newTournament = Tournament.builder()
                .name("New Name")
                .tournamentStartDate(LocalDate.of(2023, 3, 15))
                .tournamentEndDate(LocalDate.of(2023, 3, 20))
                .registrationStartDate(LocalDate.of(2023, 2, 1))
                .registrationEndDate(LocalDate.of(2023, 2, 28))
                .venue("New Venue")
                .build();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(existingTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(existingTournament);

        // Act
        Tournament updatedTournament = tournamentService.updateTournament(tournamentId, newTournament);

        // Assert
        assertNotNull(updatedTournament);
        assertEquals("New Name", updatedTournament.getName());
        assertEquals(LocalDate.of(2023, 3, 15), updatedTournament.getTournamentStartDate());
        assertEquals(LocalDate.of(2023, 3, 20), updatedTournament.getTournamentEndDate());
        assertEquals(LocalDate.of(2023, 2, 1), updatedTournament.getRegistrationStartDate());
        assertEquals(LocalDate.of(2023, 2, 28), updatedTournament.getRegistrationEndDate());
        assertEquals("New Venue", updatedTournament.getVenue());
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(existingTournament);
    }

    @Test
    public void updateNullTournamentAndShouldThrowAnException() {
        // Arrange
        Long tournamentId = 1L;
        Tournament newTournament = new Tournament();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.updateTournament(tournamentId, newTournament);
        });
        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

}