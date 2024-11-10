package org.fencing.demo.tournament;

import java.time.LocalDate;
import java.util.List;

public interface TournamentService {

    Tournament addTournament(Tournament tournament);

    List<Tournament> listTournaments();

    Tournament getTournament(Long id);

    Tournament updateTournament(Long id, Tournament tournament);

    void deleteTournament(Long id);

    List<Tournament> findByAvailability(LocalDate startDate, LocalDate endDate);
}
