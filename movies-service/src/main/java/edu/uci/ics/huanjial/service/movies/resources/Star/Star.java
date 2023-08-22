package edu.uci.ics.huanjial.service.movies.resources.Star;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.Star.*;
import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("star")
public class Star {
    @Path("search")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovie(@Context HttpHeaders headers,
                                @QueryParam("name")String name,
                                @QueryParam("birthYear") Integer birthYear,
                                @QueryParam("movieTitle") String movieTitle,
                                @QueryParam("limit") Integer limit,
                                @QueryParam("offset") Integer offset,
                                @QueryParam("orderby") String orderby,
                                @QueryParam("direction") String direction) {
        ServiceLogger.LOGGER.info("Star.searchMovie");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        limit = (limit == null || (limit != 10 && limit != 25 && limit != 50 && limit != 100))? 10:limit; //Need to check null first, then check < 0
        offset = (offset == null || offset < 0 || NotMultipleOfLimit(offset, limit))? 0:offset;
        orderby = (orderby == null|| (!orderby.equals("name")) && (!orderby.equals("birthYear")))? "name":orderby; //title / rating
        direction = (direction == null || (!direction.equals("asc") && !direction.equals("desc")))? "asc":direction;

        try {
            StarSearchResponseModel responseModel;
            StarModel[] smList = StarHelper.searchStars(name, birthYear, movieTitle, limit, offset, orderby, direction);
            if(smList == null){
                responseModel = new StarSearchResponseModel(213, null);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            responseModel = new StarSearchResponseModel(212, smList);
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();

        }
        catch (SQLException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
    }

    private Boolean NotMultipleOfLimit(Integer offset, Integer limit){
        if(offset % limit != 0){
            return true;
        }
        return false;
    }




    @Path("{id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarByID(String jsonText,@Context HttpHeaders headers,@PathParam("id") String id){

        ServiceLogger.LOGGER.info("Star.getStarByID");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        try {
            StarModel sm = getStarModel(id);
            ServiceLogger.LOGGER.info("Finish getting star model from Star.getStarModel");
            if(sm == null){ //Not found stars
                ServiceLogger.LOGGER.info("No star found");
                StarSearchByIDResponseModel responseModel = new StarSearchByIDResponseModel(213,null);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            ServiceLogger.LOGGER.info("Found star");
            StarSearchByIDResponseModel responseModel = new StarSearchByIDResponseModel(212, sm);
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Star.getStarByID");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }


    }

    private StarModel getStarModel(String id) throws SQLException {
        try {
            ServiceLogger.LOGGER.info("Star.getStarModel");
            String query = "select * from stars where id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,id);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            StarModel sm = null;
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No stars found with given id.");
                return null;
            }
            else{
                do {
                    ServiceLogger.LOGGER.info("Found stars with given id.");
                    sm = new StarModel();
                    sm.setId(rs.getString("id"));
                    sm.setName(rs.getString("name"));
                    if(rs.getObject("birthYear")==null){
                        sm.setBirthYear(null);
                    }
                    else{
                        sm.setBirthYear(((Integer)rs.getObject("birthYear")).toString());
                    }
                    //ServiceLogger.LOGGER.info("birthYear is: " + by.toString());
                }while(rs.next());
            }
            return sm;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred Star.getStarModel");
            throw new SQLException();
        }
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStar(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Star.addStar");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        GeneralResponseModel responseModel;
        StarAddRequestModel requestModel = null;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, StarAddRequestModel.class);
            if (!GeneralHelper.checkPrivilege(email, 3)) {
                responseModel = new GeneralResponseModel(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            String newStarID = StarHelper.getStarNewID();

            Integer correctBirthYear = StarHelper.checkBirthYear(requestModel.getBirthYear());

            if(StarHelper.starExists(requestModel.getName())){
                responseModel = new GeneralResponseModel(222);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            Boolean success = StarHelper.addStar(newStarID,requestModel.getName(), correctBirthYear);
            if(success){
                responseModel = new GeneralResponseModel(220);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            responseModel = new GeneralResponseModel(221);
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();

        }catch (IOException e){
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("JsonParseException occurred in Star.addStar");
                ServiceLogger.LOGGER.warning("Error message: " + e.toString());
                responseModel = new GeneralResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("JsonMappingException occurred in Star.addStar");
                ServiceLogger.LOGGER.warning("Error message: " + e.toString());
                responseModel = new GeneralResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else { //case -1;
                ServiceLogger.LOGGER.info("IOException occurred in Star.addStar");
                ServiceLogger.LOGGER.warning("Error message: " + e.toString());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
        }
        catch (SQLException e ){
            ServiceLogger.LOGGER.info("SQLException occurred in Star.addStar");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }

    }

    @Path("starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response starsIn(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Star.starsIn");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        StarsInRequestModel requestModel = null;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonText, StarsInRequestModel.class);

            if (!GeneralHelper.checkPrivilege(email, 3)) {
                responseModel = new GeneralResponseModel(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            if(!StarHelper.movieExists(requestModel.getMovieid())){ //Case 211: No movies found with search parameters.
                responseModel = new GeneralResponseModel(211);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            if(StarHelper.starAlreadyExistsInMovie(requestModel)){
                responseModel = new GeneralResponseModel(232); //Case 232: Star already exists in movie.
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            Boolean success = StarHelper.addStarInMovie(requestModel);
            if(success){
                responseModel = new GeneralResponseModel(230); //Case 230: Star successfully added to movie.
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            responseModel = new GeneralResponseModel(231);//Case 231: Could not add star to movie.
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();

        }catch (IOException e){
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("JsonParseException occurred in Star.starsIn");
                responseModel = new GeneralResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("JsonMappingException occurred in Star.starsIn");
                responseModel = new GeneralResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else { //case -1;
                ServiceLogger.LOGGER.info("IOException occurred in Star.starsIn");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
        }catch (SQLException e ){
            ServiceLogger.LOGGER.info("SQLException occurred in Star.starsIn");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }


    }
}
