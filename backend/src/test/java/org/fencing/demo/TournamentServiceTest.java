package org.fencing.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
//import org.mockito.MockitoAnnotations;
import java.util.Optional;

import org.fencing.demo.tournament.*;
import org.fencing.demo.tournament.Tournament;
import org.fencing.demo.tournament.TournamentRepository;
import org.fencing.demo.tournament.TournamentServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {
    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    // Add Tournament tests
    @Test
    public void addTournament_ValidTournament_ReturnsSavedTournament() {
        Tournament tournament = createValidTournament();
        when(tournamentRepository.save(tournament)).thenReturn(tournament);

        Tournament result = tournamentService.addTournament(tournament);

        assertNotNull(result);
        assertEquals("Spring Championship", result.getName());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    public void addTournament_ExistingName_ThrowsDataIntegrityViolationException() {

        // assuming this tournament already exists in the database
        Tournament existingTournament = createValidTournament();


        when(tournamentRepository.save(any(Tournament.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            tournamentService.addTournament(existingTournament);
        });

        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    public void addTournament_TournamentStartDateBeforeToday_ThrowsIllegalArgumentException() {
        Tournament tournament = createValidTournament();
        tournament.setTournamentStartDate(LocalDate.now().minusDays(1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.addTournament(tournament);
        });

        assertEquals("Tournament start date must be after today", exception.getMessage());
    }

    @Test
    public void addTournament_TournamentEndDateBeforeStartDate_ThrowsIllegalArgumentException() {
        Tournament tournament = createValidTournament();
        tournament.setTournamentStartDate(LocalDate.now().plusDays(2));
        tournament.setTournamentEndDate(LocalDate.now().plusDays(1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tournamentService.addTournament(tournament);
        });

        assertEquals("Tournament end date must be after start date", exception.getMessage());
    }

    @Test
    public void addTournament_RegistrationStartDateInPast_CallsRepositoryAndThrowsException() {
        Tournament tournament = createValidTournament();
        tournament.setRegistrationStartDate(LocalDate.now().minusDays(1));

        when(tournamentRepository.save(any(Tournament.class)))
            .thenThrow(new DataIntegrityViolationException("Registration start date must be in the present or future"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            tournamentService.addTournament(tournament);
        });

        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    public void addTournament_RegistrationEndDateNotInFuture_CallsRepositoryAndThrowsException() {
        Tournament tournament = createValidTournament();
        tournament.setRegistrationEndDate(LocalDate.now());

        when(tournamentRepository.save(any(Tournament.class)))
            .thenThrow(new DataIntegrityViolationException("Registration end date must be in the future"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            tournamentService.addTournament(tournament);
        });

        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    public void addTournament_ValidDates_Succeeds() {
        Tournament tournament = createValidTournament();
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.addTournament(tournament);

        assertNotNull(result);
        verify(tournamentRepository, times(1)).save(tournament);
    }

    // Get Tournament tests
    @Test
    public void getTournament_NonExistingId_ThrowsTournamentNotFoundException() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.getTournament(1L);
        });

        assertEquals("Could not find Tournament 1", exception.getMessage());
        verify(tournamentRepository, times(1)).findById(1L);
    }

    // Update Tournament tests
    @Test
    public void updateTournament_ExistingTournament_ReturnsUpdatedTournament() {
        Long tournamentId = 1L;
        Tournament existingTournament = createValidTournament();
        existingTournament.setId(tournamentId);

        Tournament newTournament = createValidTournament();
        newTournament.setName("New Name");

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(existingTournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(existingTournament);

        Tournament updatedTournament = tournamentService.updateTournament(tournamentId, newTournament);

        assertNotNull(updatedTournament);
        assertEquals("New Name", updatedTournament.getName());
    }

    @Test
    public void updateTournament_NonExistingTournament_ThrowsTournamentNotFoundException() {
        Long tournamentId = 1L;
        Tournament newTournament = createValidTournament();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class,
                () -> tournamentService.updateTournament(tournamentId, newTournament));
    }

    @Test
    public void updateTournament_InvalidDates_ThrowsIllegalArgumentException() {
        Long tournamentId = 1L;
        Tournament existingTournament = createValidTournament();
        existingTournament.setId(tournamentId);

        Tournament newTournament = createValidTournament();
        newTournament.setTournamentStartDate(LocalDate.of(2023, 1, 1));
        newTournament.setTournamentEndDate(LocalDate.of(2022, 12, 31));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(existingTournament));

        assertThrows(IllegalArgumentException.class,
                () -> tournamentService.updateTournament(tournamentId, newTournament));

        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    // List Tournaments test
    @Test
    public void listTournaments_MultipleTournaments_ReturnsListOfTournaments() {
        List<Tournament> tournaments = Arrays.asList(createValidTournament(), createValidTournament());
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<Tournament> result = tournamentService.listTournaments();

        assertEquals(2, result.size());
        verify(tournamentRepository, times(1)).findAll();
    }

    // Find By Availability tests
    @Test
    public void findByAvailability_ValidDateRange_ReturnsTournaments() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<Tournament> tournaments = Arrays.asList(createValidTournament());

        when(tournamentRepository.findByTournamentStartDateLessThanEqualAndTournamentEndDateGreaterThanEqual(
                endDate, startDate)).thenReturn(tournaments);

        List<Tournament> result = tournamentService.findByAvailability(startDate, endDate);

        assertEquals(1, result.size());
        verify(tournamentRepository, times(1))
                .findByTournamentStartDateLessThanEqualAndTournamentEndDateGreaterThanEqual(endDate, startDate);
    }

    @Test
    public void findByAvailability_InvalidDateRange_ThrowsIllegalArgumentException() {
        LocalDate startDate = LocalDate.of(2023, 2, 28);
        LocalDate endDate = LocalDate.of(2023, 2, 1);

        assertThrows(IllegalArgumentException.class, () -> tournamentService.findByAvailability(startDate, endDate));
    }

    // Helper method
    private Tournament createValidTournament() {
        return Tournament.builder()
                .name("Spring Championship")
                .registrationStartDate(LocalDate.now().plusDays(1))
                .registrationEndDate(LocalDate.now().plusDays(30))
                .tournamentStartDate(LocalDate.now().plusDays(60))
                .tournamentEndDate(LocalDate.now().plusDays(65))
                .venue("Sports Arena")
                .events(new HashSet<>())
                .build();
    }
}
