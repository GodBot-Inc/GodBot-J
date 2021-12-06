package com.godbot.utils.customExceptions.requests;

public class EndpointMovedException extends RequestException {
    public EndpointMovedException(String errorMessage) {
        super(errorMessage);
    }

    public EndpointMovedException() {
    }
}
