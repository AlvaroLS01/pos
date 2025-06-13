package com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos;

import java.util.Map;

public class CounterRangeParamDto extends CounterRangeKey{

	protected Map<String, Object> extensions;
	
	public Map<String, Object> getExtensions() {
		return extensions;
	}
	
	public void setExtensions(Map<String, Object> extensions) {
		this.extensions = extensions;
	}
	
}
