package edu.uci.ics.huanjial.service.movies.General;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.movies.Models.Privilege.VerifyPrivilegeRequestModel;
import edu.uci.ics.huanjial.service.movies.Models.Privilege.VerifyPrivilegeResponseModel;
import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralHelper {
    public static boolean invalidEmailFormat(String email){
        String validEmailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if(email == null){
            return true;
        }
        Pattern pattern = Pattern.compile(validEmailRegex);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()==false){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean invalidEmailLen(String email){
        char[] emailChar = email.toCharArray();
        if(emailChar.length > 50){
            return true;
        }else{
            return false;
        }
    }


    public static boolean checkPrivilege(String email, Integer plevel) {
        ServiceLogger.LOGGER.info("Verifying privilege level with IDM...");


        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String IDM_URI = MovieService.getMovieConfigs().getIdmConfigs().getIdmUri();

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = MovieService.getMovieConfigs().getIdmConfigs().getPrivilegePath();

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);
        ServiceLogger.LOGGER.info("WebTarget: "+ webTarget.toString());
        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Set the payload
        ServiceLogger.LOGGER.info("Setting payload of the request");
        ServiceLogger.LOGGER.info("In movie check privilege, email: "+ email + ", plevel: "+ plevel.toString());
        VerifyPrivilegeRequestModel requestModel = new VerifyPrivilegeRequestModel(email, plevel);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        // Check that status code of the request
        if (response.getStatus() == 200){
            ServiceLogger.LOGGER.info("Received status 200!");
            String jsonText = response.readEntity(String.class);
            VerifyPrivilegeResponseModel responseModel = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                responseModel = mapper.readValue(jsonText, VerifyPrivilegeResponseModel.class);
                ServiceLogger.LOGGER.info("jsonText: " + jsonText);
                return (responseModel.getResultCode()==140);
            }
            catch (IOException e){
                return false;
            }
        }else{
            String jsonText = response.readEntity(String.class);
            VerifyPrivilegeResponseModel responseModel = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                responseModel = mapper.readValue(jsonText, VerifyPrivilegeResponseModel.class);
                ServiceLogger.LOGGER.info("jsonText: " + jsonText);
                ServiceLogger.LOGGER.info("response message: " + responseModel.getMessage());
            }
            catch (IOException e){
            }
            ServiceLogger.LOGGER.warning("Received status " + response.getStatus() + " -> wrong.");
        }
        return false;
    }

    public static Boolean movieExists(String movieId) throws SQLException {
        try {
            ServiceLogger.LOGGER.info("GeneralHelper.movieExists");
            String query = "select * from movies where id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,movieId);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            if(rs.next()){
                ServiceLogger.LOGGER.info("Movie exists in db");
                return true;
            }
            ServiceLogger.LOGGER.info("Movie does NOT exist in db");
            return false;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in StarHelper.movieExists");
            throw new SQLException();
        }
    }

    public static Boolean movieExistsInRatings(String movieId) throws SQLException {
        try {
            ServiceLogger.LOGGER.info("GeneralHelper.movieExists");
            String query = "select * from ratings where movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,movieId);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            if(rs.next()){
                ServiceLogger.LOGGER.info("Movie exists in db");
                return true;
            }
            ServiceLogger.LOGGER.info("Movie does NOT exist in db");
            return false;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in StarHelper.movieExists");
            throw new SQLException();
        }
    }

}
