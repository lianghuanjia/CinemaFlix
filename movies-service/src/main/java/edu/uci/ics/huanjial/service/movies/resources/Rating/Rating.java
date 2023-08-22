package edu.uci.ics.huanjial.service.movies.resources.Rating;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.Rating.RatingRequestModel;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("rating")
public class Rating {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rate(String jsonText, @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Rating.rate");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        RatingRequestModel requestModel = null;
        GeneralResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonText, RatingRequestModel.class);
            ServiceLogger.LOGGER.info("Finish getting requestModel by mapper");
            String movieID = requestModel.getId();
            Float rating = requestModel.getRating();

            if(!GeneralHelper.movieExists(movieID) || !GeneralHelper.movieExistsInRatings(movieID)){
                ServiceLogger.LOGGER.info("Movie does not exist either in movies or ratings");
                responseModel = new GeneralResponseModel(211); //Case 211: No movies found with search parameters.
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            Boolean success = RatingHelper.updateRating(movieID, rating);
            ServiceLogger.LOGGER.info("Finish calling  RatingHelper.updateRating");
            if(success){
                ServiceLogger.LOGGER.info("Successfully update rating");
                responseModel = new GeneralResponseModel(250); //Case 250: Rating successfully updated.
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            ServiceLogger.LOGGER.info("Fail to update rating");
            responseModel = new GeneralResponseModel(251); //Case 251: Could not update rating.
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();

        }
        catch (IOException e){
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("JsonParseException occurred in Rating.rate");
                responseModel = new GeneralResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("JsonMappingException occurred in Rating.rate");
                responseModel = new GeneralResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else { //case -1;
                ServiceLogger.LOGGER.info("IOException occurred in Rating.rate");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
        }catch (SQLException e ){
            ServiceLogger.LOGGER.info("SQLException occurred in Rating.rate");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }


    }
}
