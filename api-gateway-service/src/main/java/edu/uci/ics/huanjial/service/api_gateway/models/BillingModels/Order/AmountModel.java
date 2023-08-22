package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Order;

public class AmountModel {
    private String total;
    private String currency;

    public AmountModel() {
    }

    public AmountModel(String total, String currency) {
        this.total = total;
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
