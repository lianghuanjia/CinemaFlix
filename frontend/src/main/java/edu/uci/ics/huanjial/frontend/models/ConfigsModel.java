package edu.uci.ics.huanjial.frontend.models;

import java.util.Map;

public class ConfigsModel {
    private Map<String, String> serviceConfig;
    private Map<String, String> loggerConfig;

    public ConfigsModel() { }

    public Map<String, String> getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(Map<String, String> service) {
        this.serviceConfig = service;
    }

    public Map<String, String> getLoggerConfig() {
        return loggerConfig;
    }

    public void setLoggerConfig(Map<String, String> loggerConfig) {
        this.loggerConfig = loggerConfig;
    }
}