package org.fencing.demo.groupmatchmaking;

import java.util.*;

import org.fencing.demo.player.Player;
import org.fencing.demo.playerrank.PlayerRank;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Component
@Validated
public class GroupDistributionService {
    public Map<Integer, List<Player>> distributePlayersIntoGroups(@NotNull SortedSet<PlayerRank> rankings) {

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
