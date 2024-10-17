package org.fencing.demo.tournament;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {

    List<Tournament> findByTournamentStartDateLessThanEqualAndTournamentEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

}
