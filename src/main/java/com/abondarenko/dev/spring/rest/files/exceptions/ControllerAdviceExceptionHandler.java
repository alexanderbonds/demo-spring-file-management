package com.abondarenko.dev.spring.rest.files.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdviceExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleException(final AbstractException e) {
        final MessageError err = new MessageError(e.getHttpStatus().value(), e.getMessage());
        return new ResponseEntity<>(err, e.getHttpStatus());
    }
}
