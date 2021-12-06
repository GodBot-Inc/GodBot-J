package com.godbot.utils.customExceptions.requests;

public class RequestException extends RuntimeException {
    public RequestException(String errorMessage) {
        super(errorMessage);
    }

    public RequestException() {
    }
}
