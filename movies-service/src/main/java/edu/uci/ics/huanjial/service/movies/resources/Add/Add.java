package edu.uci.ics.huanjial.service.movies.resources.Add;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.Models.Add.AddRequestModel;
import edu.uci.ics.huanjial.service.movies.Models.Add.AddResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.GenreModel;
import edu.uci.ics.huanjial.service.movies.MovieService;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

@Path("add")
public class Add {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response addMovie(String jsonText, @Context HttpHeaders headers){
        ServiceLogger.LOGGER.info("Add.addMovie");
        AddResponseModel responseModel;

        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("In Add: email is: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        try {
            ObjectMapper mapper = new ObjectMapper();
            AddRequestModel requestModel = mapper.readValue(jsonText, AddRequestModel.class);
            ServiceLogger.LOGGER.info("Finish getting requestModel from mapper");
            if(!GeneralHelper.checkPrivilege(email, 3)){
                responseModel = new AddResponseModel(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            Boolean exists  = AddHelper.movieExists(requestModel);
            ServiceLogger.LOGGER.info("Finish checking movieExists");
            if(exists){ //The movie exists and grList is the genreIDs of the movie.
                ServiceLogger.LOGGER.info("movie exists");
                responseModel = new AddResponseModel(216);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            //movie doesn't exist, able to insert.
            ServiceLogger.LOGGER.info("The movie can be added to the database. It doesn't exist in the database yet");

            ServiceLogger.LOGGER.info("Get new movie ID");
            String newMovieID = getNewMovieID();
            ServiceLogger.LOGGER.info("Finish getting new movie ID");

            ServiceLogger.LOGGER.info("Add new movie into database");
            Boolean success = addMovieIntoDB(requestModel, newMovieID);
            ServiceLogger.LOGGER.info("Finish adding new movie into database");
            if(success){
                ServiceLogger.LOGGER.info("Adding successfully");
                ServiceLogger.LOGGER.info("Update rating table");
                updateRatintTable(newMovieID);
                ServiceLogger.LOGGER.info("Finish updating rating table");
                responseModel = new AddResponseModel(214);
                responseModel.setMovieid(newMovieID);
                ServiceLogger.LOGGER.info("Finish setting new movie ID");
                ArrayList<Integer> correctIdList = new ArrayList<>();
                ArrayList<String> reqGenreName = new ArrayList<>();
                for(GenreModel gmArray: requestModel.getGenres()){
                    reqGenreName.add(gmArray.getName());
                }
                for(int i = 0; i < reqGenreName.size(); i++) {
                    Integer genreID = checkGenreExist(reqGenreName.get(i), newMovieID);
                    correctIdList.add(genreID);
                }
                ServiceLogger.LOGGER.info("Finish getting correctIDList");
                Integer[] correctIdArry = correctIdList.toArray(new Integer[correctIdList.size()]);
                responseModel.setGenreid(correctIdArry);
                ServiceLogger.LOGGER.info("Finish /add endpoint");
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            else{
                ServiceLogger.LOGGER.info("Failed to add new movie into database");
                responseModel = new AddResponseModel(215);
                responseModel.setMovieid(newMovieID);
                responseModel.setGenreid(getGenreIDFromRequestModel(requestModel));
                ServiceLogger.LOGGER.info("Finish /add endpoint");
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
        }catch (IOException | SQLException e){
            if( e instanceof JsonParseException){
                responseModel = new AddResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            else if(e instanceof JsonMappingException){
                ServiceLogger.LOGGER.info("Exception message: ");
                ServiceLogger.LOGGER.info(((JsonMappingException) e).toString());
                responseModel = new AddResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            else{ //case -1;
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

        }
    }

    private void updateRatintTable(String movieID){
        try {
            ServiceLogger.LOGGER.info("Add.updateRatintTable");
            String query = "INSERT INTO ratings (movieId, rating, numVotes) VALUES (?, ?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            Double  r = 0.0;
            Float rating = r.floatValue();
            ps.setString(1,movieID);
            ps.setFloat(2, rating);
            ps.setInt(3, 0);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in Add.updateRatintTable");
        }
    }

    private Integer checkGenreExist(String genreName, String movieID) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("Add.checkGenreExist");
            //ArrayList<Integer> idList = new ArrayList<>();
            String query = "SELECT GROUP_CONCAT(NAME) as GenreNames,  GROUP_CONCAT(id) as GenreIDs FROM genres;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            String GenreNames = null;
            String GenreIDs = null;
            while(rs.next()) { //has genreNames
                GenreNames = rs.getString("GenreNames").toLowerCase();
                GenreIDs = rs.getString("GenreIDs");
            }
            if(GenreNames == null){ //if no genreNames, then the user's input can be new type of genre.
                //update genre table and genres_in_moves
                ServiceLogger.LOGGER.info("No genre is in database. ");
                Integer newGenreID = updateGenreTable(genreName);
                updateMovie_In_Genre( newGenreID, movieID);
                return newGenreID;
            }
            else if(!GenreNames.contains(",")) { //Only 1 genre name and id exists in the db.
                ServiceLogger.LOGGER.info("Only 1 genre & id pair in the db.");
                if (genreName.toLowerCase().equals(GenreNames)){ //User's input genre equals to the db one
                    //use the db's genre and it's genre id, no need to update genre table
                    //BUT!!! Need to update genres_in_movies.
                    ServiceLogger.LOGGER.info("Found matched genre & id pair in db");
                    updateMovie_In_Genre(Integer.parseInt(GenreIDs), movieID);
                    ServiceLogger.LOGGER.info("Updated movie_in_genre");
                    return Integer.parseInt(GenreIDs);
                }
                else{
                    //not equal to the db's genre name. User's genre info is new.
                    // " x " We still need to check the input id is alright. " x "
                    // Update: For revised movie service, we don't need to check the input id from the request.
                    ServiceLogger.LOGGER.info("Given genre does NOT exist in the db. Insert it into genre table and genre_and_movie table");
                    Integer newGenreID = updateGenreTable(genreName);
                    ServiceLogger.LOGGER.info("Updated genre table");
                    updateMovie_In_Genre(newGenreID, movieID);
                    ServiceLogger.LOGGER.info("Updated movie_in_genre");
                    //" x " If the user's id == db's id, we need to generate a new one. " x "
                    return newGenreID;
                }
            }
            else{
                //More than 1 genre info in db
                String[] nameArray = GenreNames.split(",");
                String[] idArray = GenreIDs.split(",");
                for(int i = 0; i < nameArray.length; i ++){
                    if(genreName.toLowerCase().equals(nameArray[i].toLowerCase())){
                        //input genre equals to one of the genre names in db
                        //we use the genre's name and it's id.
                        //Therefore, we don't need to update the genre table,
                        //BUT!!! We need to update the genres_in_movies table
                        ServiceLogger.LOGGER.info("Given genre is found among MULTIPLE genreS");
                        updateMovie_In_Genre(Integer.parseInt(idArray[i]), movieID);
                        ServiceLogger.LOGGER.info("Updated movie_in_genre");
                        return Integer.parseInt(idArray[i]);
                    }
                }
                //not equal to any names in the db:
                //Then update the genre table AND genres_in_movies table.

                ServiceLogger.LOGGER.info("Given genre is NOT found among MULTIPLE genreS");
                Integer newGenreID =  updateGenreTable(genreName);
                ServiceLogger.LOGGER.info("Updated genre table");
                updateMovie_In_Genre(newGenreID, movieID);
                ServiceLogger.LOGGER.info("Updated movie_in_genre");
                //Warning: If we update genre table and genres_in_movie table,
                //we need to update genre table FIRST, then update genres_in_movies table SECONDLY.
                return newGenreID;
            }
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("In AddPage checkGenreExist: SQLException occurred.");
            throw new SQLException();
        }
    }


    private Integer updateGenreTable(String genreName){
        try {
            ServiceLogger.LOGGER.info("Update genres table");
            String query = "INSERT INTO genres (name) VALUES ( ? );";
            //                      insert into genres (id, name) VALUES (24, 'hahaha');
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,genreName);
            ServiceLogger.LOGGER.info("Finish setting up parameters of prepared statement");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            ServiceLogger.LOGGER.info("Finish updating genres table");
            ServiceLogger.LOGGER.info("Finish updating genres table");
            Integer genreID = getGenreID(genreName);
            ServiceLogger.LOGGER.info("Finish getting Add.genreID by calling getGenreID in Add.updateGenreTable");
            return genreID;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in Add.updateGenreTable");
            return null;
        }
    }

    private Integer getGenreID(String genreName){
        try{
            String query = "select id from genres where name = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,genreName);
            ResultSet rs = ps.executeQuery();
            Integer genreID = null;
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No ID found with such genre name: " + genreName + " found in database");
                return null;
            }
            else{
                do{
                    ServiceLogger.LOGGER.info("Found genre ID in database.");
                    genreID = rs.getInt("id");
                }while (rs.next());
            }
            return genreID;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in Add.getGenreID");
            return null;
        }
    }


    private void updateMovie_In_Genre(Integer newID, String movieID){
        try {
            ServiceLogger.LOGGER.info("Update genres_in_movies table");
            String m_i_g_query = "insert into genres_in_movies (genreId, movieId) VALUES (?, ?);";
            //                   insert into genres_in_movies (genreId, movieId) VALUES (24, 'cs0000001');
            PreparedStatement ps = MovieService.getCon().prepareStatement(m_i_g_query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setInt(1, newID);
            ps.setString(2, movieID);
            ServiceLogger.LOGGER.info("Finish setting up parameters of prepared statement");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            ServiceLogger.LOGGER.info("Finish updating genres_in_movies table");

        }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in Add.updateMovie_In_Genre");
        }
    }


    private Boolean addMovieIntoDB(AddRequestModel requestModel, String newMovieID) {
        //INSERT INTO movies VALUES ('cs0000001','Happy Sam', 2019, 'Super Sam', null, null, null, null, null,null);
        try {
            ServiceLogger.LOGGER.info("addMovieIntoDB: Get query");
            String insertQuery = "INSERT INTO movies VALUES(?,?,?,?,?,?,?,?,?,?);";
            ServiceLogger.LOGGER.info("addMovieIntoDB: Get prepared statement");
            PreparedStatement ps = MovieService.getCon().prepareStatement(insertQuery);
            ServiceLogger.LOGGER.info("addMovieIntoDB: Finish getting prepared statement");
            ps.setString(1,newMovieID); //id String
            ServiceLogger.LOGGER.info("addMovieIntoDB: movieID set");
            ps.setString(2, requestModel.getTitle()); //title String
            ServiceLogger.LOGGER.info("addMovieIntoDB: title set");
            ps.setInt(3,requestModel.getYear());//year int
            ServiceLogger.LOGGER.info("addMovieIntoDB: year set");
            ps.setString(4, requestModel.getDirector());// director String
            ServiceLogger.LOGGER.info("addMovieIntoDB: director set");
            if(requestModel.getBackdrop_path()==null){
                ps.setNull(5, Types.INTEGER);
            }
            else {
                ps.setString(5, requestModel.getBackdrop_path());//backdrop_path String
            }
            ServiceLogger.LOGGER.info("addMovieIntoDB: Backdrop_path set");
            if(requestModel.getBudget() == null){
                ps.setNull(6, Types.INTEGER);
            }
            else {
                ps.setInt(6, requestModel.getBudget());//budget Int
            }
            ServiceLogger.LOGGER.info("addMovieIntoDB: Budget set");
            if(requestModel.getOverview()==null){
                ps.setNull(7, Types.INTEGER);
            }
            else {
                ps.setString(7, requestModel.getOverview());//overview String
            }
            ServiceLogger.LOGGER.info("addMovieIntoDB: Overview set");
            if(requestModel.getPoster_path() == null){
                ps.setNull(8, Types.INTEGER);
            }
            else {
                ps.setString(8, requestModel.getPoster_path()); //poster_path String
            }
            ServiceLogger.LOGGER.info("addMovieIntoDB: Poster_path set");
            if(requestModel.getRevenue()==null){
                ps.setNull(9, Types.INTEGER);
            }
            else {
                ps.setInt(9, requestModel.getRevenue()); //revenue Int
            }
            ServiceLogger.LOGGER.info("addMovieIntoDB: Revenue set");
            ps.setInt(10,0); //hidden there is no hidden in the request model, so just set it to 0.
            ServiceLogger.LOGGER.info("addMovieIntoDB: Hidden set");

            ServiceLogger.LOGGER.info("addMovieIntoDB: Prepared statement: ");
            ServiceLogger.LOGGER.info("addMovieIntoDB: \n" + ps.toString());

            ServiceLogger.LOGGER.info("addMovieIntoDB: About to execute prepared statement");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("addMovieIntoDB: Finish executing");
         //check if the genre exist or not. When checking, ignore case(lower and upper).
            //createGenreInDB(); //if not exist, put the new GenreID, Genre name into db.
            //updateGenre_in_movies(); //Update the genre_in_movies table.
            //For the things of genre, should I check it before ps.executeUpdate, or after?
            //I should


            return true;
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("AddPage -> addMovieIntoDB: SQLException occurred");
            ServiceLogger.LOGGER.warning("SQLException message: ");
            ServiceLogger.LOGGER.warning(e.toString());
            return false;
        }
    }



    //We get the biggest movie ID from the database, and then
    private String getNewMovieID() throws SQLException{

        try {
            String getIDQuery = "SELECT id from movies where id like '%cs%' order by id desc limit 1;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(getIDQuery);

            ResultSet rs = ps.executeQuery();
            String oldID = null;
            while(rs.next()){
                oldID = rs.getString("id");
            }
            if(oldID == null){
                return "cs0000001";
            }
            String noCsString = oldID.replace("cs","00");
            Integer intOldID = Integer.parseInt(noCsString);
            ServiceLogger.LOGGER.info("Biggest number existing in DB is: " + intOldID);
            Integer newMovieID = intOldID+1; //don't do intOldID++. This will assign intOldID to newMovieID first then + 1


            int numOfDigits = 7;
            String csPrefix = "cs";
            String num = newMovieID.toString();
            int len = num.length();
            int remainDigits = numOfDigits - len;
            String zeroPrefix = "";
            for (int i = 0; i < remainDigits; i++) {
                zeroPrefix += "0";
            }
            String newID = csPrefix + zeroPrefix + num;
            return newID;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("AddPage getMovieID: SQLException occurred.");
            throw new SQLException();
        }
    }


    private Integer[] getGenreIDFromRequestModel(AddRequestModel requestModel){
        GenreModel[] grModelArray = requestModel.getGenres();
        ArrayList<Integer> intArrayList = new ArrayList<Integer>();
        for(GenreModel gmModel: grModelArray){
            intArrayList.add(gmModel.getId());
        }
        Integer[] intArray = intArrayList.toArray(new Integer[intArrayList.size()]);
        return intArray;
    }

    private Integer[] getGenreIDArray(ArrayList<String> genresList) throws NumberFormatException{
        Integer[] intArray = new Integer[genresList.size()];
        try {
            for (int i = 0; i < genresList.size(); i++) {
                intArray[i] = Integer.parseInt(genresList.get(i));
            }
            return intArray;
        }
        catch (NumberFormatException e){
            ServiceLogger.LOGGER.warning("AddPage -> getGenreIDArray: NumberFormatException occurred");
            throw new NumberFormatException();
        }
    }
}
