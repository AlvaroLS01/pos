package com.comerzzia.api.loyalty.service.triggers.actions.executions;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecution;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecutionExample;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface TriggersActionsExecutionsService {
   public static final String LY_TRIGGER_ACTION_EXECUTING = "LY_TRIGGER_ACTION_EXECUTING";
   public static final String LY_TRIGGER_ACTION_ENDED = "LY_TRIGGER_ACTION_ENDED";

   TriggerActionExecution insert(IDatosSesion datosSesion, TriggerActionExecution newRecord) throws ApiException;

   TriggerActionExecution selectByPrimaryKey(IDatosSesion datosSesion, String accExecutionUid);

   List<TriggerActionExecution> selectByExample(TriggerActionExecutionExample example);

   int delete(IDatosSesion datosSesion, String accExecutionUid);

   int deleteByActionUid(IDatosSesion datosSesion, String actionUid);

   int updateByPrimaryKey(IDatosSesion datosSesion, TriggerActionExecution record);

   void executeTriggerAction(IDatosSesion datosSesion, String triggerUid, String actionUid, String triggerExecUid, String newAccExecutionUid) throws ApiException;

}