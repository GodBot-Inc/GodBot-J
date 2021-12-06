package com.godbot.utils.customExceptions.requests;

public class RateLimitException extends RequestException {
    public RateLimitException(String errorMessage) {
        super(errorMessage);
    }

    public RateLimitException() {
    }
}
