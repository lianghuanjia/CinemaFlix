package edu.uci.ics.huanjial.service.movies.configs;


import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import static edu.uci.ics.huanjial.service.movies.configs.MovieConfigs.MAX_SERVICE_PORT;
import static edu.uci.ics.huanjial.service.movies.configs.MovieConfigs.MIN_SERVICE_PORT;

public class IDMConfigs {
    // IDM service configs
    private String scheme;
    private String hostName;
    private int port;
    private String path;
    // IDM endpoints
    private String privilege;

    public IDMConfigs() { }

    public IDMConfigs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException("Unable to create Configs from ConfigsModel.");
        } else {
            scheme = cm.getIdmConfig().get("scheme");
            if (scheme == null) {
                System.err.println("IDM scheme not found in configuration file.");
            } else {
                System.err.println("IDM scheme: " + scheme);
            }

            hostName = cm.getIdmConfig().get("hostName");
            if (hostName == null) {
                System.err.println("IDM host name not found in configuration file.");
            } else {
                System.err.println("IDM hostName: " + hostName);
            }

            port = Integer.parseInt(cm.getIdmConfig().get("port"));
            if (port == 0) {
                System.err.println("No port found in configuration file.");
            } else if (port < MIN_SERVICE_PORT || port > MAX_SERVICE_PORT) {
                System.err.println("Port is not within valid range.");
            } else {
                System.err.println("IDM port: " + port);
            }

            path = cm.getIdmConfig().get("path");
            if (path == null) {
                System.err.println("IDM path not found in configuration file.");
            } else {
                System.err.println("IDM path: " + path);
            }

            privilege = cm.getIdmEndpoints().get("privilege");
            if (privilege == null) {
                System.err.println("No path for privilege endpoint found in configuration file.");
            } else {
                System.err.println("IDM privilege endpoint: " + privilege);
            }
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("IDM scheme: " + scheme);
        ServiceLogger.LOGGER.config("IDM hostname: " + hostName);
        ServiceLogger.LOGGER.config("IDM port: " + port);
        ServiceLogger.LOGGER.config("IDM path: " + path);
        ServiceLogger.LOGGER.config("IDM endpoint privilege: " + privilege);
    }

    public String getIdmUri() {
        return scheme + hostName + ":" + port + path;
    }
    public String getPrivilegePath() {
        return privilege;
    }


    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
}
