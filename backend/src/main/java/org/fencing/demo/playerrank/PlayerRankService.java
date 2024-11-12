package org.fencing.demo.playerrank;

import java.util.List;

public interface PlayerRankService {
    List<PlayerRank> getAllPlayerRanksForEvent(Long eventId);
    List<PlayerRank> getAllPlayerRanksForPlayer(String username);
}
