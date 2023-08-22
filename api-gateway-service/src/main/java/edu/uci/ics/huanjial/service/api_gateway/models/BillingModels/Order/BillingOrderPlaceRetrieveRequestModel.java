package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class BillingOrderPlaceRetrieveRequestModel extends RequestModel {
    private String email;

    public BillingOrderPlaceRetrieveRequestModel() {
    }

    public BillingOrderPlaceRetrieveRequestModel(
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
