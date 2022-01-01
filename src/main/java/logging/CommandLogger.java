package logging;

import utils.MongoCommunication;

import java.util.logging.Logger;

public class CommandLogger implements DefaultLogger {

    private final Logger logger;
    private final MongoCommunication dbCommunication;

    public CommandLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        this.dbCommunication = MongoCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        logger.info(loggerObj.getAsString());
    }

    public void infoAndSave(LoggerContent loggerObj) {
        logger.info(loggerObj.getAsString());
        dbCommunication.audioProcessLog(loggerObj.getDBScheme());
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        logger.warning(loggerObj.getAsString());
        dbCommunication.audioProcessLog(loggerObj.getDBScheme());
    }
}
