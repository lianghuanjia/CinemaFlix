package edu.uci.ics.huanjial.service.api_gateway.configs;

import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.GatewayService;

import static edu.uci.ics.huanjial.service.api_gateway.GatewayService.ANSI_RED;
import static edu.uci.ics.huanjial.service.api_gateway.GatewayService.ANSI_RESET;

public class GatewayConfigs {
    public static final int MIN_SERVICE_PORT = 1024;
    public static final int MAX_SERVICE_PORT = 65535;

    // Default gateway configs
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_PORT = 6243;
    private final String DEFAULT_PATH = "/api/g";
    private final int    DEFAULT_DELAY = 500;
    private final int    DEFAULT_THREADS = 3;
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "gateway.log";
    // Default database configs
    public final int    DEFAULT_CONNECTIONS = 3;

    // Gateway configs
    private String scheme;
    private String hostName;
    private int port;
    private String path;
    private int requestDelay;
    private int numThreads;
    // Logger configs
    private String outputDir;
    private String outputFile;
    // Database configs
    private int dbConnections;
    private String dbUsername;
    private String dbPassword;
    private String dbHostname;
    private int dbPort;
    private String dbName;
    private String dbDriver;
    private String dbSettings;

    private boolean dbConfigValid = true;

    public GatewayConfigs() { }

