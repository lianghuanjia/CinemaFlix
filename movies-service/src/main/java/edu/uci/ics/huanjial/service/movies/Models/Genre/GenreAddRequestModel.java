package edu.uci.ics.huanjial.service.movies.Models.Genre;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenreAddRequestModel {
    private String name;

    public GenreAddRequestModel() {
    }

    public GenreAddRequestModel(@JsonProperty(value = "name", required = true) String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
