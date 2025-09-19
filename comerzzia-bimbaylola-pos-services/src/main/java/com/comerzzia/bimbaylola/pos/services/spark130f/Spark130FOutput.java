package com.comerzzia.bimbaylola.pos.services.spark130f;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Spark130FOutput {

	@XmlElement(name = "check_number_current_shift")
	private String check;

	@XmlElement(name = "fiscal_document_number")
	private String fd;

	@XmlElement(name = "fiscal_document_attribute")
	private String fp;

	@XmlElement(name = "date")
	private String date;

	@XmlElement(name = "time")
	private String time;

	@XmlElement(name = "place")
	private String kassa;

	@XmlElement(name = "receipt_total")
	private String sumCk;

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getFd() {
		return fd;
	}

	public void setFd(String fd) {
		this.fd = fd;
	}

	public String getFp() {
		return fp;
	}

	public void setFp(String fp) {
		this.fp = fp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getKassa() {
		return kassa;
	}

	public void setKassa(String kassa) {
		this.kassa = kassa;
	}

	public String getSumCk() {
		return sumCk;
	}

	public void setSumCk(String sumCk) {
		this.sumCk = sumCk;
	}

}
