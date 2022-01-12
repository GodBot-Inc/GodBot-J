package lavaplayerHandlers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import logging.AudioLogger;
import logging.LoggerContent;

import java.util.HashMap;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final AudioLogger logger = new AudioLogger("AudioResultHandlerLogger");

    /*
     1 -> TrackLoaded
     2 -> PlaylistLoaded
     3 -> NoMatches
     4 -> NotLoaded
     10 -> error
     */
    public int actionType = 0;
    public AudioTrack audioTrack;
    
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        this.actionType = 1;
        this.audioTrack = audioTrack;
        logger.info(
                new LoggerContent(
                        "info",
                        "trackLoaded",
                        "",
                        new HashMap<>() {{
                            put("Track", audioTrack.getInfo().title);
                        }}
                )
        );
    }
    
    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        if (audioPlaylist.getTracks().isEmpty()) {
            this.actionType = 10;
            return;
        }
        this.actionType = 2;
        this.audioTrack = audioPlaylist.getTracks().get(0);
    }

    @Override
    public void noMatches() {
        this.actionType = 3;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        this.actionType = 4;
    }
}
