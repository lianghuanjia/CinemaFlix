package edu.uci.ics.huanjial.service.movies.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;


public class ServiceLogger {
    public static final Logger LOGGER = Logger.getLogger(ServiceLogger.class.getName());
    private static FileHandler fileHandler;
    private static Formatter formatter;

    public static void initLogger(String outputDir, String outputFile) throws IOException {
        // Remove the default ConsoleHandler
        LOGGER.getParent().removeHandler(LOGGER.getParent().getHandlers()[0]);
        try {
            // Create directory for logs
            File logDir = new File(outputDir);
            if ( !(logDir.exists()) ) {
                logDir.mkdir();
            }

            // Create FileHandler
            fileHandler = new FileHandler(outputDir + outputFile);
            // Create simple formatter
            formatter = new ServiceFormatter();
            // Assign handler to logger
            LOGGER.addHandler(fileHandler);
            // Set formatter to the handler
            fileHandler.setFormatter(formatter);
            // Create new ConsoleHandler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.CONFIG);
            LOGGER.addHandler(consoleHandler);
            consoleHandler.setFormatter(formatter);

            // Setting Level to ALL
            fileHandler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);

            LOGGER.config("Logging initialized.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to initialize logging. Service terminating.");
        }
    }
}
