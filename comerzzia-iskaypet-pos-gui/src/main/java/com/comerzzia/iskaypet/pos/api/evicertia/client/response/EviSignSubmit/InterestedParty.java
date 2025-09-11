
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

@Generated("jsonschema2pojo")
@JsonPropertyOrder({"address"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterestedParty {

    // Variables
    @JsonProperty("address")
    private String address;
    @JsonProperty("additionalProperties")
    private Map<String, Object> additionalProperties;

    // Constructores
    public InterestedParty() {
    }

    public InterestedParty(String address, Map<String, Object> additionalProperties) {
        this.address = address;
        this.additionalProperties = additionalProperties;
    }

    // Getters and Setters (and add)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void addAdditionalProperty(String name, Object value) {
        if (this.additionalProperties == null) {
            this.additionalProperties = new LinkedHashMap<String, Object>();
        }
        this.additionalProperties.put(name, value);
    }

}
