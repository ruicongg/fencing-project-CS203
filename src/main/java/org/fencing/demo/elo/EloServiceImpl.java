package org.fencing.demo.elo;

import org.fencing.demo.events.Event;
import org.fencing.demo.match.Match;
import org.fencing.demo.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EloServiceImpl implements EloService {

    @Autowired
    private EloChangeRepository eloChangeRepository;

    private static final int K_FACTOR = 32;

    public void calculateAndSaveEloChange(Match match) {
        Player winner = match.getWinner();
        Player loser = match.getLoser();
        Event event = match.getGroupStage().getEvent();

        double expectedScoreWinner = calculateExpectedScore(winner.getElo(), loser.getElo());
        double expectedScoreLoser = 1 - expectedScoreWinner;

        int eloChangeWinner = (int) Math.round(K_FACTOR * (1 - expectedScoreWinner));
        int eloChangeLoser = (int) Math.round(K_FACTOR * (0 - expectedScoreLoser));

        saveEloChange(winner, event, eloChangeWinner);
        saveEloChange(loser, event, eloChangeLoser);
    }

    private double calculateExpectedScore(int playerElo, int opponentElo) {
        return 1 / (1 + Math.pow(10, (opponentElo - playerElo) / 400.0));
    }

    private void saveEloChange(Player player, Event event, int eloChange) {
        EloChange eloChangeEntity = EloChange.builder()
                .player(player)
                .event(event)
                .eloChange(eloChange)
                .isApplied(false)
                .build();
        eloChangeRepository.save(eloChangeEntity);
    }

    @Transactional
    public void applyEloChanges(Event event) {
        List<EloChange> eloChanges = eloChangeRepository.findByEventAndIsAppliedFalse(event);
        for (EloChange eloChange : eloChanges) {
            Player player = eloChange.getPlayer();
            player.setElo(player.getElo() + eloChange.getEloChange());
            eloChange.setApplied(true);
        }
    }
}
