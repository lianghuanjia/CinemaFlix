package edu.uci.ics.huanjial.service.movies.resources.Rating;

import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingHelper {
    public static Boolean updateRating(String movieID, Float rating){
        try {
            ServiceLogger.LOGGER.info("RatingHelper.updateRating");
            String query = "select * from ratings where movieId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting ps for select * from ratings with given movieId");
            ps.setString(1,movieID);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing select statement");
            Float avgRating = null;
            Integer numVotes = null;
            while(rs.next()){
                avgRating = rs.getFloat("rating");
                numVotes = rs.getInt("numVotes");
            }
            ServiceLogger.LOGGER.info("Finish getting rating and numVotes from ResultSet");
            ServiceLogger.LOGGER.info("Rating: " + avgRating.toString() + ", numVotes: " + numVotes.toString());
            Float newRating = (numVotes*avgRating+rating)/(numVotes + 1);
            Integer newNumVotes = numVotes + 1;
            ServiceLogger.LOGGER.info("newRating: " + newRating.toString() + ", newNumVotes: "+newNumVotes.toString());
            String updateQuery = "update ratings set rating = ?, numVotes = ? where movieId = ?;";
            PreparedStatement ps1 = MovieService.getCon().prepareStatement(updateQuery);
            ServiceLogger.LOGGER.info("Finish getting ps for updating statement");
            ps1.setFloat(1,newRating);
            ps1.setInt(2, newNumVotes);
            ps1.setString(3, movieID);
            ServiceLogger.LOGGER.info("Finish setting parameters");
            ps1.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing update query");
            return true;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in RatingHelper.updateRating");
            return false;
        }
    }
}
