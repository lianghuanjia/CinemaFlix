package edu.uci.ics.huanjial.service.billing.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.huanjial.service.billing.General.GeneralResponseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CCRetrieveResponse extends GeneralResponseModel {
    private CCModel creditcard;

    public CCRetrieveResponse() {
    }

    public CCRetrieveResponse(int caseInt, CCModel creditcard) {
        super(caseInt);
        this.creditcard = creditcard;
    }

    public CCModel getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(CCModel creditcard) {
        this.creditcard = creditcard;
    }
}
