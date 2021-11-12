package discord.audio.lavaplayer;

import discord.audio.QueueSystem;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import utils.customExceptions.audio.PlayerNotFoundException;
import utils.customExceptions.audio.QueueEmptyException;

public class TrackEventListener extends AudioEventAdapter {
    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            QueueSystem queue = QueueSystem.getInstance();
            try {
                if (queue.canPlayNext(player)) {
                    player.playTrack(queue.getNextAndDelete(player));
                }
            } catch (PlayerNotFoundException e) {
                queue.registerPlayer(player);
                onTrackEnd(player, track, endReason);
            } catch (QueueEmptyException ignore) {}
        } else {
            System.out.println(endReason.name());
        }
        System.out.println("Track Ended " + track.getInfo().title);

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any discord.audio, might want to just start a new track
    }
}
