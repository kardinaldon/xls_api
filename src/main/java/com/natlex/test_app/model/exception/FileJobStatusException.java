package com.natlex.test_app.model.exception;

import com.natlex.test_app.model.enumeration.FileJobStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FileJobStatusException  extends ResponseStatusException {
    public FileJobStatusException(FileJobStatus fileJobStatus) {
        super(HttpStatus.FORBIDDEN
                , "downloading the file is not possible at the moment, because the status of the operation is "
                        + fileJobStatus
        );
    }
}
