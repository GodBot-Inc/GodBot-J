package utils.logging;

import utils.DBCommunication;

import java.util.logging.Logger;

public class CommandLogger implements DefaultLogger{

    private static final CommandLogger loggerObj = new CommandLogger();
    private final Logger logger;
    private final DBCommunication dbCommunication;

    private CommandLogger() {
        this.logger = Logger.getLogger(this.getClass().getName());
        dbCommunication = DBCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
        dbCommunication.commandLog(loggerObj);
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
        dbCommunication.audioProcessLog(loggerObj);
    }

    public static CommandLogger getInstance() { return loggerObj; }
}
