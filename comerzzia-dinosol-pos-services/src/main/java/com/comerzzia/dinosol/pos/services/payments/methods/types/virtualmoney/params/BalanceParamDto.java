package com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.params;

import java.math.BigDecimal;

public class BalanceParamDto {

	private String balanceTypeCode;

	private BigDecimal initialBalance;

	public BalanceParamDto(String balanceTypeCode, BigDecimal initialBalance) {
		super();
		this.balanceTypeCode = balanceTypeCode;
		this.initialBalance = initialBalance;
	}

	public String getBalanceTypeCode() {
		return balanceTypeCode;
	}

	public void setBalanceTypeCode(String balanceTypeCode) {
		this.balanceTypeCode = balanceTypeCode;
	}

	public BigDecimal getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(BigDecimal initialBalance) {
		this.initialBalance = initialBalance;
	}

}
