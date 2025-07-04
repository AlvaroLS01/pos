
package com.comerzzia.cardoso.pos.persistence.devoluciones.referenciadas.refund.response;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Token {

    @SerializedName("tokenType")
    @Expose
    private String tokenType;
    @SerializedName("token")
    @Expose
    private String token;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
