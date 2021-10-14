package discord;

import net.dv8tion.jda.api.JDA;
import utils.logging.GeneralLogger;
import utils.logging.LoggerContent;

import java.security.KeyException;
import java.util.HashMap;

public class JDAManager {

    private static final JDAManager managerObj = new JDAManager();
    private final GeneralLogger logger;

    //                   BotName Destination
    private final HashMap<String, JDA> JDAStorage = new HashMap<>();

    private JDAManager() {
        this.logger = new GeneralLogger(this.getClass().getName() + "Logger");
    }

    public void registerJDA(String botName, JDA jda) {
        JDAStorage.put(botName, jda);
        logger.info(new LoggerContent("registerJDA", new HashMap<String, String>() {{put("botName", botName);}}, "info"));
    }

    public JDA getJDA(String botName) throws KeyException {
        if (!JDAStorage.containsKey(botName)) {
            throw new KeyException("Could not find the botName " + botName);
        }
        logger.info(new LoggerContent("getJDA", new HashMap<String, String>() {{put("botName", botName);}}, "info"));
        return JDAStorage.get(botName);
    }

    public static JDAManager getInstance() {
        return managerObj;
    }
}
