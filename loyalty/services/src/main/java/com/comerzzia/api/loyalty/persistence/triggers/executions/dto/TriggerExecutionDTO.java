package com.comerzzia.api.loyalty.persistence.triggers.executions.dto;

import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecution;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecution;

public class TriggerExecutionDTO extends TriggerExecution {
   protected TriggerActionExecution lastActionExecution;

   public TriggerActionExecution getLastActionExecution() {
      return lastActionExecution;
   }

   public void setLastActionExecution(TriggerActionExecution lastActionExecution) {
      this.lastActionExecution = lastActionExecution;
   }
}
