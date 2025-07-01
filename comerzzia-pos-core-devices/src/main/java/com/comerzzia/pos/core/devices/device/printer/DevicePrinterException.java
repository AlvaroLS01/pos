package com.comerzzia.pos.core.devices.device.printer;

public class DevicePrinterException extends Exception {

	/**
     * 
     */
    private static final long serialVersionUID = -3930205257429014022L;

	public DevicePrinterException() {
	}

	public DevicePrinterException(String message) {
		super(message);
	}

	public DevicePrinterException(String message, Throwable cause) {
		super(message, cause);
	}

	public DevicePrinterException(Throwable cause) {
		super(cause);
	}

	public DevicePrinterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
