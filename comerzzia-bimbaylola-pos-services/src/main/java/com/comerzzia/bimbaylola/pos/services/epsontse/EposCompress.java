package com.comerzzia.bimbaylola.pos.services.epsontse;

public class EposCompress {

	private Boolean required;
	private String type;

	public EposCompress() {
	}

	public EposCompress(Boolean required, String type) {
		super();
		this.required = required;
		this.type = type;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
