package com.natlex.test_app.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GeologicalNotFoundException  extends ResponseStatusException {

    public GeologicalNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "Geological not not found");
    }

}
