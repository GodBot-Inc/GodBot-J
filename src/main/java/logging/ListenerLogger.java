package logging;

import utils.MongoCommunication;

import java.util.logging.Logger;

public class ListenerLogger implements DefaultLogger{

    private final Logger logger;
    private final MongoCommunication dbCommunication;

    public ListenerLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        dbCommunication = MongoCommunication.getInstance();
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    public void infoAndSave(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }
}
