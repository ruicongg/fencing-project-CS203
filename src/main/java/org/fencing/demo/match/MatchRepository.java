package org.fencing.demo.match;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Long> {

    Set<Match> findByEventId(Long eventId);

    void deleteByEventIdAndId(Long eventId, Long matchId);

    @Query("SELECT m FROM Match m WHERE m.player1.id = :playerId OR m.player2.id = :playerId")
    List<Match> findMatchesByPlayerId(@Param("playerId") Long playerId);
}
