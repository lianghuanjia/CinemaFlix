package edu.uci.ics.huanjial.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.billing.BillingService;
import edu.uci.ics.huanjial.service.billing.General.GeneralHelper;
import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.Service;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path("cart")
public class CartPage {


        @Path("insert")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response insertCart(String jsonText, @Context HttpHeaders headers){
            ServiceLogger.LOGGER.info("CartPage");
            ServiceLogger.LOGGER.info("insertCart");

            ObjectMapper mapper = new ObjectMapper();
            InsertRequestModel requestModel = null;
            GeneralResponseModel responseModel = null;

            try{
                ServiceLogger.LOGGER.info("About to map jsonText with request model");
                requestModel = mapper.readValue(jsonText, InsertRequestModel.class);
                if(GeneralHelper.invalidEmailFormat(requestModel.getEmail())) {
                    ServiceLogger.LOGGER.info("Invalid email format");
                    return GeneralHelper.returnResponse(-11, headers);
                }
                if(GeneralHelper.invalidEmailLen(requestModel.getEmail())) {
                    ServiceLogger.LOGGER.info("Invalid email length");
                    return GeneralHelper.returnResponse(-10, headers);
                }
                if(GeneralHelper.invalidQuantityValue(requestModel.getQuantity())){
                    ServiceLogger.LOGGER.info("Invalid quantity value");
                    return GeneralHelper.returnResponse(33, headers);
                }
                CartHelper.insert(requestModel);
                return GeneralHelper.returnResponse(3100, headers);

            }catch (IOException e){
                if (e instanceof JsonParseException) {
                    ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                    return GeneralHelper.returnResponse(-3, headers);
                }
                else if (e instanceof JsonMappingException) {
                    ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                    return GeneralHelper.returnResponse(-2, headers);
                }
                else {
                    ServiceLogger.LOGGER.warning("IOException.");
                    return GeneralHelper.returnResponse(-1, headers);
                }
            }
            catch (SQLException e){
                ServiceLogger.LOGGER.warning("SQLException occurred in CartPage.insertCart");
                if(e.toString().startsWith("java.sql.SQLException: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry")){
                    return GeneralHelper.returnResponse(311, headers);
                }
                else {
                    return GeneralHelper.returnResponse(-1, headers);
                }
            }
        }

        @Path("update")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response updateCart(String jsonText, @Context HttpHeaders headers){
            ServiceLogger.LOGGER.info("CartPage");
            ServiceLogger.LOGGER.info("updateCart");

            ObjectMapper mapper = new ObjectMapper();
            UpdateRequestModel requestModel = null;

            try {
                ServiceLogger.LOGGER.info("About to map");
                requestModel = mapper.readValue(jsonText, UpdateRequestModel.class);
                ServiceLogger.LOGGER.info("Finish mapping");
                if (GeneralHelper.invalidEmailFormat(requestModel.getEmail())) {
                    ServiceLogger.LOGGER.info("Invalid email format");
                    return GeneralHelper.returnResponse(-11, headers);
                }
                if (GeneralHelper.invalidEmailLen(requestModel.getEmail())) {
                    ServiceLogger.LOGGER.info("Invalid email length");
                    return GeneralHelper.returnResponse(-10, headers);
                }
                if (GeneralHelper.invalidQuantityValue(requestModel.getQuantity())) {
                    ServiceLogger.LOGGER.info("Invalid quantity value");
                    return GeneralHelper.returnResponse(33, headers);
                }
                Integer influenceRow = CartHelper.update(requestModel);
                if (influenceRow == 0) {
                    return GeneralHelper.returnResponse(312, headers);
                } else {
                    return GeneralHelper.returnResponse(3110, headers);
                }
            }
            catch(IOException e){
                    if (e instanceof JsonParseException) {
                        ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                        return GeneralHelper.returnResponse(-3, headers);
                    } else if (e instanceof JsonMappingException) {
                        ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                        return GeneralHelper.returnResponse(-2, headers);
                    } else {
                        ServiceLogger.LOGGER.warning("IOException.");
                        return GeneralHelper.returnResponse(-1, headers);
                    }
                }catch(SQLException e){
                    ServiceLogger.LOGGER.warning("SQLException occurred in CartPage.insertCart");
                    ServiceLogger.LOGGER.info("Exception content is: ");
                    ServiceLogger.LOGGER.info(e.toString());
                    return GeneralHelper.returnResponse(-1, headers);
                }
            }


