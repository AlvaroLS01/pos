package com.comerzzia.pos.core.devices.device;

public interface DeviceCallback<T> {

	void onFailure(Throwable caught);
	void onSuccess(T result);
	
}
