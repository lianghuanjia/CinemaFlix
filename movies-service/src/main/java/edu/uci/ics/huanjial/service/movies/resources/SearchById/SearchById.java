package edu.uci.ics.huanjial.service.movies.resources.SearchById;

import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.Models.GenreModel;
import edu.uci.ics.huanjial.service.movies.Models.SearchById.IdMovieModel;
import edu.uci.ics.huanjial.service.movies.Models.SearchById.SearchByIdResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.SearchById.StarModel;
import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("get/{id}")
public class SearchById {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovie(   @Context HttpHeaders headers, //email, sessionID, transactionID
                                @PathParam("id") String id){

        SearchByIdResponseModel responseModel;

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        try{

            Boolean privilege = GeneralHelper.checkPrivilege(email, 4);

            IdMovieModel Idmm = getIdMovieModel(id, privilege);
            if(Idmm == null){
                responseModel = new SearchByIdResponseModel(211);
                ServiceLogger.LOGGER.info("Case 211: No movies found with search parameters.");
                return Response.status(Response.Status.OK).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
            }
            else{
                ServiceLogger.LOGGER.info("Found movie with such ID");
                    if (Idmm.getHidden()==1 && privilege==false){ //insufficient privilege
                        responseModel = new SearchByIdResponseModel(141);
                        return Response.status(Response.Status.OK).header("sessionID", sessionID).entity(responseModel).build();
                    }

                responseModel = new SearchByIdResponseModel(210);
                ServiceLogger.LOGGER.info("Case 210: Found movies with search parameters.");
                Idmm.setHidden(null); // Because there is no hidden field in the response model
                ServiceLogger.LOGGER.info(responseModel.getResultCode().toString());
                ServiceLogger.LOGGER.info(responseModel.getMessage());
                responseModel.setMovie(Idmm);
                return Response.status(Response.Status.OK).header("transactionID", transactionID).header("sessionID", sessionID).entity(responseModel).build();
            }

        }catch ( SQLException e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();

        }

    }

    private IdMovieModel getIdMovieModel(String id, Boolean privilege) throws SQLException{

        try{
            ServiceLogger.LOGGER.info("About to get query");
            String query = getQuery();
            ServiceLogger.LOGGER.info("Done");
            ServiceLogger.LOGGER.info("About to get prepared statement");
            ServiceLogger.LOGGER.info("The query is: ");
            ServiceLogger.LOGGER.info(query);
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,id);
            ServiceLogger.LOGGER.info("Done");
            ServiceLogger.LOGGER.info("PreparedStatement:\n");
            ServiceLogger.LOGGER.info(ps.toString());
            ServiceLogger.LOGGER.info("About to execute ps");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Done");
            //ArrayList<MovieModel> movieList = new ArrayList<MovieModel>();
            ServiceLogger.LOGGER.info("Privilege's level: " + privilege);
            IdMovieModel mm = new IdMovieModel();
            if(!rs.next()){
                return null;
            }
            else{
                do{
                    ServiceLogger.LOGGER.info("In while loop to retrieve info from result set");
                    mm.setMovieId(rs.getString("id"));
                    mm.setTitle(rs.getString("title"));
                    mm.setDirector(rs.getString("director")); //director is not null in database.
                    mm.setYear(rs.getInt("year"));
                    String backDropPath = ((String)rs.getObject("backdrop_path")==null)? null:(String)rs.getObject("backdrop_path");
                    mm.setBackdrop_path(backDropPath);
                    Integer budget = ((Integer) rs.getObject("budget")==null)? null:(Integer)rs.getObject("budget");
                    mm.setBudget(budget);
                    ServiceLogger.LOGGER.info("Finish budget");
                    mm.setOverview(rs.getString("overview"));
                    mm.setPoster_path(rs.getString("poster_path"));
                    Integer revenue = ((Integer) rs.getObject("revenue")==null)? null:(Integer) rs.getObject("revenue");
                    mm.setRevenue(revenue);
                    mm.setRating(rs.getFloat("rating"));
                    mm.setNumVotes(rs.getInt("numVotes"));
                    ServiceLogger.LOGGER.info("Finsh numVotes");
                    mm.setHidden(rs.getInt("hidden")); // we set the hidden as the db shows, then after that we check the privilege.
                    ServiceLogger.LOGGER.info("Finish getting hidden");
                    String genreString = rs.getString("GenreNames"); //Genre id n genre cannot be null bc they are not null in db.
                    String genreIdString = rs.getString("GenreIds"); // Is this a string contains all genre IDs ???? Make sure !!!!!
                    ServiceLogger.LOGGER.info("About to get gmlist");
                    ArrayList<GenreModel> gmList = getGenreModelList(genreString, genreIdString);
                    if(gmList.isEmpty()){
                        mm.setGenres(null);
                    }
                    else {
                        GenreModel[] gmArray = gmList.toArray(new GenreModel[gmList.size()]);
                        mm.setGenres(gmArray);
                    }
                    ServiceLogger.LOGGER.info("Finish genre[]");
                    //String[] array = list.toArray(new String[list.size()]);
                    ServiceLogger.LOGGER.info("About to get stars[]");
                    String starIdsString = rs.getString("StarIDs");
                    String names = rs.getString("StarNames");
                    String birthYears = rs.getString("BirthYears");
                    ServiceLogger.LOGGER.info("About to get smList");
                    ArrayList<StarModel> smList = getStarModelList(starIdsString, names, birthYears);
                    ServiceLogger.LOGGER.info("Finish getting smList");
                    if(smList.isEmpty()){
                        ServiceLogger.LOGGER.info("setting Stars[] as null");
                        mm.setStars(null);
                    }
                    else {
                        ServiceLogger.LOGGER.info("Transferring list to array");
                        StarModel[] smArray = smList.toArray(new StarModel[smList.size()]);
                        mm.setStars(smArray);
                    }
                    ServiceLogger.LOGGER.info("Finish one movieModel");
                }while (rs.next());
            }
            ServiceLogger.LOGGER.info("Finish getting movieList");
            return mm;
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("In IDGetMoviePage func searchMovie: SQLException");
            throw new SQLException();
        }

    }
