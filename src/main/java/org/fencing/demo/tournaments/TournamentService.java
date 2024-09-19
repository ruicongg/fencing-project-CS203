package org.fencing.demo.tournaments;

import java.util.List;
import java.util.Optional;

public interface TournamentService {

    Tournament save(Tournament tournament);

    List<Tournament> findAll();

    Optional<Tournament> find(Long id);

    boolean isExists(Long id);

    Tournament update(Long id, Tournament tournament);

    void delete(Long id);
}
