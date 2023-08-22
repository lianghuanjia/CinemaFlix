package edu.uci.ics.huanjial.service.movies.Models.Rating;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingRequestModel {
    private String id;
    private Float rating;

    public RatingRequestModel() {
    }

    public RatingRequestModel(@JsonProperty(value = "id", required = true) String id,
                              @JsonProperty(value = "rating", required = true) Float rating) {
        this.id = id;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
