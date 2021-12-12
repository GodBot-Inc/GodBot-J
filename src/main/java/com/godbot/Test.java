package com.godbot;

import com.godbot.utils.apis.youtube.YoutubeApi;
import com.godbot.utils.interpretations.youtube.YoutubePlaylistInterpretation;

public class Test {
    public static void main(String[] args) {
        YoutubePlaylistInterpretation youtubePlaylistInterpretation;
        try {
            youtubePlaylistInterpretation =
                    YoutubeApi.getPlaylistInfoAsync("PLKfD8K0QKDy842ZyemShYfzYx3nRflHUu");
        } catch(Exception e) {
            System.out.println("oh hell no");
            return;
        }
        System.out.println(youtubePlaylistInterpretation);
    }
}
