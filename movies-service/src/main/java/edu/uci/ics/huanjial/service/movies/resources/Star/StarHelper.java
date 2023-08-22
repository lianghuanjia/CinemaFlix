package edu.uci.ics.huanjial.service.movies.resources.Star;

import edu.uci.ics.huanjial.service.movies.Models.Star.StarAddRequestModel;
import edu.uci.ics.huanjial.service.movies.Models.Star.StarModel;
import edu.uci.ics.huanjial.service.movies.Models.Star.StarsInRequestModel;
import edu.uci.ics.huanjial.service.movies.MovieService;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;

public class StarHelper {
    public static String getStarNewID() throws SQLException{

        try {
            String getIDQuery = "SELECT id from stars where id like '%ss%' order by id desc limit 1;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(getIDQuery);

            ResultSet rs = ps.executeQuery();
            String oldID = null;
            while(rs.next()){
                oldID = rs.getString("id");
            }
            if(oldID == null){
                return "ss0000001";
            }
            String noSsString = oldID.replace("ss","00");
            Integer intOldID = Integer.parseInt(noSsString);
            ServiceLogger.LOGGER.info("Biggest number existing in DB is: " + intOldID);
            Integer newMovieID = intOldID+1; //don't do intOldID++. This will assign intOldID to newMovieID first then + 1


            int numOfDigits = 7;
            String SsPrefix = "ss";
            String num = newMovieID.toString();
            int len = num.length();
            int remainDigits = numOfDigits - len;
            String zeroPrefix = "";
            for (int i = 0; i < remainDigits; i++) {
                zeroPrefix += "0";
            }
            String newID = SsPrefix + zeroPrefix + num;
            return newID;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("StarHelper.getStarNewID: SQLException occurred.");
            throw new SQLException();
        }
    }

