package com.example.demo.tournaments;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface TournamentRepository extends CrudRepository<Tournament, Long> {

}
