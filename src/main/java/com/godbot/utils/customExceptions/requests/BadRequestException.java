package com.godbot.utils.customExceptions.requests;

public class BadRequestException extends RequestException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException() {
    }
}
