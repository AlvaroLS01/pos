
package com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "legalName",
    "address",
    "emailAddress",
    "phoneNumber",
    "signingMethod",
    "signOrder"
})
@Generated("jsonschema2pojo")
public class SigningParty {

    @JsonProperty("name")
    private String name;
    @JsonProperty("legalName")
    private String legalName;
    @JsonProperty("address")
    private String address;
    @JsonProperty("emailAddress")
    private String emailAddress;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("signingMethod")
    private String signingMethod;
    @JsonProperty("signOrder")
    private Integer signOrder;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("legalName")
    public String getLegalName() {
        return legalName;
    }

    @JsonProperty("legalName")
    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    @JsonProperty("emailAddress")
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @JsonProperty("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("signingMethod")
    public String getSigningMethod() {
        return signingMethod;
    }

    @JsonProperty("signingMethod")
    public void setSigningMethod(String signingMethod) {
        this.signingMethod = signingMethod;
    }

    @JsonProperty("signOrder")
    public Integer getSignOrder() {
        return signOrder;
    }

    @JsonProperty("signOrder")
    public void setSignOrder(Integer signOrder) {
        this.signOrder = signOrder;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
