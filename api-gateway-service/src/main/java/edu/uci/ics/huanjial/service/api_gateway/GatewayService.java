package edu.uci.ics.huanjial.service.api_gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import edu.uci.ics.huanjial.service.api_gateway.configs.*;
import edu.uci.ics.huanjial.service.api_gateway.configs.*;
import edu.uci.ics.huanjial.service.api_gateway.connectionpool.ConnectionPool;
//import edu.uci.ics.kpmckeow.service.api_gateway.configs.*; //What is this?????
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.threadpool.ThreadPool;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class GatewayService {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static GatewayService gatewayService;

    private static ConnectionPool conPool;
    private static ThreadPool threadPool;
    private static GatewayConfigs gatewayConfigs;
    private static IDMConfigs idmConfigs;
    private static MovieConfigs movieConfigs;
    private static BillingConfigs billingConfigs;

    public static ConnectionPool getConPool() {
        return conPool;
    }

    public static void setConPool(ConnectionPool conPool) {
        GatewayService.conPool = conPool;
    }

    public static GatewayConfigs getGatewayConfigs() {
        return gatewayConfigs;
    }


    public static IDMConfigs getIdmConfigs() {
        return idmConfigs;
    }


    public static MovieConfigs getMovieConfigs() {
        return movieConfigs;
    }


    public static BillingConfigs getBillingConfigs() {
        return billingConfigs;
    }

    public static void main(String[] args) {
        gatewayService = new GatewayService();
        gatewayService.initService(args);
    }

    private void initService(String[] args) {
        // Validate arguments
        validateArguments(args);

        // Exec the arguments
        execArguments(args);

        // Initialize logging
        initLogging();
        ServiceLogger.LOGGER.config("Starting service...");
        gatewayConfigs.currentConfigs();
        idmConfigs.currentConfigs();
        movieConfigs.currentConfigs();
        billingConfigs.currentConfigs();

        // Create a connection to the database
        connectToDatabase();

        // Initialize threadPool
        initThreadPool();

        // Initialize HTTP sever
        initHTTPServer();

        ServiceLogger.LOGGER.config(ANSI_GREEN + "Service initialized." + ANSI_RESET);
    }

    private void validateArguments(String[] args) {
        boolean isConfigOptionSet = false;
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "--default":
                case "-d":
                    if (i + 1 < args.length) {
                        exitAppFailure("Invalid arg after " + args[i] + " option: " + args[i + 1]);
                    }
                case "--config":
                case "-c":
                    if (!isConfigOptionSet) {
                        isConfigOptionSet = true;
                        ++i;
                    } else {
                        exitAppFailure("Conflicting configuration file arguments.");
                    }
                    break;

                default:
                    exitAppFailure("Unrecognized argument: " + args[i]);
            }
        }
    }

    private void execArguments(String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                switch (args[i]) {
                    case "--config":
                    case "-c":
                        // Config file specified. Load it.
                        getConfigFile(args[i + 1]);
                        ++i;
                        break;
                    case "--default":
                    case "-d":
                        System.err.println("Default config options selected.");
                        gatewayConfigs = new GatewayConfigs();
                        break;
                    default:
                        exitAppFailure("Unrecognized argument: " + args[i]);
                }
            }
        } else {
            System.err.println("No config file specified. Using default values.");
            gatewayConfigs = new GatewayConfigs();
        }
    }

    private void getConfigFile(String configFile) {
        try {
            System.err.println("Config file name: " + configFile);
            gatewayConfigs = new GatewayConfigs(loadConfigs(configFile));
            idmConfigs = new IDMConfigs(loadConfigs(configFile));
            movieConfigs = new MovieConfigs(loadConfigs(configFile));
            billingConfigs = new BillingConfigs(loadConfigs(configFile));
            System.err.println(ANSI_GREEN + "Configuration file successfully loaded." + ANSI_RESET);
        } catch (NullPointerException e) {
            System.err.println(ANSI_RED + "Config file not found. Using default values." + ANSI_RESET);
            gatewayConfigs = new GatewayConfigs();
        }
    }

    private ConfigsModel loadConfigs(String file) {
        System.err.println("Loading configuration file...");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigsModel configs = null;

        try {
            configs = mapper.readValue(new File(file), ConfigsModel.class);
        } catch (IOException e) {
            exitAppFailure("Unable to load configuration file.");
        }
        return configs;
    }

    private void initLogging() {
        try {
            ServiceLogger.initLogger(gatewayConfigs.getOutputDir(), gatewayConfigs.getOutputFile());
        } catch (IOException e) {
            exitAppFailure("Unable to initialize logging.");
        }
    }

    private void connectToDatabase() {
        // Initialize connection pool
        conPool = new ConnectionPool(gatewayConfigs.DEFAULT_CONNECTIONS,
                gatewayConfigs.getDbDriver(),
                gatewayConfigs.getDbUrl(),
                gatewayConfigs.getDbUsername(),
                gatewayConfigs.getDbPassword());
    }

    private void initThreadPool() {
        // Initialize thread pool
        ServiceLogger.LOGGER.config("Initializing thread pool.");
        threadPool = new ThreadPool(gatewayConfigs.getNumThreads());
        ServiceLogger.LOGGER.config(ANSI_GREEN + "Initializing thread pool." + ANSI_RESET);
    }

    private void initHTTPServer() {
        ServiceLogger.LOGGER.config("Initializing HTTP server...");
        String scheme = gatewayConfigs.getScheme();
        String hostName = gatewayConfigs.getHostName();
        int port = gatewayConfigs.getPort();
        String path = gatewayConfigs.getPath();

        try {
            ServiceLogger.LOGGER.config("Building URI from configs...");
            URI uri = UriBuilder.fromUri(scheme + hostName + path).port(port).build();
            ServiceLogger.LOGGER.config("Final URI: " + uri.toString());

            ResourceConfig rc = new ResourceConfig().packages("edu.uci.ics.huanjial.service.api_gateway.resources");
            ServiceLogger.LOGGER.config("Set Jersey resources.");
            rc.register(JacksonFeature.class);
            ServiceLogger.LOGGER.config("Set Jackson as serializer.");
            ServiceLogger.LOGGER.config("Starting HTTP server...");
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, rc, false);
            server.start();
            ServiceLogger.LOGGER.config(ANSI_GREEN + "HTTP server started." + ANSI_RESET);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void exitAppFailure(String message) {
        System.err.println("ERROR: " + message);
        System.err.println("Usage options: " );
        System.err.println("\tSpecify configuration file:");
        System.err.println("\t\t--config [file]");
        System.err.println("\t\t-c [file]");
        System.err.println("\tUse default configuration:");
        System.err.println("\t\t--default");
        System.err.println("\t\t-d");
        System.exit(-1);
    }

    public static ThreadPool getThreadPool() {
        return threadPool;
    }

    public static void setThreadPool(ThreadPool threadPool) {
        GatewayService.threadPool = threadPool;
    }
}
