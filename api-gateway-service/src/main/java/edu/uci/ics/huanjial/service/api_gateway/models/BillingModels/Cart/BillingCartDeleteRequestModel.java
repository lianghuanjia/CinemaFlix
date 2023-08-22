package edu.uci.ics.huanjial.service.api_gateway.models.BillingModels.Cart;

import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class BillingCartDeleteRequestModel extends RequestModel {
    private String email;
    private String movieId;

    public BillingCartDeleteRequestModel() {
    }

    public BillingCartDeleteRequestModel(String email, String movieId) {
        this.email = email;
        this.movieId = movieId;
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
}
