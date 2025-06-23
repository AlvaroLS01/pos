package com.comerzzia.bimbaylola.pos.services.epsontse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class EposOutput {

	@XmlElement(name = "result")
	private String result;

	@XmlElement(name = "logTimeStart")
	private String logTimeStart;

	@XmlElement(name = "logTimeFinish")
	private String logTimeFinish;

	@XmlElement(name = "transactionNumber")
	private String transactionNumber;

	@XmlElement(name = "serialNumber")
	private String serialNumber;

	@XmlElement(name = "signatureCounter")
	private String signatureCounter;

	@XmlElement(name = "signature")
	private String signature;

	@XmlElement(name = "qr")
	private String qr;

	public EposOutput() {
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getLogTimeStart() {
		return logTimeStart;
	}

	public void setLogTimeStart(String logTimeStart) {
		this.logTimeStart = logTimeStart;
	}

	public String getLogTimeFinish() {
		return logTimeFinish;
	}

	public void setLogTimeFinish(String logTimeFinish) {
		this.logTimeFinish = logTimeFinish;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSignatureCounter() {
		return signatureCounter;
	}

	public void setSignatureCounter(String signatureCounter) {
		this.signatureCounter = signatureCounter;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getQr() {
		return qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}

}
