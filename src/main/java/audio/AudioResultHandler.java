package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class AudioResultHandler implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private final SlashCommandEvent event;

    public AudioResultHandler(AudioPlayer player, SlashCommandEvent event) {
        this.player = player;
        this.event = event;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {

    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {

    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException e) {

    }
}