    public GatewayConfigs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            ServiceLogger.LOGGER.severe("ConfigsModel not found.");
            throw new NullPointerException("ConfigsModel not found.");
        } else {
            // Set service configs
            scheme = cm.getGatewayConfig().get("scheme");
            if (scheme == null) {
                scheme = DEFAULT_SCHEME;
                System.err.println(ANSI_RED + "Scheme not found in configuration file. Using default." + ANSI_RESET);
            } else {
                System.err.println("Scheme: " + scheme);
            }

            hostName = cm.getGatewayConfig().get("hostName");
            if (hostName == null) {
                hostName = DEFAULT_HOSTNAME;
                System.err.println(ANSI_RED + "Hostname not found in configuration file. Using default." + ANSI_RESET);
            } else {
                System.err.println("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getGatewayConfig().get("port"));
            if (port == 0) {
                port = DEFAULT_PORT;
                System.err.println(ANSI_RED + "Port not found in configuration file. Using default." + ANSI_RESET);
            } else if (port < MIN_SERVICE_PORT || port > MAX_SERVICE_PORT) {
                port = DEFAULT_PORT;
                System.err.println(ANSI_RED + "Port is not within valid range. Using default." + ANSI_RESET);
            } else {
                System.err.println("Port: " + port);
            }

            path = cm.getGatewayConfig().get("path");
            if (path == null) {
                path = DEFAULT_PATH;
                System.err.println(ANSI_RED + "Path not found in configuration file. Using default." + ANSI_RESET);
            } else {
                System.err.println("Path: " + path);
            }

            requestDelay = Integer.parseInt(cm.getGatewayConfig().get("requestDelay"));
            if (requestDelay <= 0) {
                requestDelay = 500;
                System.err.println(ANSI_RED + "Request delay not found in configuration file. Using default." + ANSI_RESET);
            } else {
                System.err.println("Request Delay: " + requestDelay);
            }

            numThreads = Integer.parseInt(cm.getGatewayConfig().get("numThreads"));
            if (numThreads <= 0) {
                numThreads = DEFAULT_THREADS;
                System.err.println(ANSI_RED + "Number of threads not found in configuration file. Using default." + ANSI_RESET);
            } else {
                System.err.println("Number of threads: " + numThreads);
            }

            // Set logger configs
            outputDir = cm.getLoggerConfig().get("outputDir");
            if (outputDir == null) {
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println(ANSI_RED + "Logging output directory not found in configuration file. Using default." + ANSI_RESET);
            } else {
                System.err.println("Logging output directory: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if (outputFile == null) {
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println(ANSI_RED + "Logging output file not found in configuration file. Using default." + ANSI_RESET);
            } else {
                System.err.println("Logging output file: " + outputFile);
            }

            // Set database configs
            dbConnections = Integer.parseInt(cm.getDatabaseConfig().get("dbConnections"));
            if (dbConnections < 1) {
                dbConnections = 3;
                System.err.println(ANSI_RED + "Number of connections has invalid value." + ANSI_RESET);
            } else {
                System.err.println("Database connection: " + dbConnections);
            }

            dbUsername = cm.getDatabaseConfig().get("dbUsername");
            if (dbUsername == null) {
                System.err.println(ANSI_RED + "No database username found in configuration file." + ANSI_RESET);
                dbConfigValid = false;
            } else {
                System.err.println("Database username: " + dbUsername);
            }

            dbPassword = cm.getDatabaseConfig().get("dbPassword");
            if (dbPassword == null) {
                System.err.println(ANSI_RED + "No database password found in configuration file." + ANSI_RESET);
                dbConfigValid = false;
            } else {
                System.err.println("Database password found in configuration file.");
            }

            dbHostname = cm.getDatabaseConfig().get("dbHostname");
            if (dbHostname == null) {
                System.err.println(ANSI_RED + "No database hostname found in configuration file." + ANSI_RESET);
                dbConfigValid = false;
            } else {
                System.err.println("Database hostname: " + dbHostname);
            }

            dbPort = Integer.parseInt(cm.getDatabaseConfig().get("dbPort"));
            if (dbPort == 0) {
                System.err.println(ANSI_RED + "No database port found in configuration file." + ANSI_RESET);
                dbConfigValid = false;
            } else if (dbPort < MIN_SERVICE_PORT || dbPort > MAX_SERVICE_PORT) {
                System.err.println(ANSI_RED + "Database port is not within a valid range." + ANSI_RESET);
                dbConfigValid = false;
            } else {
                System.err.println("Database port: " + dbPort);
            }

            dbName = cm.getDatabaseConfig().get("dbName");
            if (dbName == null) {
                System.err.println(ANSI_RED + "No database name found in configuration file." + ANSI_RESET);
                dbConfigValid = false;
            } else {
                System.err.println("Database name: " + dbName);
            }

            dbDriver = cm.getDatabaseConfig().get("dbDriver");
            if (dbDriver == null) {
                System.err.println(ANSI_RED + "No driver found in configuration file." + ANSI_RESET);
                dbConfigValid = false;
            } else {
                System.err.println("Database driver: " + dbDriver);
            }

            dbSettings = cm.getDatabaseConfig().get("dbSettings");
            if (dbSettings == null) {
                System.err.println(ANSI_RED + "No connection settings found in configuration file." + ANSI_RESET);
                dbConfigValid = false;
            } else {
                System.err.println("Database connection settings: " + dbSettings);
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
        ServiceLogger.LOGGER.config("Database hostname: " + dbHostname);
        ServiceLogger.LOGGER.config("Database port: " + dbPort);
        ServiceLogger.LOGGER.config("Database username: " + dbUsername);
        ServiceLogger.LOGGER.config("Database password provided? " + (dbPassword != null));
        ServiceLogger.LOGGER.config("Database name: " + dbName);
        ServiceLogger.LOGGER.config("Database driver: " + dbDriver);
        ServiceLogger.LOGGER.config("Database connection settings: " + dbSettings);
    }

    public String getDbUrl() {
        return "jdbc:mysql://" + dbHostname + ":" + dbPort + "/" + dbName + dbSettings;
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

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbHostname() {
        return dbHostname;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getDbSettings() {
        return dbSettings;
    }

    public boolean isDbConfigValid() {
        return dbConfigValid;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getRequestDelay() {
        return requestDelay;
    }

    public void setRequestDelay(int requestDelay) {
        this.requestDelay = requestDelay;
    }
}