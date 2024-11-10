package org.fencing.demo.tournament;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {

    List<Tournament> findByTournamentStartDateLessThanEqualAndTournamentEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

}
