package org.fencing.demo.events;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class EventController {
    
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/tournaments/{tournamentId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event addEvent(@PathVariable Long tournamentId, @RequestBody Event event) {
        return eventService.addEvent(tournamentId, event);
    }

    @GetMapping("/tournaments/{tournamentId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getAllEventsByTournamentId(@PathVariable Long tournamentId) {
        return eventService.getAllEventsByTournamentId(tournamentId);
    }

    @PutMapping("/tournaments/{tournamentId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Event updateEvent(@PathVariable Long tournamentId, @PathVariable Long eventId, @RequestBody Event event) {
        return eventService.updateEvent(tournamentId, eventId, event);
    }

    @DeleteMapping("/tournaments/{tournamentId}/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMatch(@PathVariable Long tournamentId, @PathVariable Long eventId) {
        eventService.deleteEvent(tournamentId, eventId);
    }
}
