package edu.uci.ics.huanjial.service.api_gateway.models;


public class GeneralResponseModel {
    private Integer resultCode;
    private String message;

    public GeneralResponseModel() {
    }

    public GeneralResponseModel(Integer resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
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
