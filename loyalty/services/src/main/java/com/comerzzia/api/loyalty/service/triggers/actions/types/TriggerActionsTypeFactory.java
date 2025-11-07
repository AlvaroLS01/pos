package com.comerzzia.api.loyalty.service.triggers.actions.types;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;

public interface TriggerActionsTypeFactory {
   public static final String LY_TRIGGER_ACTION_CLASS_NOT_FOUND = "LY_TRIGGER_ACTION_CLASS_NOT_FOUND";

   ActionTypeAbstractService getActionImplementation(TriggerAction triggerAction) throws ApiException;

}