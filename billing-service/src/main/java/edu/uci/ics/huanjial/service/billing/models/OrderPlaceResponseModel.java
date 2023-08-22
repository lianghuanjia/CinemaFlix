package edu.uci.ics.huanjial.service.billing.models;

import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;

public class OrderPlaceResponseModel extends GeneralResponseModel {
    private String redirectURL;
    private String token;

    public OrderPlaceResponseModel(int caseInt, String redirectURL, String token) {
        super(caseInt);
        this.redirectURL = redirectURL;
        this.token = token;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
