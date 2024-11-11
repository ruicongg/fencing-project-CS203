package org.fencing.demo.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MatchNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public MatchNotFoundException (Long id) {
        super("Could not find Match " + id);
    }
}
