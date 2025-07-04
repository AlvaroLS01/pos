
package com.comerzzia.cardoso.pos.persistence.taxfree.mode.response;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Details {

    @SerializedName("GetTaxFreeModeResponse")
    @Expose
    private List<GetTaxFreeModeResponse> getTaxFreeModeResponse;

    public List<GetTaxFreeModeResponse> getGetTaxFreeModeResponse() {
        return getTaxFreeModeResponse;
    }

    public void setGetTaxFreeModeResponse(List<GetTaxFreeModeResponse> getTaxFreeModeResponse) {
        this.getTaxFreeModeResponse = getTaxFreeModeResponse;
    }

}
