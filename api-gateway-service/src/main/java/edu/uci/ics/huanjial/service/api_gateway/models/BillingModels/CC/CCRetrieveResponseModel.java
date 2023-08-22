package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.CC;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CCRetrieveResponseModel{
    private CCModel creditcard;
    private Integer resultCode;
    private String message;

    public CCRetrieveResponseModel() {
    }

    public CCRetrieveResponseModel(CCModel creditcard, Integer resultCode, String message) {
        this.creditcard = creditcard;
        this.resultCode = resultCode;
        this.message = message;
    }

    public CCModel getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(CCModel creditcard) {
        this.creditcard = creditcard;
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
