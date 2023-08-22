package edu.uci.ics.huanjial.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IDMVerifySessionRequestModel {
    private String email;
    private String sessionID;

    public IDMVerifySessionRequestModel() {
    }

    public IDMVerifySessionRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "sessionID", required = true) String sessionID
            )
    {
        this.email = email;
        this.sessionID = sessionID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
