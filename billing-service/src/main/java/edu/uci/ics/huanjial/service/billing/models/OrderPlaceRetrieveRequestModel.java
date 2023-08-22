package edu.uci.ics.huanjial.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderPlaceRetrieveRequestModel {
    private String email;

    public OrderPlaceRetrieveRequestModel() {
    }

    public OrderPlaceRetrieveRequestModel(
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
