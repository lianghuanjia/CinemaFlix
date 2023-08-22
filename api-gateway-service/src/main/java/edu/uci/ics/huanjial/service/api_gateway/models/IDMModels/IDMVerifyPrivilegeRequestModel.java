package edu.uci.ics.huanjial.service.api_gateway.models.IDMModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class IDMVerifyPrivilegeRequestModel extends RequestModel {

    private String email;
    private int plevel;

    public IDMVerifyPrivilegeRequestModel() {
    }

    public IDMVerifyPrivilegeRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPlevel() {
        return plevel;
    }

    public void setPlevel(int plevel) {
        this.plevel = plevel;
    }
}
