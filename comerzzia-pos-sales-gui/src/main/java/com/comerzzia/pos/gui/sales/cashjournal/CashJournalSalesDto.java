package com.comerzzia.pos.gui.sales.cashjournal;

import com.comerzzia.core.facade.model.DocTypeDetail;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalLine;
import com.comerzzia.omnichannel.facade.model.payments.PaymentMethodDetail;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CashJournalSalesDto extends CashJournalLine {
	
	private PaymentMethodDetail paymentMethod;
	private DocTypeDetail docType;
}	
