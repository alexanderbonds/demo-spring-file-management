package com.abondarenko.dev.spring.rest.files.exceptions.implementations;

import com.abondarenko.dev.spring.rest.files.exceptions.AbstractException;
import org.springframework.http.HttpStatus;

public class FileServerException extends AbstractException {
    public FileServerException(final String message) {
        super(message);
    }

    @Override
    protected HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
