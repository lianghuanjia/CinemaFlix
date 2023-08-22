package edu.uci.ics.huanjial.service.billing.resources;

import com.paypal.api.payments.Sale;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.huanjial.service.billing.BillingService;
import edu.uci.ics.huanjial.service.billing.General.PayPalClient;
import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.billing.models.*;

import javax.sql.rowset.serial.SerialClob;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class OrderHelper {

    public static Map<String, Object> place(String email, ItemModel[] itemArray) throws SQLException, PayPalRESTException{
        try {

            ServiceLogger.LOGGER.info("OrderHelper -> place");
            ServiceLogger.LOGGER.info("Create Query");
            //String query = "insert into sales (email, movieId, quantity, saleDate) select email, movieId, quantity,  ?  from carts where email = ?;";
            String getTotalPriceQuery = "select sum(unit_price*discount*quantity) as totalPrice from\n" +
                    "\n" +
                    "(select carts.movieId, unit_price, discount, id, email, quantity from movie_prices inner join carts where movie_prices.movieId  = carts.movieId )\n" +
                    "\n" +
                    "as cart_with_price where  email = ?;";
            //"select sum(unit_price*discount*quantity) as totalPrice from  (select carts.movieId, unit_price, discount, id, email, quantity from movie_prices inner join carts where movie_prices.movieId  = carts.movieId group by carts.movieId) as cart_with_price where  email = ?;";
            //get the place date
            long millis = System.currentTimeMillis();
            Date date = new Date(millis);

            ServiceLogger.LOGGER.info("Get getTotalPriceQuery prepared statement");

            PreparedStatement ps = BillingService.getCon().prepareStatement(getTotalPriceQuery);
            ServiceLogger.LOGGER.info("Put email into getTotalPriceQuery statement");
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Execute getTotalPriceQuery statement");
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Finish executing getTotalPriceQueryStatement prepared statement");
            Double sum = 0.0;
            while(rs.next()){
                sum = rs.getDouble("totalPrice");
            }
            ServiceLogger.LOGGER.info("Total amount sum is: " + sum);
            String roundSum = String.format("%.2f", sum);
            ServiceLogger.LOGGER.info("Total amount sum after rounding: " + roundSum);
            ServiceLogger.LOGGER.info("Call PayPal API");
            PayPalClient ppClient = new PayPalClient(roundSum);
            Map<String, Object>response = ppClient.createPayment();
            ServiceLogger.LOGGER.info("Finish creating payment");

            String insert_sales_transactions_query = "{call insert_sales_transactions(?, ?, ?, ?,?)}";
            //call insert_sales_transactions('haha@uci.edu', 'tt012345', 20, CURDATE(), '123abc@$%');
            for(ItemModel im: itemArray) {
                CallableStatement cs = BillingService.getCon().prepareCall(insert_sales_transactions_query);
                ServiceLogger.LOGGER.info("Finish getting callableStatement");
                cs.setString(1, email);
                cs.setString(2, im.getMovieId());
                cs.setInt(3, im.getQuantity());
                cs.setDate(4, date);
                cs.setString(5, getToken(response));
                ServiceLogger.LOGGER.info("Finish setting up parameters");
                cs.execute();
                ServiceLogger.LOGGER.info("Finish execution 1 query");
            }
            return response;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("Exception content: ");
            ServiceLogger.LOGGER.info(e.toString());
            ServiceLogger.LOGGER.info("OrderHelper -> place: SQLException");
            throw new SQLException();
        }
        catch (PayPalRESTException e){
            ServiceLogger.LOGGER.info("OrderHelper -> place: PayPalRESTException");
            throw new PayPalRESTException(e);
        }
    }

    public static String getToken(Map<String, Object> response){
        Object redirectUrl = response.get("redirect_url");
        String strRedirectUrl = redirectUrl.toString();
        String[] split_url = strRedirectUrl.split("token=");
        String token = split_url[1];
        return token;
    }

    public static Integer updateTransactionId(String token, String transactionId) throws SQLException{
        try{
            ServiceLogger.LOGGER.info("OrderHelper -> updateTransactionId");
            ServiceLogger.LOGGER.info("Get query");
            String query = "update transactions set transactionId = ? where token = ?;";
            ServiceLogger.LOGGER.info("Finish getting query");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, transactionId);
            ps.setString(2, token);
            ServiceLogger.LOGGER.info("Finish setting up parameters");
            Integer influencedRow = ps.executeUpdate();
            return influencedRow;
        }catch (SQLException e){
            ServiceLogger.LOGGER.info("OrderHelper -> updateTransactionId: SQLException occurred");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException();
        }
    }
    /*
    public static OrderModel[] retrieve(String email) throws SQLException{
        try{
            ServiceLogger.LOGGER.info("OrderHelper -> Retrieve");
            ServiceLogger.LOGGER.info("Get query");
            String query = "select * from sales where email = ?;";
            ServiceLogger.LOGGER.info("Get prepared statement");
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Set variables to prepared statement");
            ps.setString(1, email);
            ArrayList<OrderModel> omList = new ArrayList<>();

            ResultSet rs = ps.executeQuery();
            if(rs.next() == false){
                ServiceLogger.LOGGER.info("Result set is empty. No item retrieved with this email.");
                return null;
            }else{
                do{
                    OrderModel om = new OrderModel();
                    om.setEmail(rs.getString("email"));
                    om.setMovieId(rs.getString("movieId"));
                    om.setQuantity(rs.getInt("quantity"));
                    om.setSaleDate(rs.getDate("saleDate"));
                    omList.add(om);
                }while(rs.next());
            }
            OrderModel[] omArray = omList.toArray(new OrderModel[omList.size()]);
            return omArray;
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SQLException occurred in CartHelper.retrieve");
            ServiceLogger.LOGGER.info("Exception message is: \n");
            ServiceLogger.LOGGER.info(e.toString());
            throw new SQLException(e);
        }
    }

     */
    public static TransactionsModel[] retrieve(String email) throws SQLException, PayPalRESTException{
        ServiceLogger.LOGGER.info("OrderHelper -> retrieve");
        String getTransactionIDsQuery = "select distinct transactionId from sales inner join transactions t on sales.id = t.sId where email = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(getTransactionIDsQuery);
        ps.setString(1,email);
        ServiceLogger.LOGGER.info("Finish setting up parameter");
        ResultSet rs = ps.executeQuery();
        ArrayList<TransactionsModel> tmList = new ArrayList<>();
        APIContext apiContext = new APIContext(PayPalClient.clientID, PayPalClient.clientSecret, "sandbox");
        if(!rs.next()){
            ServiceLogger.LOGGER.info("No transaction IDs related to the email " + email);
            return null;
        }else{
            do{
                String transactionID = rs.getString("transactionID");
                if(transactionID != null){
                    TransactionsModel transaction = getTransaction(transactionID, apiContext);
                    tmList.add(transaction);
                }
            }while (rs.next());
        }
        ServiceLogger.LOGGER.info("Finish getting TransactionsModel LIST in retrieve.");
        TransactionsModel[] tmArray = tmList.toArray(new TransactionsModel[tmList.size()]);
        ServiceLogger.LOGGER.info("Finish transferring into array.");
        return tmArray;
    }

    public static TransactionsModel getTransaction(String transactionID, APIContext apiContext) throws PayPalRESTException, SQLException{
        try {
            ServiceLogger.LOGGER.info("OrderHelper -> getTransaction");
            ServiceLogger.LOGGER.info("Transaction ID: " + transactionID);
            TransactionsModel transaction = new TransactionsModel();
            Sale sale = Sale.get(apiContext, transactionID);
            ServiceLogger.LOGGER.info("Finish getting Sale object.");
            transaction.setTransactionId(sale.getId());// ID
            transaction.setState(sale.getState());// state
            AmountModel amount = new AmountModel(sale.getAmount().getTotal(), sale.getAmount().getCurrency());
            transaction.setAmount(amount); // amount
            TransactionFeeModel transaction_fee = new TransactionFeeModel(sale.getTransactionFee().getValue(), sale.getTransactionFee().getCurrency());
            transaction.setTransaction_fee(transaction_fee);
            transaction.setCreate_time(sale.getCreateTime());
            transaction.setUpdate_time(sale.getUpdateTime());
            transaction.setItems(getItemArray(transactionID));
            ServiceLogger.LOGGER.info("Finishing 1 transactionModel");
            return transaction;
        }catch (PayPalRESTException e){
            throw new PayPalRESTException(e);
        }catch (SQLException e){
            throw new SQLException();
        }
    }

    public static TransactionsItemsModel[] getItemArray(String transactionID)throws SQLException{
        ServiceLogger.LOGGER.info("OrderHelper getItemArray");
        String query = "select * from\n" +
                "(select sales.id as saleID, email, sales.movieId, quantity, unit_price, discount, saleDate from sales inner join movie_prices on sales.movieId = movie_prices.movieId ) as sale_with_prices\n" +
                "    inner join transactions on transactions.sId = sale_with_prices.saleID where transactionId = ?;";
        try {
            ArrayList<TransactionsItemsModel> tiList = new ArrayList<TransactionsItemsModel>();
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Finish getting prepared statement");
            ps.setString(1,transactionID);
            ServiceLogger.LOGGER.info("Finish setting up parameter");
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                ServiceLogger.LOGGER.info("No such items related to the transaction ID");
                return null;
            }else{
                do{
                    TransactionsItemsModel tiModel = new TransactionsItemsModel();
                    tiModel.setEmail(rs.getString("email"));
                    tiModel.setMovieId(rs.getString("movieId"));
                    tiModel.setQuantity(rs.getInt("quantity"));
                    tiModel.setUnit_price(rs.getFloat("unit_price"));
                    tiModel.setDiscount(rs.getFloat("discount"));
                    tiModel.setSaleDate(rs.getDate("saleDate").toString());
                    tiList.add(tiModel);
                }while (rs.next());
            }
            ServiceLogger.LOGGER.info("Finishing getting transaction item LIST");
            TransactionsItemsModel[] tiArray = tiList.toArray(new TransactionsItemsModel[tiList.size()]);
            return tiArray;
        }
        catch (SQLException e){
            throw new SQLException();
        }
    }

}
