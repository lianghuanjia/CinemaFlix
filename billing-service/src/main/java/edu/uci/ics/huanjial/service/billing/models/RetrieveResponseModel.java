package edu.uci.ics.huanjial.service.billing.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RetrieveResponseModel extends GeneralResponseModel {
    ItemModel[] items = null;

    public RetrieveResponseModel(int caseInt, ItemModel[] items) {
        super(caseInt);
        this.items = items;
    }

    public ItemModel[] getItems() {
        return items;
    }

    public void setItems(ItemModel[] items) {
        this.items = items;
    }
}
