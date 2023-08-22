package edu.uci.ics.huanjial.frontend.configs;


import edu.uci.ics.huanjial.frontend.logger.ServiceLogger;
import edu.uci.ics.huanjial.frontend.models.ConfigsModel;
import edu.uci.ics.huanjial.frontend.FrontEnd;

public class Configs {
    private final int MIN_SERVICE_PORT = 1024;
    private final int MAX_SERVICE_PORT = 65535;
    // Default service configs
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_PORT = 6243;
    private final String DEFAULT_PATH = "/api/idm";
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "idm.log";
    // Default session configs
    private final int DEFAULT_SESSION_TIMEOUT = 600000; // 10 minutes
    private final int DEFAULT_SESSION_EXPIRATION = 1800000; // 30 minutes

    // Service configs
    private String scheme;
    private String hostName;
    private int    port;
    private String path;
    // Logger configs
    private String outputDir;
    private String outputFile;
    // Database configs
    private String dbUsername;
    private String dbPassword;
    private String dbHostname;
    private int    dbPort;
    private String dbName;
    private String dbDriver;
    private String dbSettings;

    public Configs() {
        scheme = DEFAULT_SCHEME;
        hostName = DEFAULT_HOSTNAME;
        port = DEFAULT_PORT;
        path = DEFAULT_PATH;
        outputDir = DEFAULT_OUTPUTDIR;
        outputFile = DEFAULT_OUTPUTFILE;
    }

    public Configs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException(FrontEnd.ANSI_RED + "Unable to create Configs from ConfigsModel." + FrontEnd.ANSI_RESET);
        } else {
            // Set service configs
            scheme = cm.getServiceConfig().get("scheme");
            if (scheme == null) {
                scheme = DEFAULT_SCHEME;
                System.err.println(FrontEnd.ANSI_RED + "Scheme not found in configuration file. Using default." + FrontEnd.ANSI_RESET);
            } else {
                System.err.println("Scheme: " + scheme);
            }

            hostName = cm.getServiceConfig().get("hostName");
            if (hostName == null) {
                hostName = DEFAULT_HOSTNAME;
                System.err.println(FrontEnd.ANSI_RED + "Hostname not found in configuration file. Using default." + FrontEnd.ANSI_RESET);
            } else {
                System.err.println("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getServiceConfig().get("port"));
            if (port == 0) {
                port = DEFAULT_PORT;
                System.err.println(FrontEnd.ANSI_RED + "Port not found in configuration file. Using default." + FrontEnd.ANSI_RESET);
            } else if (port < MIN_SERVICE_PORT || port > MAX_SERVICE_PORT) {
                port = DEFAULT_PORT;
                System.err.println(FrontEnd.ANSI_RED + "Port is not within valid range. Using default." + FrontEnd.ANSI_RESET);
            } else {
                System.err.println("Port: " + port);
            }

            path = cm.getServiceConfig().get("path");
            if (path == null) {
                path = DEFAULT_PATH;
                System.err.println(FrontEnd.ANSI_RED + "Path not found in configuration file. Using default." + FrontEnd.ANSI_RESET);
            } else {
                System.err.println("Path: " + path);
            }

            // Set logger configs
            outputDir = cm.getLoggerConfig().get("outputDir");
            if (outputDir == null) {
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println(FrontEnd.ANSI_RED + "Logging output directory not found in configuration file. Using default." + FrontEnd.ANSI_RESET);
            } else {
                System.err.println("Logging output directory: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if (outputFile == null) {
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println(FrontEnd.ANSI_RED + "Logging output file not found in configuration file. Using default." + FrontEnd.ANSI_RESET);
            } else {
                System.err.println("Logging output file: " + outputFile);
            }
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("Scheme: " + scheme);
        ServiceLogger.LOGGER.config("Hostname: " + hostName);
        ServiceLogger.LOGGER.config("Port: " + port);
        ServiceLogger.LOGGER.config("Path: " + path);
        ServiceLogger.LOGGER.config("Logger output directory: " + outputDir);
        ServiceLogger.LOGGER.config("Logger output file: " + outputFile);
    }

    public String getScheme() {
        return scheme;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
