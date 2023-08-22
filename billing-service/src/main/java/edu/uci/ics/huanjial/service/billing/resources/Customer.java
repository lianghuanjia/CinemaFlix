package edu.uci.ics.huanjial.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.billing.General.GeneralHelper;
import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.CustomerInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.billing.models.CustomerModel;
import edu.uci.ics.huanjial.service.billing.models.CustomerRetrieveRequestModel;
import edu.uci.ics.huanjial.service.billing.models.CustomerRetrieveResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("customer")
public class Customer {

    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Customer");
        ServiceLogger.LOGGER.info("insert");

        ObjectMapper mapper = new ObjectMapper();
        CustomerInsertUpdateRequestModel requestModel = null;
        GeneralResponseModel responseModel = null;

        try{
            ServiceLogger.LOGGER.info("About to map jsonText with request model");
            requestModel = mapper.readValue(jsonText, CustomerInsertUpdateRequestModel.class);

            if (CreditCardHelper.invalidLenCreditCard(requestModel.getCcId())) {
                return GeneralHelper.returnResponse(321, headers);
            }else if (CreditCardHelper.invalidValueCreditCard(requestModel.getCcId())) {
                return GeneralHelper.returnResponse(322, headers);
            }
            CustomerHelper.insert(requestModel);
            return GeneralHelper.returnResponse(3300, headers);

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
            ServiceLogger.LOGGER.warning("SQLException occurred in Customer.insert");
            System.out.println(e.toString());
            if(e.toString().startsWith("java.sql.SQLException: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry")){
                return GeneralHelper.returnResponse(333, headers);
            }
            else if(e.toString().startsWith("java.sql.SQLException: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails ")){
                return GeneralHelper.returnResponse(331, headers);
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
    public Response update(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Customer");
        ServiceLogger.LOGGER.info("update");

        ObjectMapper mapper = new ObjectMapper();
        CustomerInsertUpdateRequestModel requestModel = null;

        try {
            ServiceLogger.LOGGER.info("About to map");
            requestModel = mapper.readValue(jsonText, CustomerInsertUpdateRequestModel.class);
            ServiceLogger.LOGGER.info("Finish mapping");

            if (CreditCardHelper.invalidLenCreditCard(requestModel.getCcId())) {
                return GeneralHelper.returnResponse(321, headers);
            }else if (CreditCardHelper.invalidValueCreditCard(requestModel.getCcId())) {
                return GeneralHelper.returnResponse(322, headers);
            }

            Integer influenceRow = CustomerHelper.update(requestModel);
            if (influenceRow == 0) {
                return GeneralHelper.returnResponse(332, headers);
            } else {
                return GeneralHelper.returnResponse(3310, headers);
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
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in Customer.insert");
            System.out.println(e.toString());
            if(e.toString().startsWith("java.sql.SQLException: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails ")){
                return GeneralHelper.returnResponse(331, headers);
            }
            else {
                return GeneralHelper.returnResponse(-1, headers);
            }
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieve(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Customer");
        ServiceLogger.LOGGER.info("retrieve");

        ObjectMapper mapper = new ObjectMapper();
        CustomerRetrieveRequestModel requestModel = null;

        try {
            ServiceLogger.LOGGER.info("About to map");
            requestModel = mapper.readValue(jsonText, CustomerRetrieveRequestModel.class);
            ServiceLogger.LOGGER.info("Finish mapping");

            CustomerModel cm = CustomerHelper.retrieve(requestModel.getEmail());
            if(cm == null){
                return GeneralHelper.returnResponse(332, headers);
            }
            else{
                CustomerRetrieveResponseModel responseModel = new CustomerRetrieveResponseModel(3320, cm);
                return Response.status(GeneralHelper.getStatus(3320)).entity(responseModel).header("email",headers.getHeaderString("email")).header("sessionID", headers.getHeaderString("sessionID")).header("transactionID", headers.getHeaderString("transactionID")).build();
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



}
