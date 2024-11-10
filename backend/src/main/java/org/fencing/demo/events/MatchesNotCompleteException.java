package org.fencing.demo.events;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MatchesNotCompleteException extends RuntimeException {
    public MatchesNotCompleteException() {
        super("not all matches completed");
    }
}