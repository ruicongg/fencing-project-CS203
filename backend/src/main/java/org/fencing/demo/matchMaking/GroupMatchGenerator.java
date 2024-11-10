package org.fencing.demo.matchmaking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.fencing.demo.match.Match;
import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.Event;
import org.fencing.demo.stages.GroupStage;
import org.springframework.stereotype.Component;

// note that groupstage is not a refactor, it is a bug fix
@Component
public class GroupMatchGenerator {
    
    /**
     * Generates matches for all groups of players
     */
    Map<Integer, List<Match>> generateGroupMatches(Map<Integer, List<PlayerRank>> groups, Event event) {
        Map<Integer, List<Match>> resultMap = new TreeMap<>();
        
        for (Map.Entry<Integer, List<PlayerRank>> entry : groups.entrySet()) {
            Integer groupNumber = entry.getKey();
            List<PlayerRank> players = entry.getValue();
            GroupStage groupStage = event.getGroupStages().get(groupNumber);
            List<Match> matches = generateMatchesForGroup(players, event);
            resultMap.put(groupNumber, matches);
        }
        
        return resultMap;
    }

    private List<Match> generateMatchesForGroup(List<PlayerRank> players, Event event) {
        return generateAllPossiblePairings(players).stream()
            .map(pair -> createMatch(pair, event))
            .toList();
    }

    private List<Pair> generateAllPossiblePairings(List<PlayerRank> players) {
        List<Pair> pairings = new ArrayList<>();
        
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                pairings.add(new Pair(players.get(i), players.get(j)));
            }
        }
        return pairings;
    }

    private Match createMatch(Pair pair, Event event) {
        Match match = new Match();
        match.setPlayer1(pair.getPlayerRank1().getPlayer());
        match.setPlayer2(pair.getPlayerRank2().getPlayer());
        match.setEvent(event);
        // match.setGroupStage(groupStage);
        return match;
    }
}