package org.fencing.demo.player;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // This will find players by their username and return a list
    List<Player> findByUsername(String username);

}


