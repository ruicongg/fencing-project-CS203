package org.fencing.demo.player;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fencing.demo.events.Event;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.match.Match;
import org.fencing.demo.match.MatchRepository;
import org.fencing.demo.tournament.Tournament;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService{
    private PlayerRepository playerRepository;
    private EventRepository eventRepository;
    private MatchRepository matchRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, EventRepository eventRepository, MatchRepository matchRepository, PasswordEncoder passwordEncoder){
        this.playerRepository = playerRepository;
        this.eventRepository = eventRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public List<Player> listPlayers(){
        return playerRepository.findAll();
    }

    @Override
    public Player getPlayer(Long id){
        return playerRepository.findById(id).orElse(null);
    }
    @Override
    public Player addPlayer(Player player){
        return playerRepository.save(player);
    }
    @Override
    public Player updatePlayer(Long id, Player player) {
        // Find the existing player by id
        Optional<Player> existingPlayer = playerRepository.findById(id);

        if (existingPlayer.isPresent()) {
            Player updatedPlayer = existingPlayer.get();

            // Update the fields of the existing player with the new player data 
            updatedPlayer.setElo(player.getElo());
            
            // Save the updated player
            return playerRepository.save(updatedPlayer);
        } else {
            // Return null or throw an exception if player with given id is not found
            throw new PlayerNotFoundException(id);
        }
    }
    @Override
    public void deletePlayer(Long id){
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            playerRepository.delete(player.get());
        } else {
            // Handle the case where the player does not exist
            throw new IllegalArgumentException("Player with id " + id + " does not exist");
        }
    }

    // Get all tournaments a player participated in
    public List<Tournament> findTournamentsByPlayer(Long playerId) {
        List<Event> events = eventRepository.findEventsByPlayerId(playerId);
        
        // Extract unique tournaments from the events
        return events.stream()
                     .map(Event::getTournament) // Get the tournament associated with each event
                     .distinct() // Ensure unique tournaments
                     .collect(Collectors.toList());
    }

    // Get all events a player participated in
    public List<Event> findEventsByPlayer(Long playerId) {
        return eventRepository.findEventsByPlayerId(playerId);
    }

    // Get all wins for the player
    public List<Match> getWonMatches(Long playerId) {
        List<Match> matches = matchRepository.findMatchesByPlayerId(playerId);

        return matches.stream()
            .filter(match -> match.getWinner().getId().equals(playerId)) // Check if the player is the winner
            .collect(Collectors.toList());
    }

    // Get all losses for the player
    public List<Match> getLostMatches(Long playerId) {
        List<Match> matches = matchRepository.findMatchesByPlayerId(playerId);

        return matches.stream()
            .filter(match -> !match.getWinner().getId().equals(playerId)) // Check if the player is not the winner
            .collect(Collectors.toList());
    }

}
