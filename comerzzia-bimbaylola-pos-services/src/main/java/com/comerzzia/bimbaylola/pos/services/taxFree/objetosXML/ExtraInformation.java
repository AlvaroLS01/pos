package com.comerzzia.bimbaylola.pos.services.taxFree.objetosXML;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "extra_information")
@Component
@Primary
@Scope("prototype")
public class ExtraInformation {

	@XmlElement(name = "article_code")
	protected String article_code;

	@XmlElement(name = "family")
	protected Family family;

	@XmlElement(name = "brand")
	protected Brand brand;

	@XmlElement(name = "vat_code")
	protected String vat_code;

	@XmlElement(name = "sales_group")
	protected String sales_group;

	@XmlElement(name = "fashion")
	protected String fashion;

	@XmlElement(name = "fashion_description")
	protected String fashion_description;

	@XmlElement(name = "merchandise_category")
	protected String merchandise_category;

	@XmlElement(name = "merchandise_category_description")
	protected String merchandise_category_description;

	public String getArticle_code() {
		return article_code;
	}

	public void setArticle_code(String article_code) {
		this.article_code = article_code;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public String getVat_code() {
		return vat_code;
	}

	public void setVat_code(String vat_code) {
		this.vat_code = vat_code;
	}

	public String getSales_group() {
		return sales_group;
	}

	public void setSales_group(String sales_group) {
		this.sales_group = sales_group;
	}

	public String getFashion() {
		return fashion;
	}

	public void setFashion(String fashion) {
		this.fashion = fashion;
	}

	public String getFashion_description() {
		return fashion_description;
	}

	public void setFashion_description(String fashion_description) {
		this.fashion_description = fashion_description;
	}

	public String getMerchandise_category() {
		return merchandise_category;
	}

	public void setMerchandise_category(String merchandise_category) {
		this.merchandise_category = merchandise_category;
	}

	public String getMerchandise_category_description() {
		return merchandise_category_description;
	}

	public void setMerchandise_category_description(String merchandise_category_description) {
		this.merchandise_category_description = merchandise_category_description;
	}

}
