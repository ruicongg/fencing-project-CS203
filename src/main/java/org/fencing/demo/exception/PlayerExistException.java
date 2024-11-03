package org.fencing.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PlayerExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PlayerExistException(String username) {
        super("A player with this username: " + username + " already exists");
    }  
}
