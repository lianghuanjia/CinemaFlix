package edu.uci.ics.huanjial.service.api_gateway.utilities;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.models.Model;

import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.lang.reflect.Constructor;

import static edu.uci.ics.huanjial.service.api_gateway.GatewayService.ANSI_RED;
import static edu.uci.ics.huanjial.service.api_gateway.GatewayService.ANSI_RESET;
import static edu.uci.ics.huanjial.service.api_gateway.utilities.HTTPStatusCodes.setHTTPStatus;
import static edu.uci.ics.huanjial.service.api_gateway.utilities.ResultCodes.INTERNAL_SERVER_ERROR;
import static edu.uci.ics.huanjial.service.api_gateway.utilities.ResultCodes.JSON_MAPPING_EXCEPTION;
import static edu.uci.ics.huanjial.service.api_gateway.utilities.ResultCodes.JSON_PARSE_EXCEPTION;


public class ModelValidator {
    public static Model verifyModel(String jsonText, Class modelType) throws ModelValidationException {
        ServiceLogger.LOGGER.info("Verifying model format...");
        ObjectMapper mapper = new ObjectMapper();
        String warning = "";
        Model model;

        try {
            ServiceLogger.LOGGER.info("Attempting to deserialize JSON to POJO");
            model = (Model) mapper.readValue(jsonText, modelType);
            ServiceLogger.LOGGER.info("Successfully deserialized JSON to POJO.");
        } catch (JsonMappingException e) {
            warning = "Unable to map JSON to POJO--request has invalid format.";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + "\n" + ExceptionUtils.exceptionStackTraceAsString(e) + ANSI_RESET);
            throw new ModelValidationException(warning, e);
        } catch (JsonParseException e) {
            warning = "Unable to parse JSON--text is not in valid JSON format.";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + "\n" + ExceptionUtils.exceptionStackTraceAsString(e) + ANSI_RESET);
            throw new ModelValidationException(warning, e);
        } catch (IOException e) {
            warning = "IOException while mapping JSON to POJO.";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + "\n" + ExceptionUtils.exceptionStackTraceAsString(e) + ANSI_RESET);
            throw new ModelValidationException(warning, e);
        }
        ServiceLogger.LOGGER.info("Model has been validated.");
        return model;
    }

    public static Response returnInvalidRequest(ModelValidationException e, Class modelType) {
        try {
            Class<?> model = Class.forName(modelType.getName());
            Constructor<?> constructor;
            constructor = model.getConstructor(Integer.TYPE);
            Object object = null;
            int resultCode;

            if (e.getCause() instanceof JsonMappingException) {
                object = constructor.newInstance(JSON_MAPPING_EXCEPTION);
                resultCode = JSON_MAPPING_EXCEPTION;
            } else if (e.getCause() instanceof JsonParseException) {
                object = constructor.newInstance(JSON_PARSE_EXCEPTION);
                resultCode = JSON_PARSE_EXCEPTION;
            } else {
                object = constructor.newInstance(INTERNAL_SERVER_ERROR);
                resultCode = INTERNAL_SERVER_ERROR;
            }
            return Response.status(setHTTPStatus(resultCode)).entity(object).build();
        } catch (Exception ex) {
            ServiceLogger.LOGGER.warning("Unable to create ResponseModel " + modelType.getName());
            ServiceLogger.LOGGER.warning(ANSI_RED + ExceptionUtils.exceptionStackTraceAsString(e) + ANSI_RESET);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
