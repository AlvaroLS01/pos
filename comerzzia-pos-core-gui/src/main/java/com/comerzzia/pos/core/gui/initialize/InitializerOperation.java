package com.comerzzia.pos.core.gui.initialize;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitializerOperation {
	
	public enum OperationResult
	{
	    ERROR, WARN, OK, PENDING
	}
	
	protected String operationDesc;
	protected OperationResult operationResult;
	protected List<String> operationMsgs = new ArrayList<String>();

	public InitializerOperation(String operationDesc) {
		this.operationDesc = operationDesc;
		operationResult = OperationResult.PENDING;
	}
}
