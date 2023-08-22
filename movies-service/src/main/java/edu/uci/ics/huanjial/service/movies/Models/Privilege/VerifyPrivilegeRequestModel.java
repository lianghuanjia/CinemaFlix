package edu.uci.ics.huanjial.service.movies.Models.Privilege;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyPrivilegeRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "plevel", required = true)
    private Integer plevel;

    @JsonCreator
    public VerifyPrivilegeRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "plevel", required = true) Integer plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    @Override
    public String toString() {
        return "Email: " + email + " Privilege Level: " + plevel;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("plevel")
    public Integer getPlevel() {
        return plevel;
    }
}
