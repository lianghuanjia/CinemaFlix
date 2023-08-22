package edu.uci.ics.huanjial.service.api_gateway.models.IDMModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class IDMRegisterRequestModel extends RequestModel {
    private String email;
    private char[] password;

    public IDMRegisterRequestModel() {
    }

    public IDMRegisterRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "password", required = true) char[] password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
