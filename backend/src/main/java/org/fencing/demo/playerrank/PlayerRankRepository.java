package org.fencing.demo.playerrank;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRankRepository extends JpaRepository<PlayerRank, Long> {
    List<PlayerRank> findByEventId(Long eventId);
    List<PlayerRank> findByPlayerId(Long playerId);
}
