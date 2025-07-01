package com.comerzzia.pos.core.devices.device;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceConfigKey {
	private String key;
	private String label;
	
	public DeviceConfigKey(String key, String label) {
		this.key = key;
		this.label = label;
		
		if (label == null) {
			this.label = key;			
		}
	}
}
