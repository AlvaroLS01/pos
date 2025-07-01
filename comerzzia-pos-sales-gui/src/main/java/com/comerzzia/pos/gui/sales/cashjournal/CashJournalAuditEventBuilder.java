package com.comerzzia.pos.gui.sales.cashjournal;

import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalHdr;

public class CashJournalAuditEventBuilder extends ComerzziaAuditEventBuilder {
	public CashJournalAuditEventBuilder(CashJournalHdr cashJournal) {
		if (cashJournal == null) return;
		
		insertField("cashJournal", cashJournal);		
	}
}
