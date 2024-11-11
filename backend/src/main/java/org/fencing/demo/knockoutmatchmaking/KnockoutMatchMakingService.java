package org.fencing.demo.knockoutmatchmaking;

import java.util.List;

import org.fencing.demo.knockoutstage.KnockoutStage;
import org.fencing.demo.match.Match;

public interface KnockoutMatchMakingService {

    KnockoutStage createNextKnockoutStage(Long eventId);

    List<Match> createMatchesInKnockoutStage(Long eventId);
}
