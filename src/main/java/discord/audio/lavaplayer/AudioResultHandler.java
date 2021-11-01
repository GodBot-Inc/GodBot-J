package discord.audio.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord.audio.PlayerManager;
import discord.audio.QueueSystem;
import discord.snippets.Embeds.errors.NotFoundError;
import discord.snippets.Embeds.errors.StandardError;
import discord.snippets.Embeds.trackInfo.PlayTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utils.customExceptions.audio.PlayerNotFound;
import utils.customExceptions.audio.QueueEmpty;
import utils.linkProcessing.interpretations.Interpretation;

import java.awt.*;
import java.util.HashMap;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private final SlashCommandEvent event;
    public final String identifier;
    public String actionType = null;
    public boolean nowPlaying = false;

    public AudioResultHandler(AudioPlayer player, SlashCommandEvent event, String identifier) {
        this.player = player;
        this.event = event;
        this.identifier = identifier;
    }
    
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        System.out.printf("TrackLoaded %s", identifier);
        this.nowPlaying = PlayerManager.playTrack(player, audioTrack);
        this.actionType = "trackLoaded";
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        System.out.printf("playlistLoaded %s\n", identifier);
        if (audioPlaylist.getTracks().isEmpty()) {
            event.replyEmbeds(StandardError.build(String.format("Could not fetch the song %s", identifier)))
                    .setEphemeral(true)
                    .queue();
            actionType = "error";
            return;
        }
        this.nowPlaying = PlayerManager.playTrack(player, audioPlaylist.getSelectedTrack());
        this.actionType = "playlistLoaded";
    }

    @Override
    public void noMatches() {
        System.out.printf("NoMatches %s", identifier);
        actionType = "noMatches";
        event
                .replyEmbeds(
                        NotFoundError.build("Nothing found for " + this.identifier)
                )
                .queue();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        System.out.printf("LoadFailed %s", identifier);
        event
                .replyEmbeds(StandardError.build("Playing the track failed"))
                .queue();
        actionType = "loadFailed";
    }
}
