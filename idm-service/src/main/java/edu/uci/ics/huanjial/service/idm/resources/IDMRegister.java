package edu.uci.ics.huanjial.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.idm.models.IDMRegisterRequestModel;
import edu.uci.ics.huanjial.service.idm.models.ResultCodeMessageResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("register")
public class IDMRegister {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response register(String jsonText){
        ServiceLogger.LOGGER.info("IDM Register");
        ObjectMapper mapper = new ObjectMapper();
        ResultCodeMessageResponseModel idmregRESPONSEm = null; //why set the reponse model as null first?
        IDMRegisterRequestModel idmregREQUESTm;

        try{
            idmregREQUESTm = mapper.readValue(jsonText, IDMRegisterRequestModel.class);
            RegisterHelper regHelper = new RegisterHelper(idmregREQUESTm.getEmail(),idmregREQUESTm.getPassword());

            if(regHelper.invalidLenPassword()==true){
                idmregRESPONSEm = new ResultCodeMessageResponseModel(-12);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmregRESPONSEm).build();
            }
            else if(regHelper.invalidEmailFormat() == true){
                idmregRESPONSEm = new ResultCodeMessageResponseModel(-11);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmregRESPONSEm).build();
            }
            else if(regHelper.invalidEmailLen() == true){
                idmregRESPONSEm = new ResultCodeMessageResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmregRESPONSEm).build();
            }
            else if (regHelper.invalidPasswordLenReq()==true){
                idmregRESPONSEm = new ResultCodeMessageResponseModel(12);
                return Response.status(Response.Status.OK).entity(idmregRESPONSEm).build();
            }
            else if (regHelper.passwordCharReqMatches()==false){
                idmregRESPONSEm = new ResultCodeMessageResponseModel(13);
                return Response.status(Response.Status.OK).entity(idmregRESPONSEm).build();
            }
            else if (regHelper.emailAlreadyInUse() == true) {
                    idmregRESPONSEm = new ResultCodeMessageResponseModel(16);
                    return Response.status(Response.Status.OK).entity(idmregRESPONSEm).build();
                }
            else{
                    regHelper.insertRegisterInfo();
                    idmregRESPONSEm = new ResultCodeMessageResponseModel(110);
                    return Response.status(Response.Status.OK).entity(idmregRESPONSEm).build();
                }
        }catch (IOException | SQLException e){
            if(e instanceof JsonParseException) { //case -3
                idmregRESPONSEm = new ResultCodeMessageResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmregRESPONSEm).build();
            }
            else if(e instanceof JsonMappingException){ // case -2
                idmregRESPONSEm = new ResultCodeMessageResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).entity(idmregRESPONSEm).build();
            }
            else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
