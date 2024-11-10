package org.fencing.demo.matchmaking;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

class GroupSizeCalculator {
    private final int numberOfPlayers;
    private static final int MIN_GROUP_SIZE = 4;
    private static final int MAX_GROUP_SIZE = 7;

    public GroupSizeCalculator(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    int findOptimalGroupSize() {
        Map<Integer, Integer> mapSizeToRemainder = generateMapSizeToRemainder();
        return findOptimalGroupSizeByMap(mapSizeToRemainder);
    }

    int findNumberOfGroups(int groupSize) {
        int baseGroups = numberOfPlayers / groupSize;
        // there will be one more group if the number of players is not divisible by the
        // group size exactly
        return numberOfPlayers % groupSize > 0 ? baseGroups + 1 : baseGroups;
    }

    private Map<Integer, Integer> generateMapSizeToRemainder() {
        Map<Integer, Integer> mapSizeToRemainder = new HashMap<>();
        for (int size = MIN_GROUP_SIZE; size <= MAX_GROUP_SIZE; size++) {
            mapSizeToRemainder.put(size, numberOfPlayers % size);
        }
        return mapSizeToRemainder;
    }

    /**
     * Finds the optimal group size by:
     * 1. Finding the largest remainder
     * 2. If multiple sizes have the same largest remainder, picks the smallest
     * size
     * 3. Throws an error if there are no players in the event
     *
     * @param mapSizeToRemainder Map of group size to its remainder
     * @return The optimal group size
     * @throws IllegalArgumentException if the map is empty (no players)
     */
    private int findOptimalGroupSizeByMap(Map<Integer, Integer> mapSizeToRemainder) {
        return mapSizeToRemainder.entrySet().stream()
                .max(Comparator.<Map.Entry<Integer, Integer>, Integer>comparing(Map.Entry::getValue)
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No players in event"));
    }

}
