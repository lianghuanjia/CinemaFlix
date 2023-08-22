package edu.uci.ics.huanjial.service.billing.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel extends GeneralResponseModel {
    private TransactionsModel[] transactions;

    public OrderRetrieveResponseModel() {
    }

    public OrderRetrieveResponseModel(int caseInt, TransactionsModel[] transactions) {
        super(caseInt);
        this.transactions = transactions;
    }

    public TransactionsModel[] getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionsModel[] transactions) {
        this.transactions = transactions;
    }
}
