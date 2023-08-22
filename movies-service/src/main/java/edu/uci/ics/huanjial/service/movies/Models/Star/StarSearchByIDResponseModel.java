package edu.uci.ics.huanjial.service.movies.Models.Star;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.huanjial.service.movies.General.GeneralResponseModel;

@JsonPropertyOrder({"resultCode", "message", "StarModel"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarSearchByIDResponseModel extends GeneralResponseModel {
    private StarModel stars;

    public StarSearchByIDResponseModel() {
    }

    public StarSearchByIDResponseModel(Integer caseInt, StarModel stars) {
        super(caseInt);
        this.stars = stars;
    }

    public StarModel getStars() {
        return stars;
    }

    public void setStars(StarModel stars) {
        this.stars = stars;
    }
}
