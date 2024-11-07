package org.fencing.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MatchesNotCompleteException.class)
    public ResponseEntity<String> handleMatchesNotCompleteException(MatchesNotCompleteException ex) {
        // Directly returning the message defined in MatchesNotCompleteException
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PlayerExistException.class)
    public ResponseEntity<String> handlePlayerExistException(PlayerExistException ex) {
        // Directly returning the message defined in MatchesNotCompleteException
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<String> handlePlayerNotFoundException(PlayerNotFoundException ex) {
        // Directly returning the message defined in MatchesNotCompleteException
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


}

