package com.natlex.test_app.model.exception;


public class FileHandlingException extends RuntimeException {
    public FileHandlingException(String massage) {
        super(massage);
    }
}
