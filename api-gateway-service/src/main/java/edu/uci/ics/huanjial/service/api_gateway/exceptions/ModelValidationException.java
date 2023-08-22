package edu.uci.ics.huanjial.service.api_gateway.exceptions;

public class ModelValidationException extends Exception {
    public ModelValidationException() {
    }

    public ModelValidationException(String message) {
        super(message);
    }

    public ModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
