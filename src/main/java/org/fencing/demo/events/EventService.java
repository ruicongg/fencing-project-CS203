package org.fencing.demo.events;

import java.util.List;
import org.fencing.demo.player.*;

public interface EventService {
    Event addEvent(Long tournamentId, Event event);

    List<Event> getAllEventsByTournamentId(Long tournamentId);

    Event getEvent(Long id);

    Event updateEvent(Long tournamentId, Long eventId, Event event);

    void updatePlayerEloAfterEvent(Long eventId);

    Event addPlayerToEvent(Long eventId, Long playerId);

    void deleteEvent(Long tournamentId, Long eventId);
}
