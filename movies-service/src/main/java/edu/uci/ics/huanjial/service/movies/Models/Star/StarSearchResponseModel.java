package edu.uci.ics.huanjial.service.movies.Models.Star;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;

@JsonPropertyOrder({"resultCode", "message", "stars"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarSearchResponseModel extends GeneralResponseModel {
    private StarModel[] stars;

    public StarSearchResponseModel(Integer caseInt) {
        super(caseInt);
    }

    public StarSearchResponseModel() {
    }

    public StarSearchResponseModel(Integer caseInt, StarModel[] stars) {
        super(caseInt);
        this.stars = stars;
    }

    public StarModel[] getStars() {
        return stars;
    }

    public void setStars(StarModel[] stars) {
        this.stars = stars;
    }
}
