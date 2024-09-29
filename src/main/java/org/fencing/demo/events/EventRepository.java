package org.fencing.demo.events;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.fencing.demo.stages.*;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByTournamentId(Long tournamentId);

    void deleteByTournamentIdAndId(Long tournamentId, Long eventId);

    KnockoutStage findKnockoutStageById(Long eventId);

    @Query("SELECT e FROM Event e JOIN e.rankings pr WHERE pr.player.id = :playerId")
    List<Event> findEventsByPlayerId(@Param("playerId") Long playerId);
}
