package org.fencing.demo.stages;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GroupStageNotFoundException extends RuntimeException {
    //A unique identifier for this class version, which is useful during 
    // the serialization and deserialization process.
    private static final long serialVersionUID = 1L;

    public GroupStageNotFoundException (Long id) {
        super("Could not find GroupStage " + id);
    }
}
