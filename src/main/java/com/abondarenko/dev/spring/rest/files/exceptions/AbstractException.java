package com.abondarenko.dev.spring.rest.files.exceptions;

import org.springframework.http.HttpStatus;

public abstract class AbstractException extends RuntimeException {
    protected AbstractException(final String message) {
        super(message);
    }

    protected abstract HttpStatus getHttpStatus();
}
