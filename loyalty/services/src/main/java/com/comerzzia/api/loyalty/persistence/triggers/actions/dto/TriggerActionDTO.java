package com.comerzzia.api.loyalty.persistence.triggers.actions.dto;

import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecution;

public class TriggerActionDTO extends TriggerAction {
   protected TriggerActionExecution lastActionExecution;

   public TriggerActionExecution getLastActionExecution() {
      return lastActionExecution;
   }

   public void setLastActionExecution(TriggerActionExecution lastActionExecution) {
      this.lastActionExecution = lastActionExecution;
   }
}
