package com.comerzzia.api.loyalty.service.triggers.actions.types;

import org.springframework.stereotype.Service;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.core.servicios.ContextHolder;

@Service
public class TriggerActionsTypeFactoryImpl implements TriggerActionsTypeFactory {
   @Override
   public ActionTypeAbstractService getActionImplementation(TriggerAction triggerAction) throws ApiException {
       ActionTypeAbstractService service;
       try {
          service = getService(triggerAction);
          service.setTriggerAction(triggerAction);
       } catch (ClassNotFoundException e) {
          throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, 
                                 LY_TRIGGER_ACTION_CLASS_NOT_FOUND, 
                                 new String[] {triggerAction.getActionType().toString(),  e.getMessage()}); 
       }
              
       return service;
    }
    
    // method separation for custom factories inheritance
    protected ActionTypeAbstractService getService(TriggerAction triggerAction) throws ClassNotFoundException {
       switch (triggerAction.getActionType()) {
          case 1:         
            return (ActionTypeAbstractService)ContextHolder.getBean("ActionCouponsService");
          case 2:         
             return (ActionTypeAbstractService)ContextHolder.getBean("ActionPointsService");
          case 3:         
             return (ActionTypeAbstractService)ContextHolder.getBean("ActionTagsService");
          default:
             return null; 
       }       
    }
}
