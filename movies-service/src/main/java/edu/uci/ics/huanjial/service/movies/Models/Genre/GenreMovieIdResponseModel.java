package edu.uci.ics.huanjial.service.movies.Models.Genre;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;
import edu.uci.ics.huanjial.service.movies.Models.GenreModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"resultCode", "message", "genres"})
public class GenreMovieIdResponseModel extends GeneralResponseModel {
    private GenreModel[] genres;

    public GenreMovieIdResponseModel() {
    }

    public GenreMovieIdResponseModel(Integer caseInt, GenreModel[] genres) {
        super(caseInt);
        this.genres = genres;
    }

    public GenreMovieIdResponseModel(Integer caseInt) {
        super(caseInt);
    }

    public GenreModel[] getGenres() {
        return genres;
    }

    public void setGenres(GenreModel[] genres) {
        this.genres = genres;
    }
}
