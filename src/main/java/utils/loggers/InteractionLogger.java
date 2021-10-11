package utils.loggers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class InteractionLogger {

    private static final InteractionLogger loggerObj = new InteractionLogger();
    private final Logger logger;

    private InteractionLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public static InteractionLogger getInstance() { return loggerObj; }

    public void info(String commandIdentifier, HashMap<String, String> args) {
        StringBuilder finalString = new StringBuilder(commandIdentifier);
        for (Map.Entry<String, String> entry : args.entrySet()) {
            finalString.append(String.format("|%s-%s", entry.getKey(), entry.getValue()));
        }
        logger.info(finalString.toString());
    }

    public void warn(String warnMessage) {
        logger.warning(warnMessage);
    }

}
