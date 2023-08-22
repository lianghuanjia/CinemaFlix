package edu.uci.ics.huanjial.service.api_gateway.models.MovieModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class MovieModelGenreAddRequestModel extends RequestModel {
    private String name;

    public MovieModelGenreAddRequestModel() {
    }

    public MovieModelGenreAddRequestModel(@JsonProperty(value = "name", required = true) String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
