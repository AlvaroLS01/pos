
package com.comerzzia.iskaypet.pos.api.evicertia.client.response.EviSignSubmit;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pushNotificationUrl",
    "pushNotificationFilter",
    "pushNotificationExtraData",
    "costCentre",
    "timeToLive",
    "interestedPartiesNotifications"
})
@Generated("jsonschema2pojo")
public class Options {

    @JsonProperty("pushNotificationUrl")
    private String pushNotificationUrl;
    @JsonProperty("pushNotificationFilter")
    private List<String> pushNotificationFilter;
    @JsonProperty("pushNotificationExtraData")
    private String pushNotificationExtraData;
    @JsonProperty("costCentre")
    private String costCentre;
    @JsonProperty("timeToLive")
    private Integer timeToLive;
    @JsonProperty("Language")
    private String language;
    @JsonProperty("AffidavitLanguage")
    private String affidavitLanguage;
    @JsonProperty("interestedPartiesNotifications")
    private List<String> interestedPartiesNotifications;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("pushNotificationUrl")
    public String getPushNotificationUrl() {
        return pushNotificationUrl;
    }

    @JsonProperty("pushNotificationUrl")
    public void setPushNotificationUrl(String pushNotificationUrl) {
        this.pushNotificationUrl = pushNotificationUrl;
    }

    @JsonProperty("pushNotificationFilter")
    public List<String> getPushNotificationFilter() {
        return pushNotificationFilter;
    }

    @JsonProperty("pushNotificationFilter")
    public void setPushNotificationFilter(List<String> pushNotificationFilter) {
        this.pushNotificationFilter = pushNotificationFilter;
    }

    @JsonProperty("pushNotificationExtraData")
    public String getPushNotificationExtraData() {
        return pushNotificationExtraData;
    }

    @JsonProperty("pushNotificationExtraData")
    public void setPushNotificationExtraData(String pushNotificationExtraData) {
        this.pushNotificationExtraData = pushNotificationExtraData;
    }

    @JsonProperty("costCentre")
    public String getCostCentre() {
        return costCentre;
    }

    @JsonProperty("costCentre")
    public void setCostCentre(String costCentre) {
        this.costCentre = costCentre;
    }

    @JsonProperty("timeToLive")
    public Integer getTimeToLive() {
        return timeToLive;
    }

    @JsonProperty("timeToLive")
    public void setTimeToLive(Integer timeToLive) {
        this.timeToLive = timeToLive;
    }

    @JsonProperty("Language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("Language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("AffidavitLanguage")
    public String getAffidavitLanguage() {
        return affidavitLanguage;
    }

    @JsonProperty("AffidavitLanguage")
    public void setAffidavitLanguage(String affidavitLanguage) {
        this.affidavitLanguage = affidavitLanguage;
    }

    @JsonProperty("interestedPartiesNotifications")
    public List<String> getInterestedPartiesNotifications() {
        return interestedPartiesNotifications;
    }

    @JsonProperty("interestedPartiesNotifications")
    public void setInterestedPartiesNotifications(List<String> interestedPartiesNotifications) {
        this.interestedPartiesNotifications = interestedPartiesNotifications;
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
