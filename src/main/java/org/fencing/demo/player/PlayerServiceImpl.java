package org.fencing.demo.player;
import java.util.List;
import java.util.Optional;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService{
    private PlayerRepository players;
    private PasswordEncoder passwordEncoder;

    public PlayerServiceImpl(PlayerRepository players){
        this.players = players;
    }

    @Override
    public List<Player> listPlayers(){
        return players.findAll();
    }

    @Override
    public Player getPlayer(Long id){
        return players.findById(id).orElse(null);
    }
    @Override
    public Player addPlayer(Player player){
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        return players.save(player);
    }
    @Override
    public Player updatePlayer(Long id, Player player) {
        // Find the existing player by id
        Optional<Player> existingPlayer = players.findById(id);

        if (existingPlayer.isPresent()) {
            Player updatedPlayer = existingPlayer.get();

            // Update the fields of the existing player with the new player data
            updatedPlayer.setUsername(player.getUsername());
            updatedPlayer.setPassword(passwordEncoder.encode(player.getPassword()));
            updatedPlayer.setEmail(player.getEmail()); 
            updatedPlayer.setElo(player.getElo());
            
            // Save the updated player
            return players.save(updatedPlayer);
        } else {
            // Return null or throw an exception if player with given id is not found
            return null;
        }
    }
    @Override
    public void deletePlayer(Long id){
        Optional<Player> player = players.findById(id);
        if (player.isPresent()) {
            players.delete(player.get());
        } else {
            // Handle the case where the player does not exist
            throw new IllegalArgumentException("Player with id " + id + " does not exist");
        }
    }

    @Override
    public Player login(String username, String password) throws InvalidCredentialsException {
        Optional<Player> player = players.findByUsername(username);
        if (player.isPresent() && passwordEncoder.matches(password, player.get().getPassword())) {
            return player.get();
        } else {    
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

}
