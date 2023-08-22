package edu.uci.ics.huanjial.service.billing.General;

import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;

public class GeneralResponseModel {
    private int resultCode;
    private String message;

    public GeneralResponseModel() {
    }

    public GeneralResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public GeneralResponseModel(int caseInt){
        switch (caseInt){
            case -11:
                this.resultCode = -11;
                this.message = "Email address has invalid format.";
                ServiceLogger.LOGGER.info("Case -11: Email address has invalid format.");
                break;
            case -10:
                this.resultCode = -10;
                this.message = "Email address has invalid length.";
                ServiceLogger.LOGGER.info("Case -10: Email address has invalid length.");
                break;
            case -3:
                this.resultCode = -3;
                this.message = "JSON Parse Exception.";
                ServiceLogger.LOGGER.info("Case -3: JSON Parse Exception.");
                break;
            case -2:
                this.resultCode = -2;
                this.message = "JSON Mapping Exception.";
                ServiceLogger.LOGGER.info("Case -2: JSON Mapping Exception.");
                break;
            case -1:
                this.resultCode = -1;
                this.message = "Internal Server Error";
                ServiceLogger.LOGGER.info("Case -1: Internal Server Error");
                break;
            case 33:
                this.resultCode = 33;
                this.message = "Quantity has invalid value.";
                ServiceLogger.LOGGER.info("Case 33: Quantity has invalid value.");
                break;
            case 311:
                this.resultCode = 311;
                this.message = "Duplicate insertion.";
                ServiceLogger.LOGGER.info("Case 311: Duplicate insertion.");
                break;
            case 312:
                this.resultCode = 312;
                this.message = "Shopping item does not exist.";
                ServiceLogger.LOGGER.info("Case 312: Shopping item does not exist.");
                break;
            case 321:
                this.resultCode = 321;
                this.message = "Credit card ID has invalid length.";
                ServiceLogger.LOGGER.info("Case 321: Credit card ID has invalid length.");
                break;
            case 322:
                this.resultCode = 322;
                this.message = "Credit card ID has invalid value.";
                ServiceLogger.LOGGER.info("Case 322: Credit card ID has invalid value.");
                break;
            case 323:
                this.resultCode = 323;
                this.message = "expiration has invalid value.";
                ServiceLogger.LOGGER.info("Case 323: expiration has invalid value.");
                break;
            case 324:
                this.resultCode = 324;
                this.message = "Credit card does not exist.";
                ServiceLogger.LOGGER.info("Case 324: Credit card does not exist.");
                break;
            case 325:
                this.resultCode = 325;
                this.message = "Duplicate insertion.";
                ServiceLogger.LOGGER.info("Case 325: Duplicate insertion.");
                break;
            case 331:
                this.resultCode = 331;
                this.message = "Credit card ID not found.";
                ServiceLogger.LOGGER.info("Case 331: Credit card ID not found.");
                break;
            case 332:
                this.resultCode = 332;
                this.message = "Customer does not exist.";
                ServiceLogger.LOGGER.info("Case 332: Customer does not exist.");
                break;
            case 333:
                this.resultCode = 333;
                this.message = "Duplicate insertion.";
                ServiceLogger.LOGGER.info("Case 333: Duplicate insertion.");
                break;
            case 341:
                this.resultCode = 341;
                this.message = "Shopping cart for this customer not found.";
                ServiceLogger.LOGGER.info("Case 341: Shopping cart for this customer not found.");
                break;
            case 342:
                this.resultCode = 342;
                this.message = "Create payment failed.";
                ServiceLogger.LOGGER.info("Case 342: Create payment failed.");
                break;
            case 3100:
                this.resultCode = 3100;
                this.message = "Shopping cart item inserted successfully.";
                ServiceLogger.LOGGER.info("Case 3100: Shopping cart item inserted successfully.");
                break;
            case 3110:
                this.resultCode = 3110;
                this.message = "Shopping cart item updated successfully.";
                ServiceLogger.LOGGER.info("Case 3110: Shopping cart item updated successfully.");
                break;
            case 3120:
                this.resultCode = 3120;
                this.message = "Shopping cart item deleted successfully.";
                ServiceLogger.LOGGER.info("Case 3120: Shopping cart item deleted successfully.");
                break;
            case 3130:
                this.resultCode = 3130;
                this.message = "Shopping cart retrieved successfully.";
                ServiceLogger.LOGGER.info("Case 3130: Shopping cart retrieved successfully.");
                break;
            case 3140:
                this.resultCode = 3140;
                this.message = "Shopping cart cleared successfully.";
                ServiceLogger.LOGGER.info("Case 3140: Shopping cart cleared successfully.");
                break;
            case 3200:
                this.resultCode = 3200;
                this.message = "Credit card inserted successfully.";
                ServiceLogger.LOGGER.info("Case 3200: Credit card inserted successfully.");
                break;
            case 3210:
                this.resultCode = 3210;
                this.message = "Credit card updated successfully.";
                ServiceLogger.LOGGER.info("Case 3210: Credit card updated successfully.");
                break;
            case 3220:
                this.resultCode = 3220;
                this.message = "Credit card deleted successfully.";
                ServiceLogger.LOGGER.info("Case 3220: Credit card deleted successfully.");
                break;
            case 3230:
                this.resultCode = 3230;
                this.message = "Credit card retrieved successfully.";
                ServiceLogger.LOGGER.info("Case 3230: Credit card retrieved successfully.");
                break;
            case 3300:
                this.resultCode = 3300;
                this.message = "Customer inserted successfully.";
                ServiceLogger.LOGGER.info("Case 3300: Customer inserted successfully.");
                break;
            case 3310:
                this.resultCode = 3310;
                this.message = "Customer updated successfully.";
                ServiceLogger.LOGGER.info("Case 3310: Customer updated successfully.");
                break;
            case 3320:
                this.resultCode = 3320;
                this.message = "Customer retrieved successfully.";
                ServiceLogger.LOGGER.info("Case 3320: Customer retrieved successfully.");
                break;
            case 3400:
                this.resultCode = 3400;
                this.message = "Order placed successfully.";
                ServiceLogger.LOGGER.info("Case 3400: Order placed successfully.");
                break;
            case 3410:
                this.resultCode = 3410;
                this.message = "Orders retrieved successfully.";
                ServiceLogger.LOGGER.info("Case 3410: Orders retrieved successfully.");
                break;
            case 3421:
                this.resultCode = 3421;
                this.message = "Token not found.";
                ServiceLogger.LOGGER.info("Case 3421: Token not found.");
                break;
            case 3422:
                this.resultCode = 3422;
                this.message = "Payment can not be completed.";
                ServiceLogger.LOGGER.info("Case 3422: Payment can not be completed.");
                break;
            case 3420:
                this.resultCode = 3420;
                this.message = "Payment is completed successfully.";
                ServiceLogger.LOGGER.info("Case 3420: Payment is completed successfully.");
                break;
            default:
                this.resultCode = 0;
                this.message = "Warning! Default message.";
                ServiceLogger.LOGGER.info("Warning! Default message");
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
}
