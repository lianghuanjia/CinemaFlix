package edu.uci.ics.huanjial.service.movies.resources.Search;

import edu.uci.ics.huanjial.service.movies.Models.Search.MovieModel;
import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class SearchHelper {

    public ArrayList<MovieModel> searchMovie(String title, String genre, Integer year,
                                             String director, Boolean hidden, Integer offset,
                                             Integer limit, String orderby, String direction, Boolean privilege) throws SQLException {
        try {
            System.out.println("In searchMovie, direction is: " + direction);
            PreparedStatement ps = getPreparedStatement(title, genre, year, director, hidden, offset, limit, orderby, direction, privilege);
            ServiceLogger.LOGGER.info("The prepared statement is: ");
            ServiceLogger.LOGGER.info(ps.toString());
            ServiceLogger.LOGGER.info("About to execute query");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Done");
            ArrayList<MovieModel> movieList = new ArrayList<MovieModel>();
            ServiceLogger.LOGGER.info("About to get result");
            while (rs.next()) {
                MovieModel mm = new MovieModel();
                mm.setMovieId(rs.getString("id"));//Not movieID. After changing the query the movieId column might be null
                mm.setTitle(rs.getString("title"));
                mm.setDirector(rs.getString("director"));
                mm.setYear(rs.getInt("year"));
                mm.setRating(rs.getFloat("rating"));
                mm.setNumVotes(rs.getInt("numVotes"));
                //ServiceLogger.LOGGER.info("About to check privilege !!");
                if (privilege == false) {
                    mm.setHidden(null);
                } else {
                    mm.setHidden(rs.getBoolean("hidden"));
                }
                //ServiceLogger.LOGGER.info("Finish setting hidden");
                movieList.add(mm);
                }
            ServiceLogger.LOGGER.info("Finish getting movieList");
            return movieList;
            }
        catch(SQLException e){
            ServiceLogger.LOGGER.warning("In SearchHelper func searchMovie: SQLException");
            System.out.println("Error message: " + e.toString());
            throw new SQLException();
            }
        }

    public static PreparedStatement getPreparedStatement(String title, String genre, Integer year,
                               String director, Boolean hidden, Integer offset,
                               Integer limit, String orderby, String direction, Boolean privilege) throws SQLException{
        String baseQuery = "select * from\n" +
                "    (select * from movies left join ratings on movies.id = ratings.movieId) as MOVIE_RATING\n" +
                "\n" +
                "        left join\n" +
                "\n" +
                "    (select movieId, group_concat(name) as GenreNames from genres_in_movies\n" +
                "        inner join\n" +
                "        genres on genres_in_movies.genreId = genres.id group by movieId) as MOVIE_GENRE on MOVIE_GENRE.movieId = MOVIE_RATING.id ";

                /*
                "SELECT * FROM\n" +
                "    ratings\n" +
                "    right join\n" +
                "    (SELECT * FROM movies left outer join\n" +
                "    (SELECT * FROM\n" +
                "        (SELECT movieId as genreMID, group_concat(id) as GenreIDs, group_concat(name) as GenreNames FROM (genres inner join genres_in_movies gim on genres.id = gim.genreId) GROUP BY movieId) genre_movie_t\n" +
                "             left outer join\n" +
                "            (SELECT movieId, group_concat(id) as StarIDs, group_concat(name) as StarNames, GROUP_CONCAT( IFNULL(birthYear, 'null')) AS BirthYears from stars inner join stars_in_movies sim on stars.id = sim.starId group by movieId)\n" +
                "                star_movie_t on genre_movie_t.genreMID = star_movie_t.movieID) genre_star_movie_t\n" +
                "    on movies.id = genre_star_movie_t.genreMID) as rating_left_table on ratings.movieId = rating_left_table.genreMID ";
                 */
        String titleCon = (title == null || title.trim().isEmpty())? "":" title LIKE  ?  AND"; //Notice: no space around %
        String genreCon = (genre == null || genre.trim().isEmpty())? "":" GenreNames LIKE ? AND";
        String yearCon = (year == null)? "":" year = ?  AND";
        String directorCon = (director == null || director.trim().isEmpty())? "":" director LIKE ? AND" ;
        String hiddenCon = "" ;

        if(hidden == null){ hiddenCon =  " hidden = 0 AND" ;}
        else if(hidden == false){hiddenCon = " hidden = 0 AND";}
        else{
            if(privilege == true){
                hiddenCon = "";
            }
            else{
                hiddenCon = " hidden = 0 AND";
            }
        }

        ServiceLogger.LOGGER.info("In SearchHelper getPreparedStatement, hiddenCon is: " + hiddenCon);

        String optionalField = titleCon + genreCon + yearCon + directorCon + hiddenCon;
        String begin = "";
        if(!optionalField.isEmpty()){
            begin = " WHERE ";

            String ANDstr = "AND";
            if(optionalField.endsWith(ANDstr)) {
                begin  = begin + optionalField.substring(0, optionalField.length() - ANDstr.length());
            }
        }

        String secondarySort = getSecondarySort(orderby);

        System.out.println("orderby: " + orderby);
        System.out.println("direction: " + direction);
        System.out.println("secondary Sort: " + secondarySort);
        System.out.println("limit: " + limit);
        System.out.println("offset: " + offset);

        String extraField = begin + " ORDER BY " + orderby + " " + direction +", " + secondarySort + " LIMIT " + limit + " OFFSET " + offset + " ;";

        String wholeQuery = baseQuery + extraField;

        System.out.println("\n\n" + wholeQuery + "\n\n");

        try {

            PreparedStatement ps = MovieService.getCon().prepareStatement(wholeQuery);

            Integer index = 1;

            if(title != null && !title.trim().isEmpty()){
                ps.setString(index, "%"+title+"%");
                index ++;
            }
            if(genre != null && !genre.trim().isEmpty()){
                ps.setString(index, "%"+genre+"%");
                index ++;
            }
            if(year != null){ //Didn't check if the year is < 0
                ps.setInt(index, year);
                index ++;
            }
            if(director != null && !director.trim().isEmpty()){
                ps.setString(index, "%"+director+"%");
                index++;
            }

            return ps;


        }catch (SQLException e){
           throw new SQLException();
        }
    }

    private static String getSecondarySort(String orderby){
        if(orderby.equals("title")){
            return " rating desc ";
        }
        return " title asc ";
    }
}
