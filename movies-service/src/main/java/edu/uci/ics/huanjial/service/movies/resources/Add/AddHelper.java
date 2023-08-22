package edu.uci.ics.huanjial.service.movies.resources.Add;

import edu.uci.ics.huanjial.service.movies.Models.Add.AddRequestModel;
import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddHelper {

    public static Boolean movieExists(AddRequestModel requestModel) throws SQLException{
        String title = requestModel.getTitle();
        String director = requestModel.getDirector();
        Integer year = requestModel.getYear();

        try {
            ServiceLogger.LOGGER.info("Check movie exist.");
            String query = "SELECT title, year, director, movieId, hidden, GROUP_CONCAT(genreId) AS genreIDs from movies left join genres_in_movies gim on movies.id = gim.movieId " +
                            "WHERE title  = ? AND year = ? AND director = ? AND (hidden = 0 or hidden is null);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1,title);
            ps.setInt(2,year);
            ps.setString(3, director);
            ServiceLogger.LOGGER.info("Look for movie query: ");
            ServiceLogger.LOGGER.info(ps.toString());
            ResultSet rs = ps.executeQuery();
            ArrayList<String> grList = new ArrayList<String>();
            while(rs.next()){
                String dbTitle = rs.getString("title");
                Integer dbYear = rs.getInt("year");
                String dbDirector = (rs.getString("director"));
                System.out.println("title: " + dbTitle);
                System.out.println("year: " + dbYear);
                System.out.println("director: " + dbDirector);
                if((title.equals(dbTitle) && (year.intValue() == dbYear.intValue()) && director.equals(dbDirector)))
                {
                    ServiceLogger.LOGGER.info("Found matched movie");
                    return true;
                }
            }
            return false;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning("In AddHelper func movieExists: SQLException occured");
            throw new SQLException();
        }
    }

    private static ArrayList<String> getGenreIDArray(ArrayList<String> gIdsList, String genreIDs){
        ServiceLogger.LOGGER.info("AddHelper -> getGenreIDArray: ready to split genreIDs string with , ");
        ServiceLogger.LOGGER.info("String genreIDs: "+genreIDs);
        String[] genreIDsArray = genreIDs.split(",");
        ServiceLogger.LOGGER.info("Finish splitting String movieID(s)");
        for (String genreID : genreIDsArray) {
            gIdsList.add(genreID);
            }
        ServiceLogger.LOGGER.info("AddHelper -> getGenreIDArray: finish spliting and putting them into ArrayList<String>");
        return gIdsList;

    }

}
