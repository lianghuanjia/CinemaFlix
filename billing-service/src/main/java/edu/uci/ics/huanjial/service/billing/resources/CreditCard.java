package edu.uci.ics.huanjial.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.billing.General.GeneralHelper;
import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.CCDeleteRetrieveRequest;
import edu.uci.ics.huanjial.service.billing.models.CCInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.billing.models.CCModel;
import edu.uci.ics.huanjial.service.billing.models.CCRetrieveResponse;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Path("creditcard")
public class CreditCard {

    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCard(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("CreditCard -> insertCreditCard");
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        mapper.setDateFormat(dateFormat);
        CCInsertUpdateRequestModel requestModel = null;
        GeneralResponseModel responseModel;

        try {
            ServiceLogger.LOGGER.info("About to use mapper to map json text and CC insert request model");
            requestModel = mapper.readValue(jsonText, CCInsertUpdateRequestModel.class);

            if (CreditCardHelper.invalidLenCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(321, headers);
            } else if (CreditCardHelper.invalidValueCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(322, headers);
            } else if (CreditCardHelper.invalidValueExpiration(requestModel)) {
                return GeneralHelper.returnResponse(323, headers);
            }
            else
                {
                CreditCardHelper.insert(requestModel);
                return GeneralHelper.returnResponse(3200, headers);
                }
        }
        catch (IOException e){
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
            ServiceLogger.LOGGER.warning("SQLException occurred in CreditCard insert");
            if(e.toString().startsWith("java.sql.SQLException: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry"))
        //if(e.toString().startsWith("java.sql.SQLException: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry"))
            {
                return GeneralHelper.returnResponse(325, headers);
            }
            return GeneralHelper.returnResponse(-1, headers);
        }
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("CreditCard -> insertCreditCard");
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        mapper.setDateFormat(dateFormat);
        CCInsertUpdateRequestModel requestModel = null;
        GeneralResponseModel responseModel;

        try {
            ServiceLogger.LOGGER.info("About to use mapper to map json text and CC insert request model");
            requestModel = mapper.readValue(jsonText, CCInsertUpdateRequestModel.class);

            if (CreditCardHelper.invalidLenCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(321, headers);
            } else if (CreditCardHelper.invalidValueCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(322, headers);
            } else if (CreditCardHelper.invalidValueExpiration(requestModel)) {
                return GeneralHelper.returnResponse(323, headers);
            }

            Integer influecedRow = CreditCardHelper.update(requestModel);
            if(influecedRow == 0){
                return GeneralHelper.returnResponse(324, headers);
            }
            else{
                return GeneralHelper.returnResponse(3210, headers);
            }
        }
        catch(IOException e){
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
            ServiceLogger.LOGGER.warning("SQLException occurred in CreditCard update");
            return GeneralHelper.returnResponse(-1, headers);
        }






    }

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("CreditCard");
        ServiceLogger.LOGGER.info("delete");

        ObjectMapper mapper = new ObjectMapper();
        CCDeleteRetrieveRequest requestModel = null;

        try {
            ServiceLogger.LOGGER.info("About to map");
            requestModel = mapper.readValue(jsonText, CCDeleteRetrieveRequest.class);
            ServiceLogger.LOGGER.info("Finish mapping");

            if (CreditCardHelper.invalidLenCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(321, headers);
            } else if (CreditCardHelper.invalidValueCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(322, headers);
            }

            Integer influencedRow = CreditCardHelper.delete(requestModel);
            if(influencedRow == 0){
                return GeneralHelper.returnResponse(324, headers);
            }
            else{
                return GeneralHelper.returnResponse(3220, headers);
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
            ServiceLogger.LOGGER.warning("SQLException occurred in CreditCard.delete");
            ServiceLogger.LOGGER.info("Exception content is: ");
            ServiceLogger.LOGGER.info(e.toString());
            return GeneralHelper.returnResponse(-1, headers);
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieve(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("CreditCard");
        ServiceLogger.LOGGER.info("retrieve");

        ObjectMapper mapper = new ObjectMapper();
        CCDeleteRetrieveRequest requestModel = null;

        try {
            ServiceLogger.LOGGER.info("About to map");
            requestModel = mapper.readValue(jsonText, CCDeleteRetrieveRequest.class);
            ServiceLogger.LOGGER.info("Finish mapping");

            if (CreditCardHelper.invalidLenCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(321, headers);
            } else if (CreditCardHelper.invalidValueCreditCard(requestModel.getId())) {
                return GeneralHelper.returnResponse(322, headers);
            }

            CCModel[] ccmArray = CreditCardHelper.retrieve(requestModel);
            if(ccmArray == null){
                return GeneralHelper.returnResponse(324, headers);
            }
            else{
                for(CCModel ccm: ccmArray){
                    System.out.println("\n\n" + ccm.getId() + " " + ccm.getFirstName() + " " + ccm.getLastName() + " " + ccm.getExpiration());
                }

                CCRetrieveResponse responseModel = new CCRetrieveResponse(3230, ccmArray[0]);
                return Response.status(GeneralHelper.getStatus(3230)).entity(responseModel).header("email",headers.getHeaderString("email")).header("sessionID", headers.getHeaderString("sessionID")).header("transactionID", headers.getHeaderString("transactionID")).build();
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
            ServiceLogger.LOGGER.warning("SQLException occurred in CreditCard.retrieve");
            ServiceLogger.LOGGER.info("Exception content is: ");
            ServiceLogger.LOGGER.info(e.toString());
            return GeneralHelper.returnResponse(-1, headers);
        }
    }




}
