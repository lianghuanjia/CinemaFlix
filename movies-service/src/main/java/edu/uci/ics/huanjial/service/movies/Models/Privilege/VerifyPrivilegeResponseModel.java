package edu.uci.ics.huanjial.service.movies.Models.Privilege;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyPrivilegeResponseModel {
    private Integer resultCode;
    private String message;

    public VerifyPrivilegeResponseModel() {
    }

    public VerifyPrivilegeResponseModel(Integer resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
