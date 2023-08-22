package edu.uci.ics.huanjial.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRetrieveRequestModel {
    private String email;

    public CustomerRetrieveRequestModel() {
    }

    public CustomerRetrieveRequestModel(
            @JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
