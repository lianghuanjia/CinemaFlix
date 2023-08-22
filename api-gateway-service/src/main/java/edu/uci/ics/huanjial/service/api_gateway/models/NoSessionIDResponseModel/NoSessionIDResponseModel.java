package edu.uci.ics.huanjial.service.api_gateway.models.NoSessionIDResponseModel;

public class NoSessionIDResponseModel {
    private Integer resultCode;
    private String message;

    public NoSessionIDResponseModel() {
    }

    public NoSessionIDResponseModel(Integer resultCode, String message) {
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
