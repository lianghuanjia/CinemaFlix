package edu.uci.ics.huanjial.service.idm.resources;

import edu.uci.ics.huanjial.service.idm.IDMService;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.idm.security.Session;
import edu.uci.ics.huanjial.service.idm.security.Token;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionHelper {
    private String email;
    private String sessionID;

    public SessionHelper() {
    }

    public SessionHelper(String email, String sessionID) {
        this.email = email;
        this.sessionID = sessionID;
    }

    public boolean invalidTokenLen(){
        if(this.sessionID.length() > 128){
            return true;
        }
        return false;
    }

    public boolean invalidEmailFormat(){
        String validEmailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if(this.email == null){
            return true;
        }
        Pattern validEmailPattern = Pattern.compile(validEmailRegex);
        Matcher matcher = validEmailPattern.matcher(this.email);
        if(matcher.matches()==false){
            return true;
        }
        return false;
    }

    public boolean invalidEmailLen(){
        char[] emailChar = this.email.toCharArray();
        System.out.println(emailChar.length);
        if(emailChar.length > 50){
            return true;
        }else{
            return false;
        }
    }

    public boolean userNotFound() throws SQLException {
        try{
            String query = "SELECT email FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("In LoginHelper func: userNotFound: Trying query: " + query);
            ps.setString(1, this.email);
            ResultSet rs = ps.executeQuery(); // Using executeQuery for RETRIEVAL from database
            ServiceLogger.LOGGER.info("Query succeed.");
            if(rs.next() == false){ // No such a user
                return true;
            }
            return false; //.next() is true -> has the user in the database. User Not Found is Incorrect -> False.

        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException catched inside SessionHelper func: userNotFound.");
            ServiceLogger.LOGGER.info("Error message: "+ e.toString());
            throw new SQLException();
        }
    }

    /*
        lookUpSession returns the status of the session:
        0 -> session NOT FOUND
        1 -> ACTIVE
        2 -> CLOSE
        3 -> EXPIRED
        4 -> REVOKED
    */
    public int lookUpSession() throws SQLException{
        try{
            String query = "SELECT status FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,this.sessionID);
            ResultSet rs = ps.executeQuery();
            int status = 0;
            if(!rs.next()){
                ServiceLogger.LOGGER.info("SessionID not found");
                return status;
            }do{
                status = rs.getInt("status");
                return status;
            }while(rs.next());
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("SQLException catched inside SessionHelper func: lookUpSession.");
            throw new SQLException();
        }
    }
    /*
    When we use this function, we need to make sure the sessionID exists in the database.
     */
    public Session getSession() throws SQLException{
        try{
            ServiceLogger.LOGGER.info("Getting query");
            String query = "SELECT sessionID, email, timeCreated, lastUsed, exprTime FROM sessions WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,this.sessionID);
            ResultSet rs = ps.executeQuery();
            Session session = null;
            while(rs.next()){
                session = Session.rebuildSession(rs.getString("email"),
                        Token.rebuildToken(rs.getString("sessionID")),
                        rs.getTimestamp("timeCreated"),
                        rs.getTimestamp("lastUsed"),
                        rs.getTimestamp("exprTime"));
            }

            /*
   rebuildSession(String userName, Token sessionID, Timestamp timeCreated, Timestamp lastUsed, Timestamp exprTime) {
        return new Session(userName, sessionID, timeCreated, lastUsed, exprTime);
    }
             */
            if(session == null){
                ServiceLogger.LOGGER.warning("Session is NULL");
            }
            ServiceLogger.LOGGER.info("Finished rebuilding session.");
            return session;
            }
        catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException catched inside SessionHelper func: getSession.");
            throw new SQLException();
        }
    }

    public void setStatusRevoked(String sessionID){
        try{
            ServiceLogger.LOGGER.info("Setting status to be revoked.");
            String query = "UPDATE sessions SET status = ? WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1,4);
            ps.setString(2, sessionID);
            ServiceLogger.LOGGER.info("Trying query: " + query);
            int rowsAffected = ps.executeUpdate(); // This may be more than 1 because there might be multiple sessions associated with the user.
            ServiceLogger.LOGGER.info("Query succeed.");
            ServiceLogger.LOGGER.info("Number of rows are affected when updating status to revoked: " + rowsAffected);
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("In LoginHelper func setStatusRevoked: SQLException occurred.");
        }
    }

    public void setStatusExpired(String sessionID){
        try{
            ServiceLogger.LOGGER.info("Setting status to be expired.");
            String query = "UPDATE sessions SET status = ? WHERE sessionID = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1,3);
            ps.setString(2, sessionID);
            ServiceLogger.LOGGER.info("Trying query: " + query);
            int rowsAffected = ps.executeUpdate(); // This may be more than 1 because there might be multiple sessions associated with the user.
            ServiceLogger.LOGGER.info("Query succeed.");
            ServiceLogger.LOGGER.info("Number of rows are affected when updating status to revoked: " + rowsAffected);
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("In LoginHelper func setStatusRevoked: SQLException occurred.");
        }
    }

    /*
    createNewSession creates a new session,
    and returns the sessionID.
     */
    public String createNewSession(String email) throws SQLException{
        try{
            ServiceLogger.LOGGER.info("Create new session.");
            Session session = Session.createSession(email);
            String query = "INSERT INTO sessions (sessionID, email, status, timeCreated, lastUsed, exprTime) VALUES (?,?,?,?,?,?);";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            String sessionID = session.getSessionID().toString();
            ps.setString(1, sessionID); //sessionID
            ps.setString(2, session.getEmail());
            ps.setInt(3,1); //set the status as 1 -> ACTIVE
            ps.setTimestamp(4, session.getTimeCreated());
            ps.setTimestamp(5, session.getLastUsed());
            ps.setTimestamp(6, session.getExprTime());
            ServiceLogger.LOGGER.info("Trying query: " + query);
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeed.");
            return sessionID;
        }catch(SQLException e){
            ServiceLogger.LOGGER.info("In LoginHelper func logInSuccessfully: SQLException occurred.");
            throw new SQLException();
        }
    }

    public void updateLastUsedTime(Session session){
        try {
            ServiceLogger.LOGGER.info("Update session's last used time in database");
            String query = "update sessions set lastUsed = ? where sessionID = ? ;";
            session.update();
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setTimestamp(1, session.getLastUsed());
            ps.setString(2, this.sessionID);
            ServiceLogger.LOGGER.info("Finish setting parameters");
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Finish executing update query.");
        }
        catch (SQLException e){
            ServiceLogger.LOGGER.info("SessionHelper -> updateLastUsedTime: SQLException occurred");
        }
    }





    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
