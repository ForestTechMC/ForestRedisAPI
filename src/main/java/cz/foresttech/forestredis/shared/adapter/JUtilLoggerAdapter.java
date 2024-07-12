package cz.foresttech.forestredis.shared.adapter;

import java.util.logging.Logger;

public class JUtilLoggerAdapter implements ILoggerAdapter {

    private final Logger logger;

    public JUtilLoggerAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }
}
