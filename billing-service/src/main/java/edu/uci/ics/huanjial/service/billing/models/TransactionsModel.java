package edu.uci.ics.huanjial.service.billing.models;

public class TransactionsModel {
    private String transactionId;
    private String state;
    private AmountModel amount;
    private TransactionFeeModel transaction_fee;
    private String create_time;
    private String update_time;
    private TransactionsItemsModel[] items;

    public TransactionsModel() {
    }

    public TransactionsModel(
            String transactionId, String state, AmountModel amount,
            TransactionFeeModel transaction_fee, String create_time,
            String update_time, TransactionsItemsModel[] items) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AmountModel getAmount() {
        return amount;
    }

    public void setAmount(AmountModel amount) {
        this.amount = amount;
    }

    public TransactionFeeModel getTransaction_fee() {
        return transaction_fee;
    }

    public void setTransaction_fee(TransactionFeeModel transaction_fee) {
        this.transaction_fee = transaction_fee;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public TransactionsItemsModel[] getItems() {
        return items;
    }

    public void setItems(TransactionsItemsModel[] items) {
        this.items = items;
    }
}
