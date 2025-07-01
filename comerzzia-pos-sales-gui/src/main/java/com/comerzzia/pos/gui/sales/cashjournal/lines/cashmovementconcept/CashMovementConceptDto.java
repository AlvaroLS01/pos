
package com.comerzzia.pos.gui.sales.cashjournal.lines.cashmovementconcept;

import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalConcept;
import com.comerzzia.pos.core.gui.helper.HelperRow;

import javafx.beans.property.SimpleStringProperty;

public class CashMovementConceptDto extends HelperRow<CashJournalConcept>{
	
	public CashMovementConceptDto(CashJournalConcept concept){
		helperDesc = new SimpleStringProperty();
		helperCode = new SimpleStringProperty();
		helperDesc.setValue(concept.getCashJournalConceptDes());
		helperCode.setValue(concept.getCashJournalConceptCode());
		this.object = concept;
	}

}
