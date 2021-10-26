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
    private final String identifier;
    private final HashMap<String, Interpretation> interpretations;

    public AudioResultHandler(AudioPlayer player, SlashCommandEvent event, String identifier, HashMap<String, Interpretation> interpretations) {
        this.player = player;
        this.event = event;
        this.identifier = identifier;
        this.interpretations = interpretations;
    }
    
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        QueueSystem qSystem = QueueSystem.getInstance();
        System.out.printf("TrackLoaded %s", identifier);
        boolean nowPlaying = PlayerManager.playTrack(player, audioTrack);
        event
                .replyEmbeds(
                        PlayTrack.build(
                                audioTrack,
                                event.getMember(),
                                nowPlaying,
                                interpretations
                        )
                )
                .queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        System.out.printf("playlistLoaded %s\n", identifier);
        if (audioPlaylist.getTracks().isEmpty()) {
            event.replyEmbeds(StandardError.build(String.format("Could not fetch the song %s", identifier)))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        QueueSystem qSystem = QueueSystem.getInstance();
        boolean nowPlaying = PlayerManager.playTrack(player, audioPlaylist.getSelectedTrack());
        event.replyEmbeds(
                PlayTrack.build(
                        audioPlaylist.getSelectedTrack(),
                        event.getMember(),
                        nowPlaying,
                        interpretations
                )
        )
                .queue();
    }

    @Override
    public void noMatches() {
        System.out.printf("NoMatches %s", identifier);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(
                String.format(
                        "<:noTracksFound:897394939586043934> No matches for %s",
                        identifier
                )
        );
        eb.setColor(Color.GRAY);
        event
                .replyEmbeds(eb.build())
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        System.out.printf("LoadFailed %s", identifier);
        event
                .replyEmbeds(StandardError.build("Playing the track failed"))
                .setEphemeral(true)
                .queue();
    }
}
