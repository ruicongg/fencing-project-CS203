package org.fencing.demo.player;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
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
    private PasswordEncoder passwordEncoder;

    public PlayerServiceImpl(PlayerRepository playerRepository, EventRepository eventRepository, MatchRepository matchRepository, PasswordEncoder passwordEncoder){
        this.playerRepository = playerRepository;
        this.eventRepository = eventRepository;
        this.matchRepository = matchRepository;
        this.passwordEncoder = passwordEncoder;
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
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        return playerRepository.save(player);
    }
    @Override
    public Player updatePlayer(Long id, Player player) {
        // Find the existing player by id
        Optional<Player> existingPlayer = playerRepository.findById(id);

        if (existingPlayer.isPresent()) {
            Player updatedPlayer = existingPlayer.get();

            // Update the fields of the existing player with the new player data
            updatedPlayer.setUsername(player.getUsername());
            updatedPlayer.setPassword(passwordEncoder.encode(player.getPassword()));
            updatedPlayer.setEmail(player.getEmail()); 
            updatedPlayer.setElo(player.getElo());
            
            // Save the updated player
            return playerRepository.save(updatedPlayer);
        } else {
            // Return null or throw an exception if player with given id is not found
            return null;
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

    @Override
    public Player login(String username, String password) throws InvalidCredentialsException {
        Optional<Player> player = playerRepository.findByUsername(username);
        if (player.isPresent() && passwordEncoder.matches(password, player.get().getPassword())) {
            return player.get();
        } else {    
            throw new InvalidCredentialsException("Invalid username or password");
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
