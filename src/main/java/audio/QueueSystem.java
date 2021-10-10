package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sun.org.apache.xml.internal.security.keys.ContentHandlerAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import utils.CustomExceptions.audio.PlayerNotFound;
import utils.CustomExceptions.audio.QueueEmpty;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.IOException;
import java.security.KeyException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class QueueSystem {

    private final HashMap<AudioPlayer, List<AudioTrack>> queueStorage = new HashMap<>();
    private Logger logger;
    private static final QueueSystem queueSystemObj = new QueueSystem();

    private Logger getLogger() throws IOException {
        Dotenv env = Dotenv.load();

        String dir = env.get("LOGGER_DIR");

        Logger logger = Logger.getLogger("QueueSystemLogger");
        FileHandler fh = new FileHandler(dir + "\\QueueSystem.log");
        SimpleFormatter formatter = new SimpleFormatter();

        logger.addHandler(fh);
        fh.setFormatter(formatter);
        return logger;
    }

    public void registerPlayer(AudioPlayer player) throws KeyAlreadyExistsException {
        if (queueStorage.containsKey(player)) {
            throw new KeyAlreadyExistsException("Player already registered " + player);
        }
        queueStorage.put(player, new ArrayList<AudioTrack>());
        logger.info("Registered Player " + player);
    }

    public void removePlayer(AudioPlayer player) throws PlayerNotFound {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        queueStorage.remove(player);
        logger.info("Removed Player " + player);
    }

    public AudioTrack getNextAndDelete(AudioPlayer player) throws PlayerNotFound, QueueEmpty {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmpty("Queue of Player " + player + " is empty");
        }
        logger.info("Returned Track " + queueStorage.get(player).get(0).getInfo().title + " and removed it");
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

    public void addTrack(AudioPlayer player, AudioTrack track, int index) throws PlayerNotFound, IndexOutOfBoundsException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        if (queueStorage.get(player).size() > index) {
            throw new IndexOutOfBoundsException("Index" + index + " is too large for a list of size " + queueStorage.get(player).size());
        }
        logger.info("Queued Track " + track.getInfo().title);
        queueStorage.get(player).add(track);
    }

    public void removeTrack(AudioPlayer player, AudioTrack track) throws PlayerNotFound, QueueEmpty, KeyResolverException {
        if (!queueStorage.containsKey(player)) {
            throw new PlayerNotFound("Could not find Player " + player);
        }
        if (queueStorage.get(player).isEmpty()) {
            throw new QueueEmpty("Queue of Player " + player + "is empty");
        }
        if (!queueStorage.get(player).contains(track)) {
            throw new KeyResolverException("Could not find track " + track.getInfo().title);
        }
        logger.info("Removed track " + track.getInfo().title);
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
        logger.info("Removed Track " + queueStorage.get(player).get(index).getInfo().title);
        queueStorage.get(player).remove(index);
    }

    private QueueSystem() {
        try {
            logger = getLogger();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static QueueSystem getInstance() { return queueSystemObj; }
}