    public static Boolean starExists(String name) throws SQLException {
        try {
            ServiceLogger.LOGGER.info("StarHelper.starExists");
            String query = "select name from stars where name = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1, name);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing query");
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No star found with name. It means the star does NOT exist.");
                return false;
            }
            ServiceLogger.LOGGER.info("Found star in db. Star exist");
            return true;
        }
        catch (SQLException e ){
            ServiceLogger.LOGGER.info("SQLException occurred in StarHelper.starExists");
            throw new SQLException();
        }
    }

    public static Integer checkBirthYear(Integer birthYear){
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if(birthYear > currentYear){ // currentYear:2019, birthYear: 2020
            return null;
        }
        return birthYear;
    }

    public static Boolean addStar(String starID, String name, Integer birthYear){
        try {
            ServiceLogger.LOGGER.info("StarHelper.addStar");
            String query = "insert into stars (id, name, birthYear) VALUES (?, ?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1, starID);
            ps.setString(2, name);
            if(birthYear == null){
                ps.setNull(3, Types.INTEGER);
            }
            else {
                ps.setInt(3, birthYear);
            }
            ServiceLogger.LOGGER.info("Finish setting parameters");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing.");
            ServiceLogger.LOGGER.info("Insert successfully. Return true");
            return true;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in StarHelper.addStar");
            ServiceLogger.LOGGER.info("Insert failed. Return false");
            return false;
        }
    }

    public static Boolean movieExists(String movieId) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("StarHelper.movieExists");
            String query = "select * from movies where id = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,movieId);
            ServiceLogger.LOGGER.info("Finish setting parameter");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            if(rs.next()){
                ServiceLogger.LOGGER.info("Movie exists in db");
                return true;
            }
            ServiceLogger.LOGGER.info("Movie does NOT exist in db");
            return false;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in StarHelper.movieExists");
            throw new SQLException();
        }
    }

    public static Boolean starAlreadyExistsInMovie(StarsInRequestModel requestModel) throws SQLException {
        ServiceLogger.LOGGER.info("StarHelper.starAlreadyExistsInMovie");
        String movieID = requestModel.getMovieid();
        String starID = requestModel.getStarid();
        try {
            String query = "select * from stars_in_movies where movieId = ? and starId = ?;";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1, movieID);
            ps.setString(2,starID);
            ServiceLogger.LOGGER.info("Finish setting parameters");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            if(rs.next()){ //can found, so the star exists in the movie.
                ServiceLogger.LOGGER.info("There is starID and movieID set in the db, star exists in the movie.");
                return true;
            }
            ServiceLogger.LOGGER.info("Star does NOT exist in the movie");
            return false;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in StarHelper.starAlreadyExistsInMovie");
            throw new SQLException();
        }
    }

    public static Boolean addStarInMovie(StarsInRequestModel requestModel){
        ServiceLogger.LOGGER.info("StarHelper.addStarInMovie");
        String movieID = requestModel.getMovieid();
        String starID = requestModel.getStarid();
        try {
            String query = "insert into stars_in_movies (starId, movieId) VALUES (?, ?);";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1, starID);
            ps.setString(2, movieID);
            ServiceLogger.LOGGER.info("Finish setting parameters");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing");
            return true;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in StarHelper.addStarInMovie");
            return false;
        }
    }

    public static StarModel[] searchStars(String name, Integer birthYear,
                                          String movieTitle, Integer limit,
                                          Integer offset,String orderby,
                                          String direction) throws SQLException {
        try {
            ServiceLogger.LOGGER.info("StarHelper.searchStars");
            PreparedStatement ps = getPreparedStatement(name, birthYear, movieTitle, limit, offset, orderby, direction);
            ServiceLogger.LOGGER.info("Prepared Statement: " + ps.toString());
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            ArrayList<StarModel> smList = new ArrayList<StarModel>();
            if (!rs.next()) {
                ServiceLogger.LOGGER.warning("No stars found");
                return null;
            } else {
                ServiceLogger.LOGGER.info("Found stars");
                do {
                    StarModel sm = new StarModel();
                    sm.setId(rs.getString("id"));
                    sm.setName(rs.getString("name"));
                    if (rs.getObject("birthYear") == null) {
                        sm.setBirthYear(null);
                    } else {
                        sm.setBirthYear(((Integer) rs.getObject("birthYear")).toString());
                    }
                    smList.add(sm);
                } while (rs.next());
            }

            StarModel[] smArray = smList.toArray(new StarModel[smList.size()]);
            ServiceLogger.LOGGER.info("Finish transferring list to array.");
            return smArray;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in: StarHelper.searchStars");
            throw new SQLException();
        }
    }

    public static PreparedStatement getPreparedStatement(String name, Integer birthYear,
                                            String movieTitle, Integer limit,
                                            Integer offset,String orderby,
                                            String direction) throws SQLException {
        String baseQuery = "select movieTitle, id, birthYear, name from (\n" +
                "select movies.title as movieTitle, stars_with_movies.id, stars_with_movies.birthYear, stars_with_movies.name from movies inner join\n" +
                "    (select stars.id, stars.name, stars.birthYear, sim.movieId from stars inner join stars_in_movies sim on stars.id = sim.starId) as stars_with_movies where stars_with_movies.movieId = movies.id ) as all_info ";

        String nameCon = (name == null || name.trim().isEmpty())? "":" name LIKE  ?  AND"; //Notice: no space around %
        String birthYearCon = (birthYear == null)? "":" birthYear = ? AND";
        String movieTitleCon = (movieTitle == null || movieTitle.trim().isEmpty())? "":" movieTitle LIKE ?  AND";

        String optionalField = nameCon + birthYearCon + movieTitleCon;
        String begin = "";
        if(!optionalField.isEmpty()){
            begin = " WHERE ";

            String ANDstr = "AND";
            if(optionalField.endsWith(ANDstr)) {
                begin  = begin + optionalField.substring(0, optionalField.length() - ANDstr.length());
            }
        }

        String secondarySort = getSecondarySort(orderby);

        String extraField = begin + " ORDER BY " + orderby + " " + direction +", " + secondarySort + " LIMIT " + limit + " OFFSET " + offset + " ;";

        String wholeQuery = baseQuery + extraField;

        try {

            PreparedStatement ps = MovieService.getCon().prepareStatement(wholeQuery);

            Integer index = 1;

            if(name != null && !name.trim().isEmpty()){
                ps.setString(index, "%"+name+"%");
                index ++;
            }

            if(birthYear != null){ //Didn't check if the year is < 0
                ps.setInt(index, birthYear);
                index ++;
            }
            if(movieTitle != null && !movieTitle.trim().isEmpty()){
                ps.setString(index, "%"+movieTitle+"%");
                index++;
            }

            return ps;


        }catch (SQLException e){
            throw new SQLException();
        }



    }

    private static String getSecondarySort(String orderby){
        if(orderby.equals("name")){
            return (" birthYear asc ");
        }
        return ("name asc");
    }
/*
        (@Context
    HttpHeaders headers,
    @QueryParam("name")String name,
    @QueryParam("birthYear") Integer birthYear,
    @QueryParam("movieTitle") String movieTitle,
    @QueryParam("limit") Integer limit,
    @QueryParam("offset") Integer offset,
    @QueryParam("orderby") String orderby,
    @QueryParam("direction") String direction)

 */
}
