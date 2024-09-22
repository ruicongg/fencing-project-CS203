package org.fencing.demo.tournaments;

import java.util.List;
import java.time.LocalDate;

public interface TournamentService {

    Tournament addTournament(Tournament tournament);

    List<Tournament> listTournaments();

    Tournament getTournament(Long id);

    // ! might want to remove
    // boolean doesTournamentExist(Long id);

    Tournament updateTournament(Long id, Tournament tournament);

    void deleteTournament(Long id);

    List<Tournament> findByStartDateTournament(LocalDate date);

    List<Tournament> findByEndDateTournament(LocalDate date);

    List<Tournament> findByAvailability(LocalDate startDate, LocalDate endDate);
}
