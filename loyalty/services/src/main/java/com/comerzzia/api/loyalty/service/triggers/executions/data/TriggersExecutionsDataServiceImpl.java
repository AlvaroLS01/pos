package com.comerzzia.api.loyalty.service.triggers.executions.data;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataExample;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataMapper;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.dto.TriggerExecutionDataDTO;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class TriggersExecutionsDataServiceImpl implements TriggersExecutionsDataService {
   @Autowired
   TriggerExecutionDataMapper mapper;
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public void insert(IDatosSesion datosSesion, String triggerExecUid, List<TriggerExecutionDataKey> records) throws ApiException {
      for (TriggerExecutionDataKey newRecord : records) {
         newRecord.setUidActividad(datosSesion.getUidActividad());
         newRecord.setTriggerExecUid(triggerExecUid);
         mapper.insert(newRecord);
      }
   }
      
   @Override
   public List<TriggerExecutionDataKey> selectByExample(TriggerExecutionDataExample example) {
      return mapper.selectByExample(example);
   }   

   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int delete(IDatosSesion datosSesion, String triggerExecUid) {
      TriggerExecutionDataExample example = new TriggerExecutionDataExample(datosSesion);
      example.or().andTriggerExecUidEqualTo(triggerExecUid);
      
      return mapper.deleteByExample(example);
   }
   
   @Override
   public List<TriggerExecutionDataKey> selectByTriggerExecUid(IDatosSesion datosSesion, String triggerExecUid) {
      TriggerExecutionDataExample example = new TriggerExecutionDataExample(datosSesion);
      
      example.or().andTriggerExecUidEqualTo(triggerExecUid);
      
      return mapper.selectByExample(example);
   }   
   
   @Override
   public List<TriggerExecutionDataDTO> selectDTOByTriggerExecUid(IDatosSesion datosSesion, String triggerExecUid) {
      TriggerExecutionDataExample example = new TriggerExecutionDataExample(datosSesion);
      
      example.or().andTriggerExecUidEqualTo(triggerExecUid);
      
      return mapper.selectDTOByExample(example);
   }   
   
   @Override
   public List<TriggerExecutionDataDTO> selectDTOByExample(TriggerExecutionDataExample example) {
      return mapper.selectDTOByExample(example);
   }  

}
