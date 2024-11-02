package org.fencing.demo.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MatchesNotCompleteException extends RuntimeException {
    public MatchesNotCompleteException(String message) {
        super(message);
    }
}