package utils;

import net.dv8tion.jda.api.JDA;
import utils.loggers.DefaultLogger;

import java.security.KeyException;
import java.util.HashMap;

public class JDAManager {

    private static final JDAManager managerObj = new JDAManager();
    private final DefaultLogger logger;

    //
    private final HashMap<String, JDA> JDAStorage = new HashMap<>();

    private JDAManager() {
        logger = new DefaultLogger(this.getClass().getName() + "Logger");
    }

    public void registerJDA(String botName, JDA jda) {
        JDAStorage.put(botName, jda);
        logger.info("registerJDA", new HashMap<String, String>() {{ put("botName", botName); }});
    }

    public JDA getJDA(String botName) throws KeyException {
        if (!JDAStorage.containsKey(botName)) {
            throw new KeyException("Could not find the botName " + botName);
        }
        logger.info("getJDA", new HashMap<String, String>() {{ put("botName", botName); }});
        return JDAStorage.get(botName);
    }

    public static JDAManager getInstance() {
        return managerObj;
    }
}
