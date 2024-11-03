package org.fencing.demo.events;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;

@RestController
public class EventController {
    
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/tournaments/{tournamentId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event addEvent(@PathVariable Long tournamentId, @Valid @RequestBody Event event) {
        return eventService.addEvent(tournamentId, event);
    }

    @GetMapping("/tournaments/{tournamentId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> getAllEventsByTournamentId(@PathVariable Long tournamentId) {
        return eventService.getAllEventsByTournamentId(tournamentId);
    }

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Event getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PutMapping("/tournaments/{tournamentId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Event updateEvent(@PathVariable Long tournamentId, @PathVariable Long eventId, @Valid @RequestBody Event event) {
        return eventService.updateEvent(tournamentId, eventId, event);
    }

    @PostMapping("/events/{eventId}/players/{username}")
    public Event addPlayerToEvent(@PathVariable Long eventId, @PathVariable String username) {
        return eventService.addPlayerToEvent(eventId, username);
    }

    @DeleteMapping("/tournaments/{tournamentId}/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long tournamentId, @PathVariable Long eventId) {
        eventService.deleteEvent(tournamentId, eventId);
    }

    @DeleteMapping("/events/{eventId}/players")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePlayerFromEvent(@PathVariable Long eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        eventService.removePlayerFromEvent(eventId, username);
    }

    @DeleteMapping("/events/{eventId}/players/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminRemovesPlayerFromEvent(@PathVariable Long eventId, @PathVariable String username) {
        eventService.adminRemovesPlayerFromEvent(eventId, username);
    }
}
