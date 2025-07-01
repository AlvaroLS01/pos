package com.comerzzia.pos.core.devices.device.scale;

import java.math.BigDecimal;
import java.util.Map;

public interface DeviceScaleCallback {
	void onSuccess(BigDecimal weight, BigDecimal price, Map<String,Object> customData);
	
	void onStatusUpdate(int status, Map<String,Object> statusData);
	
	default void onCancel() {/* Do nothing */};
	default void onFailure() {/* Do nothing */};
	
}
