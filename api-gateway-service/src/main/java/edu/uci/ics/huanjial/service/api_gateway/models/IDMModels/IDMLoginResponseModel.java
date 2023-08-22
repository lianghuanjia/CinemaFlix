package edu.uci.ics.huanjial.service.api_gateway.models.IDMModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;

import javax.xml.ws.Service;


@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDMLoginResponseModel {
    private int resultCode;
    private String message;
    private String sessionID;

    public IDMLoginResponseModel() {
    }

    public IDMLoginResponseModel(int resultCode, String message, String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public IDMLoginResponseModel(int caseCode){
        switch(caseCode){
            case -12:
                this.resultCode = -12;
                this.message = "Password has invalid length.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case -12: Password has invalid length.");
                break;
            case -11:
                this.resultCode = -11;
                this.message = "Email address has invalid format.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case -11: Email address has invalid format.");
                break;
            case -10:
                this.resultCode = -10;
                this.message = "Email address has invalid length.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case -10: Email address has invalid length.");
                break;
            case -3:
                this.resultCode = -3;
                this.message = "JSON Parse Exception.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
                break;
            case -2:
                this.resultCode = -2;
                this.message = "JSON Mapping Exception.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
                break;
            case -1:
                this.resultCode = -1;
                this.message = "Internal server error.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case -1: Internal server error.");
                break;
            case 11:
                this.resultCode = 11;
                this.message = "Passwords do not match.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case 11: Passwords do not match.");
                break;
            case 14:
                this.resultCode = 14;
                this.message = "User not found.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case 14: User not found.");
                break;
            case 120: // In this case, we need to set the session id later!!!
                this.resultCode = 120;
                this.message = "User logged in successfully.";
                ServiceLogger.LOGGER.info("Case 120: User logged in successfully.");
                break;
            default:
                this.resultCode = 0;
                this.message = null;
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Default");
                break;
        }
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
