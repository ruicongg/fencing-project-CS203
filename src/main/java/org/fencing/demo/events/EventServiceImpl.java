package org.fencing.demo.events;

import java.util.List;
import java.time.LocalDate;

import org.fencing.demo.tournaments.TournamentNotFoundException;
import org.fencing.demo.tournaments.TournamentRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final TournamentRepository tournamentRepository;

    public EventServiceImpl(EventRepository eventRepository, TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public Event addEvent(Long tournamentId, Event event) {
        if (tournamentId == null || event == null) {
            throw new IllegalArgumentException("Tournament ID and Event cannot be null");
        }
        return tournamentRepository.findById(tournamentId).map(tournament -> {
            event.setTournament(tournament);
            return eventRepository.save(event);
        }).orElseThrow(() -> new TournamentNotFoundException(tournamentId));
    }

    @Override
    public List<Event> getAllEventsByTournamentId(Long tournamentId) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new TournamentNotFoundException(tournamentId);
        }
        return eventRepository.findByTournamentId(tournamentId);
    }

    @Override
    public Event getEvent(Long eventId) {
        if (eventId == null){
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    @Override
    @Transactional
    public Event updateEvent(Long tournamentId, Long eventId, Event newEvent) {
        if (tournamentId == null || eventId == null || newEvent == null) {
            throw new IllegalArgumentException("Tournament ID, Event ID and updated Event cannot be null");
        }
        Event existingEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (!existingEvent.getTournament().equals(newEvent.getTournament())) {
            throw new IllegalArgumentException("Tournament cannot be changed");
        }
        if (!(LocalDate.now().isBefore(newEvent.getStartDate().toLocalDate()))){
            throw new IllegalArgumentException("One player will not have an opponent");
        }
        existingEvent.setGender(newEvent.getGender());
        existingEvent.setWeapon(newEvent.getWeapon());
        existingEvent.setStartDate(newEvent.getStartDate());
        existingEvent.setEndDate(newEvent.getEndDate());
        // existingEvent.setGroupStages(newEvent.getGroupStages());
        existingEvent.setKnockoutStages(newEvent.getKnockoutStages());
        existingEvent.setRankings(newEvent.getRankings());

        return eventRepository.save(existingEvent);
        
    }

    @Override
    @Transactional
    public void deleteEvent(Long tournamentId, Long eventId) {
        if (tournamentId == null || eventId == null) {
            throw new IllegalArgumentException("Tournament ID and Event ID cannot be null");
        }
        eventRepository.deleteByTournamentIdAndId(tournamentId, eventId);
    }

}
