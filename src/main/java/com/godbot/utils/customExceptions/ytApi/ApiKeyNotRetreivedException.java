package com.godbot.utils.customExceptions.ytApi;

public class ApiKeyNotRetreivedException extends RuntimeException {
    public ApiKeyNotRetreivedException(String errorMessage) {
        super(errorMessage);
    }
}
