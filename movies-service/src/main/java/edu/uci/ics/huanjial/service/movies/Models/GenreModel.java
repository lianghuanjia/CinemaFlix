package edu.uci.ics.huanjial.service.movies.Models;

public class GenreModel {
    private Integer id = 0;
    private String name = null;

    public GenreModel() {
    }

    public GenreModel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
