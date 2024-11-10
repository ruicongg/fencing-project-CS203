package org.fencing.demo.matchmaking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.IntStream;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.events.PlayerRankEloComparator;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GroupDistributionService {
    Map<Integer, List<PlayerRank>> distributePlayersIntoGroups(@NotNull Set<PlayerRank> rankings) {
        Set<PlayerRank> players = new TreeSet<>(new PlayerRankEloComparator());
        players.addAll(rankings);

        int numberOfPlayers = players.size();

        // throws an exception if there are no players
        GroupSizeCalculator groupSizeCalculator = new GroupSizeCalculator(numberOfPlayers);
        int groupSize = groupSizeCalculator.findOptimalGroupSize();
        int numberOfGroups = groupSizeCalculator.findNumberOfGroups(groupSize);
        return distributePlayersRoundRobin(players, numberOfGroups);
    }

    private static Map<Integer, List<PlayerRank>> distributePlayersRoundRobin(
            Set<PlayerRank> sortedPlayers, 
            int numberOfGroups) {
        Map<Integer, List<PlayerRank>> groups = initializeGroups(numberOfGroups);   
        Iterator<PlayerRank> iterator = sortedPlayers.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            groups.get(index % numberOfGroups).add(iterator.next());
            index++;
        }
        return groups;
    }

    private static Map<Integer, List<PlayerRank>> initializeGroups(int numberOfGroups) {
        Map<Integer, List<PlayerRank>> groups = new TreeMap<>();
        for (int i = 0; i < numberOfGroups; i++) {
            groups.put(i, new ArrayList<>());
        }
        return groups;
    }

}