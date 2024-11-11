package org.fencing.demo.events;

import java.util.List;

import org.fencing.demo.player.Player;

public interface EventService {
    Event addEvent(Long tournamentId, Event event);

    List<Event> getAllEventsByTournamentId(Long tournamentId);

    Event getEvent(Long id);

    Event updateEvent(Long tournamentId, Long eventId, Event event);

    Event addPlayerToEvent(Long eventId, String username);

    void deleteEvent(Long tournamentId, Long eventId);

    Event removePlayerFromEvent(Long eventId, String username);

    Event adminRemovesPlayerFromEvent(Long eventId, String username);
    
    List<Player> updatePlayerEloAfterEvent(Long eventId);
}
