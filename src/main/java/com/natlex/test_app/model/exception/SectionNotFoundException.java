package com.natlex.test_app.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SectionNotFoundException extends ResponseStatusException {

    public SectionNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "Section not not found");
    }

}
