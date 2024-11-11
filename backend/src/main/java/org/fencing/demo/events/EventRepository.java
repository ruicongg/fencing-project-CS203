package org.fencing.demo.events;

import java.util.List;
import java.util.Optional;

import org.fencing.demo.knockoutstage.KnockoutStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByTournamentId(Long tournamentId);

    void deleteByTournamentIdAndId(Long tournamentId, Long id);

    KnockoutStage findKnockoutStageById(Long eventId);

    @Query("SELECT e FROM Event e JOIN e.rankings pr WHERE pr.player.id = :playerId")
    List<Event> findEventsByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT e FROM Event e JOIN e.rankings pr WHERE pr.player.username = :username")
    List<Event> findEventsByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT e FROM Event e " + 
           "LEFT JOIN FETCH e.matches WHERE e.id = :id")
    Optional<Event> findByIdWithMatches(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Event e " + 
           "LEFT JOIN FETCH e.groupStages WHERE e.id = :id")
    Optional<Event> findByIdWithGroupStages(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Event e " + 
           "LEFT JOIN FETCH e.knockoutStages WHERE e.id = :id")
    Optional<Event> findByIdWithKnockoutStages(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Event e " + 
           "LEFT JOIN FETCH e.rankings WHERE e.id = :id")
    Optional<Event> findByIdWithRankings(@Param("id") Long id);
}
