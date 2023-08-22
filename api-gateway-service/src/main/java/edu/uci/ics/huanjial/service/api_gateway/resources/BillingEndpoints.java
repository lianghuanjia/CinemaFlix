package edu.uci.ics.huanjial.service.api_gateway.resources;

import edu.uci.ics.huanjial.service.api_gateway.GatewayService;
import edu.uci.ics.huanjial.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.CC.BillingCCDeleteRetrieveRequest;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.CC.BillingCCInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.CC.CCRetrieveResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart.BillingCartDeleteRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart.BillingCartInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart.BillingCartRetrieveClearRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart.ShoppingCartRetrieveResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Customer.BillingCustomerInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Customer.BillingCustomerRetrieveRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Customer.CustomerRetrieveResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Order.BillingOrderPlaceResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Order.BillingOrderPlaceRetrieveRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Order.BillingOrderRetrieveResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.GeneralResponseModel;
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

@Path("billing")
public class BillingEndpoints {
    @Path("cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insertToCart");
        BillingCartInsertUpdateRequestModel requestModel;
        try {
            requestModel = (BillingCartInsertUpdateRequestModel) ModelValidator.verifyModel(jsonText, BillingCartInsertUpdateRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);//should be shopping cart response model.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to updateCartRequest");
        BillingCartInsertUpdateRequestModel requestModel;
        try {
            requestModel = (BillingCartInsertUpdateRequestModel) ModelValidator.verifyModel(jsonText, BillingCartInsertUpdateRequestModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID


        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to deleteCartRequest");
        BillingCartDeleteRequestModel requestModel;
        try {
            requestModel = (BillingCartDeleteRequestModel) ModelValidator.verifyModel(jsonText, BillingCartDeleteRequestModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieveCartRequest");
        BillingCartRetrieveClearRequestModel requestModel;
        try {
            requestModel = (BillingCartRetrieveClearRequestModel) ModelValidator.verifyModel(jsonText, BillingCartRetrieveClearRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShoppingCartRetrieveResponseModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to clearCartRequest");
        BillingCartRetrieveClearRequestModel requestModel;
        try {
            requestModel = (BillingCartRetrieveClearRequestModel) ModelValidator.verifyModel(jsonText, BillingCartRetrieveClearRequestModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartClear());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("creditcard/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insertCreditCardRequest");
        BillingCCInsertUpdateRequestModel requestModel;
        try {
            requestModel = (BillingCCInsertUpdateRequestModel) ModelValidator.verifyModel(jsonText, BillingCCInsertUpdateRequestModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("creditcard/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to updateCreditCardRequest");
        BillingCCInsertUpdateRequestModel requestModel;
        try {
            requestModel = (BillingCCInsertUpdateRequestModel) ModelValidator.verifyModel(jsonText, BillingCCInsertUpdateRequestModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("creditcard/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to deleteCreditCardRequest");
        BillingCCDeleteRetrieveRequest requestModel;
        try {
            requestModel = (BillingCCDeleteRetrieveRequest) ModelValidator.verifyModel(jsonText, BillingCCDeleteRetrieveRequest.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("creditcard/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieveCreditCardRequest");
        BillingCCDeleteRetrieveRequest requestModel;
        try {
            requestModel = (BillingCCDeleteRetrieveRequest) ModelValidator.verifyModel(jsonText, BillingCCDeleteRetrieveRequest.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CCRetrieveResponseModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("customer/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insertCustomerRequest");
        BillingCustomerInsertUpdateRequestModel requestModel;
        try {
            requestModel = (BillingCustomerInsertUpdateRequestModel) ModelValidator.verifyModel(jsonText, BillingCustomerInsertUpdateRequestModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("customer/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to updateCustomerRequest");
        BillingCustomerInsertUpdateRequestModel requestModel;
        try {
            requestModel = (BillingCustomerInsertUpdateRequestModel) ModelValidator.verifyModel(jsonText, BillingCustomerInsertUpdateRequestModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("customer/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieveCustomerRequest");
        BillingCustomerRetrieveRequestModel requestModel;
        try {
            requestModel = (BillingCustomerRetrieveRequestModel) ModelValidator.verifyModel(jsonText, BillingCustomerRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerRetrieveResponseModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to placeOrderRequest");
        BillingOrderPlaceRetrieveRequestModel requestModel;
        try {
            requestModel = (BillingOrderPlaceRetrieveRequestModel) ModelValidator.verifyModel(jsonText, BillingOrderPlaceRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, BillingOrderPlaceResponseModel.class);
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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderPlace());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieveOrderRequest");
        BillingOrderPlaceRetrieveRequestModel requestModel;
        try {
            requestModel = (BillingOrderPlaceRetrieveRequestModel) ModelValidator.verifyModel(jsonText, BillingOrderPlaceRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, BillingOrderRetrieveResponseModel.class);
        }

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
        // get the IDM URI from IDM configs
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set request type
        cr.setRequestType("POST");
        // set the transactionID

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");

        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }
}
