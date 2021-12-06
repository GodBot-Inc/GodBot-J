package com.godbot.utils.customExceptions.ytApi;

public class CouldNotExtractInfoException extends RuntimeException {
    public CouldNotExtractInfoException(String errorMessage) {
        super(errorMessage);
    }
}
