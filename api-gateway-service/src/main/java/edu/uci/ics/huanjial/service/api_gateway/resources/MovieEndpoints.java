package edu.uci.ics.huanjial.service.api_gateway.resources;

import edu.uci.ics.huanjial.service.api_gateway.GatewayService;
import edu.uci.ics.huanjial.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart.BillingCartInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.IDMModels.IDMVerifySessionResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.MovieModel.*;
import edu.uci.ics.huanjial.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.models.NoSessionIDResponseModel.NoSessionIDResponseModel;
import edu.uci.ics.huanjial.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.huanjial.service.api_gateway.utilities.General;
import edu.uci.ics.huanjial.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.huanjial.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.util.Iterator;
import java.util.Map;

@Path("movies")
public class MovieEndpoints {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {

        ServiceLogger.LOGGER.info("Received request to searchMovieRequest");

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();



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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieSearch());
        // set the request model
        cr.setRequest(null);
        // set request type
        cr.setRequestType("GET");
        // set the transactionID
        cr.setQueryParams(queryParams);

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("get/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieRequest(@Context HttpHeaders headers, @PathParam("movieid")String movieid ) {
        ServiceLogger.LOGGER.info("Received request to getMovieRequest");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        String rawEndPoint = GatewayService.getMovieConfigs().getEPMovieGet();
        String modifiedEndPoint = General.replaceCurlyBraceEndpoint(rawEndPoint, movieid);

        cr.setEndpoint(modifiedEndPoint);
        // set the request model
        cr.setRequest(null);
        // set request type
        cr.setRequestType("GET");

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovieRequest(@Context HttpHeaders headers, String jsonText) {

        ServiceLogger.LOGGER.info("Received request to addMovieRequest");
        MovieModelAddRequestModel requestModel;
        try {
            requestModel = (MovieModelAddRequestModel) ModelValidator.verifyModel(jsonText, MovieModelAddRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, MovieModelAddResponseModel.class);//should be shopping cart response model.class);
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
        ServiceLogger.LOGGER.info("In API gateway movies Add, email is: " + email);
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
        System.out.println("Client request email: " + cr.getEmail());
        // set sessionID
        cr.setSessionID(sessionResponse.getSessionID());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieAdd());
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

    @Path("delete/{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieRequest(@Context HttpHeaders headers, @PathParam("movieid")String movieid ) {
        ServiceLogger.LOGGER.info("Received request to deleteMovieRequest");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        String rawEndPoint = GatewayService.getMovieConfigs().getEPMovieDelete();
        String modifiedEndPoint = General.replaceCurlyBraceEndpoint(rawEndPoint, movieid);

        cr.setEndpoint(modifiedEndPoint);
        // set the request model
        cr.setRequest(null);
        // set request type
        cr.setRequestType("DELETE");

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("genre")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresRequest(@Context HttpHeaders headers ) {
        ServiceLogger.LOGGER.info("Received request to getGenresRequest");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());
        // set the request model
        cr.setRequest(null);
        // set request type
        cr.setRequestType("GET");

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("genre/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenreRequest(@Context HttpHeaders headers, String jsonText) {

        ServiceLogger.LOGGER.info("Received request to addMovieRequest");
        MovieModelGenreAddRequestModel requestModel;
        try {
            requestModel = (MovieModelGenreAddRequestModel) ModelValidator.verifyModel(jsonText, MovieModelGenreAddRequestModel.class);
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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreAdd());
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

    @Path("genre/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovieRequest(@Context HttpHeaders headers, @PathParam("movieid")String movieid) {
        ServiceLogger.LOGGER.info("Received request to getGenresForMovieRequest");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        String rawEndPoint = GatewayService.getMovieConfigs().getEPGenreMovie();
        String modifiedEndPoint = General.replaceCurlyBraceEndpoint(rawEndPoint, movieid);

        cr.setEndpoint(modifiedEndPoint);
        // set the request model
        cr.setRequest(null);
        // set request type
        cr.setRequestType("GET");

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("star/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {

        ServiceLogger.LOGGER.info("Received request to starSearchRequest");

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarSearch());
        // set the request model
        cr.setRequest(null);
        // set request type
        cr.setRequestType("GET");
        // set the transactionID
        cr.setQueryParams(queryParams);

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("star/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarRequest(@Context HttpHeaders headers, @PathParam("id")String id) {
        ServiceLogger.LOGGER.info("Received request to getStarRequest");

        if (headers.getHeaderString("sessionID") == null) {
            ServiceLogger.LOGGER.info("SessionID does NOT exist");
            NoSessionIDResponseModel responseModel = new NoSessionIDResponseModel(-17, "SessionID not provided in request header.");
            return Response.status(Status.BAD_REQUEST).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").entity(responseModel).build();
        }
        ServiceLogger.LOGGER.info("SessionID exists");

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");

        ServiceLogger.LOGGER.info("Check current session status");
        IDMVerifySessionResponseModel sessionResponse = General.getSessionResponseModel(email, sessionID);
        if (sessionResponse == null) {
            ServiceLogger.LOGGER.info("SessionResponse: null");
            return Response.status(Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").build();
        } else if (sessionResponse.getResultCode() != 130) {
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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        String rawEndPoint = GatewayService.getMovieConfigs().getEPStarGet();
        String modifiedEndPoint = General.replaceCurlyBraceEndpoint(rawEndPoint, id);

        cr.setEndpoint(modifiedEndPoint);
        // set the request model
        cr.setRequest(null);
        // set request type
        cr.setRequestType("GET");

        ServiceLogger.LOGGER.info("Add client request to ClientRequestQueue");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Finish adding client request to ClientRequestQueue");
        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);

        return Response.status(Status.NO_CONTENT).entity(responseModel).header("Access-Control-Allow-Headers", "*").header("Access-Control-Expose-Headers", "*").header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID", transactionID).header("email", email).header("sessionID", sessionResponse.getSessionID()).build();

    }

    @Path("star/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarRequest(@Context HttpHeaders headers, String jsonText) {

            ServiceLogger.LOGGER.info("Received request to addStarRequest");
            MovieModelStarAddRequestModel requestModel;
            try {
                requestModel = (MovieModelStarAddRequestModel) ModelValidator.verifyModel(jsonText, MovieModelStarAddRequestModel.class);
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
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            // get the register endpoint path from IDM configs
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarAdd());
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

    @Path("star/starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovieRequest(@Context HttpHeaders headers, String jsonText) {

            ServiceLogger.LOGGER.info("Received request to addStarToMovieRequest");
            MovieModelStarsInRequestModel requestModel;
            try {
                requestModel = (MovieModelStarsInRequestModel) ModelValidator.verifyModel(jsonText, MovieModelStarsInRequestModel.class);
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
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            // get the register endpoint path from IDM configs
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarIn());
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

    @Path("rating")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingRequest(@Context HttpHeaders headers, String jsonText) {

            ServiceLogger.LOGGER.info("Received request to updateRatingRequest");
            MovieModelRatingRequestModel requestModel;
            try {
                requestModel = (MovieModelRatingRequestModel) ModelValidator.verifyModel(jsonText, MovieModelRatingRequestModel.class);
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
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            // get the register endpoint path from IDM configs
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPRating());
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
}

