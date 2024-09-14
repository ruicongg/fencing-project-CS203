package com.example.demo.tournaments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

@Service
public class TournamentServiceImpl implements TournamentService {

    private TournamentRepository tournamentRepository;

    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament save(Tournament tournamentEntity) {
        return tournamentRepository.save(tournamentEntity);
    }

    @Override
    public List<Tournament> findAll() {
        return StreamSupport.stream(tournamentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Tournament> find(Long id) {
        return tournamentRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return tournamentRepository.existsById(id);
    }

    @Override
    public Tournament update(Long id, Tournament tournamentEntity) {
        tournamentEntity.setId(id);

        return tournamentRepository.findById(id).map(existingTournament -> {
            Optional.ofNullable(tournamentEntity.getName()).ifPresent(existingTournament::setName);
            return tournamentRepository.save(existingTournament);
        }).orElseThrow(() -> new RuntimeException("Tournament does not exist"));
    }

    @Override
    public void delete(Long id) {
        tournamentRepository.deleteById(id);
    }

}
