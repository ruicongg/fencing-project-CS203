package com.example.demo.player;

import java.util.List;

public interface PlayerService {
    List<Player> listPlayers();
    Player getPlayer(Long id);
    Player addPlayer(Player player);
    Player updatPlayer(Long id, Player player);
    void deletePlayer(Long id);
}
