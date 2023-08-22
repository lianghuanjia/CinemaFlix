package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.CC;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

import java.sql.Date;

public class BillingCCInsertUpdateRequestModel extends RequestModel {
    private String id; //required
    private String firstName; //required
    private String lastName; //required
    private Date expiration; //required. >= today

    public BillingCCInsertUpdateRequestModel() {
    }

    public BillingCCInsertUpdateRequestModel(@JsonProperty(value = "id", required = true) String id,
                                             @JsonProperty(value = "firstName", required = true) String firstName,
                                             @JsonProperty(value = "lastName", required = true) String lastName,
                                             @JsonProperty(value = "expiration", required = true) Date expiration) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiration = expiration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
