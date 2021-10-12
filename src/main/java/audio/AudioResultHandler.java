package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import utils.presets.Embeds;

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

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        player.playTrack(audioTrack);
        event.replyEmbeds(Embeds.playTrack(audioTrack)).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        AudioTrack track = audioPlaylist.getTracks().get(0);
        player.playTrack(track);
        event.replyEmbeds(Embeds.playTrack(track)).queue();
    }

    @Override
    public void noMatches() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(
                String.format(
                        "<:noTracksFound:897394939586043934> No matches for %s",
                        this.identifier
                )
        );
        eb.setColor(Color.GRAY);
        event.
                replyEmbeds(eb.build()).
                setEphemeral(true).
                queue();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        event.
                replyEmbeds(Embeds.error("Loading of the track failed")).
                setEphemeral(true).
                queue();
    }
}
