package org.fencing.demo.player;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
public class PlayerController {
    private PlayerService playerService;

    public PlayerController(PlayerService ps) {
        this.playerService = ps;
    }

    // List players (all? or in a tournament? or works for both)
    @GetMapping("/players")
    public List<Player> listPlayers() {
        return playerService.listPlayers();
    }

    // Search for player by ID, else throw PlayerNotFound exception
    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable Long id) {
        Player player = playerService.getPlayer(id);

        if (player == null)
            throw new PlayerNotFoundException(id);
        return player;
    }

    // updates player info
    @PutMapping("/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @Valid @RequestBody Player updatedPlayerInfo) {
        Player player = playerService.updatePlayer(id, updatedPlayerInfo);
        if (player == null)
            throw new PlayerNotFoundException(id);

        return player;
    }

    // Deletes player (from tournament or sys?)
    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
        } catch (EmptyResultDataAccessException e) {
            throw new PlayerNotFoundException(id);
        }
    }

}