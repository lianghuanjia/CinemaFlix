package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart;

import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class BillingCartInsertUpdateRequestModel extends RequestModel {
    private String email;
    private String movieId;
    private Integer quantity;

    public BillingCartInsertUpdateRequestModel(String email, String movieId, Integer quantity) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
    }

    public BillingCartInsertUpdateRequestModel() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
