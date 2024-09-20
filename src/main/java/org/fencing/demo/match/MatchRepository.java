package org.fencing.demo.match;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentId(Long tournamentId);
    void deleteByTournamentIdAndId(Long tournamentId, Long matchId);
}
