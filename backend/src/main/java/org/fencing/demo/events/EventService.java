package org.fencing.demo.events;

import java.util.List;

import org.fencing.demo.player.Player;

public interface EventService {
    Event addEvent(Long tournamentId, Event event);

    List<Event> getAllEventsByTournamentId(Long tournamentId);

    Event getEvent(Long id);

    Event updateEvent(Long tournamentId, Long eventId, Event event);

    Event addPlayerToEvent(Long eventId, Long playerId);

    void deleteEvent(Long tournamentId, Long eventId);

    List<Player> updatePlayerEloAfterEvent(Long eventId);
}
