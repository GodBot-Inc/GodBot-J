package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.HashMap;
import java.util.List;

public class QueueSystem {

    private final HashMap<AudioPlayer, List<AudioTrack>> queueStorage = new HashMap<>();



    public AudioTrack getNextSong(Integer guildID, Integer channelID, AudioPlayer player) {
    }
}
