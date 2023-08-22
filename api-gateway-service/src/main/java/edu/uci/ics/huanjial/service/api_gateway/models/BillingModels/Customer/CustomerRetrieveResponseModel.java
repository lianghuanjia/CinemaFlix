package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Customer;


public class CustomerRetrieveResponseModel {
    private CustomerModel customer;
    private Integer resultCode;
    private String message;

    public CustomerRetrieveResponseModel() {
    }

    public CustomerRetrieveResponseModel(CustomerModel customer, Integer resultCode, String message) {
        this.customer = customer;
        this.resultCode = resultCode;
        this.message = message;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
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
