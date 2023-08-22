package edu.uci.ics.huanjial.service.movies.Models.SearchById;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.huanjial.service.movies.Models.GenreModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdMovieModel {
    private String movieId;
    private String title;
    private String director;
    private Integer year;
    private String backdrop_path;
    private Integer budget;
    private String overview;
    private String poster_path;
    private Integer hidden;
    private Integer revenue;
    private Float rating; //required
    private Integer numVotes; //required
    private GenreModel[] genres;
    private StarModel[] stars;

    public IdMovieModel() {
    }

    public IdMovieModel(String movieId, String title, String director, Integer year, String backdrop_path, Integer budget, String overview, String poster_path, Integer hidden, Integer revenue, Float rating, Integer numVotes, GenreModel[] genres, StarModel[] stars) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.hidden = hidden;
        this.revenue = revenue;
        this.rating = rating;
        this.numVotes = numVotes;
        this.genres = genres;
        this.stars = stars;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
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

    public Integer getHidden() {
        return hidden;
    }

    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Integer getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }

    public GenreModel[] getGenres() {
        return genres;
    }

    public void setGenres(GenreModel[] genres) {
        this.genres = genres;
    }

    public StarModel[] getStars() {
        return stars;
    }

    public void setStars(StarModel[] stars) {
        this.stars = stars;
    }
}


