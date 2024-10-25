package org.fencing.demo.player;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public PlayerNotFoundException(Long id){
        super ("Could not find Player" +id);
    }
}
