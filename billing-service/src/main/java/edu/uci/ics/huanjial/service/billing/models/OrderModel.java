package edu.uci.ics.huanjial.service.billing.models;

import java.sql.Date;

public class OrderModel {
    private String email;
    private String movieId;
    private Integer quantity;
    private Date saleDate;

    public OrderModel() {
    }

    public OrderModel(String email, String movieId, Integer quantity, Date saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.saleDate = saleDate;
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

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
}
