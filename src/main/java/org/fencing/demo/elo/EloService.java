package org.fencing.demo.elo;

import org.fencing.demo.events.Event;
import org.fencing.demo.match.Match;

public interface EloService {
    void calculateAndSaveEloChange(Match match);
    void applyEloChanges(Event event);  
}
