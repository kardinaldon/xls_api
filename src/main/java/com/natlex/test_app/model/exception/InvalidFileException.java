package com.natlex.test_app.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidFileException extends ResponseStatusException {
    public InvalidFileException() {
        super(HttpStatus.BAD_REQUEST, "the service is intended for files with the extension .xls");
    }
}
