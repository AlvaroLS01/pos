package com.comerzzia.pos.core.devices.device.scanner;

public class DeviceScannerDataReaded {
	private byte[] scanRawData;
	private String scanData;
	private int scanDataTypeId;
	private String scanDataType;
	
	public byte[] getScanRawData() {
		return scanRawData;
	}
	public void setScanRawData(byte[] scanRawData) {
		this.scanRawData = scanRawData;
	}
	public String getScanData() {
		return scanData;
	}
	public void setScanData(String scanData) {
		this.scanData = scanData;
	}
	public int getScanDataTypeId() {
		return scanDataTypeId;
	}
	public void setScanDataTypeId(int scanDataTypeId) {
		this.scanDataTypeId = scanDataTypeId;
	}
	public String getScanDataType() {
		return scanDataType;
	}
	public void setScanDataType(String scanDataType) {
		this.scanDataType = scanDataType;
	}

	
}
