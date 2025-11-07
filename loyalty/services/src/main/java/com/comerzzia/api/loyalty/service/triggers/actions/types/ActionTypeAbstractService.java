package com.comerzzia.api.loyalty.service.triggers.actions.types;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.service.triggers.TriggersService;
import com.comerzzia.api.loyalty.service.triggers.executions.data.TriggersExecutionsDataService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;


public abstract class ActionTypeAbstractService {
   public static final String LY_TRIGGER_ACTIION_TYPE_NULL_DATA = "LY_TRIGGER_ACTIION_TYPE_NULL_DATA";
   
   @Autowired
   TriggersService triggerService;
   
   @Autowired
   TriggersExecutionsDataService triggersExecutionsDataService;
   
   protected TriggerAction triggerAction;
   
   public ActionTypeAbstractService() {
   }
      
   
   public TriggerAction getTriggerAction() {
      return triggerAction;
   }


   public void setTriggerAction(TriggerAction triggerAction) {
      this.triggerAction = triggerAction;
   }



   public <T> T createDataObject(Class<T> objectType) {
      if (triggerAction.getActionData() == null) {
         return null;
      }
      
      ObjectMapper objectMapper = new ObjectMapper();
      // jaxb annotations support
      JaxbAnnotationModule module = new JaxbAnnotationModule();
      objectMapper.registerModule(module);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      
      try {
         return (T) objectMapper.readValue(new String(triggerAction.getActionData()), objectType);
      } catch (JsonParseException | JsonMappingException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   
   public abstract void executeAction(IDatosSesion datosSesion) throws ApiException;
   
   public abstract Object getDataObject();
   
   protected List<TriggerExecutionDataKey> getTriggerExecutionData(IDatosSesion datosSesion) {
      // search trigger of the action
      Trigger trigger = triggerService.selectByPrimaryKey(datosSesion, triggerAction.getTriggerUid());      
                 
      // query customers from trigger execution data
      return triggersExecutionsDataService.selectByTriggerExecUid(datosSesion, trigger.getLastTriggerExecUid());
   }
}
