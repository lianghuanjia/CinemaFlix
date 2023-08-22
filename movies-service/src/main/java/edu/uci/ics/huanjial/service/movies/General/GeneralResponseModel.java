package edu.uci.ics.huanjial.service.movies.General;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

public class GeneralResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    public GeneralResponseModel() {
    }

    public GeneralResponseModel(Integer resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public GeneralResponseModel(Integer caseInt){
        switch (caseInt){
            case 141:
                resultCode = 141;
                message = "User has insufficient privilege.";
                ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
                break;
            case 210:
                resultCode = 210;
                message = "Found movies with search parameters.";
                ServiceLogger.LOGGER.info("Case 210: Found movies with search parameters.");
                break;
            case 211:
                resultCode = 211;
                message = "No movies found with search parameters.";
                ServiceLogger.LOGGER.info("Case 211: No movies found with search parameters.");
                break;
            case 212:
                resultCode = 212;
                message = "Found stars with search parameters.";
                ServiceLogger.LOGGER.info("Case 212: Found stars with search parameters.");
                break;
            case 213:
                resultCode = 213;
                message = "No stars found with search parameters.";
                ServiceLogger.LOGGER.info("Case 213: No stars found with search parameters.");
                break;
            case 214:
                resultCode = 214;
                message = "Movie successfully added.";
                ServiceLogger.LOGGER.info("Case 214: Movie successfully added.");
                break;
            case 215:
                resultCode = 215;
                message = "Could not add movie.";
                ServiceLogger.LOGGER.info("Case 215: Could not add movie.");
                break;
            case 216:
                resultCode = 216;
                message = "Movie already exists.";
                ServiceLogger.LOGGER.info("Case 216: Movie already exists.");
                break;
            case 217:
                resultCode = 217;
                message = "Genre successfully added.";
                ServiceLogger.LOGGER.info("Case 217: Genre successfully added.");
                break;
            case 218:
                resultCode = 218;
                message = "Genre could not be added.";
                ServiceLogger.LOGGER.info("Case 218: Genre could not be added.");
                break;
            case 219:
                resultCode = 219;
                message = "Genres successfully retrieved.";
                ServiceLogger.LOGGER.info("Case 219: Genres successfully retrieved.");
                break;
            case 220:
                resultCode = 220;
                message = "Star successfully added.";
                ServiceLogger.LOGGER.info("Case 220: Star successfully added.");
                break;
            case 221:
                resultCode = 221;
                message = "Could not add star.";
                ServiceLogger.LOGGER.info("Case 221: Could not add star.");
                break;
            case 222:
                resultCode = 222;
                message = "Star already exists.";
                ServiceLogger.LOGGER.info("Case 222: Star already exists.");
                break;
            case 230:
                resultCode = 230;
                message = "Star successfully added to movie.";
                ServiceLogger.LOGGER.info("Case 230: Star successfully added to movie.");
                break;
            case 231:
                resultCode = 231;
                message = "Could not add star to movie.";
                ServiceLogger.LOGGER.info("Case 231: Could not add star to movie.");
                break;
            case 232:
                resultCode = 232;
                message = "Star already exists in movie.";
                ServiceLogger.LOGGER.info("Case 232: Star already exists in movie.");
                break;
            case 240:
                resultCode = 240;
                message = "Movie successfully removed.";
                ServiceLogger.LOGGER.info("Case 240: Movie successfully removed.");
                break;
            case 241:
                resultCode = 241;
                message = "Could not remove movie.";
                ServiceLogger.LOGGER.info("Case 241: Could not remove movie.");
                break;
            case 242:
                resultCode = 242;
                message = "Movie has been already removed.";
                ServiceLogger.LOGGER.info("Case 242: Movie has been already removed.");
                break;
            case 250:
                resultCode = 250;
                message = "Rating successfully updated.";
                ServiceLogger.LOGGER.info("Case 250: Rating successfully updated.");
                break;
            case 251:
                resultCode = 251;
                message = "Could not update rating.";
                ServiceLogger.LOGGER.info("Case 251: Could not update rating.");
                break;
            case -3:
                resultCode = -3;
                message = "JSON parse exception.";
                ServiceLogger.LOGGER.info("Case -3: JSON parse exception.");
                break;
            case -2:
                resultCode = -2;
                message = "JSON mapping exception.";
                ServiceLogger.LOGGER.info("Case -2: JSON mapping exception.");
                break;
            case -1:
                resultCode = -1;
                message = "Internal server error.";
                ServiceLogger.LOGGER.info("Case -1: Internal server error.");
                break;
            default:
                resultCode = 0;
                message = "Default, warning";
                ServiceLogger.LOGGER.info("default, warning");
                break;
        }
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
