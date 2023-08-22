package edu.uci.ics.huanjial.service.billing.models;

public class TransactionsItemsModel {
    private String email;
    private String movieId;
    private Integer quantity;
    private Float unit_price;
    private Float discount;
    private String saleDate;

    public TransactionsItemsModel() {
    }

    public TransactionsItemsModel(String email, String movieId, Integer quantity, Float unit_price, Float discount, String saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.discount = discount;
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

    public Float getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Float unit_price) {
        this.unit_price = unit_price;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }
}
