package lavaplayerHandlers;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import logging.AudioLogger;
import logging.LoggerContent;
import net.dv8tion.jda.api.entities.Member;
import utils.AudioPlayerExtender;
import utils.AudioTrackExtender;

import java.util.HashMap;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final AudioPlayerExtender audioPlayer;
    private final Member requester;
    private final AudioLogger logger = new AudioLogger("AudioResultHandlerLogger");

    /*
     1 -> TrackLoaded
     2 -> PlaylistLoaded
     3 -> NoMatches
     4 -> NotLoaded
     10 -> error
     */
    public int actionType = 0;
    public int position = 0;

    public AudioResultHandler(
            AudioPlayerExtender audioPlayer,
            Member member
    ) {
        this.audioPlayer = audioPlayer;
        this.requester = member;
    }
    
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        this.actionType = 1;
        audioPlayer.openConnection();
        position = audioPlayer.play(
                new AudioTrackExtender(
                        audioTrack,
                        new HashMap<>(),
                        requester
                )
        );
        logger.info(
                new LoggerContent(
                        "info",
                        "trackLoaded",
                        "",
                        new HashMap<>() {{
                            put("Track", audioTrack.getInfo().title);
                            put("Position", String.valueOf(position));
                        }}
                )
        );
    }
    
    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        this.actionType = 2;
        if (audioPlaylist.getTracks().isEmpty()) {
            this.actionType = 10;
            return;
        }
        audioPlayer.openConnection();
        position = audioPlayer.play(
                new AudioTrackExtender(
                        audioPlaylist.getTracks().get(0),
                        new HashMap<>(),
                        requester
                )
        );
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
