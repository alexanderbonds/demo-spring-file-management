package com.abondarenko.dev.spring.rest.files.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdviceExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleException(final AbstractException e) {
        log.error("Got an error: {}", e.getMessage());

        final MessageError err = new MessageError(e.getHttpStatus().value(), e.getMessage());
        return new ResponseEntity<>(err, e.getHttpStatus());
    }
}
