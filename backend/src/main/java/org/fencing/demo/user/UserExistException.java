package org.fencing.demo.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserExistException(String username) {
        super("User with username " + username + " already exists");
    }   
}
