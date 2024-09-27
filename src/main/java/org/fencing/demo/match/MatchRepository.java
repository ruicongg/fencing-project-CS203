package org.fencing.demo.match;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Set<Match> findByEventId(Long eventId);
    void deleteByEventIdAndId(Long eventId, Long matchId);
}
