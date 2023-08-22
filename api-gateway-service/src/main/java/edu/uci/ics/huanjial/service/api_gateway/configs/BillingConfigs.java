package edu.uci.ics.huanjial.service.api_gateway.configs;

import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.GatewayService;

public class BillingConfigs {
    // Billing service configs
    private String scheme;
    private String hostName;
    private int port;
    private String path;

    // Billing endpoints
    private String EPCartInsert;
    private String EPCartUpdate;
    private String EPCartDelete;
    private String EPCartRetrieve;
    private String EPCartClear;
    private String EPCcInsert;
    private String EPCcUpdate;
    private String EPCcDelete;
    private String EPCcRetrieve;
    private String EPCustomerInsert;
    private String EPCustomerUpdate;
    private String EPCustomerRetrieve;
    private String EPOrderPlace;
    private String EPOrderRetrieve;

    public BillingConfigs() { }

    public BillingConfigs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException(GatewayService.ANSI_RED + "Unable to create Configs from ConfigsModel." + GatewayService.ANSI_RESET);
        }

        /* -------------------- Service Configurations -------------------- */

        scheme = cm.getBillingConfig().get("scheme");
        if (scheme == null) {
            System.err.println(GatewayService.ANSI_RED + "Billing scheme not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing] scheme: " + scheme);
        }

        hostName = cm.getBillingConfig().get("hostName");
        if (hostName == null) {
            System.err.println(GatewayService.ANSI_RED + "Billing host name not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing] hostName: " + hostName);
        }

        port = Integer.parseInt(cm.getBillingConfig().get("port"));
        if (port == 0) {
            System.err.println(GatewayService.ANSI_RED + "No port found in configuration file." + GatewayService.ANSI_RESET);
        } else if (port < GatewayConfigs.MIN_SERVICE_PORT || port > GatewayConfigs.MAX_SERVICE_PORT) {
            System.err.println(GatewayService.ANSI_RED + "Port is not within valid range." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing] port: " + port);
        }

        path = cm.getBillingConfig().get("path");
        if (path == null) {
            System.err.println(GatewayService.ANSI_RED + "Billing path not found in configuration file." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing] path: " + path);
        }


        /* -------------------- Endpoint Configurations -------------------- */
        EPCartInsert = cm.getBillingEndpoints().get("EPCartInsert");
        if(EPCartInsert == null){
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/cart/insert found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Update Cart: " + EPCartInsert);
        }

        EPCartUpdate = cm.getBillingEndpoints().get("EPCartUpdate");
        if (EPCartUpdate == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/cart/update found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Update Cart: " + EPCartUpdate);
        }

        EPCartDelete = cm.getBillingEndpoints().get("EPCartDelete");
        if (EPCartDelete == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/cart/delete found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Delete Cart: " + EPCartDelete);
        }

        EPCartRetrieve = cm.getBillingEndpoints().get("EPCartRetrieve");
        if (EPCartRetrieve == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/cart/retrieve found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Retrieve Cart: " + EPCartRetrieve);
        }

        EPCartClear = cm.getBillingEndpoints().get("EPCartClear");
        if (EPCartClear == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/cart/clear found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Clear Cart: " + EPCartClear);
        }

        EPCcInsert = cm.getBillingEndpoints().get("EPCcInsert");
        if (EPCcInsert == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/creditcard/insert found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Insert Credit Card: " + EPCcInsert);
        }

        EPCcUpdate = cm.getBillingEndpoints().get("EPCcUpdate");
        if (EPCcUpdate == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/creditcard/update found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Update Credit Card: " + EPCcUpdate);
        }

        EPCcDelete = cm.getBillingEndpoints().get("EPCcDelete");
        if (EPCcDelete == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/creditcard/delete found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Delete Credit Card: " + EPCcDelete);
        }

        EPCcRetrieve = cm.getBillingEndpoints().get("EPCcRetrieve");
        if (EPCcRetrieve == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/creditcard/retrieve found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Retrieve Credit Card: " + EPCcRetrieve);
        }

        EPCustomerInsert = cm.getBillingEndpoints().get("EPCustomerInsert");
        if (EPCustomerInsert == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/customer/insert found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Insert Customer: " + EPCustomerInsert);
        }

        EPCustomerUpdate = cm.getBillingEndpoints().get("EPCustomerUpdate");
        if (EPCustomerUpdate == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/customer/update found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Update Customer: " + EPCustomerUpdate);
        }

        EPCustomerRetrieve = cm.getBillingEndpoints().get("EPCustomerRetrieve");
        if (EPCustomerRetrieve == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/customer/retrieve found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Retrieve Customer: " + EPCustomerRetrieve);
        }

        EPOrderPlace = cm.getBillingEndpoints().get("EPOrderPlace");
        if (EPOrderPlace == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/order/place found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Place Order: " + EPOrderPlace);
        }

        EPOrderRetrieve = cm.getBillingEndpoints().get("EPOrderRetrieve");
        if (EPOrderRetrieve == null) {
            System.err.println(GatewayService.ANSI_RED + "No path for endpoint /api/billing/order/retrieve found." + GatewayService.ANSI_RESET);
        } else {
            System.err.println("[Billing][EP] Retrieve Order: " + EPOrderRetrieve);
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("[Billing] scheme: " + scheme);
        ServiceLogger.LOGGER.config("[Billing] hostname: " + hostName);
        ServiceLogger.LOGGER.config("[Billing] port: " + port);
        ServiceLogger.LOGGER.config("[Billing] path: " + path);
        ServiceLogger.LOGGER.config("[Billing][EP] Update Cart: " + EPCartUpdate);
        ServiceLogger.LOGGER.config("[Billing][EP] Delete Cart: " + EPCartDelete);
        ServiceLogger.LOGGER.config("[Billing][EP] Retrieve Cart: " + EPCartRetrieve);
        ServiceLogger.LOGGER.config("[Billing][EP] Clear Cart: " + EPCartClear);
        ServiceLogger.LOGGER.config("[Billing][EP] Insert Credit Card: " + EPCcInsert);
        ServiceLogger.LOGGER.config("[Billing][EP] Update Credit Card: " + EPCcUpdate);
        ServiceLogger.LOGGER.config("[Billing][EP] Delete Credit Card: " + EPCcDelete);
        ServiceLogger.LOGGER.config("[Billing][EP] Retrieve Credit Card: " + EPCcRetrieve);
        ServiceLogger.LOGGER.config("[Billing][EP] Insert Customer: " + EPCustomerInsert);
        ServiceLogger.LOGGER.config("[Billing][EP] Update Customer: " + EPCcUpdate);
        ServiceLogger.LOGGER.config("[Billing][EP] Retrieve Customer: " + EPCcRetrieve);
        ServiceLogger.LOGGER.config("[Billing][EP] Place Order: " + EPOrderPlace);
        ServiceLogger.LOGGER.config("[Billing][EP] Retrieve Order: " + EPOrderRetrieve);
    }

    public String getBillingUri() {
        return scheme + hostName + ":" + port + path;
    }

    public String getEPCartUpdate() {
        return EPCartUpdate;
    }

    public String getEPCartDelete() {
        return EPCartDelete;
    }

    public String getEPCartRetrieve() {
        return EPCartRetrieve;
    }

    public String getEPCartClear() {
        return EPCartClear;
    }

    public String getEPCcInsert() {
        return EPCcInsert;
    }

    public String getEPCcUpdate() {
        return EPCcUpdate;
    }

    public String getEPCcDelete() {
        return EPCcDelete;
    }

    public String getEPCcRetrieve() {
        return EPCcRetrieve;
    }

    public String getEPCustomerInsert() {
        return EPCustomerInsert;
    }

    public String getEPCustomerUpdate() {
        return EPCustomerUpdate;
    }

    public String getEPCustomerRetrieve() {
        return EPCustomerRetrieve;
    }

    public String getEPOrderPlace() {
        return EPOrderPlace;
    }

    public String getEPOrderRetrieve() {
        return EPOrderRetrieve;
    }

    public String getEPCartInsert() {
        return EPCartInsert;
    }
}
