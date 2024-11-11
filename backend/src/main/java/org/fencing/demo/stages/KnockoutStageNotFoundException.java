package org.fencing.demo.stages;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class KnockoutStageNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public KnockoutStageNotFoundException (Long id) {
        super("Could not find KnockoutStage " + id);
    }

}
