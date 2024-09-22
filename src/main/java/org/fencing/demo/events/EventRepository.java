package org.fencing.demo.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByTournamentId(Long tournamentId);
    void deleteByTournamentIdAndId(Long tournamentId, Long eventId);
}
