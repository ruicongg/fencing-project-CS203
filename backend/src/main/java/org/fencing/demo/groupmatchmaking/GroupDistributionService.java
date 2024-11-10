package org.fencing.demo.groupmatchmaking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fencing.demo.events.PlayerRank;
import org.fencing.demo.player.Player;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;

@Component
public class GroupDistributionService {
    public Map<Integer, List<Player>> distributePlayersIntoGroups(@NotNull TreeSet<PlayerRank> rankings) {

        Set<Player> players = new TreeSet<>();
        rankings.forEach(pr -> players.add(pr.getPlayer()));
        int numberOfPlayers = players.size();
        GroupSizeCalculator groupSizeCalculator = new GroupSizeCalculator();
        
        // this throws an exception if there are not enough players to create at least one group
        int optimalNumberOfGroups = groupSizeCalculator.calculateOptimalNumberOfGroups(numberOfPlayers);

        return distributePlayersRoundRobin(players, optimalNumberOfGroups);
    }

    private static Map<Integer, List<Player>> distributePlayersRoundRobin(
            Set<Player> sortedPlayers, 
            int numberOfGroups) {

        Map<Integer, List<Player>> groups = initializeGroups(numberOfGroups);
        Iterator<Player> iterator = sortedPlayers.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            groups.get(index % numberOfGroups).add(iterator.next());
            index++;
        }
        return groups; 
    }


    private static Map<Integer, List<Player>> initializeGroups(int numberOfGroups) {
        Map<Integer, List<Player>> groups = new TreeMap<>();
        for (int i = 0; i < numberOfGroups; i++) {
            groups.put(i, new ArrayList<>());
        }
        return groups;
    }

    

}
