package edu.uci.ics.huanjial.service.idm.resources;

import edu.uci.ics.huanjial.service.idm.IDMService;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyPrivilegeHelper {
    private String email;
    private int plevel;

    public VerifyPrivilegeHelper() {
    }

    public VerifyPrivilegeHelper(String email, int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    public boolean invalidPlevelRange(){
        if(this.plevel <= 0 || this.plevel > 5){
            return true;
        }
        return false;
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

    public boolean userNotFound() throws SQLException{
        try{
            String query = "SELECT email FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("In VerifyPrivilegeHelper func userNotFound: Trying query: " + query);
            ps.setString(1, this.email);
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeed.");
            if(rs.next() == false){
                return true;
            }
            return false;
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning("In VerifyPrivilegeHelper func userNotFound: occurred SQLException");
            throw new SQLException();
        }
    }

    /*
    1.Get plevel from database according to the given email
    2.Compare the pleve with the plevel in the request
     */
    public boolean plevelSufficient() throws SQLException{
        try{
            String query = "SELECT plevel FROM users WHERE email = ?;";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1,this.email);
            ResultSet rs = ps.executeQuery();
            int userPlevel = 0;
            while(rs.next()){
                userPlevel = rs.getInt("plevel");
            }
            if(userPlevel == 0){
                ServiceLogger.LOGGER.warning("In VerifyPrivilegeHelper func plevelSufficient: userPlevel is still 0. It should be 1-5.");
            }
            if(this.plevel >= userPlevel){
                return true;
            }
            return false;

        }catch(SQLException e){
            ServiceLogger.LOGGER.info("In VerifyPrivilegeHelper func plevelSufficient: occurred SQLException");
            throw new SQLException();
        }
    }

}
