package org.fencing.demo.events;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import org.fencing.demo.stages.*;

public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByTournamentId(Long tournamentId);
    void deleteByTournamentIdAndId(Long tournamentId, Long eventId);
    KnockoutStage getKnockoutStage();
}
