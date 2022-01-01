package singeltons;

import logging.GeneralLogger;
import logging.LoggerContent;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class JDAManager {

    private static final JDAManager managerObj = new JDAManager();
    private final GeneralLogger logger;

    //               ApplicationId BotJDA
    private final HashMap<String, JDA> JDAStorage = new HashMap<>();

    private JDAManager() {
        this.logger = new GeneralLogger(this.getClass().getName() + "Logger");
    }

    public void registerJDA(String applicationId, JDA jda) {
        JDAStorage.put(applicationId, jda);
        this.logger.info(
                new LoggerContent(
                        "info",
                        "JDAManager-registerJDA",
                        "",
                        new HashMap<>() {{
                            put("applicationId", applicationId);
                        }}
                )
        );
    }

    @Nullable
    public JDA getJDA(String applicationId) {
        if (!JDAStorage.containsKey(applicationId)) {
            return null;
        }
        this.logger.info(
                new LoggerContent(
                        "info",
                        "JDAManager-getJDA",
                        "",
                        new HashMap<>() {{
                            put("applicationId", applicationId);
                        }}
                )
        );
        return JDAStorage.get(applicationId);
    }

    public static JDAManager getInstance() {
        return managerObj;
    }
}
