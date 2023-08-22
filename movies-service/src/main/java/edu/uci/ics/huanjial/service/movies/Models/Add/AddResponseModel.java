package edu.uci.ics.huanjial.service.movies.Models.Add;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"resultCode", "message", "movieid", "genreid"})
public class AddResponseModel extends GeneralResponseModel {
    private String movieid = null;
    private Integer[] genreid = null;

    public AddResponseModel() {
    }

    public AddResponseModel(Integer caseInt, String movieid, Integer[] genreid) {
        super(caseInt);
        this.movieid = movieid;
        this.genreid = genreid;
    }

    public AddResponseModel(Integer caseInt) {
        super(caseInt);
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
