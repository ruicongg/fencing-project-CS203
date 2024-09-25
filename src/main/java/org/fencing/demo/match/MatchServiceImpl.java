package org.fencing.demo.match;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.fencing.demo.events.EventNotFoundException;
import org.fencing.demo.events.EventRepository;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final EventRepository eventRepository;

    public MatchServiceImpl(MatchRepository matchRepository, EventRepository eventRepository) {
        this.matchRepository = matchRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public Match addMatch(Long eventId, Match match) {
        if (eventId == null || match == null) {
            throw new IllegalArgumentException("Event ID and Match cannot be null");
        }
        return eventRepository.findById(eventId).map(event -> {
            match.setEvent(event);
            return matchRepository.save(match);
        }).orElseThrow(() -> new EventNotFoundException(eventId));
    }

    @Override
    public List<Match> getAllMatchesByEventId(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        return matchRepository.findByEventId(eventId);
    }

    @Override
    public Match getMatch(Long matchId) {
        if (matchId == null){
            throw new IllegalArgumentException("Match ID cannot be null");
        }
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
    }

    @Override
    @Transactional
    public Match updateMatch(Long eventId, Long matchId, Match newMatch) {
        if (eventId == null || matchId == null || newMatch == null) {
            throw new IllegalArgumentException("Event ID, Match ID and updated Match cannot be null");
        }
        Match existingMatch = matchRepository.findById(matchId).orElseThrow(() -> new MatchNotFoundException(matchId));
        if (!existingMatch.getEvent().equals(newMatch.getEvent())) {
            throw new IllegalArgumentException("Event cannot be changed");
        }
        existingMatch.setPlayer1(newMatch.getPlayer1());
        existingMatch.setPlayer2(newMatch.getPlayer2());
        existingMatch.setPlayer1Score(newMatch.getPlayer1Score());
        existingMatch.setPlayer2Score(newMatch.getPlayer2Score());
        return matchRepository.save(existingMatch);
        
    }

    @Override
    @Transactional
    public void deleteMatch(Long eventId, Long matchId) {
        if (eventId == null || matchId == null) {
            throw new IllegalArgumentException("Event ID and Match ID cannot be null");
        }
        matchRepository.deleteByEventIdAndId(eventId, matchId);
    }
}