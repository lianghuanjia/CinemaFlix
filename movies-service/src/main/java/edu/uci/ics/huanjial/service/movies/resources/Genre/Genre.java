package edu.uci.ics.huanjial.service.movies.resources.Genre;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.Genre.GenreAddRequestModel;
import edu.uci.ics.huanjial.service.movies.Models.Genre.GenreMovieIdResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.Genre.GenreResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.GenreModel;
import edu.uci.ics.huanjial.service.movies.Models.Privilege.VerifyPrivilegeResponseModel;
import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("genre")
public class Genre {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response genre( @Context HttpHeaders headers){
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        try {
            ArrayList<GenreModel> gmList = getGenreList();
            if (gmList == null) {
                GenreResponseModel responseModel = new GenreResponseModel(219, null);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            GenreModel[] gmArray = gmList.toArray(new GenreModel[gmList.size()]);
            GenreResponseModel responseModel = new GenreResponseModel(219, gmArray);
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Genre.genre");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }


    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenre(String jsonText, @Context HttpHeaders headers) {
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        GeneralResponseModel responseModel = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            GenreAddRequestModel requestModel = mapper.readValue(jsonText, GenreAddRequestModel.class);

            if(!GeneralHelper.checkPrivilege(email, 3)){
                ServiceLogger.LOGGER.info("Insufficient privilege");
                responseModel = new GeneralResponseModel(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            if(GenreExist(requestModel.getName())){
                ServiceLogger.LOGGER.info("Genre duplicated.");
                responseModel = new GeneralResponseModel(218);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

            Boolean success = addGenreInDB(requestModel.getName());
            if(success){
                ServiceLogger.LOGGER.info("Successfully added genre");
                responseModel = new GeneralResponseModel(217);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            else { //Not successfully added the movie.
                ServiceLogger.LOGGER.info("Failed added genre");
                responseModel = new GeneralResponseModel(218);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
        }catch (IOException e) {
            if (e instanceof JsonParseException) {
                responseModel = new GeneralResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else if (e instanceof JsonMappingException) {
                responseModel = new GeneralResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            } else { //case -1;
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Genre.addGenre");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
    }

    private Boolean addGenreInDB(String genreName) {
        try {
            ServiceLogger.LOGGER.info("Genre.addGenreInDB");
            String query = "INSERT INTO genres (name) VALUES ( ? );";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,genreName);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing");
            ServiceLogger.LOGGER.info("Return true");
            return true;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Genre.addGenreInDB");
            ServiceLogger.LOGGER.info("Return false");
            return false;
        }

    }

    private Boolean GenreExist(String genreName) throws SQLException{
        try{
            String query = "select * from genres where name = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,genreName);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){ //No info about the given genre name in the db. It doesn't exist. Return false;
                return false;
            }else{
                return true;
            }
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Genre.checkGenreExist");
            throw new SQLException();
        }
    }

    private ArrayList<GenreModel> getGenreList() throws SQLException{
        try{
            String query = "select * from genres;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            ArrayList<GenreModel> gmList = new ArrayList<GenreModel>();
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No genre found in the database. It's empty.");
                return null;
            }
            else {
                do{
                    GenreModel gm = new GenreModel();
                    gm.setId(rs.getInt("id"));
                    gm.setName(rs.getString("name"));
                    gmList.add(gm);
                }while (rs.next());
            }
            return gmList;
        }catch (SQLException e ){
            ServiceLogger.LOGGER.info("SQLException occurred in Genre.getGenreList");
            throw new SQLException();
        }
    }

    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenreByMovieID(@Context HttpHeaders headers,
                                    @PathParam("movieid") String movieid){
        ServiceLogger.LOGGER.info("Genre.getGenreByMovieID");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        if(!GeneralHelper.checkPrivilege(email, 3)){
            ServiceLogger.LOGGER.info("The user has insufficient privilege.");
            VerifyPrivilegeResponseModel responseModel = new VerifyPrivilegeResponseModel(141,"User has insufficient privilege.");
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
        try{
            GenreMovieIdResponseModel responseModel = null;
            ArrayList<GenreModel> gmList = getGenreModelList(movieid);

            if(gmList == null){
                ServiceLogger.LOGGER.info("No genre retrieved with given movieId");
                responseModel = new GenreMovieIdResponseModel(211, null);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            ServiceLogger.LOGGER.info("Genres retrieved with given movieId");
            GenreModel[] gmArray = gmList.toArray(new GenreModel[gmList.size()]);
            ServiceLogger.LOGGER.info("Finish converting GenreModel ArrayList to Array");
            responseModel = new GenreMovieIdResponseModel(219, gmArray);
            return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Genre.getGenreByMovieID");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
    }

    private ArrayList<GenreModel> getGenreModelList(String movieId) throws SQLException {
        try {
            ServiceLogger.LOGGER.info("Genre.getGenreModelList");
            String query = "select genreId, name from genres inner join genres_in_movies gim on genres.id = gim.genreId where gim.movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,movieId);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            ArrayList<GenreModel> gmList = new ArrayList<GenreModel>();
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No genre found with given movieId. Return NULL");
                return null;
            }
            else{
                ServiceLogger.LOGGER.info("Found genre with given movieId");
                do{
                   GenreModel gm = new GenreModel();
                   gm.setId(rs.getInt("genreId"));
                   gm.setName(rs.getString("name"));
                   gmList.add(gm);
                }while (rs.next());
            }
            ServiceLogger.LOGGER.info("Finish getting GenreModel list");
            return gmList;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Genre.getGenreModelList");
            throw new SQLException();
        }
    }
}
