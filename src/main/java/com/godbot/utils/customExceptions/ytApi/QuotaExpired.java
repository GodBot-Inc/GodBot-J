package com.godbot.utils.customExceptions.ytApi;

public class QuotaExpired extends RuntimeException {
    public QuotaExpired(String message) {
        super(message);
    }

    public QuotaExpired() {
    }
}
