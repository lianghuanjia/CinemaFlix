package edu.uci.ics.huanjial.service.billing.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

public class CCInsertUpdateRequestModel {
    private String id; //required
    private String firstName; //required
    private String lastName; //required
    private Date expiration; //required. >= today

    public CCInsertUpdateRequestModel() {
    }

    public CCInsertUpdateRequestModel(@JsonProperty(value = "id", required = true) String id,
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
