package org.fencing.demo.tournaments;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.time.LocalDate;


public interface TournamentRepository extends CrudRepository<Tournament, Long> {
    public List<Tournament> findByDate(LocalDate date);
}
