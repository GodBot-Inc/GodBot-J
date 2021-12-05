package com.godbot.utils.linkProcessing;

import org.asynchttpclient.AsyncCompletionHandler;
import org.json.JSONObject;

public abstract class AsyncHttpReceiver extends AsyncCompletionHandler<Object> {
    protected JSONObject response;
    protected boolean completed = false;

    public JSONObject getResponse() {
        return response;
    }

    public boolean getCompleted() {
        return completed;
    }
}
