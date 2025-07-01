package com.comerzzia.pos.gui.sales.cashjournal.lines.cashmovementconcept;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalConcept;
import com.comerzzia.omnichannel.facade.service.cashjournal.concept.CashJournalConceptServiceFacade;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.helper.HelperSceneController;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
@CzzScene
public class CashMovementConceptController extends HelperSceneController<CashMovementConceptDto>{

	@Autowired
	protected CashJournalConceptServiceFacade conceptServices;
	
	public void setTitle(){
		lbTitle.setText(I18N.getText("Conceptos"));
	}

	@Override
	public List<CashMovementConceptDto> buildHelpersRows(Map<String, Object> params) {
		List<CashMovementConceptDto> concepts = new ArrayList<>();
		List<CashJournalConcept> conceptosBean = conceptServices.findAll(new CashJournalConcept());
		
		for(CashJournalConcept concepto: conceptosBean){
			if (concepto.getManual()) {
				concepts.add(new CashMovementConceptDto(concepto));
			}
		}
		return concepts;
	}
}
