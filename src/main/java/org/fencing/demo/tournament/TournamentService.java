package org.fencing.demo.tournament;

import java.util.List;
import java.time.LocalDate;

public interface TournamentService {

    Tournament addTournament(Tournament tournament);

    List<Tournament> listTournaments();

    Tournament getTournament(Long id);

    Tournament updateTournament(Long id, Tournament tournament);

    void deleteTournament(Long id);

    List<Tournament> findByAvailability(LocalDate startDate, LocalDate endDate);
}
