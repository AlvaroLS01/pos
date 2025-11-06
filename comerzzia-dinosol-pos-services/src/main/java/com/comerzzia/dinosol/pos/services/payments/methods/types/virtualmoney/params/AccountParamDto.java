package com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.params;

import java.util.List;

public class AccountParamDto {

	private String startDate;

	private String endDate;

	private List<BalanceParamDto> initialBalances;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<BalanceParamDto> getInitialBalances() {
		return initialBalances;
	}

	public void setInitialBalances(List<BalanceParamDto> initialBalances) {
		this.initialBalances = initialBalances;
	}

}
