package com.comerzzia.bimbaylola.pos.services.epsontse;

public class EposInput {

	private String clientId;
	private String processData;
	private String processType;
	private String additionalData;
	private Integer transactionNumber;

	private String puk;
	private String adminPin;
	private String timeAdminPin;

	private String userId;

	private String pin;
	private String hash;

	private String newDateTime;
	private Boolean useTimeSync;

	private Boolean deleteData;

	public EposInput() {
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getProcessData() {
		return processData;
	}

	public void setProcessData(String processData) {
		this.processData = processData;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public Integer getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(Integer transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getPuk() {
		return puk;
	}

	public void setPuk(String puk) {
		this.puk = puk;
	}

	public String getAdminPin() {
		return adminPin;
	}

	public void setAdminPin(String adminPin) {
		this.adminPin = adminPin;
	}

	public String getTimeAdminPin() {
		return timeAdminPin;
	}

	public void setTimeAdminPin(String timeAdminPin) {
		this.timeAdminPin = timeAdminPin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getNewDateTime() {
		return newDateTime;
	}

	public void setNewDateTime(String newDateTime) {
		this.newDateTime = newDateTime;
	}

	public Boolean getUseTimeSync() {
		return useTimeSync;
	}

	public void setUseTimeSync(Boolean useTimeSync) {
		this.useTimeSync = useTimeSync;
	}

	public Boolean getDeleteData() {
		return deleteData;
	}

	public void setDeleteData(Boolean deleteData) {
		this.deleteData = deleteData;
	}

}
