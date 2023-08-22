package edu.uci.ics.huanjial.service.api_gateway.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.api_gateway.GatewayService;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.models.IDMModels.IDMVerifySessionRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.IDMModels.IDMVerifySessionResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class General {
    public static IDMVerifySessionResponseModel getSessionResponseModel(String email, String sessionID) {
        ServiceLogger.LOGGER.info("Verifying session status with IDM...");


        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String IDM_URI = GatewayService.getIdmConfigs().getIdmUri();
        ServiceLogger.LOGGER.info("The uri is: ");
        ServiceLogger.LOGGER.info(IDM_URI);

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = GatewayService.getIdmConfigs().getEPSessionVerify();
        ServiceLogger.LOGGER.info("The endpoint is: ");
        ServiceLogger.LOGGER.info(IDM_ENDPOINT_PATH);

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);
        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Set the payload
        ServiceLogger.LOGGER.info("Setting payload of the request");
        IDMVerifySessionRequestModel requestModel = new IDMVerifySessionRequestModel(email, sessionID);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");


        // Check that status code of the request
        if (response.getStatus() == 200){
            ServiceLogger.LOGGER.info("Received status 200!");
            String jsonText = response.readEntity(String.class);
            IDMVerifySessionResponseModel responseModel = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                responseModel = mapper.readValue(jsonText, IDMVerifySessionResponseModel.class);
                ServiceLogger.LOGGER.info("jsonText: " + jsonText);
                return responseModel;
            }
            catch (IOException e){
                return null;
            }
        }else{
            ServiceLogger.LOGGER.warning("Received status " + response.getStatus() + " -> wrong.");
        }
        return null;
    }

    public static void putDataIntoDatabase(String transactionID, String email, String sessionID, String response, Integer status){
        try {
            ServiceLogger.LOGGER.info("Put response into API gateway database");
            String query = "insert into responses (transactionid, email, sessionid, response, httpstatus) VALUES (?, ?, ?, ?, ?);";
            ServiceLogger.LOGGER.info("About to get connection from connection pool");
            Connection connection = GatewayService.getConPool().requestCon();
            ServiceLogger.LOGGER.info("Finish getting connection");
            ServiceLogger.LOGGER.info("About to get prepared statement");
            PreparedStatement ps = connection.prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ServiceLogger.LOGGER.info("About to set parameters");
            ps.setString(1, transactionID);
            ps.setString(2,email);
            ps.setString(3,sessionID);
            ps.setString(4,response);
            ps.setInt(5, status);
            ServiceLogger.LOGGER.info("Finish setting parameters.");
            ServiceLogger.LOGGER.info("About to execute prepared statement");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing");
            ServiceLogger.LOGGER.info("About to release the connection");
            GatewayService.getConPool().releaseCon(connection);
            ServiceLogger.LOGGER.info("Finish releasing connection");
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("General -> putDataIntoDatabase: SQLException occurred");
        }
    }

    public static String replaceCurlyBraceEndpoint(String rawEndpoint, String pathParam){
        int startIndex = rawEndpoint.indexOf("{");
        int endIndex = rawEndpoint.indexOf("}");
        String toBeReplaced = rawEndpoint.substring(startIndex, endIndex+1);
        String modifiedEndpoint = rawEndpoint.replace(toBeReplaced, pathParam);
        return modifiedEndpoint;
    }
}
