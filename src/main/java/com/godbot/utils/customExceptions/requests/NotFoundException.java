package com.godbot.utils.customExceptions.requests;

public class NotFoundException extends RequestException {
    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public NotFoundException() {
    }
}
