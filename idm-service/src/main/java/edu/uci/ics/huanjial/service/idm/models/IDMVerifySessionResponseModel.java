package edu.uci.ics.huanjial.service.idm.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDMVerifySessionResponseModel {
    private int resultCode;
    private String message;
    private String sessionID;

    public IDMVerifySessionResponseModel() {
    }

    public IDMVerifySessionResponseModel(int resultCode, String message, String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public IDMVerifySessionResponseModel(int caseCode){
        switch(caseCode){
            case -13:
                this.resultCode = -13;
                this.message = "Token has invalid length.";
                this.sessionID = null; //Should I set it to null?
                ServiceLogger.LOGGER.warning("Case -13: Token has invalid length.");
                break;
            case -11:
                this.resultCode = -11;
                this.message = "Email address has invalid format.";
                this.sessionID = null;
                ServiceLogger.LOGGER.warning("Case -11: Email address has invalid format.");
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
            case 14:
                this.resultCode = 14;
                this.message = "User not found.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case 14: User not found.");
                break;

            case 130: // need insert sessionID later;
                this.resultCode = 130;
                this.message = "Session is active.";

                ServiceLogger.LOGGER.info("Case 130: Session is active.");
                break;

            // 131 - 134 do NOT return a session ID.
            case 131:
                this.resultCode = 131;
                this.message = "Session is expired.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case 131:  Session is expired.");
                break;

            case 132:
                this.resultCode = 132;
                this.message = "Session is closed.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case 132: Session is closed.");
                break;

            case 133:
                this.resultCode = 133;
                this.message = "Session is revoked.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case 133: Session is revoked.");
                break;

            case 134:
                this.resultCode = 134;
                this.message = "Session not found.";
                this.sessionID = null;
                ServiceLogger.LOGGER.info("Case 134: Session not found.");
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
