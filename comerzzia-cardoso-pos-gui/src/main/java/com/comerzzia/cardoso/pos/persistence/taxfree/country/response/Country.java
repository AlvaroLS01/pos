
package com.comerzzia.cardoso.pos.persistence.taxfree.country.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Country {

    @SerializedName("CountryId")
    @Expose
    private String countryId;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
