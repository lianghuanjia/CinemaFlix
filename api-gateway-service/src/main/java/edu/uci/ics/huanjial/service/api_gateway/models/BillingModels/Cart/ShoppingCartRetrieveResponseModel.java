package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.huanjial.service.api_gateway.models.GeneralResponseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartRetrieveResponseModel{
    private Integer resultCode;
    private String message;
    private ItemModel[] items = null;

    public ShoppingCartRetrieveResponseModel() {
    }

    public ShoppingCartRetrieveResponseModel(Integer resultCode, String message, ItemModel[] items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
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

    public ItemModel[] getItems() {
        return items;
    }

    public void setItems(ItemModel[] items) {
        this.items = items;
    }
}
