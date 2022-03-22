package com.abondarenko.dev.spring.rest.files.exceptions.implementations;

import com.abondarenko.dev.spring.rest.files.exceptions.AbstractException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AbstractException {
    public ResourceNotFoundException(final String message) {
        super(message);
    }

    @Override
    protected HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
