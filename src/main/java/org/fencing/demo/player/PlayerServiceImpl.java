package org.fencing.demo.player;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService{
    private PlayerRepository players;

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
            updatedPlayer.setPassword(player.getPassword());
            updatedPlayer.setEmail(player.getEmail()); 
            updatedPlayer.setElo(player.getElo());
            
            // Save the updated player
            return players.save(updatedPlayer);
        } else {
            // Return null or throw an exception if player with given id is not found
            return null;
        }
    }
    public void deletePlayer(Long id){
        Optional<Player> player = players.findById(id);
        if (player.isPresent()) {
            players.delete(player.get());
        } else {
            // Handle the case where the player does not exist
            throw new IllegalArgumentException("Player with id " + id + " does not exist");
        }
    }

}
