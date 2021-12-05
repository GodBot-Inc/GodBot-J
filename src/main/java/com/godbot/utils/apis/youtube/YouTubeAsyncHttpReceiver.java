package com.godbot.utils.apis.youtube;

import com.godbot.utils.audio.DurationCalc;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class YouTubeAsyncHttpReceiver extends AsyncCompletionHandler<Object> {
    private JSONObject response;
    private boolean completed = false;
    private long videoDuration = 0;

    @Override
    public Object onCompleted(Response response) {
        System.out.println("completed");
        JSONObject jsonResponse = new JSONObject(response.getResponseBody());
        this.response = jsonResponse;
        try {
            System.out.println(jsonResponse);
            System.out.println(
                    jsonResponse
                            .getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONObject("contentDetails")
                            .getString("duration")
            );
            videoDuration = DurationCalc.ytStringToLong(
                    jsonResponse
                            .getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONObject("contentDetails")
                            .getString("duration")
            );
        } catch (JSONException ignore) {
            System.out.println("JSONEXCEPTION " + response.getResponseBody());
        }
        this.completed = true;
        return response;
    }

    public JSONObject getResponse() {
        return response;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getVideoDuration() {
        return videoDuration;
    }
}
