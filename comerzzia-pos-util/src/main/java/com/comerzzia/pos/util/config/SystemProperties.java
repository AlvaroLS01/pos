package com.comerzzia.pos.util.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SystemProperties {
	private Map<String, String> properties = new HashMap<>();
	
}
