package edu.uci.ics.huanjial.service.billing.resources;

import edu.uci.ics.huanjial.service.billing.BillingService;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.CCDeleteRetrieveRequest;
import edu.uci.ics.huanjial.service.billing.models.CCInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.billing.models.CCModel;
import edu.uci.ics.huanjial.service.billing.models.CCRetrieveResponse;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CreditCardHelper {

    public static Boolean invalidLenCreditCard(String id){

        return id.length() < 16 || id.length() > 20;
    }

    public static Boolean invalidValueCreditCard(String id){
        return !id.matches("\\d+");
    }

    public static Boolean invalidValueExpiration(CCInsertUpdateRequestModel requestModel){
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);
        Date requestDate = requestModel.getExpiration();
        return requestDate.before(currentDate);
    }

    public static void insert(CCInsertUpdateRequestModel requestModel) throws SQLException {
        ServiceLogger.LOGGER.info("CCHelper -> insert.");
        ServiceLogger.LOGGER.info("Get query");
        String query = "INSERT INTO creditcards VALUES (?, ?, ?, ?);";
        //INSERT INTO creditcards VALUES ('9999999999999999999', 'The Test First Name', 'The Test Last Name', CURDATE() +  INTERVAL 1 DAY);
        ServiceLogger.LOGGER.info("Get prepared statement");
        try{
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getId());
            ps.setString(2, requestModel.getFirstName());
            ps.setString(3, requestModel.getLastName());
            ps.setDate(4, requestModel.getExpiration());

            ServiceLogger.LOGGER.info("Finish setting variables");
            ServiceLogger.LOGGER.info("Execute prepared statement");

            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException occurred in CCHelper.insert");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }

        //insert carts (email, movieId, quantity) values ('sam@uci.edu', 'tt123',1);
    }


    public static Integer update (CCInsertUpdateRequestModel requestModel) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("CCHelper -> update");
            ServiceLogger.LOGGER.info("Get query");
            String query = "update creditcards set firstName = ?, lastName = ?, expiration = ? where id = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getFirstName());
            ps.setString(2, requestModel.getLastName());
            ps.setDate(3, requestModel.getExpiration());
            ps.setString(4, requestModel.getId());
            ServiceLogger.LOGGER.info("Finish setting variables");
            ServiceLogger.LOGGER.info("Execute prepared statement");

            Integer influenceRow = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            return influenceRow;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CCHelper.update");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }

    public static Integer delete (CCDeleteRetrieveRequest requestModel) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("CartHelper -> delete");
            ServiceLogger.LOGGER.info("Get query");
            String query = "delete from creditcards where id = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getId());
            ServiceLogger.LOGGER.info("Finish setting variables");
            ServiceLogger.LOGGER.info("Execute prepared statement");

            Integer influenceRow = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing prepared statement");
            return influenceRow;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CCHelper.delete");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }

    public static CCModel[] retrieve(CCDeleteRetrieveRequest requestModel) throws SQLException{
        try{
            ServiceLogger.LOGGER.info("CartHelper -> delete");
            ServiceLogger.LOGGER.info("Get query");
            String query = "select * from creditcards where id = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getId());
            ArrayList<CCModel> ccmList = new ArrayList<>();

            ResultSet rs = ps.executeQuery();
            if(rs.next() == false){
                ServiceLogger.LOGGER.info("Result set is empty. No item retrieved with this given credit card id.");
                return null;
            }else{
                do{
                    CCModel ccm = new CCModel();
                    ccm.setId(rs.getString("id"));
                    ccm.setFirstName(rs.getString("firstName"));
                    ccm.setLastName(rs.getString("lastName"));
                    ccm.setExpiration(rs.getString("expiration"));
                    ccmList.add(ccm);
                }while(rs.next());
            }
            CCModel[] ccmArray = ccmList.toArray(new CCModel[ccmList.size()]);
            return ccmArray;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CCHelper.retrieve");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }

}
