package org.fencing.demo.playerrank;

import java.util.List;

import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerNotFoundException;
import org.fencing.demo.player.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerRankServiceImpl implements PlayerRankService {
    private PlayerRankRepository playerRankRepository;
    private PlayerRepository playerRepository;
    private EventRepository eventRepository;

    public PlayerRankServiceImpl (PlayerRankRepository playerRankRepositor, PlayerRepository playerRepository, EventRepository eventRepository) {
        this.playerRankRepository = playerRankRepositor;
        this.playerRepository = playerRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<PlayerRank> getAllPlayerRanksForEvent(Long eventId) {
        // Verify the event exists
        eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        
        // Return all PlayerRank entries associated with the given eventId
        return playerRankRepository.findByEventId(eventId);
    }

    @Override
    public List<PlayerRank> getAllPlayerRanksForPlayer(String username) {
        // Retrieve the Player entity using the username
        List<Player> player = playerRepository.findByUsername(username);
        if (player.size() == 0) {
            throw new PlayerNotFoundException(username);
        } 

        // Return all PlayerRank entries associated with the player's ID
        return playerRankRepository.findByPlayerId(player.get(0).getId());
    }
}
