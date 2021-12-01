package discord;

import net.dv8tion.jda.api.JDA;
import utils.customExceptions.JDANotFoundException;
import utils.logging.GeneralLogger;
import utils.logging.LoggerContent;

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
        this.logger.info(
                new LoggerContent(
                        "info",
                        "JDAManager-registerJDA",
                        "",
                        new HashMap<>() {{
                            put("botName", botName);
                        }}
                )
        );
    }

    public JDA getJDA(String botName) throws JDANotFoundException {
        if (!JDAStorage.containsKey(botName)) {
            throw new JDANotFoundException("Could not find the botName " + botName);
        }
        this.logger.info(
                new LoggerContent(
                        "info",
                        "JDAManager-getJDA",
                        "",
                        new HashMap<>() {{
                            put("botName", botName);
                        }}
                )
        );
        return JDAStorage.get(botName);
    }

    public static JDAManager getInstance() {
        return managerObj;
    }
}
