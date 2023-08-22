package edu.uci.ics.huanjial.service.api_gateway.models.MovieModel;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieModelAddResponseModel {
    private Integer resultCode;
    private String message;
    private String movieid = null;
    private Integer[] genreid = null;

    public MovieModelAddResponseModel() {
    }

    public MovieModelAddResponseModel(Integer resultCode, String message, String movieid, Integer[] genreid) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieid = movieid;
        this.genreid = genreid;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMovieid() {
        return movieid;
    }

    public void setMovieid(String movieid) {
        this.movieid = movieid;
    }

    public Integer[] getGenreid() {
        return genreid;
    }

    public void setGenreid(Integer[] genreid) {
        this.genreid = genreid;
    }
}
