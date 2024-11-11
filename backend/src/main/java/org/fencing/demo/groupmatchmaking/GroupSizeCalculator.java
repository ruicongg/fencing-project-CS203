package org.fencing.demo.groupmatchmaking;

import java.util.*;

class GroupSizeCalculator {

    private static final int MIN_GROUP_SIZE = 4;
    private static final int MAX_GROUP_SIZE = 7;



    /**
     * Calculates the optimal number of groups for the given number of players.
     * 1. If the number of players is divisible by the maximum group size, return
     * the number of groups that can be created.
     * 2. Otherwise, find the group size that has the smallest remainder when the
     * number of players is divided by the group size. If there are multiple group
     * sizes with the same remainder, choose the largest group size.
     * 
     * @return The optimal number of groups
     * @throws IllegalArgumentException if the number of players is less than the
     *                                  minimum required for more than one group
     */

    public int calculateOptimalNumberOfGroups(int numberOfPlayers) {

        if (numberOfPlayers < MAX_GROUP_SIZE) {
            throw new IllegalArgumentException("Not enough players to create groups, need at least " + (MAX_GROUP_SIZE + 1) + " players for more than one group");
        }
        if (numberOfPlayers % MAX_GROUP_SIZE == 0) {
            return numberOfPlayers / MAX_GROUP_SIZE;
        }
        Map<Integer, Integer> mapNumberOfGroupsToRemainder = new HashMap<>();
        for (int size = MIN_GROUP_SIZE; size < MAX_GROUP_SIZE; size++) {
            mapNumberOfGroupsToRemainder.put(numberOfPlayers / size, numberOfPlayers % size);
        }
        return mapNumberOfGroupsToRemainder.entrySet().stream()
                .min(Comparator.<Map.Entry<Integer, Integer>, Integer>comparing(Map.Entry::getValue)
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("This should never be thrown, hashmap will always have at least one entry"));
    }


}
