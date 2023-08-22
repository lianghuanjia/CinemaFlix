package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Order;

public class BillingOrderPlaceResponseModel{
    private Integer resultCode;
    private String message;
    private String redirectURL;
    private String token;

    public BillingOrderPlaceResponseModel() {
    }

    public BillingOrderPlaceResponseModel(String redirectURL, String token, Integer resultCode, String message) {
        this.redirectURL = redirectURL;
        this.token = token;
        this.resultCode = resultCode;
        this.message = message;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
