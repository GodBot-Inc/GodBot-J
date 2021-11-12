package discord.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import utils.customExceptions.audio.PlayerNotFoundException;
import utils.customExceptions.audio.QueueEmptyException;
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

    public boolean canPlayNext(AudioPlayer player)
            throws PlayerNotFoundException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
        }
        return !queueStorage.get(player).isEmpty();
    }

    public void removePlayer(AudioPlayer player)
            throws PlayerNotFoundException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
        }
        queueStorage.remove(player);
    }

    public AudioTrack getNextAndDelete(AudioPlayer player)
            throws PlayerNotFoundException,
            QueueEmptyException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmptyException("Queue of Player " + player + " is empty");
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

    public List<AudioTrack> getQueue(AudioPlayer player)
            throws PlayerNotFoundException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
        }
        return queueStorage.get(player);
    }

    public void addTrack(AudioPlayer player, AudioTrack track)
            throws PlayerNotFoundException,
            IndexOutOfBoundsException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
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

    public void removeTrack(
            AudioPlayer player,
            AudioTrack track
    )
            throws PlayerNotFoundException,
            QueueEmptyException,
            KeyException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmptyException("Queue of Player " + player + "is empty");
        }
        if (!queueStorage.get(player).contains(track)) {
            throw new KeyException("Could not find track " + track.getInfo().title);
        }
        queueStorage.get(player).remove(track);
    }

    public void removeTrackByIndex(
            AudioPlayer player,
            int index
    )
            throws PlayerNotFoundException,
            QueueEmptyException,
            IndexOutOfBoundsException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmptyException("Queue of Player " + player + " is empty");
        }
        if (queueStorage.get(player).size() > index) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is too long for the queue size " + queueStorage.get(player).size()
            );
        }
        queueStorage.get(player).remove(index);
    }

    public void clearQueue(AudioPlayer player)
            throws PlayerNotFoundException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFoundException("Could not find Player " + player);
        }
        queueStorage.get(player).clear();
    }

    public static QueueSystem getInstance() { return queueSystemObj; }
}
