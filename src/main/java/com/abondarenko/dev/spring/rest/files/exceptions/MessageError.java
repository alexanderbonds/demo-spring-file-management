package com.abondarenko.dev.spring.rest.files.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class MessageError {

    private final Date timestamp;
    private final int status;
    private final String message;

    public MessageError(final int status, final String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
