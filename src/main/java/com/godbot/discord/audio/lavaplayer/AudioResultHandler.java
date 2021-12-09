package com.godbot.discord.audio.lavaplayer;

import com.godbot.discord.audio.AudioPlayerManagerWrapper;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private final AudioManager audioManager;
    private final VoiceChannel channel;

    private final String identifier;
    /*
     1 -> TrackLoaded
     2 -> PlaylistLoaded
     3 -> NoMatches
     4 -> NotLoaded
     10 -> error
     */
    private int actionType = 0;
    private AudioTrack audioTrack;
    private AudioPlaylist audioPlaylist;
    private boolean nowPlaying;

    public AudioResultHandler(
            AudioPlayer player,
            AudioManager audioManager,
            VoiceChannel channel,
            String identifier
    ) {
        this.player = player;
        this.audioManager = audioManager;
        this.channel = channel;
        this.identifier = identifier;
    }
    
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        System.out.printf("TrackLoaded %s", identifier);
        this.actionType = 1;
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(channel);
        }
        nowPlaying = AudioPlayerManagerWrapper.playTrack(player, audioTrack);
    }
    
    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        System.out.printf("playlistLoaded %s\n", identifier);
        this.actionType = 2;
        if (audioPlaylist.getTracks().isEmpty()) {
            this.actionType = 10;
            return;
        }
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(channel);
        }
        nowPlaying = AudioPlayerManagerWrapper.playTrack(player, audioPlaylist.getTracks().get(0));
    }

    @Override
    public void noMatches() {
        this.actionType = 3;
        System.out.printf("NoMatches %s", identifier);
    }

    @Override
    public void loadFailed(FriendlyException e) {
        this.actionType = 4;
        System.out.printf("LoadFailed %s", identifier);
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getActionType() {
        return actionType;
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public AudioPlaylist getAudioPlaylist() {
        return audioPlaylist;
    }

    public boolean isNowPlaying() {
        return nowPlaying;
    }
}
