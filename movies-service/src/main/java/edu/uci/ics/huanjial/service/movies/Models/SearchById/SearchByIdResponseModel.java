package edu.uci.ics.huanjial.service.movies.Models.SearchById;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"resultCode", "message", "idMovieModel"})
public class SearchByIdResponseModel extends GeneralResponseModel {
    private IdMovieModel movie;

    public SearchByIdResponseModel() {
    }

    public SearchByIdResponseModel(Integer caseInt) {
        super(caseInt);
    }

    public SearchByIdResponseModel(Integer caseInt, IdMovieModel movie) {
        super(caseInt);
        this.movie = movie;
    }

    public IdMovieModel getMovie() {
        return movie;
    }

    public void setMovie(IdMovieModel movie) {
        this.movie = movie;
    }
}
