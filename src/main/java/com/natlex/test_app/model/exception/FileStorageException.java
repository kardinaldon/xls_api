package com.natlex.test_app.model.exception;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String massage) {
        super(massage);
    }
}
