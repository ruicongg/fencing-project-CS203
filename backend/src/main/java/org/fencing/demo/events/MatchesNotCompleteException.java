package org.fencing.demo.events;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MatchesNotCompleteException extends RuntimeException {
    public MatchesNotCompleteException() {
        super("not all matches completed");
    }
}
