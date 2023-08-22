package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.CC;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class BillingCCDeleteRetrieveRequest extends RequestModel {
    private String id;

    public BillingCCDeleteRetrieveRequest() {
    }

    public BillingCCDeleteRetrieveRequest(@JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