//check this.
    public ArrayList<StarModel> getStarModelList (String starIdsString, String names, String birthYears){

        ArrayList<StarModel> smList = new ArrayList<StarModel>();
        if(starIdsString == null){
            return smList;
        }
        String[] idList = starIdsString.split(",");
        String[] nameList = names.split(",");
        String[] byList = birthYears.split(",");
        ServiceLogger.LOGGER.info("In getStarModelList, finish arrayfying");
        for(int i = 0; i < idList.length; i++){
            StarModel sm = new StarModel();
            sm.setId(idList[i]);
            sm.setName(nameList[i]);
            if(byList[i].equals("null")){
                sm.setBirthYear(null);
            }else{
                sm.setBirthYear(Integer.parseInt(byList[i]));
            }
            smList.add(sm);
            ServiceLogger.LOGGER.info("Done for 1 star model");
        }
        ServiceLogger.LOGGER.info("About to return smList in getStarModelList");
        return smList;
    }

    public ArrayList<GenreModel> getGenreModelList(String genres, String genreIDs) throws SQLException{
        ArrayList<GenreModel> gmList = new ArrayList<GenreModel>();
        if(genres == null){
            return gmList;
        }
        String[] genreList = genres.split(",");
        String[] genreIdList = genreIDs.split(",");
        for(int i = 0 ; i < genreList.length; i++){
            GenreModel gm = new GenreModel(Integer.parseInt(genreIdList[i]), genreList[i]);
            gmList.add(gm);
        }
        return gmList;
    }

    private String getQuery(){
        String query = "SELECT * FROM\n" +
                "    ratings\n" +
                "    right join\n" +
                "    (SELECT * FROM movies left outer join\n" +
                "    (SELECT * FROM\n" +
                "        (SELECT movieId as genreMID, group_concat(id) as GenreIDs, group_concat(name) as GenreNames FROM (genres inner join genres_in_movies gim on genres.id = gim.genreId) GROUP BY movieId) genre_movie_t\n" +
                "             left outer join\n" +
                "            (SELECT movieId, group_concat(id) as StarIDs, group_concat(name) as StarNames, GROUP_CONCAT( IFNULL(birthYear, 'null')) AS BirthYears from stars inner join stars_in_movies sim on stars.id = sim.starId group by movieId)\n" +
                "                star_movie_t on genre_movie_t.genreMID = star_movie_t.movieID) genre_star_movie_t\n" +
                "    on movies.id = genre_star_movie_t.genreMID) as rating_left_table on ratings.movieId = rating_left_table.genreMID where rating_left_table.id = ? ;";


                /*"SELECT * FROM\n" +
                "    ratings\n" +
                "    right join\n" +
                "\n" +
                "    (SELECT * FROM movies inner join\n" +
                "    (SELECT * FROM\n" +
                "\n" +
                "        (SELECT movieId as genreMID, group_concat(id) as GenreIDs, group_concat(name) as GenreNames FROM (genres inner join genres_in_movies gim on genres.id = gim.genreId) GROUP BY movieId) genre_movie_t\n" +
                "            inner join\n" +
                "            (SELECT movieId, group_concat(id) as StarIDs, group_concat(name) as StarNames, GROUP_CONCAT( IFNULL(birthYear, 'null')) AS BirthYears from stars inner join stars_in_movies sim on stars.id = sim.starId group by movieId) star_movie_t on genre_movie_t.genreMID = star_movie_t.movieID) genre_star_movie_t\n" +
                "    on movies.id = genre_star_movie_t.movieId) as rating_left_table on ratings.movieId = rating_left_table.movieId where rating_left_table.movieId = ? ;";
                */
        return query;

    }




}
