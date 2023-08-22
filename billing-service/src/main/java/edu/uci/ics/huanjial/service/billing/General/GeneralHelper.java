package edu.uci.ics.huanjial.service.billing.General;

import edu.uci.ics.huanjial.service.billing.logger.ServiceLogger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralHelper {
    public static Response.Status getStatus(int caseInt)
    {
        switch (caseInt){
            case -11:
            case -10:
            case -3:
            case -2:
                ServiceLogger.LOGGER.info("GeneralHelper -> getStatus: return Response.Status.BAD_REQUEST");
                return Response.Status.BAD_REQUEST;
            case -1:
                ServiceLogger.LOGGER.warning("Internal Server Error");
                return Response.Status.INTERNAL_SERVER_ERROR;
            case 33:
            case 311:
            case 312:
            case 321:
            case 322:
            case 323:
            case 324:
            case 325:
            case 331:
            case 332:
            case 333:
            case 341:
            case 342:
            case 3100:
            case 3110:
            case 3120:
            case 3130:
            case 3140:
            case 3200:
            case 3210:
            case 3220:
            case 3230:
            case 3300:
            case 3310:
            case 3320:
            case 3400:
            case 3410:
            case 3420:
            case 3421:
            case 3422:
                ServiceLogger.LOGGER.info("GeneralHelper -> getStatus: return Response.Status.OK");
                return Response.Status.OK;
            default:
                ServiceLogger.LOGGER.warning("Warning! GeneralHelper -> getStatus: return Response.Status.INTERNAL_SERVER_ERROR");
                return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }

    public static boolean invalidEmailFormat(String email){
        String validEmailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if(email == null){
            return true;
        }
        Pattern pattern = Pattern.compile(validEmailRegex);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()==false){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean invalidEmailLen(String email){
        char[] emailChar = email.toCharArray();
        if(emailChar.length > 50){
            return true;
        }else{
            return false;
        }
    }

    public static Response returnResponse(Integer caseInt, HttpHeaders headers){
        GeneralResponseModel responseModel = new GeneralResponseModel(caseInt);
        String headerEmail = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        return Response.status(getStatus(caseInt)).entity(responseModel).header("email",headerEmail).header("sessionID", sessionID).header("transactionID", transactionID).build();
    }

    public static boolean invalidQuantityValue(Integer quantity){
        return (quantity <= 0);
    }

}
