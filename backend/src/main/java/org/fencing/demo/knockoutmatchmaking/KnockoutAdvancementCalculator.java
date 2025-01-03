package org.fencing.demo.knockoutmatchmaking;

import org.springframework.stereotype.Component;

@Component
public class KnockoutAdvancementCalculator {
    private static final int SMALL_EVENT_THRESHOLD = 32;
    private static final int MEDIUM_EVENT_THRESHOLD = 64;
    private static final int MINIMUM_CUT_THRESHOLD = 40;
    private static final double MAX_ELIMINATION_PERCENTAGE = 0.20;

    public int calculateNumberOfPlayersAdvancing(int totalPlayers) {
        if (totalPlayers <= SMALL_EVENT_THRESHOLD) {
            return totalPlayers;
        }

        // Find the next power of 2 below the total players
        int nextPowerOf2 = getLargestPowerOf2LessThan(totalPlayers);

        // Calculate minimum players needed to reach next power of 2
        double minCutPercentage = 1.0 - ((double) nextPowerOf2 / totalPlayers);

        // If the cut percentage to reach power of 2 is less than max elimination,
        // use that instead
        if (minCutPercentage <= MAX_ELIMINATION_PERCENTAGE) {
            return nextPowerOf2;
        }

        // Otherwise use the maximum elimination percentage
        return (int) (totalPlayers * (1 - MAX_ELIMINATION_PERCENTAGE));
    }

    public int calculateNumberOfByes(int playersAdvancing) {
        // If exactly a power of 2, no byes needed
        if (isPowerOfTwo(playersAdvancing)) {
            return 0;
        }

        // Find next lower power of 2
        int targetRound = getLargestPowerOf2LessThan(playersAdvancing);

        // Calculate how many matches needed to reach target
        int matchesNeeded = (playersAdvancing - targetRound);

        // Players that don't need to play get byes
        return playersAdvancing - (matchesNeeded * 2);
    }

    private boolean isPowerOfTwo(int number) {
        return (number & (number - 1)) == 0;
    }

    private int getLargestPowerOf2LessThan(int number) {
        int power = 1;
        while (power * 2 <= number) {
            power *= 2;
        }
        return power;
    }
}