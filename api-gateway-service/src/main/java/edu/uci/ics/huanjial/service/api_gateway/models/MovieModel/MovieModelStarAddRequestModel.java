package edu.uci.ics.huanjial.service.api_gateway.models.MovieModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.huanjial.service.api_gateway.models.RequestModel;

public class MovieModelStarAddRequestModel extends RequestModel {
    private String name;
    private Integer birthYear;

    public MovieModelStarAddRequestModel() {
    }

    //@JsonProperty(value = "birthYear", required = false)
    public MovieModelStarAddRequestModel(@JsonProperty(value = "name", required = true) String name,
                                         @JsonProperty(value = "birthYear", required = false) Integer birthYear)
                                //Why do I need to write Json for birthYear as well? If not then it's going to give me a mapping error.
    {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }
}
