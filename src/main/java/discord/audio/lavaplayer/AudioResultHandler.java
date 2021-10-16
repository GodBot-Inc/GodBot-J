package discord.audio.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utils.presets.Embeds;

import javax.naming.directory.SearchResult;
import java.awt.*;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private final SlashCommandEvent event;
    private final String identifier;

    public AudioResultHandler(AudioPlayer player, SlashCommandEvent event, String identifier) {
        this.player = player;
        this.event = event;
        this.identifier = identifier;
    }

    // TODO: Replace player.playTrack with playerWrapper function or just a QueueSystem.append :D
    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        System.out.printf("TrackLoaded %s", this.identifier);
        player.playTrack(audioTrack);
        event.replyEmbeds(Embeds.playTrack(audioTrack)).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        System.out.printf("playlistLoaded %s\n", this.identifier);
        if (audioPlaylist.getTracks().isEmpty()) {
            event.replyEmbeds(Embeds.error("Could not fetch your song"))
                    .setEphemeral(true)
                    .queue();
            return;
        }
        System.out.println(audioPlaylist.getTracks().get(0));
        player.playTrack(audioPlaylist.getTracks().get(0));
        System.out.println("After started Playing");
        event.replyEmbeds(Embeds.playTrack(audioPlaylist.getTracks().get(0))).queue();
    }

    @Override
    public void noMatches() {
        System.out.printf("NoMatches %s", this.identifier);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(
                String.format(
                        "<:noTracksFound:897394939586043934> No matches for %s",
                        this.identifier
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
        System.out.printf("LoadFailed %s", this.identifier);
        event
                .replyEmbeds(Embeds.error("Loading of the track failed"))
                .setEphemeral(true)
                .queue();
    }
}
