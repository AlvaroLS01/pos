
package com.comerzzia.cardoso.pos.persistence.taxfree.create.request;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Tourist {

    @SerializedName("Passport")
    @Expose
    private String passport;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("BirthDate")
    @Expose
    private String birthDate;
    @SerializedName("Country")
    @Expose
    private String country;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("RefundMode")
    @Expose
    private Integer refundMode;
    @SerializedName("PaymMode")
    @Expose
    private Integer paymMode;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("Gender")
    @Expose
    private Integer gender;
    @SerializedName("Card")
    @Expose
    private String card;
    @SerializedName("CardToken")
    @Expose
    private String cardToken;
    @SerializedName("ZipCode")
    @Expose
    private String zipCode;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("TouristId")
    @Expose
    private String touristId;
    @SerializedName("Nationality")
    @Expose
    private String nationality;

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRefundMode() {
        return refundMode;
    }

    public void setRefundMode(Integer refundMode) {
        this.refundMode = refundMode;
    }

    public Integer getPaymMode() {
        return paymMode;
    }

    public void setPaymMode(Integer paymMode) {
        this.paymMode = paymMode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTouristId() {
        return touristId;
    }

    public void setTouristId(String touristId) {
        this.touristId = touristId;
    }

	
	public String getNationality() {
		return nationality;
	}

	
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

}
