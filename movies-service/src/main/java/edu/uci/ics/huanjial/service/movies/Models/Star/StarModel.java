package edu.uci.ics.huanjial.service.movies.Models.Star;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarModel {
    private String id;  // ID (string, required)
    private String name;  // NAME (string, required)
    private String birthYear; // BIRTHYEAR (string, optional)

    public StarModel() {
    }

    public StarModel(@JsonProperty(value = "id", required = true) String id,
                     @JsonProperty(value = "name", required = true) String name,
                     String birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }
}
