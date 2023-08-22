package edu.uci.ics.huanjial.service.api_gateway.utilities;

public class ResultCodes {
    // General error codes
    public static final int INTERNAL_SERVER_ERROR = -1;
    public static final int JSON_MAPPING_EXCEPTION = -2;
    public static final int JSON_PARSE_EXCEPTION = -3;

    // Data validation failure codes - for when data fails "plausible correctness" checks
    public static final int EMAIL_INVALID_LENGTH = -10;
    public static final int EMAIL_INVALID_FORMAT = -11;
    public static final int PASSWORD_INVALID_LENGTH = -12;
    public static final int TOKEN_INVALID_LENGTH = -13;
    public static final int PRIVILEGE_LEVEL_OUT_OF_RANGE = -14;
    public static final int USER_ID_OUT_OF_RANGE = -15;

    // "Success" codes - the request had proper syntax, and the data was plausibly correct
    public static final int PASSWORD_MISMATCH = 11;
    public static final int PASSWORD_INSUFFICIENT_LENGTH = 12;
    public static final int PASSWORD_INSUFFICIENT_CHARS = 13;
    public static final int USER_NOT_FOUND = 14;
    public static final int EMAIL_ALREADY_IN_USE = 16;

    // api/idm/user/register
    public static final int REGISTRATION_SUCCSSFUL = 110;

    // api/idm/user/login
    public static final int LOGIN_SUCCESSFUL = 120;

    // api/idm/session
    public static final int SESSION_NOT_FOUND = 130;
    public static final int SESSION_ACTIVE = 131;
    public static final int SESSION_EXPIRED = 132;
    public static final int SESSION_CLOSED = 133;
    public static final int SESSION_REVOKED = 134;
    public static final int SESSION_TIMEOUT = 135;

    // api/idm/user/privilege
    public static final int USER_PRIVILEGE_GOOD = 140;
    public static final int USER_PRIVILEGE_BAD = 141;

    // api/idm/user/password
    public static final int PASSWORD_UPDATED = 150;

    // api/idm/user/get
    public static final int USER_RETRIEVED = 160;

    // api/idm/user/create
    public static final int USER_CREATED = 171;
    public static final int CANNOT_CREATE_ROOT_USER = 172;

    // api/idm/user/update
    public static final int USER_UPDATED = 180;
    public static final int CANNOT_ELEVATE_USER_TO_ROOT = 181;

    public static final int[] resultCodes = {
            INTERNAL_SERVER_ERROR, JSON_MAPPING_EXCEPTION, JSON_PARSE_EXCEPTION, EMAIL_INVALID_LENGTH,
            EMAIL_INVALID_FORMAT, PASSWORD_INVALID_LENGTH, TOKEN_INVALID_LENGTH, PRIVILEGE_LEVEL_OUT_OF_RANGE,
            USER_ID_OUT_OF_RANGE, PASSWORD_MISMATCH, PASSWORD_INSUFFICIENT_LENGTH, PASSWORD_INSUFFICIENT_CHARS,
            USER_NOT_FOUND, SESSION_NOT_FOUND, EMAIL_ALREADY_IN_USE, REGISTRATION_SUCCSSFUL, LOGIN_SUCCESSFUL,
            SESSION_ACTIVE, SESSION_EXPIRED, SESSION_REVOKED, SESSION_TIMEOUT, USER_PRIVILEGE_GOOD, USER_PRIVILEGE_BAD,
            PASSWORD_UPDATED, USER_RETRIEVED, USER_CREATED, CANNOT_CREATE_ROOT_USER, USER_UPDATED,
            CANNOT_ELEVATE_USER_TO_ROOT
    };

    public static String setMessage(int code) {
        switch (code) {
            case JSON_MAPPING_EXCEPTION:
                return "JSON mapping exception occurred.";
            case JSON_PARSE_EXCEPTION:
                return "JSON parse exception occurred.";
            case EMAIL_INVALID_LENGTH:
                return "Email address has invalid length.";
            case EMAIL_INVALID_FORMAT:
                return "Email address has invalid format.";
            case PASSWORD_INVALID_LENGTH:
                return "Password has invalid length.";
            case TOKEN_INVALID_LENGTH:
                return "Token has invalid length.";
            case PRIVILEGE_LEVEL_OUT_OF_RANGE:
                return "Privilege level is not within valid range.";
            case USER_ID_OUT_OF_RANGE:
                return "User ID is not within a valid range.";
            case PASSWORD_MISMATCH:
                return "Passwords do not match.";
            case PASSWORD_INSUFFICIENT_LENGTH:
                return "Password does not meet length requirements.";
            case PASSWORD_INSUFFICIENT_CHARS:
                return "Password does not meet character requirements.";
            case USER_NOT_FOUND:
                return "User not found.";
            case EMAIL_ALREADY_IN_USE:
                return "Email already in use.";
            case REGISTRATION_SUCCSSFUL:
                return "User registered successfully .";
            case LOGIN_SUCCESSFUL:
                return "User successfully logged in.";
            case SESSION_NOT_FOUND:
                return "Session not found.";
            case SESSION_ACTIVE:
                return "Session is active.";
            case SESSION_CLOSED:
                return "Session is closed.";
            case SESSION_EXPIRED:
                return "Session is expired.";
            case SESSION_REVOKED:
                return "Session is revoked.";
            case SESSION_TIMEOUT:
                return "Session is timed out.";
            case USER_PRIVILEGE_GOOD:
                return "User has sufficient privilege.";
            case USER_PRIVILEGE_BAD:
                return "User has insufficient privilege.";
            case PASSWORD_UPDATED:
                return "User password successfully updated.";
            case USER_RETRIEVED:
                return "User successfully retrieved.";
            case USER_CREATED:
                return "User successfully created.";
            case CANNOT_CREATE_ROOT_USER:
                return "Creating user with ROOT privilege is not allowed.";
            case USER_UPDATED:
                return "User successfully updated.";
            case CANNOT_ELEVATE_USER_TO_ROOT:
                return "Elevating user to ROOT privilege is not allowed.";
            case INTERNAL_SERVER_ERROR:

            default:
                return "Internal server error.";
        }
    }
}
