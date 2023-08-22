package edu.uci.ics.huanjial.service.api_gateway.connectionpool;

import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

import static edu.uci.ics.huanjial.service.api_gateway.GatewayService.ANSI_RED;
import static edu.uci.ics.huanjial.service.api_gateway.GatewayService.ANSI_RESET;

public class ConnectionPool {
    LinkedList<Connection> connections;
    String driver;
    String url;
    String username;
    String password;

    //constructor
    public ConnectionPool(int numCons, String driver, String url, String username, String password) {
        ServiceLogger.LOGGER.config("Initializing connection pool...");
        connections = new LinkedList<>();
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;

        try {
            Class.forName(driver);
            for (int i = 0; i < numCons; ++i) {
                ServiceLogger.LOGGER.config("Creating connection #" + (i + 1));
                Connection con = createConnection();
                connections.add(con);
            }
        } catch (ClassNotFoundException e) {
            ServiceLogger.LOGGER.severe(ANSI_RED + "Unable to load driver into memory." + ANSI_RESET);
        }
    }

    public Connection requestCon() {
        return connections.isEmpty() ? createConnection() : connections.removeFirst();
    }

    public void releaseCon(Connection con) {
            connections.add(con);
    }

    private Connection createConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            ServiceLogger.LOGGER.severe("Unable to create connection to database!");
            ServiceLogger.LOGGER.severe(ANSI_RED + ExceptionUtils.exceptionStackTraceAsString(e) + ANSI_RESET);
        }
        return con;
    }
}
