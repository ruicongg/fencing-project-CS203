package org.fencing.demo.player;

import java.util.List;

import org.fencing.demo.events.Event;
import org.fencing.demo.match.Match;
import org.fencing.demo.tournament.Tournament;


public interface PlayerService {
    List<Player> listPlayers();
    Player getPlayer(Long id);
    Player addPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    void deletePlayer(Long id);
    
    List<Tournament> findTournamentsByPlayer(Long playerId);
    List<Event> findEventsByPlayer(Long playerId);
    List<Match> getWonMatches(Long playerId);
    List<Match> getLostMatches(Long playerId);
}
