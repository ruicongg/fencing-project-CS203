package org.fencing.demo.events;

import java.util.Comparator;

public class MatchPointsComparator implements Comparator<PlayerRank> {

    @Override
    public int compare(PlayerRank pr1, PlayerRank pr2) {
         // Compare by number of wins (higher wins first)
         int winComparison = Integer.compare(pr2.getWinCount(), pr1.getWinCount());
         if (winComparison != 0) {
             return winComparison;
         }
 
         // If wins are equal, compare by number of losses (lower losses first)
         int lossComparison = Integer.compare(pr1.getLossCount(), pr2.getLossCount());
         if (lossComparison != 0) {
             return lossComparison;
         }
 
         // If wins and losses are equal, compare by point difference (higher pointDiff first)
         return Integer.compare(pr2.getScoreDiff(), pr1.getScoreDiff());
    }
}
