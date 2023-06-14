package com.natlex.test_app.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FileJobNotFoundException extends ResponseStatusException {
    public FileJobNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "FileJob not not found");
    }
}
