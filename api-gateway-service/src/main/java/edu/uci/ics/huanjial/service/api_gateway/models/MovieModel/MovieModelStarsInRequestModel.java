package edu.uci.ics.huanjial.service.api_gateway.models.MovieModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class MovieModelStarsInRequestModel extends RequestModel {
    private String starid;
    private String movieid;

    public MovieModelStarsInRequestModel() {
    }

    public MovieModelStarsInRequestModel(@JsonProperty(value = "starid", required = true) String starid,
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
