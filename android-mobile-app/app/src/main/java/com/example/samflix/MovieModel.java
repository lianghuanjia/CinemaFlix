package com.example.samflix;

import com.android.volley.toolbox.StringRequest;

public class MovieModel {
    private String movieID;
    private String title;
    private String rating;
    private String numVotes;

    public MovieModel() {
    }

    public MovieModel(String movieID, String title, String rating, String numVotes) {
        this.movieID = movieID;
        this.title = title;
        this.rating = rating;
        this.numVotes = numVotes;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(String numVotes) {
        this.numVotes = numVotes;
    }
}
