package org.fencing.demo.tournaments;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.time.LocalDate;


public interface TournamentRepository extends CrudRepository<Tournament, Long> {
    public List<Tournament> findByStartDate(LocalDate startDate);

    public List<Tournament> findByEndDate(LocalDate endDate);

    public List<Tournament> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);
}
