package edu.uci.ics.huanjial.service.movies.Models.Search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"resultCode", "message", "movies"})
public class SearchResponseModel extends GeneralResponseModel {
    @JsonProperty(value = "movies", required = true)
    private MovieModel[] movies = null;


    public SearchResponseModel() {
    }

    public SearchResponseModel(Integer caseInt, MovieModel[] movies) {
        super(caseInt);
        this.movies = movies;
    }

    public SearchResponseModel(Integer caseInt) {
        super(caseInt);
    }

    public MovieModel[] getMovies() {
        return movies;
    }

    public void setMovies(MovieModel[] movies) {
        this.movies = movies;
    }
}
