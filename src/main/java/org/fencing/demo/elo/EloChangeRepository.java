package org.fencing.demo.elo;

import org.fencing.demo.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EloChangeRepository extends JpaRepository<EloChange, Long> {

    List<EloChange> findByEventAndIsAppliedFalse(Event event);
    List<EloChange> findByEvent(Event event);
    List<EloChange> findByPlayerId(Long playerId);
}
