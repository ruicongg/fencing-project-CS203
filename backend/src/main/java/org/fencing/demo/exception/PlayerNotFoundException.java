package org.fencing.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public PlayerNotFoundException(Long id){
<<<<<<<< HEAD:backend/src/main/java/org/fencing/demo/player/PlayerNotFoundException.java
        super ("Could not find Player with ID " +id);
    }

    public PlayerNotFoundException(String username){
        super ("Could not find Player with username " +username);
========
        super ("Could not find Player " +id);
>>>>>>>> feature/elo-update:backend/src/main/java/org/fencing/demo/exception/PlayerNotFoundException.java
    }
}
