package edu.uci.ics.huanjial.service.movies.Models.Star;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StarsInRequestModel {
    private String starid;
    private String movieid;

    public StarsInRequestModel() {
    }

    public StarsInRequestModel(@JsonProperty(value = "starid", required = true) String starid,
                               @JsonProperty(value = "movieid", required = true) String movieid) {
        this.starid = starid;
        this.movieid = movieid;
    }

    public String getStarid() {
        return starid;
    }

    public void setStarid(String starid) {
        this.starid = starid;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }
}
