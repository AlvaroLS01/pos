package com.comerzzia.pos.devices.scanner.emulation;

import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.devices.scanner.DeviceScannerAbstractImpl;

public class DeviceScannerKeyboardEmulation extends DeviceScannerAbstractImpl {
	
	public static String DEFAULT_SCAN_TYPE_CODE = "Unknown";

	@Override
	public void enableReader() {
	}

	@Override
	public void disableReader() {
	}

	@Override
	protected void executeCommand(byte[] command) {
	}

	@Override
	public void connect() throws DeviceException {
	}

	@Override
	public void disconnect() throws DeviceException {
	}

}
