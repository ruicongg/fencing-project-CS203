package com.example.demo.tournaments;

import java.util.List;
import java.util.Optional;

public interface TournamentService {

    TournamentEntity save(TournamentEntity tournament);

    List<TournamentEntity> findAll();

    Optional<TournamentEntity> findOne(Long id);

    boolean isExists(Long id); 

    TournamentEntity partialUpdate(Long id, TournamentEntity tournament);

    void delete(Long id);
}
