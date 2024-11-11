package org.fencing.demo.playerrank;

import org.fencing.demo.player.Player;

public class EloCalculator {

    public static int calculateNewElo(int currentElo, int opponentElo, int score, int kValue) {
        double expectedScore = 1.0 / (1.0 + Math.pow(10, (opponentElo - currentElo) / 400.0));
        return (int) Math.round(currentElo + kValue * (score - expectedScore));
    }

    public static int calculateKValue(Player player) {
        int elo = player.getElo();
        int numberOfMatches = player.getMatchesAsPlayer1().size() + 
        player.getMatchesAsPlayer2().size();

        if (elo < 2300 && numberOfMatches < 30) {
            return 40;
        }
        if (elo >= 2400 || player.isReached2400()) {
            return 10;
        }
        return 20;
    }

    public static int changeTempElo(PlayerRank thisPlayer, PlayerRank opponent, boolean isWinner) {

        int opponentElo = opponent.getTempElo();
        int score = isWinner ? 1 : 0;
        int kValue = calculateKValue(thisPlayer.getPlayer());

        return calculateNewElo(thisPlayer.getTempElo(), opponentElo, score, kValue);
    }
}


