package discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.commons.collections4.map.HashedMap;
import utils.customExceptions.audio.PlayerNotFound;
import utils.customExceptions.audio.QueueEmpty;
import utils.logging.AudioLogger;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.security.KeyException;
import java.util.*;
import utils.logging.LoggerContent;

public class QueueSystem {

    private final HashMap<AudioPlayer, List<AudioTrack>> queueStorage = new HashMap<>();
    private final AudioLogger logger;
    private static final QueueSystem queueSystemObj = new QueueSystem();

    private QueueSystem() {
        logger = new AudioLogger(this.getClass().getName() + "Logger");
    }

    public void registerPlayer(AudioPlayer player) throws KeyAlreadyExistsException {
        if (queueStorage.containsKey(player)) {
            throw new KeyAlreadyExistsException("Player already registered " + player);
        }
        this.logger.info(
                new LoggerContent(
                        "info",
                        "QueueSystem-registerPlayer",
                        "",
                        new HashMap<>()
                )
        );
        queueStorage.put(player, new ArrayList<>());
    }

    public void removePlayer(AudioPlayer player) throws PlayerNotFound {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        queueStorage.remove(player);
    }

    public AudioTrack getNextAndDelete(AudioPlayer player) throws PlayerNotFound, QueueEmpty {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmpty("Queue of Player " + player + " is empty");
        }
        this.logger.info(
            new LoggerContent(
                    "info",
                    "QueueSystem-getNextAndDelete",
                    "",
                    new HashMap<String, String>() {{
                        put("track", queueStorage.get(player).get(0).getInfo().title);
                    }}
            )
        );
        AudioTrack next = queueStorage.get(player).get(0);
        queueStorage.get(player).remove(0);
        return next;
    }

    public List<AudioTrack> getQueue(AudioPlayer player) throws PlayerNotFound {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        return queueStorage.get(player);
    }

    public void addTrack(AudioPlayer player, AudioTrack track) throws PlayerNotFound, IndexOutOfBoundsException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        this.logger.info(
                new LoggerContent(
                        "info",
                        "QueueSystem-addTrack",
                        "",
                        new HashMap<String, String>() {{
                            put("track", track.getInfo().title);
                        }}
                )
        );
        queueStorage.get(player).add(track);
    }

    public void removeTrack(AudioPlayer player, AudioTrack track) throws PlayerNotFound, QueueEmpty, KeyException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmpty("Queue of Player " + player + "is empty");
        }
        if (!queueStorage.get(player).contains(track)) {
            throw new KeyException("Could not find track " + track.getInfo().title);
        }
        queueStorage.get(player).remove(track);
    }

    public void removeTrackByIndex(AudioPlayer player, int index) throws PlayerNotFound, QueueEmpty, IndexOutOfBoundsException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmpty("Queue of Player " + player + " is empty");
        }
        if (queueStorage.get(player).size() > index) {
            throw new IndexOutOfBoundsException("Index " + index + " is too long for the queue size " + queueStorage.get(player).size());
        }
        queueStorage.get(player).remove(index);
    }

    public static QueueSystem getInstance() { return queueSystemObj; }
}