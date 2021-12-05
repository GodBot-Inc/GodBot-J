package com.godbot.utils.customExceptions.LinkInterpretation;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String errorMessage) {
        super(errorMessage);
    }
}
