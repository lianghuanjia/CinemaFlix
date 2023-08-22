package edu.uci.ics.huanjial.service.api_gateway.utilities;

import javax.ws.rs.core.Response.Status;

import static edu.uci.ics.huanjial.service.api_gateway.utilities.ResultCodes.*;


public class HTTPStatusCodes {
    public static Status setHTTPStatus(int code) {
        switch (code) {
            case JSON_MAPPING_EXCEPTION:
            case JSON_PARSE_EXCEPTION:
            case EMAIL_INVALID_LENGTH:
            case EMAIL_INVALID_FORMAT:
            case PASSWORD_INVALID_LENGTH:
            case TOKEN_INVALID_LENGTH:
            case PRIVILEGE_LEVEL_OUT_OF_RANGE:
            case USER_ID_OUT_OF_RANGE:
                return Status.BAD_REQUEST;
            case PASSWORD_MISMATCH:
            case PASSWORD_INSUFFICIENT_LENGTH:
            case PASSWORD_INSUFFICIENT_CHARS:
            case USER_NOT_FOUND:
            case SESSION_NOT_FOUND:
            case EMAIL_ALREADY_IN_USE:
            case REGISTRATION_SUCCSSFUL:
            case LOGIN_SUCCESSFUL:
            case SESSION_ACTIVE:
            case SESSION_CLOSED:
            case SESSION_EXPIRED:
            case SESSION_REVOKED:
            case USER_PRIVILEGE_GOOD:
            case USER_PRIVILEGE_BAD:
            case PASSWORD_UPDATED:
            case USER_RETRIEVED:
            case USER_CREATED:
            case CANNOT_CREATE_ROOT_USER:
            case USER_UPDATED:
            case CANNOT_ELEVATE_USER_TO_ROOT:
                return Status.OK;
            case INTERNAL_SERVER_ERROR:

            default:
                return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
