package com.natlex.test_app.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GeologicalBadRequestException extends ResponseStatusException {

    public GeologicalBadRequestException(String cause) {
        super(HttpStatus.BAD_REQUEST, cause);
    }

}