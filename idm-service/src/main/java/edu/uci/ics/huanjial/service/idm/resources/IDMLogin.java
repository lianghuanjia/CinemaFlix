package edu.uci.ics.huanjial.service.idm.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.idm.models.IDMLoginRequestModel;
import edu.uci.ics.huanjial.service.idm.models.IDMLoginResponseModel;
import org.apache.commons.codec.DecoderException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;


@Path("login")
public class IDMLogin {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)


    public Response login(String jsonText){
        ServiceLogger.LOGGER.info("IDM Login");
        ObjectMapper mapper = new ObjectMapper();
        IDMLoginResponseModel idmloginRESPONSEm = null;
        IDMLoginRequestModel idmloginREQUESTm;

        try{
            idmloginREQUESTm = mapper.readValue(jsonText, IDMLoginRequestModel.class);
            LoginHelper loginHelper = new LoginHelper(idmloginREQUESTm.getEmail(), idmloginREQUESTm.getPassword());

            if(loginHelper.invalidPasswordLen()){
                idmloginRESPONSEm = new IDMLoginResponseModel(-12);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmloginRESPONSEm).build();
            }
            else if(loginHelper.invalidEmailFormat()){
                idmloginRESPONSEm = new IDMLoginResponseModel(-11);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmloginRESPONSEm).build();
            }
            else if(loginHelper.invalidEmailLen()){
                idmloginRESPONSEm = new IDMLoginResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmloginRESPONSEm).build();
            }
            else if(loginHelper.userNotFound()){ //NOTE! The userNotFound should be checked before checking passwordNotMatched
                idmloginRESPONSEm = new IDMLoginResponseModel(14);
                return Response.status(Response.Status.OK).entity(idmloginRESPONSEm).build();
            }
            else if(loginHelper.passwordNotMatched()){
                idmloginRESPONSEm = new IDMLoginResponseModel(11);
                return Response.status(Response.Status.OK).entity(idmloginRESPONSEm).build();
            }
            else{
                String sessionID = loginHelper.logInSuccessfully();
                idmloginRESPONSEm = new IDMLoginResponseModel(120);
                idmloginRESPONSEm.setSessionID(sessionID);
                return Response.status(Response.Status.OK).entity(idmloginRESPONSEm).build();
            }

        }catch(IOException | SQLException | DecoderException e){
            if(e instanceof JsonParseException){
                idmloginRESPONSEm = new IDMLoginResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmloginRESPONSEm).build();
            }
            else if(e instanceof JsonMappingException){
                idmloginRESPONSEm = new IDMLoginResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmloginRESPONSEm).build();
            }
            else if(e instanceof DecoderException){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

}
