package com.godbot.utils.linkProcessing;

import com.godbot.utils.apis.youtube.YouTubeAsyncHttpReceiver;

import java.util.ArrayList;

public class AsyncHttpReceiverManager {

    private final ArrayList<YouTubeAsyncHttpReceiver> asyncHttpReceivers = new ArrayList<>();

    public void add(String url, YouTubeAsyncHttpReceiver asyncHttpReceiver) {
        LinkHelper.sendAsyncGetRequest(
                url,
                asyncHttpReceiver
        );
        asyncHttpReceivers.add(asyncHttpReceiver);
    }

    public int checkLength() {
        return asyncHttpReceivers.size();
    }

    public int getCompleted() {
        int completed = 0;
        for (YouTubeAsyncHttpReceiver receiver : asyncHttpReceivers) {
            if (receiver.isCompleted()) {
                completed++;
            }
        }
        return completed;
    }

    public ArrayList<YouTubeAsyncHttpReceiver> getAsyncHttpReceivers() {
        return asyncHttpReceivers;
    }
}
