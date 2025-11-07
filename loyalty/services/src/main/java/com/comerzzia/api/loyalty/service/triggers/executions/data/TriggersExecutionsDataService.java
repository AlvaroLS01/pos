package com.comerzzia.api.loyalty.service.triggers.executions.data;

import java.util.List;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataExample;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.dto.TriggerExecutionDataDTO;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

public interface TriggersExecutionsDataService {

   void insert(IDatosSesion datosSesion, String triggerExecUid, List<TriggerExecutionDataKey> records) throws ApiException;

   List<TriggerExecutionDataKey> selectByExample(TriggerExecutionDataExample example);

   int delete(IDatosSesion datosSesion, String triggerExecUid);

   List<TriggerExecutionDataKey> selectByTriggerExecUid(IDatosSesion datosSesion, String triggerExecUid);
   
   List<TriggerExecutionDataDTO> selectDTOByTriggerExecUid(IDatosSesion datosSesion, String triggerExecUid);
   
   List<TriggerExecutionDataDTO> selectDTOByExample(TriggerExecutionDataExample example);
}