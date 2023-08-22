package edu.uci.ics.huanjial.service.api_gateway.resources;

import edu.uci.ics.huanjial.service.api_gateway.GatewayService;
import edu.uci.ics.huanjial.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.IDMModels.IDMLoginRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.IDMModels.IDMLoginResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.IDMModels.IDMRegisterRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.IDMModels.IDMVerifySessionResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.NoSessionIDResponseModel.NoSessionIDResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.huanjial.service.api_gateway.utilities.General;
import edu.uci.ics.huanjial.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.huanjial.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("idm")
public class IDMEndpoints {
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("registerUserRequest");
        IDMRegisterRequestModel requestModel;
        // Map jsonText to RequestModel
        try {
            requestModel = (IDMRegisterRequestModel) ModelValidator.verifyModel(jsonText, IDMRegisterRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }


        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // set email
        cr.setEmail(null);
        // set sessionID
        cr.setSessionID(null);
        // set the transactionID
        cr.setTransactionID(transactionID);
        System.out.println("transactionID: " + transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserRegister());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        System.out.println("Put client request into queue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        System.out.println("TransactionID in IDMEndpoints: " + transactionID);
        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).build();

    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("loginUserRequest");
        IDMLoginRequestModel requestModel;
        // Map jsonText to RequestModel
        try {
            requestModel = (IDMLoginRequestModel) ModelValidator.verifyModel(jsonText, IDMLoginRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, IDMLoginResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        System.out.println("In login, transactionID is: " + transactionID);

        ClientRequest cr = new ClientRequest();
        // set email
        cr.setEmail(null);
        // set sessionID
        cr.setSessionID(null);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserLogin());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).build();

    }

    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySessionRequest(String jsonText) {
        ServiceLogger.LOGGER.info("verifySessionRequest");
        IDMRegisterRequestModel requestModel;
        // Map jsonText to RequestModel
        try {
            requestModel = (IDMRegisterRequestModel) ModelValidator.verifyModel(jsonText, IDMRegisterRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, IDMVerifySessionResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        ClientRequest cr = new ClientRequest();
        // set email
        cr.setEmail(null);
        // set sessionID
        cr.setSessionID(null);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPSessionVerify());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).build();

    }

    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUserPrivilegeRequest(@Context HttpHeaders headers, String jsonText) {
        IDMRegisterRequestModel requestModel;
        // Map jsonText to RequestModel
        try {
            requestModel = (IDMRegisterRequestModel) ModelValidator.verifyModel(jsonText, IDMRegisterRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        //check if the sessionID exists or not:
        if(headers.getHeaderString("sessionID") == null){
            ServiceLogger.LOGGER.info("SessionID does NOT exist");
            NoSessionIDResponseModel responseModel = new NoSessionIDResponseModel(-17,"SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").entity(responseModel).build();
        }
        ServiceLogger.LOGGER.info("SessionID exists");

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        ServiceLogger.LOGGER.info("Check current session status");
        IDMVerifySessionResponseModel sessionResponse = General.getSessionResponseModel(email, sessionID);
        if(sessionResponse == null){
            ServiceLogger.LOGGER.info("SessionResponse: null");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").build();
        }
        else if(sessionResponse.getResultCode() != 130){
            ServiceLogger.LOGGER.info("Session status NOT active. ");
            return Response.status(Status.OK).entity(sessionResponse).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();
        }
        ServiceLogger.LOGGER.info("Valid (Active) session");

        ClientRequest cr = new ClientRequest();
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserPrivilegeVerify());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();

    }
}
