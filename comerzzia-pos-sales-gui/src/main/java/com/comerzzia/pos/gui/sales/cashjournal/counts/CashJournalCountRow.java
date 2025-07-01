package com.comerzzia.pos.gui.sales.cashjournal.counts;

import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalCount;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethod;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CashJournalCountRow extends CashJournalCount {
	private PaymentMethod paymentMethod;

}
