package com.godbot.utils.logging;

import java.util.logging.Logger;

public class AudioLogger implements DefaultLogger {

    private final Logger logger;

    public AudioLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    @Override
    public void info(LoggerContent loggerObj) {
        this.logger.info(loggerObj.getAsString());
    }

    @Override
    public void warn(LoggerContent loggerObj) {
        this.logger.warning(loggerObj.getAsString());
    }
}
