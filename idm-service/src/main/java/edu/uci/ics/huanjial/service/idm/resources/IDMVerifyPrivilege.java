package edu.uci.ics.huanjial.service.idm.resources;



import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.idm.models.IDMVerifyPrivilegeRequestModel;
import edu.uci.ics.huanjial.service.idm.models.ResultCodeMessageResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("privilege")
public class IDMVerifyPrivilege {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response verifyPrivilege(String jsonText){
        ServiceLogger.LOGGER.info("IDM Verify Privilege.");
        ObjectMapper mapper = new ObjectMapper();
        IDMVerifyPrivilegeRequestModel privilegeREQUESTm = null;
        ResultCodeMessageResponseModel responseModel;

        try{
            privilegeREQUESTm = mapper.readValue(jsonText, IDMVerifyPrivilegeRequestModel.class);
            VerifyPrivilegeHelper vpHelper = new VerifyPrivilegeHelper(privilegeREQUESTm.getEmail(), privilegeREQUESTm.getPlevel());
            if(vpHelper.invalidPlevelRange() == true){
                responseModel = new ResultCodeMessageResponseModel(-14);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if(vpHelper.invalidEmailFormat() == true){
                responseModel = new ResultCodeMessageResponseModel(-11);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if(vpHelper.invalidEmailLen() == true){
                responseModel = new ResultCodeMessageResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if(vpHelper.userNotFound() == true){
                responseModel = new ResultCodeMessageResponseModel(14);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else if(vpHelper.plevelSufficient() == true){
                responseModel = new ResultCodeMessageResponseModel(140);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
            else{
                responseModel = new ResultCodeMessageResponseModel(141);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }
        }catch (IOException | SQLException e){
            if(e instanceof JsonParseException) { //case -3
                responseModel = new ResultCodeMessageResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if(e instanceof JsonMappingException){
                ((JsonMappingException) e).printStackTrace();// case -2
                responseModel = new ResultCodeMessageResponseModel(-2);
                ServiceLogger.LOGGER.warning("Error message: " + ((JsonMappingException) e).toString());
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

        }



    }

}
