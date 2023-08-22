package edu.uci.ics.huanjial.service.billing.resources;

import edu.uci.ics.huanjial.service.billing.BillingService;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.CustomerInsertUpdateRequestModel;
import edu.uci.ics.huanjial.service.billing.models.CustomerModel;
import edu.uci.ics.huanjial.service.billing.models.CustomerRetrieveRequestModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerHelper {

    public static void insert(CustomerInsertUpdateRequestModel requestModel) throws SQLException {
        ServiceLogger.LOGGER.info("CustomerHelper -> insert.");
        ServiceLogger.LOGGER.info("Get query");
        String query = "insert into customers (email, firstName, lastName, ccId, address) values (?, ?, ?, ?, ?);";
        //insert into customers values ('sam@uci.edu', 'peter', 'parker', '1234567890123456789', 'new york brooklyn');
        ServiceLogger.LOGGER.info("Get prepared statement");
        try{
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getEmail());
            ps.setString(2, requestModel.getFirstName());
            ps.setString(3, requestModel.getLastName());
            ps.setString(4, requestModel.getCcId());
            ps.setString(5, requestModel.getAddress());
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

    public static Integer update (CustomerInsertUpdateRequestModel requestModel) throws SQLException{
        try {
            ServiceLogger.LOGGER.info("CartHelper -> update");
            ServiceLogger.LOGGER.info("Get query");
            String query = "update customers set  firstName = ?, lastName = ?, ccId = ?, address = ? where email = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, requestModel.getFirstName());
            ps.setString(2, requestModel.getLastName());
            ps.setString(3, requestModel.getCcId());
            ps.setString(4, requestModel.getAddress());
            ps.setString(5, requestModel.getEmail());
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

    public static CustomerModel retrieve(String email) throws SQLException{
        try{
            ServiceLogger.LOGGER.info("CustomerHelper -> Retrieve");
            ServiceLogger.LOGGER.info("Get query");
            String query = "select * from customers where email = ? ";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, email);
            CustomerModel cm = new CustomerModel();
            ResultSet rs = ps.executeQuery();
            if(rs.next() == false){
                ServiceLogger.LOGGER.info("Result set is empty. No customer retrieved with this given email.");
                return null;
            }else{
                do{
                    cm.setEmail(rs.getString("email"));
                    cm.setFirstName(rs.getString("firstName"));
                    cm.setLastName(rs.getString("lastName"));
                    cm.setCcId(rs.getString("ccId"));
                    cm.setAddress(rs.getString("address"));
                }while(rs.next());
            }
            ServiceLogger.LOGGER.info("Finish getting customer model. Return it.");
            return cm;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CCHelper.retrieve");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }
}
