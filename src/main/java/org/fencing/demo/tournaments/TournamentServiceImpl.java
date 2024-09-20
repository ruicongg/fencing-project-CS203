package org.fencing.demo.tournaments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament updateTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public List<Tournament> listTournaments() {
        return StreamSupport.stream(tournamentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Tournament> getTournament(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public boolean doesTournamentExist(Long id) {
        return tournamentRepository.existsById(id);
    }

    // ! check before deleting
    // @Override
    // public Tournament updateTournament(Long id, Tournament tournament) {
    //     tournament.setId(id);

    //     return tournamentRepository.findById(id).map(existingTournament -> {
    //         Optional.ofNullable(tournament.getName()).ifPresent(existingTournament::setName);
    //         return tournamentRepository.save(existingTournament);
    //     }).orElseThrow(() -> new RuntimeException("Tournament does not exist"));
    // }

    // ! still need to implement exception handling
    // this method assumes that you are not changing reviews
    @Override
    public Tournament updateTournament(Long id, Tournament newTournament) {
        return tournamentRepository.findById(id).map(existingTournament -> {
            existingTournament.setName(newTournament.getName());
            existingTournament.setDate(newTournament.getDate());
            return tournamentRepository.save(existingTournament);
        }).orElseThrow(() -> new IllegalArgumentException("Tournament does not exist"));
    }

    @Override
    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }

}
