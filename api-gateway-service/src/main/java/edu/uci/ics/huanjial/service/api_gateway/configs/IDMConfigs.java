package edu.uci.ics.huanjial.service.api_gateway.configs;

import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.GatewayService;

public class IDMConfigs {
    // IDM service configs
    private String scheme;
    private String hostName;
    private int port;
    private String path;

    // IDM endpoints
    private String EPUserRegister;
    private String EPUserLogin;
    private String EPSessionVerify;
    private String EPUserPrivilegeVerify;

    public IDMConfigs() {
    }

    public IDMConfigs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException(GatewayService.ANSI_RED + "Unable to create Configs from ConfigsModel." + GatewayService.ANSI_RESET);
        }

        /* -------------------- Service Configurations -------------------- */

        scheme = cm.getIdmConfig().get("scheme");
        if (scheme == null) {
            System.err.println(GatewayService.ANSI_RED + "IDM scheme not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM] scheme: " + scheme);
        }

        hostName = cm.getIdmConfig().get("hostName");
        if (hostName == null) {
            System.err.println(GatewayService.ANSI_RED + "IDM host name not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM] hostName: " + hostName);
        }

        port = Integer.parseInt(cm.getIdmConfig().get("port"));
        if (port == 0) {
            System.err.println(GatewayService.ANSI_RED + "No port found in configuration file." + GatewayService.ANSI_RESET);
        } else if (port < GatewayConfigs.MIN_SERVICE_PORT || port > GatewayConfigs.MAX_SERVICE_PORT) {
            System.err.println(GatewayService.ANSI_RED + "Port is not within valid range." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM] port: " + port);
        }

        path = cm.getIdmConfig().get("path");
        if (path == null) {
            System.err.println(GatewayService.ANSI_RED + "IDM path not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM] path: " + path);
        }


        /* -------------------- Endpoint Configurations -------------------- */

        EPUserRegister = cm.getIdmEndpoints().get("EPUserRegister");
        if (EPUserRegister == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/idm/register found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM][EP] Register User: " + EPUserRegister);
        }

        EPUserLogin = cm.getIdmEndpoints().get("EPUserLogin");
        if (EPUserLogin == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/idm/login found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM][EP] Login User: " + EPUserLogin);
        }

        EPSessionVerify = cm.getIdmEndpoints().get("EPSessionVerify");
        if (EPSessionVerify == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/idm/session found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM][EP] Verify Session: " + EPSessionVerify);
        }

        EPUserPrivilegeVerify = cm.getIdmEndpoints().get("EPUserPrivilegeVerify");
        if (EPUserPrivilegeVerify == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/idm/privilege found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[IDM][EP] Verify User Privilege: " + EPUserPrivilegeVerify);
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("[IDM] scheme: " + scheme);
        ServiceLogger.LOGGER.config("[IDM] hostname: " + hostName);
        ServiceLogger.LOGGER.config("[IDM] port: " + port);
        ServiceLogger.LOGGER.config("[IDM] path: " + path);
        ServiceLogger.LOGGER.config("[IDM][EP] Register User: " + EPUserRegister);
        ServiceLogger.LOGGER.config("[IDM][EP] Login User: " + EPUserLogin);
        ServiceLogger.LOGGER.config("[IDM][EP] Verify Session: " + EPSessionVerify);
        ServiceLogger.LOGGER.config("[IDM][EP] Verify User Privilege: " + EPUserPrivilegeVerify);
    }

    public String getIdmUri() {
        return scheme + hostName + ":" + port + path;
    }

    public String getEPUserRegister() {
        return EPUserRegister;
    }

    public String getEPUserLogin() {
        return EPUserLogin;
    }

    public String getEPSessionVerify() {
        return EPSessionVerify;
    }

    public String getEPUserPrivilegeVerify() {
        return EPUserPrivilegeVerify;
    }
}