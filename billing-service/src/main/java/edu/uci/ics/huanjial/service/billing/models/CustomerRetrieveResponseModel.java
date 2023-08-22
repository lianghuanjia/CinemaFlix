package edu.uci.ics.huanjial.service.billing.models;

import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;

public class CustomerRetrieveResponseModel extends GeneralResponseModel {
    private CustomerModel customer;

    public CustomerRetrieveResponseModel() {
    }

    public CustomerRetrieveResponseModel(int caseInt, CustomerModel customer) {
        super(caseInt);
        this.customer = customer;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }
}
