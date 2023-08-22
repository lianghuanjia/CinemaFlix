package edu.uci.ics.huanjial.service.idm.models;

import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;

public class ResultCodeMessageResponseModel {
    private int resultCode;
    private String message;

    public ResultCodeMessageResponseModel() {
    }

    public ResultCodeMessageResponseModel(int caseInt){
        switch (caseInt){
            case -14:
                this.resultCode = -14;
                this.message = "Privilege level out of valid range.";
                ServiceLogger.LOGGER.warning("Case -14, Privilege level out of valid range.");
                break;
            case -12:
                this.resultCode = -12;
                this.message = "Password has invalid length.";
                ServiceLogger.LOGGER.warning("Case -12, Password has invalid length.");
                break;
            case -11:
                this.resultCode = -11;
                this.message = "Email address has invalid format.";
                ServiceLogger.LOGGER.warning("Case -11, Email address has invalid format.");
                break;
            case -10:
                this.resultCode = -10;
                this.message = "Email address has invalid length.";
                ServiceLogger.LOGGER.warning("Case -10, Email address has invalid length.");
                break;
            case -3:
                this.resultCode = -3;
                this.message = "JSON Parse Exception.";
                ServiceLogger.LOGGER.warning("Case -3, JSON Parse Exception.");
                break;
            case -2:
                this.resultCode = -2;
                this.message = "JSON Mapping Exception.";
                ServiceLogger.LOGGER.warning("Case -2, JSON Mapping Exception.");
                break;
            case -1:
                this.resultCode = -1;
                this.message = "Internal Server Error.";
                ServiceLogger.LOGGER.warning("Case -1, Internal Server Error.");
                break;
            case 12:
                this.resultCode = 12;
                this.message = "Password does not meet length requirements.";
                ServiceLogger.LOGGER.warning("Case 12, Password does not meet length requirements.");
                break;
            case 13:
                this.resultCode = 13;
                this.message = "Password does not meet character requirements.";
                ServiceLogger.LOGGER.warning("Case 13, Password does not meet character requirements.");
                break;
            case 14:
                this.resultCode = 14;
                this.message = "User not found.";
                break;
            case 16:
                this.resultCode = 16;
                this.message = "Email already in use.";
                ServiceLogger.LOGGER.warning("Case 16, Email already in use.");
                break;
            case 110:
                this.resultCode = 110;
                this.message = "User registered successfully.";
                ServiceLogger.LOGGER.info("Case 110, User registered successfully.");
                break;
            case 140:
                this.resultCode = 140;
                this.message = "User has sufficient privilege level.";
                ServiceLogger.LOGGER.info("Case 140, User has sufficient privilege level.");
                break;
            case 141:
                this.resultCode = 141;
                this.message = "User has insufficient privilege level.";
                ServiceLogger.LOGGER.info("Case 141, User has insufficient privilege level.");
                break;
            default:
                this.resultCode = 0;
                this.message = null;
                break;
        }
    }

    public ResultCodeMessageResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
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
}
