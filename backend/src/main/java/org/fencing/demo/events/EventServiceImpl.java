package org.fencing.demo.events;

import java.util.List;

import org.fencing.demo.exception.MatchesNotCompleteException;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.fencing.demo.player.PlayerNotFoundException;
import org.fencing.demo.player.PlayerRepository;
import org.fencing.demo.stages.GroupStage;
import org.fencing.demo.stages.KnockoutStage;
import org.fencing.demo.tournament.TournamentNotFoundException;
import org.fencing.demo.tournament.TournamentRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final TournamentRepository tournamentRepository;
    private PlayerRepository playerRepository;

    public EventServiceImpl(EventRepository eventRepository, TournamentRepository tournamentRepository, PlayerRepository playerRepository) {
        this.tournamentRepository = tournamentRepository;
        this.eventRepository = eventRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public Event addEvent(Long tournamentId, Event event) {
        if (tournamentId == null || event == null) {
            throw new IllegalArgumentException("Tournament ID and Event cannot be null");
        }

        return tournamentRepository.findById(tournamentId).map(tournament -> {
            if (event.getStartDate().toLocalDate().isBefore(tournament.getTournamentStartDate())) {
                throw new IllegalArgumentException("Event start date cannt be earlier than Tournament start date");
            }
            if (event.getEndDate().isBefore(event.getStartDate())) {
                throw new IllegalArgumentException("Event end date must be after start date");
            }
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
        //System.out.println("tournament id:" + newEvent.getTournament());
        if (tournamentId == null || eventId == null || newEvent == null) {
            throw new IllegalArgumentException("Tournament ID, Event ID and updated Event cannot be null");
        }
        
        return eventRepository.findById(eventId).map(existingEvent -> {
            if (!existingEvent.getTournament().equals(newEvent.getTournament())) {
                throw new IllegalArgumentException("Cannot change the tournament of an existing event.");
            }
            if (newEvent.getStartDate().toLocalDate().isBefore(existingEvent.getTournament().getTournamentStartDate())) {
                throw new IllegalArgumentException("Event start date cannt be earlier than Tournament start date");
            }
            if (newEvent.getEndDate().isBefore(newEvent.getStartDate())) {
                throw new IllegalArgumentException("Event end date must be after start date");
            }

            existingEvent.setGender(newEvent.getGender());
            existingEvent.setWeapon(newEvent.getWeapon());
            existingEvent.setStartDate(newEvent.getStartDate());
            existingEvent.setEndDate(newEvent.getEndDate());
            // existingEvent.setRankings(newEvent.getRankings());
            // existingEvent.setGroupStages(newEvent.getGroupStages());
            // existingEvent.setKnockoutStages(newEvent.getKnockoutStages());

            return eventRepository.save(existingEvent);
            
        }).orElseThrow(() -> new EventNotFoundException(eventId));
        
    }

    public Event addPlayerToEvent(Long eventId, Long playerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        PlayerRank playerRank = new PlayerRank();
        playerRank.setPlayer(player);
        playerRank.setEvent(event);
        playerRank.setScore(0);  // Initialize score

        event.getRankings().add(playerRank);  // Add PlayerRank to event rankings

        return eventRepository.save(event);   // Save updated event
    }

    @Override
    @Transactional
    public void deleteEvent(Long tournamentId, Long eventId) {
        if (tournamentId == null || eventId == null) {
            throw new IllegalArgumentException("Tournament ID and Event ID cannot be null");
        }
        eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
        eventRepository.deleteByTournamentIdAndId(tournamentId, eventId);
    }

    @Override
    public List<Player> updatePlayerEloAfterEvent(Long eventId) {
        // if (eventId == null) {
        //     throw new IllegalArgumentException("Event ID cannot be null");
        // }
    
        Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException(eventId));


        if(!allMatchesComplete(eventId)){
            System.out.println("matches are not complete!!!!");
            throw new MatchesNotCompleteException();
        }

        for (PlayerRank pr : event.getRankings()) {
            System.out.println("this are the player rankings:"+ pr);
            Player p = pr.getPlayer();
            p.setElo(pr.getTempElo());
            if(pr.getTempElo() >= 2400){
                pr.getPlayer().setReached2400(true);
            }
            playerRepository.save(p);
        }

        return playerRepository.findAll();
        
    }

    public boolean allMatchesComplete(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    
        Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException(eventId));
    
        List<GroupStage> grpStages = event.getGroupStages();
        List<KnockoutStage> knockoutStages = event.getKnockoutStages();
    
        if (grpStages.isEmpty() || knockoutStages.isEmpty()) {
            throw new IllegalArgumentException("There are no group or knockout stages");
        }
    
        for (GroupStage grpStage : grpStages) {
            for (Match match : grpStage.getMatches()) {
                if (!match.isFinished()) {
                    return false;
                }
            }
        }
    
        for (KnockoutStage knockoutStage : knockoutStages) {
            for (Match match : knockoutStage.getMatches()) {
                if (!match.isFinished()) {
                    return false;
                }
            }
        }
    
        return true;
    }


}
