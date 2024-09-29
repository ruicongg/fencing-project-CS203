package org.fencing.demo.player;

import java.util.List;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;

public interface PlayerService {
    List<Player> listPlayers();
    Player getPlayer(Long id);
    Player addPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    void deletePlayer(Long id);
    Player login(String username, String password) throws InvalidCredentialsException;
}
