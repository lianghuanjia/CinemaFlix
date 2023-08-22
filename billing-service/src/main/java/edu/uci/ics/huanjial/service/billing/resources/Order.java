package edu.uci.ics.huanjial.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.huanjial.service.billing.General.GeneralHelper;
import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.billing.General.PayPalClient;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.Service;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@Path("order")
public class Order {

    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response place(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Order -> place");
        OrderPlaceRetrieveRequestModel requestModel = null;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonText, OrderPlaceRetrieveRequestModel.class);
            //check customer exists or not first:
            if(CustomerHelper.retrieve(requestModel.getEmail())==null){
                return GeneralHelper.returnResponse(332, headers);
            }
            ItemModel[] itemArray = CartHelper.retrieve(requestModel.getEmail());
            if(itemArray == null){
                return GeneralHelper.returnResponse(341, headers);
            }
            Map<String, Object> response = OrderHelper.place(requestModel.getEmail(), itemArray);
            Object redirectUrl = response.get("redirect_url");
            String strRedirectUrl = redirectUrl.toString();
            String[] split_url = strRedirectUrl.split("token=");
            String token = OrderHelper.getToken(response);
            OrderPlaceResponseModel responseModel = new OrderPlaceResponseModel(3400, strRedirectUrl, token);

            CartHelper.clear(requestModel.getEmail());
            return Response.status(Response.Status.OK).entity(responseModel).header("email",headers.getHeaderString("email")).header("sessionID", headers.getHeaderString("sessionID")).header("transactionID", headers.getHeaderString("transactionID")).build();

        }catch (IOException e){
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
            ServiceLogger.LOGGER.info("Order -> place, SQLException occurred");
            return GeneralHelper.returnResponse(-1, headers);
        }
        catch (PayPalRESTException e){
            return GeneralHelper.returnResponse(342, headers);
        }
    }

    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response complete(
                             @Context HttpHeaders headers,
                             @QueryParam("paymentId") String paymentId,
                             @QueryParam("token")  String token,
                             @QueryParam("PayerID") String PayerID)
    {
        ServiceLogger.LOGGER.info("Order");
        ServiceLogger.LOGGER.info("complete");
        try {
            Map<String, Object> response = PayPalClient.completePayment(paymentId, PayerID);
            Payment payment = (Payment)response.get("payment");
            String transactionId = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();
            Integer influencedRow = OrderHelper.updateTransactionId(token, transactionId);
            if(influencedRow == 0){
                return GeneralHelper.returnResponse(3421, headers);
            }
            return GeneralHelper.returnResponse(3420, headers);
        }
        catch (PayPalRESTException e){
            return GeneralHelper.returnResponse(3422, headers);
        }
        catch (SQLException e){
            return GeneralHelper.returnResponse(-1,headers);
        }
    }


    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieve(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Order");
        ServiceLogger.LOGGER.info("retrieve");

        ObjectMapper mapper = new ObjectMapper();
        OrderPlaceRetrieveRequestModel requestModel = null;

        try {
            ServiceLogger.LOGGER.info("About to map");
            requestModel = mapper.readValue(jsonText, OrderPlaceRetrieveRequestModel.class);
            ServiceLogger.LOGGER.info("Finish mapping");

            if(CustomerHelper.retrieve(requestModel.getEmail())==null){
                return GeneralHelper.returnResponse(332, headers);
            }

            TransactionsModel[] tmArray = OrderHelper.retrieve(requestModel.getEmail());

            OrderRetrieveResponseModel responseModel = new OrderRetrieveResponseModel(3410, tmArray);
            return Response.status(GeneralHelper.getStatus(3410)).entity(responseModel).header("email",headers.getHeaderString("email")).header("sessionID", headers.getHeaderString("sessionID")).header("transactionID", headers.getHeaderString("transactionID")).build();
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
            ServiceLogger.LOGGER.warning("SQLException occurred in Order.retrieve");
            ServiceLogger.LOGGER.info("Exception content is: ");
            ServiceLogger.LOGGER.info(e.toString());
            return GeneralHelper.returnResponse(-1, headers);
        }
        catch (PayPalRESTException e){
            return GeneralHelper.returnResponse(-1,headers);
        }
    }
}
