package edu.uci.ics.huanjial.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CCDeleteRetrieveRequest {
    private String id;

    public CCDeleteRetrieveRequest() {
    }

    public CCDeleteRetrieveRequest(@JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
