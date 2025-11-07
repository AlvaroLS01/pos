package com.comerzzia.api.loyalty.persistence.triggers.dto;

import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecution;

public class TriggerDTO extends Trigger {
   protected TriggerData triggerDataObject;
   protected TriggerAction action;
   protected TriggerExecution lastExecution;
   protected String lastModUser;

   public TriggerData getTriggerDataObject() {
      return this.triggerDataObject;
   }

   public void setTriggerDataObject(TriggerData triggerDataObject) {
      this.triggerDataObject = triggerDataObject;
   }

   public TriggerExecution getLastExecution() {
      return lastExecution;
   }

   public void setLastExecution(TriggerExecution lastExecution) {
      this.lastExecution = lastExecution;
   }

   public TriggerAction getAction() {
      return action;
   }

   public void setAction(TriggerAction action) {
      this.action = action;
   }

   public String getLastModUser() {
	  return lastModUser;
   }
	
   public void setLastModUser(String lastModUser) {
	  this.lastModUser = lastModUser;
   }
   
}
