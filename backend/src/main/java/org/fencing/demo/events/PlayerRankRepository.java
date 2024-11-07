package org.fencing.demo.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerRankRepository extends JpaRepository<PlayerRank, Long> {
    List<PlayerRank> findByPlayerId(Long playerId);
}