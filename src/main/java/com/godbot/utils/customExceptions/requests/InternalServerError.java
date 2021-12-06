package com.godbot.utils.customExceptions.requests;

public class InternalServerError extends RequestException {
    public InternalServerError(String errorMessage) {
        super(errorMessage);
    }

    public InternalServerError() {
    }
}
