package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Order;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingOrderRetrieveResponseModel {
    private Integer resultCode;
    private String message;
    private TransactionsModel[] transactions;

    public BillingOrderRetrieveResponseModel() {
    }

    public BillingOrderRetrieveResponseModel(Integer resultCode, String message, TransactionsModel[] transactions) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = transactions;
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

    public TransactionsModel[] getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionsModel[] transactions) {
        this.transactions = transactions;
    }
}
