package utils.logging;

import utils.LoggerObj;

import java.util.logging.Logger;

public class CommandLogger implements DefaultLogger{

    private static final CommandLogger loggerObj = new CommandLogger();
    private final Logger logger;

    private CommandLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void info(LoggerObj loggerObj) {
        this.logger.info(loggerObj.getAsString());

    }

    public static CommandLogger getInstance() { return loggerObj; }
}
