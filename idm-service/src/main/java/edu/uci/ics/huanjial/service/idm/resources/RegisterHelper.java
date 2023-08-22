package edu.uci.ics.huanjial.service.idm.resources;

import edu.uci.ics.huanjial.service.idm.IDMService;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.idm.security.Crypto;
import org.apache.commons.codec.binary.Hex;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterHelper {
    private String email = null;
    private char[] password = null;

    public RegisterHelper() {
    }

    public RegisterHelper(String email, char[] password) {
        this.email = email;
        this.password = password;
    }

    /*
    invalidLenPassword simply checks whether the password is null or empty.
    If they are, then returns true.
    Else returns false.
     */
    public boolean invalidLenPassword(){
        if(this.password == null){
            return true;
        }else if(this.password.length == 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean invalidEmailFormat(){
        String validEmailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if(this.email == null){
            return true;
        }
        Pattern pattern = Pattern.compile(validEmailRegex);
        Matcher matcher = pattern.matcher(this.email);
        if(matcher.matches()==false){
            return true;
        }
        else{
            return false;
        }
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

    public boolean invalidPasswordLenReq(){
        if(this.password.length < 7 || this.password.length > 16){
            return true;
        }return false;
    }

    public boolean passwordCharReqMatches(){
        String Symbol = "!\"#$%&'()*+,-./:;<=>?@[\\]^_´`{|}~";
        boolean lowerCase = false;
        boolean digit = false;
        boolean upperCase = false;
        boolean specialSymbol = false;

        for(char character: this.password){
            if(Character.isDigit(character)==true){
                digit = true;
            }else if(Character.isUpperCase(character) == true){
                upperCase = true;
            }else if(Character.isLowerCase(character)==true){
                lowerCase = true;
            }else if(Symbol.indexOf(character) >= 0){
                specialSymbol = true;
            }
        }
        if(lowerCase == true && upperCase == true && digit == true && specialSymbol==true){
            return true;
        }return false;
    }

    public boolean emailAlreadyInUse() throws SQLException {
        try{
            String query = "SELECT email FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("In RegisterHelper func: emailAlreadyInUse: Trying query: " + query);
            ps.setString(1,this.email);
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeed");
            if(rs.next() == false){
                return false; //Cannot find that email in the database, which means the email is not used.
            }
            return true;
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("SQLException catched inside RegisterHelper function emailAlreadyInUse.");
            throw new SQLException();
        }
    }

    public void insertRegisterInfo() throws SQLException{
        try{
            String query = "INSERT INTO users (email, status, plevel, salt, pword) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,this.email); //email
            ps.setInt(2,1); //status
            ps.setInt(3,5); //plevel
            byte[] salt = Crypto.genSalt();
            String hashedPass = Hex.encodeHexString(Crypto.hashPassword(this.password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH));
            Arrays.fill(this.password, '0'); //after encoding, set the password into 0.
            String encodedSalt = Hex.encodeHexString(salt);
            ps.setString(4, encodedSalt);
            ps.setString(5,hashedPass);
            ServiceLogger.LOGGER.info("Trying query: " + query);
            ps.executeUpdate(); //Uses executeUPDATE to insert into database.
            ServiceLogger.LOGGER.info("Query executes successfully");
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("In RegisterHelper func insertRegisterInfo, an SQLException occurred.");
            throw new SQLException();
        }
    }
/*
    public static void main(String[] args){

        String strPassword = "abc123s#Z";
        char[] password = strPassword.toCharArray();
        RegisterHelper regHelper = new RegisterHelper("abcabcabcabcabc@domain.com", password);
        System.out.println("The password meets requirements: " + regHelper.passwordCharReqMatches());

    }
*/
}
