package com.comerzzia.pos.core.gui.main.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.service.action.ActionServiceFacade;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.core.services.session.POSUserSession;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@CzzActionScene
public class AutomaticMainActionController extends MainActionControllerAbstract {
	
	
	@Autowired
	protected CashJournalSession cashJournalSession;
	
	@Autowired
	protected VariableServiceFacade variableService;
	
	@Autowired
	protected POSUserSession posUserSession;
	
	@Autowired
  	protected ActionServiceFacade actionsService;
	
	@Value("${com.comerzzia.actionssale:4001}")
	protected Long saleActionId;
	
	@Value("${com.comerzzia.actionscashmanagement:4002}")
	protected Long cashManagementActionId;
	
	protected ActionDetail saleAction;
	protected ActionDetail cashManagementAction;
	protected ActionDetail nextAction = null;
	
	@Override
	public void initializeComponents() {
		super.initializeComponents();
		saleAction = actionsService.findActionDetailById(saleActionId);
		cashManagementAction = actionsService.findActionDetailById(cashManagementActionId);
	}
	
	@Override
	protected void executeLongOperations() throws Exception {
		super.executeLongOperations();
		nextAction = null;
		Boolean automaticOpening = variableService.getVariableAsBoolean(VariableServiceFacade.CAJA_APERTURA_AUTOMATICA, true);
		
		if (cashJournalSession.isOpenedCashJournal() && cashJournalSession.checkCashJournalClosingMandatory()) {
			nextAction = saleAction;
		}else if(!cashJournalSession.isOpenedCashJournal() && automaticOpening) {
			nextAction = saleAction;
		}else {
			nextAction = cashManagementAction;
		}
		
		EffectiveActionPermissionsDto effectivePermissions = posUserSession.getEffectiveActionPermissions(posUserSession.getUser(), nextAction, true);
		if(!effectivePermissions.isCheckExecution()) {
			nextAction = null;
			throw new PermissionDeniedException(I18N.getText("No tiene permiso para ejecutar la acción por defecto."));
		}
	}
	
	@Override
	protected void succededLongOperations() {
		if(nextAction != null) {
			closeSuccess();
			openActionScene(nextAction.getActionId());
		}
	}
	
	@Override
	protected void failedLongOperations(Throwable e) {
		if (e instanceof PermissionDeniedException) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(e.getMessage());
			close();
		}else {
			super.failedLongOperations(e);
		}
	}
}