        @Path("delete")
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Response deleteCart(String jsonText, @Context HttpHeaders headers) {
            ServiceLogger.LOGGER.info("CartPage");
            ServiceLogger.LOGGER.info("deleteCart");

            ObjectMapper mapper = new ObjectMapper();
            DeleteRequestModel requestModel = null;

            try {
                ServiceLogger.LOGGER.info("About to map");
                requestModel = mapper.readValue(jsonText, DeleteRequestModel.class);
                ServiceLogger.LOGGER.info("Finish mapping");
                if (GeneralHelper.invalidEmailFormat(requestModel.getEmail())) {
                    ServiceLogger.LOGGER.info("Invalid email format");
                    return GeneralHelper.returnResponse(-11, headers);
                }
                if (GeneralHelper.invalidEmailLen(requestModel.getEmail())) {
                    ServiceLogger.LOGGER.info("Invalid email length");
                    return GeneralHelper.returnResponse(-10, headers);
                }

                Integer influencedRow = CartHelper.delete(requestModel);
                if(influencedRow == 0){
                    return GeneralHelper.returnResponse(312, headers);
                }
                else{
                    return GeneralHelper.returnResponse(3120, headers);
                }
            }
            catch (IOException e) {
                if (e instanceof JsonParseException) {
                    ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                    return GeneralHelper.returnResponse(-3, headers);
                } else if (e instanceof JsonMappingException) {
                    ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                    return GeneralHelper.returnResponse(-2, headers);
                } else {
                    ServiceLogger.LOGGER.warning("IOException.");
                    return GeneralHelper.returnResponse(-1, headers);
                }
            }catch(SQLException e){
                ServiceLogger.LOGGER.warning("SQLException occurred in CartPage.insertCart");
                ServiceLogger.LOGGER.info("Exception content is: ");
                ServiceLogger.LOGGER.info(e.toString());
                return GeneralHelper.returnResponse(-1, headers);
            }
        }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCart(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("CartPage");
        ServiceLogger.LOGGER.info("retrieveCart");

        ObjectMapper mapper = new ObjectMapper();
        RetrieveRequestModel requestModel = null;

        try {
            ServiceLogger.LOGGER.info("About to map");
            requestModel = mapper.readValue(jsonText, RetrieveRequestModel.class);
            ServiceLogger.LOGGER.info("Finish mapping");
            if (GeneralHelper.invalidEmailFormat(requestModel.getEmail())) {
                ServiceLogger.LOGGER.info("Invalid email format");
                return GeneralHelper.returnResponse(-11, headers);
            }
            if (GeneralHelper.invalidEmailLen(requestModel.getEmail())) {
                ServiceLogger.LOGGER.info("Invalid email length");
                return GeneralHelper.returnResponse(-10, headers);
            }

            ItemModel[] imArray = CartHelper.retrieve(requestModel.getEmail());
            if(imArray == null){
                return GeneralHelper.returnResponse(312, headers);
            }
            else{
                RetrieveResponseModel responseModel = new RetrieveResponseModel(3130, imArray);
                return Response.status(GeneralHelper.getStatus(3130)).entity(responseModel).header("email",headers.getHeaderString("email")).header("sessionID", headers.getHeaderString("sessionID")).header("transactionID", headers.getHeaderString("transactionID")).build();
            }
        }
        catch (IOException e) {
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                return GeneralHelper.returnResponse(-3, headers);
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                return GeneralHelper.returnResponse(-2, headers);
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return GeneralHelper.returnResponse(-1, headers);
            }
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in CartPage.insertCart");
            ServiceLogger.LOGGER.info("Exception content is: ");
            ServiceLogger.LOGGER.info(e.toString());
            return GeneralHelper.returnResponse(-1, headers);
        }
    }

    @Path("clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCart(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("CartPage");
        ServiceLogger.LOGGER.info("clearCart");

        ObjectMapper mapper = new ObjectMapper();
        ClearRequestModel requestModel = null;

        try {
            ServiceLogger.LOGGER.info("About to map");
            requestModel = mapper.readValue(jsonText, ClearRequestModel.class);
            ServiceLogger.LOGGER.info("Finish mapping");
            if (GeneralHelper.invalidEmailFormat(requestModel.getEmail())) {
                ServiceLogger.LOGGER.info("Invalid email format");
                return GeneralHelper.returnResponse(-11, headers);
            }
            if (GeneralHelper.invalidEmailLen(requestModel.getEmail())) {
                ServiceLogger.LOGGER.info("Invalid email length");
                return GeneralHelper.returnResponse(-10, headers);
            }

            Integer influencedRow = CartHelper.clear(requestModel.getEmail());
            /*
            if(influencedRow == 0){
                return GeneralHelper.returnResponse(312, headers);


            else{

             */
            return GeneralHelper.returnResponse(3140, headers);

        }
        catch (IOException e) {
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                return GeneralHelper.returnResponse(-3, headers);
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                return GeneralHelper.returnResponse(-2, headers);
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                return GeneralHelper.returnResponse(-1, headers);
            }
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in CartPage.insertCart");
            ServiceLogger.LOGGER.info("Exception content is: ");
            ServiceLogger.LOGGER.info(e.toString());
            return GeneralHelper.returnResponse(-1, headers);
        }
    }

}

