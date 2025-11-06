package com.comerzzia.dinosol.pos.persistence.enviosdomicilio;

/**
 * Bean para contener el resultado erroneo de un servicio.
 */
public class RutasErroresBean {

	private String type;
	private String title;
	private String detail;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
