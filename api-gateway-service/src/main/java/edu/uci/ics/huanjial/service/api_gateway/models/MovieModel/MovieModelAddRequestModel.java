package edu.uci.ics.huanjial.service.api_gateway.models.MovieModel;


import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class MovieModelAddRequestModel extends RequestModel {
    private String title = null;
    private String director = null;
    private Integer year = null;
    private String backdrop_path = null;
    private Integer budget = null;
    private String overview = null;
    private String poster_path = null;
    private Integer revenue = null;
    private GenreModel[] genres = null;

    public MovieModelAddRequestModel() {
    }

    public MovieModelAddRequestModel(String title, String director, Integer year, String backdrop_path, Integer budget, String overview, String poster_path, Integer revenue, GenreModel[] genres) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public GenreModel[] getGenres() {
        return genres;
    }

    public void setGenres(GenreModel[] genres) {
        this.genres = genres;
    }
}
