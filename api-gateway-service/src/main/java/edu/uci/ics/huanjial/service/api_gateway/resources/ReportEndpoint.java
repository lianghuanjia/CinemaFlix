package edu.uci.ics.huanjial.service.api_gateway.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.uci.ics.huanjial.service.api_gateway.GatewayService;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.huanjial.service.api_gateway.threadpool.ThreadPool;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("report")
public class ReportEndpoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(@Context HttpHeaders headers){
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        System.out.println("In report endpoint, transactionID: " + transactionID);
        try {
            return getResponseFromDB(transactionID);
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException in ReportEndpoint -> report.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").build();
        }

    }

    private Response getResponseFromDB(String transactionID) throws SQLException{
        try {
            System.out.println("In getResponseFromDB, transactionID is: " + transactionID);
            ServiceLogger.LOGGER.info("");
            ServiceLogger.LOGGER.info("ReportEndpoint -> getResponseFromDB");
            String query = "select * from responses where transactionid = ?;";
            ServiceLogger.LOGGER.info("About to get connection from connection pool");
            Connection connection = GatewayService.getConPool().requestCon();
            ServiceLogger.LOGGER.info("Finish getting connection");
            ServiceLogger.LOGGER.info("About to get prepared statement");
            PreparedStatement ps = connection.prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1, transactionID);
            ServiceLogger.LOGGER.info("About to execute prepared statement");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            ServiceLogger.LOGGER.info("About to release the connection");
            GatewayService.getConPool().releaseCon(connection);
            ServiceLogger.LOGGER.info("Finish releasing connection");
            String text = null;
            Integer status = null;
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No response from the given transactionID");
                return Response.status(Response.Status.NO_CONTENT).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("transactionID", transactionID).build(); //Is this correct?
            }else{
                do{
                    ServiceLogger.LOGGER.info("Get response and httpstatus from the result set");
                    text = rs.getString("response");
                    status = rs.getInt("httpstatus");
                }while(rs.next());
            }

            if(text != null && status != null){
                deleteResponse(transactionID);
                return Response.status(status).entity(text).build(); //Is this correct???
            }
            if(text == null){
                ServiceLogger.LOGGER.info("Text is null");
            }
            if(status == null){
                ServiceLogger.LOGGER.info("Status is null");
            }
            ServiceLogger.LOGGER.info("Warning!! Return NO_CONTENT");
            return Response.status(Response.Status.NO_CONTENT).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).build();
        }catch (SQLException e){
            ServiceLogger.LOGGER.info("ReportEndpoint -> getResponseFromDB: SQLException occurred");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException();
        }
    }

    private void deleteResponse(String transactionID){
        try {
            ServiceLogger.LOGGER.info("deleteResponse");
            String query = "delete from responses where transactionid = ?;";
            ServiceLogger.LOGGER.info("About to get connection from connection pool");
            Connection connection = GatewayService.getConPool().requestCon();
            ServiceLogger.LOGGER.info("Finish getting connection");
            ServiceLogger.LOGGER.info("About to get prepared statement");
            PreparedStatement ps = connection.prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1, transactionID);
            ServiceLogger.LOGGER.info("About to execute prepared statement");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing");
            ServiceLogger.LOGGER.info("About to release the connection");
            GatewayService.getConPool().releaseCon(connection);
            ServiceLogger.LOGGER.info("Finish releasing connection");
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("ReportEndpoint -> deleteResponse: SQLException occurred");
            ServiceLogger.LOGGER.info(e.toString());
        }
    }



}
