package com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos;

import java.util.List;

public interface CounterRangeManager {

	public String findRangeId(CounterRangeParamDto counterRangeParam);
	
	public List<CounterRangeDto> findAllRanges() throws Exception;
	
	public CounterRangeDto saveRange(CounterRangeKey key, String value) throws Exception;
	
	public void deleteRange(CounterRangeKey key) throws Exception;
}
