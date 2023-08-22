package edu.uci.ics.huanjial.service.api_gateway.models.MovieModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class MovieModelRatingRequestModel extends RequestModel {
    private String id;
    private Float rating;

    public MovieModelRatingRequestModel() {
    }

    public MovieModelRatingRequestModel(@JsonProperty(value = "id", required = true) String id,
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
