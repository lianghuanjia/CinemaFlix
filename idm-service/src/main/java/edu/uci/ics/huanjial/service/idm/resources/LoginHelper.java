package edu.uci.ics.huanjial.service.idm.resources;

import edu.uci.ics.huanjial.service.idm.IDMService;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.idm.security.Crypto;
import edu.uci.ics.huanjial.service.idm.security.Session;
import edu.uci.ics.huanjial.service.idm.security.Token;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import sun.rmi.runtime.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginHelper {
    private String email;
    private char[] password;

    public LoginHelper() {
    }

    public LoginHelper(String email, char[] password) {
        this.email = email;
        this.password = password;
    }

    /*
    invalidPasswordLen is to check that whether the password is empty or null.
    If the password is empty or null, simply returns true.
    Else returns false.
     */
    public boolean invalidPasswordLen(){
        if(this.password == null){
            return true;
        }else if(this.password.length == 0){
            return true;
        }else{
            return false;
        }
    }

    /*
    invalidEmailFormat checks if the email's format is correct.
    In this function, the email is checked by an VALID email regex. If the matcher.matches() returns false, then it means
        the email does NOT match the valid email format, so it returns true. (Invalid Email Format)
    Else, returns false.(Invalid email format is NOT TRUE).
     */

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

    /*
    passwordNotMatched is to see whether the password the user enters matches the user's password in the database.
    1. We need to extract the user's password from the database.
    2. Compare the database's password to the password the user provides.
    3. According to the result, returns true if they do NOT matched. returns false if they matched.
     */
    public boolean passwordNotMatched() throws SQLException, DecoderException{
        try{
            String query = "SELECT salt, pword FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,this.email);
            ServiceLogger.LOGGER.info("Try query: " + query);
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeed.");
            String DBSalt = null;
            String hashedDBPassword = null;
            while(rs.next()) {
                DBSalt = rs.getString("salt"); //Get the ENCODED salt from DB.
                hashedDBPassword = rs.getString("pword"); //Get the user's hashed password from DB
            }
            byte[] decodeSalt = Hex.decodeHex(DBSalt);
            //hashed the input password
            String hashedInputPass = Hex.encodeHexString(Crypto.hashPassword(this.password, decodeSalt, Crypto.ITERATIONS, Crypto.KEY_LENGTH));
            if(!hashedInputPass.equals(hashedDBPassword)){ //Not equals -> Not matched.
                return true;
            }
            return false;
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException catched inside LoginHelper func: passwordNotMatched.");
            throw new SQLException();
        }catch (DecoderException e) {
            ServiceLogger.LOGGER.warning("DecoderException catched inside LoginHelper func: passwordNotMatched.");
            e.printStackTrace();
            throw new DecoderException();
        }
    }

    /*
    userNotFound is to see whether the user is in the database or not.
    1. Look for the user in the database by using the user's EMAIL.
    2. Check the result.
    3. If the result is empty, then returns true        (User is not in the database -> Not Found)
    4. If the result if NOT empty, then returns false   (User in the database -> Found)
     */
    public boolean userNotFound() throws SQLException{
        try{
            String query = "SELECT email FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, this.email);
            ResultSet rs = ps.executeQuery(); // Using executeQuery for RETRIEVAL from database
            ServiceLogger.LOGGER.info("Query succeed.");
            if(rs.next() == false){ // No such a user
                ServiceLogger.LOGGER.info("LoginHelper.userNotFound: user not found");
                return true;
            }
            ServiceLogger.LOGGER.info("User found");
            return false; //.next() is true -> has the user in the database. User Not Found is Incorrect -> False.

        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException catched inside LoginHelper func: userNotFound.");
            throw new SQLException();
        }
    }

    public boolean invalidEmailLen(){
        char[] emailChar = this.email.toCharArray();
        if(emailChar.length > 50){
            return true;
        }else{
            return false;
        }
    }


    public String logInSuccessfully() throws SQLException{
        try{
            if(sessionActive()){
                setStatusRevoked();
            }
            ServiceLogger.LOGGER.info("Create new session.");
            Session session = Session.createSession(this.email);
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

    /*
     String query = "INSERT INTO users (email, status, plevel, salt, pword) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,this.email); //email
            ps.setInt(2,1); //status
            ps.setInt(3,5); //plevel
            byte[] salt = Crypto.genSalt();
            String hashedPass = Hex.encodeHexString(Crypto.hashPassword(this.password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH));
            String encodedSalt = Hex.encodeHexString(salt);
            ps.setString(4, encodedSalt);
            ps.setString(5,hashedPass);
            ServiceLogger.LOGGER.info("Trying query: " + query);
            ps.executeUpdate(); //Uses executeUPDATE to insert into database.
            ServiceLogger.LOGGER.info("Query executes successfully");
     */





    /*
    Check the status of the user using his email. If it's ACTIVE, returns true, else returns false
     */
    public boolean sessionActive() throws SQLException{
        try{
            String query = "SELECT status FROM sessions WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,this.email);
            ResultSet rs = ps.executeQuery();
            int status = 0;
            if(!rs.next()){
                ServiceLogger.LOGGER.info("In LoginHelper func sessionActive: No such a SESSION related to the user email.");
                return false;
            }else{
                do{
                    status = rs.getInt("status");
                    if(status == 1){
                        return true;
                    }
                    return false;
                }while(rs.next()); //Note: we need the while loop because there might be multiple sessions that are associated with the user.
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("In LoginHelper func sessionActive: SQLException occurred.");
            throw new SQLException();
        }
    }

    public void setStatusRevoked(){
        try{
            ServiceLogger.LOGGER.info("Setting status to be revoked.");
            String query = "UPDATE sessions SET status = ? WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1,4);
            ps.setString(2,this.email);
            ServiceLogger.LOGGER.info("Trying query: " + query);
            int rowsAffected = ps.executeUpdate(); // This may be more than 1 because there might be multiple sessions associated with the user.
            ServiceLogger.LOGGER.info("Query succeed.");
            ServiceLogger.LOGGER.info("Number of rows are affected when updating status to revoked: " + rowsAffected);
        }catch(SQLException e){
            ServiceLogger.LOGGER.warning("In LoginHelper func setStatusRevoked: SQLException occurred.");
        }
    }

}
