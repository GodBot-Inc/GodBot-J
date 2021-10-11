package utils;

import net.dv8tion.jda.api.JDA;
import utils.loggers.JDAManagerLogger;

import java.security.KeyException;
import java.util.HashMap;

public class JDAManager {

    private static final JDAManager managerObj = new JDAManager();
    private final JDAManagerLogger logger;

    //
    private final HashMap<String, JDA> JDAStorage = new HashMap<>();

    private JDAManager() {
        logger = JDAManagerLogger.getInstance();
    }

    public void registerJDA(String botName, JDA jda) {
        JDAStorage.put(botName, jda);
        logger.info("registerJDA|botName-" + botName);
    }

    public JDA getJDA(String botName) throws KeyException {
        if (!JDAStorage.containsKey(botName)) {
            throw new KeyException("Could not find the botName " + botName);
        }
        logger.info("getJDA|botName-" + botName);
        return JDAStorage.get(botName);
    }

    public static JDAManager getInstance() {
        return managerObj;
    }
}
