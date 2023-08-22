package edu.uci.ics.huanjial.service.movies.configs;

import java.util.Map;

public class ConfigsModel {

    private Map<String, String> serviceConfig;
    private Map<String, String> loggerConfig;
    private Map<String, String> databaseConfig;
    private Map<String, String> idmConfig;
    private Map<String, String> idmEndpoints;


    public Map<String, String> getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(Map<String, String> serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public Map<String, String> getLoggerConfig() {
        return loggerConfig;
    }

    public void setLoggerConfig(Map<String, String> loggerConfig) {
        this.loggerConfig = loggerConfig;
    }

    public Map<String, String> getDatabaseConfig() {
        return databaseConfig;
    }

    public void setDatabaseConfig(Map<String, String> databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public Map<String, String> getIdmConfig() {
        return idmConfig;
    }

    public void setIdmConfig(Map<String, String> idmConfig) {
        this.idmConfig = idmConfig;
    }

    public Map<String, String> getIdmEndpoints() {
        return idmEndpoints;
    }

    public void setIdmEndpoints(Map<String, String> idmEndpoints) {
        this.idmEndpoints = idmEndpoints;
    }
}
