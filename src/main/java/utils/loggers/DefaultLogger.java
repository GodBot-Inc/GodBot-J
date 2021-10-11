package utils.loggers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DefaultLogger {

    private final Logger logger;

    public DefaultLogger(String loggerName) {
        logger = Logger.getLogger(loggerName);
    }

    public void info(String methodName, HashMap<String, String> fields) {
        StringBuilder finalString = new StringBuilder(methodName);
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            finalString.append(String.format("|%s-%s", entry.getKey(), entry.getValue()));
        }
        logger.info(finalString.toString());
    }

    public void warn(String warnMessage) {
        logger.warning(warnMessage);
    }
}
