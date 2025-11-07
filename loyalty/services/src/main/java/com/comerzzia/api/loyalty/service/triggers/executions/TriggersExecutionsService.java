package com.comerzzia.api.loyalty.service.triggers.executions;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecution;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecutionExample;
import com.comerzzia.api.loyalty.persistence.triggers.executions.dto.TriggerExecutionDTO;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface TriggersExecutionsService {
   public static final String LY_TRIGGER_EXECUTING = "LY_TRIGGER_EXECUTING";
   public static final String LY_TRIGGER_ENDED = "LY_TRIGGER_ENDED";
   public static final String LY_TRIGGER_QUERY_ERROR = "LY_TRIGGER_QUERY_ERROR";
   
   TriggerExecution insert(IDatosSesion datosSesion, TriggerExecution newRecord) throws ApiException;

   TriggerExecution selectByPrimaryKey(IDatosSesion datosSesion, String triggerExecUid);

   List<TriggerExecution> selectByExample(TriggerExecutionExample example);

   TriggerExecutionDTO selectDTOByPrimaryKey(IDatosSesion datosSesion, String triggerExecUid);

   List<TriggerExecutionDTO> selectDTOByExample(TriggerExecutionExample example);

   int delete(IDatosSesion datosSesion, String triggerExecUid);

   int deleteByTriggerUid(IDatosSesion datosSesion, String triggerUid);

   int updateByPrimaryKey(IDatosSesion datosSesion, TriggerExecution record);

   TriggerExecution executeTrigger(IDatosSesion datosSesion, String triggerUid, String newTriggerExecUid) throws ApiException;

}