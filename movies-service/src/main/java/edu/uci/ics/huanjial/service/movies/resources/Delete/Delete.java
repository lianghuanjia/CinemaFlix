package edu.uci.ics.huanjial.service.movies.resources.Delete;



import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;
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

@Path("delete/{movieid}")
public class Delete {
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response remove(String jsonText, @Context HttpHeaders headers, @PathParam("movieid") String movieid){
        GeneralResponseModel responseModel;

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        try{
            ObjectMapper mapper = new ObjectMapper();

            if(!GeneralHelper.checkPrivilege(email, 3)){
                responseModel = new GeneralResponseModel(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            if(checkMovieRemovedBefore(movieid)){
                responseModel = new GeneralResponseModel(242);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            Boolean success = removeMovie(movieid);
            if(success){
                responseModel = new GeneralResponseModel(240);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            responseModel = new GeneralResponseModel(241);
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();


        }catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException in Delete page");
            ServiceLogger.LOGGER.warning("Error message: " + e.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }


    }


    private boolean checkMovieRemovedBefore(String movieid)throws SQLException{
    try {
        ServiceLogger.LOGGER.info("Delete.checkMovieRemovedBefore");
        String query = "select hidden from movies where id = ?;";
        PreparedStatement ps = MovieService.getCon().prepareStatement(query);
        ServiceLogger.LOGGER.info("Finish getting prepared statement");
        ps.setString(1, movieid);
        ServiceLogger.LOGGER.info("Prepared statement: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Finish executing query");
        Integer hidden = null;
        while(rs.next()) {
            hidden = rs.getInt("hidden");
        }
        ServiceLogger.LOGGER.info("Finish getting hidden form db");
        if (hidden == null) { //use cs0000004 as example. This movie's hidden field is null.
            ServiceLogger.LOGGER.info("Hidden is null");
            return false; //There is nothing in the hidden field in the db corresponding to the given movieid.
        } else if (hidden == 1) {
            ServiceLogger.LOGGER.info("Hidden is 1");
            return true;
        } else {
            ServiceLogger.LOGGER.info("Hidden is 0");
            return false;
        }
    }catch (SQLException e){
        ServiceLogger.LOGGER.warning("SQLException occurred: Delete.checkMovieRemovedBefore");
        ServiceLogger.LOGGER.warning(e.toString());
        throw new SQLException();
    }


    }


    public boolean removeMovie(String id){
        try{
            ServiceLogger.LOGGER.info("Delete.removeMovie");
            String query = "update movies set hidden = 1 where id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,id);
            ServiceLogger.LOGGER.info("Prepared statement: " + ps.toString());
            Integer influedRow = ps.executeUpdate();
            if(influedRow == 0){
                ServiceLogger.LOGGER.info("Influed row is 0. No movie found to remove.");
                return false;
            }
            ServiceLogger.LOGGER.info("Movie found and removed successfully.");
            return true;
        }catch (SQLException e){
            ServiceLogger.LOGGER.info("In RemovePage removeMovie, SQLException occurred");
            return false;
        }

    }




}
