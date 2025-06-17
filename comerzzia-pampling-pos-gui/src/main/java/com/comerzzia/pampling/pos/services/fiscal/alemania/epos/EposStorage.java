package com.comerzzia.pampling.pos.services.fiscal.alemania.epos;
public class EposStorage {

	private String type;
	private String vendor;

	public EposStorage() {
	}

	public EposStorage(String type, String vendor) {
		super();
		this.type = type;
		this.vendor = vendor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

}
