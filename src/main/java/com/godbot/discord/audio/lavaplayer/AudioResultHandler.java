package com.godbot.discord.audio.lavaplayer;

import com.godbot.discord.audio.PlayerManager;
import com.godbot.discord.snippets.Embeds.errors.NotFoundError;
import com.godbot.discord.snippets.Embeds.errors.StandardError;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private final InteractionHook interactionHook;
    private final AudioManager audioManager;
    private final VoiceChannel channel;

    public final String identifier;
    public String actionType = null;
    public boolean nowPlaying = false;
    public AudioTrack audioTrack;

    public AudioResultHandler(
            AudioPlayer player,
            InteractionHook interactionHook,
            AudioManager audioManager,
            VoiceChannel channel,
            String identifier) {
        this.player = player;
        this.interactionHook = interactionHook;
        this.audioManager = audioManager;
        this.channel = channel;
        this.identifier = identifier;
    }
    
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        System.out.printf("TrackLoaded %s", identifier);
        this.actionType = "trackLoaded";
        audioManager.openAudioConnection(channel);
        this.nowPlaying = PlayerManager.playTrack(player, audioTrack);
        this.audioTrack = audioTrack;
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        System.out.printf("playlistLoaded %s\n", identifier);
        if (audioPlaylist.getTracks().isEmpty()) {
            interactionHook.editOriginalEmbeds(
                    StandardError.build(
                            String.format("Could not fetch the song %s", identifier)
                    ))
                    .queue();
            actionType = "error";
            return;
        }
        this.actionType = "playlistLoaded";
        audioManager.openAudioConnection(channel);
        this.nowPlaying = PlayerManager.playTrack(player, audioPlaylist.getTracks().get(0));
        this.audioTrack = audioPlaylist.getTracks().get(0);
    }

    @Override
    public void noMatches() {
        System.out.printf("NoMatches %s", identifier);
        actionType = "noMatches";
        interactionHook
                .editOriginalEmbeds(
                        NotFoundError.build(String.format(
                                "Track could not be found %s",
                                identifier
                        ))
                )
                .queue();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        System.out.printf("LoadFailed %s", identifier);
        actionType = "loadFailed";
        interactionHook
                .editOriginalEmbeds(
                        StandardError.build(String.format(
                                "The track could not be loaded %s",
                                identifier
                        ))
                )
                .queue();
    }
}
