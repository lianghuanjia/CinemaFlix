package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart;

import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class BillingCartRetrieveClearRequestModel extends RequestModel {
    private String email; // (string, required)

    public BillingCartRetrieveClearRequestModel() {
    }

    public BillingCartRetrieveClearRequestModel(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
