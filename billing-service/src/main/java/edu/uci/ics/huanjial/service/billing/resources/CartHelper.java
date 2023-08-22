package edu.uci.ics.huanjial.service.billing.resources;

import edu.uci.ics.huanjial.service.billing.BillingService;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartHelper {
    public static void insert(InsertRequestModel requestModel) throws SQLException {
        ServiceLogger.LOGGER.info("CartHelper -> insert.");
        ServiceLogger.LOGGER.info("Get query");
        String query = "insert carts (email, movieId, quantity) values (?, ?, ?);";
        ServiceLogger.LOGGER.info("Get prepared statement");
        try{
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovieId());
            ps.setInt(3, requestModel.getQuantity());

            ServiceLogger.LOGGER.info("Finish setting variables");
            ServiceLogger.LOGGER.info("Execute prepared statement");

            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in CartHelper.insert");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }

        //insert carts (email, movieId, quantity) values ('sam@uci.edu', 'tt123',1);
    }

    public static Integer update (UpdateRequestModel requestModel) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("CartHelper -> update");
            ServiceLogger.LOGGER.info("Get query");
            String query = "update carts set quantity  = ? where email = ? and movieId = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setInt(1, requestModel.getQuantity());
            ps.setString(2, requestModel.getEmail());
            ps.setString(3, requestModel.getMovieId());
            ServiceLogger.LOGGER.info("Finish setting variables");
            ServiceLogger.LOGGER.info("Execute prepared statement");

            Integer influenceRow = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            return influenceRow;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CartHelper.update");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }

    public static Integer delete (DeleteRequestModel requestModel) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("CartHelper -> delete");
            ServiceLogger.LOGGER.info("Get query");
            String query = "delete from carts where email = ? and movieId = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getMovieId());
            ServiceLogger.LOGGER.info("Finish setting variables");
            ServiceLogger.LOGGER.info("Execute prepared statement");

            Integer influenceRow = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            return influenceRow;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CartHelper.delete");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }

    public static Integer clear (String email) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("CartHelper -> clear");
            ServiceLogger.LOGGER.info("Get query");
            String query = "delete from carts where email = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Finish setting variables");
            ServiceLogger.LOGGER.info("Execute prepared statement");

            Integer influenceRow = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            return influenceRow;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CartHelper.clear");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }



    public static ItemModel[] retrieve(String email) throws SQLException{
        try{
            ServiceLogger.LOGGER.info("CartHelper -> retrieve");
            ServiceLogger.LOGGER.info("Get query");
            String query = "select * from carts where email = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, email);
            ArrayList<ItemModel> imList = new ArrayList<>();

            ResultSet rs = ps.executeQuery();
            if(rs.next() == false){
                ServiceLogger.LOGGER.info("Result set is empty. No item retrieved with this email.");
                return null;
            }else{
                do{
                    ItemModel im = new ItemModel();
                    im.setEmail(rs.getString("email"));
                    im.setMovieId(rs.getString("movieId"));
                    im.setQuantity(rs.getInt("quantity"));
                    imList.add(im);
                }while(rs.next());
            }
            ItemModel[] imArray = imList.toArray(new ItemModel[imList.size()]);
            return imArray;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CartHelper.retrieve");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }

}
