package com.comerzzia.api.loyalty.service.triggers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.TriggerExample;
import com.comerzzia.api.loyalty.persistence.triggers.TriggerKey;
import com.comerzzia.api.loyalty.persistence.triggers.TriggerMapper;
import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerDTO;
import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerData;
import com.comerzzia.api.loyalty.service.triggers.actions.TriggersActionsService;
import com.comerzzia.api.loyalty.service.triggers.data.TriggerDataService;
import com.comerzzia.api.loyalty.service.triggers.executions.TriggersExecutionsService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class TriggersServiceImpl implements TriggersService {
   @Autowired
   TriggerMapper mapper;
   
   @Autowired
   TriggersExecutionsService triggersExecutionsService;
   
   @Autowired
   TriggersActionsService triggersActionsService;
   
   @Autowired
   TriggerDataService triggerDataService;
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public Trigger insert(IDatosSesion datosSesion, Trigger newRecord) throws ApiException {
      newRecord.setUidActividad(datosSesion.getUidActividad());
      newRecord.setTriggerUid(UUID.randomUUID().toString());
      mapper.insert(newRecord);
      
      return newRecord;
   }
   
   @Override
   public Trigger selectByPrimaryKey(IDatosSesion datosSesion, String triggerUid) {
      return mapper.selectByPrimaryKey(new TriggerKey(datosSesion.getUidActividad(), triggerUid));
   }
   
   @Override
   public List<Trigger> selectByExample(TriggerExample example) {
      return mapper.selectByExample(example);
   }   

   @Override
   public TriggerDTO selectDTOByPrimaryKey(IDatosSesion datosSesion, String triggerUid) {
      TriggerDTO triggerDTO = mapper.selectDTOByPrimaryKey(new TriggerKey(datosSesion.getUidActividad(), triggerUid));
      
      triggerDTO.setTriggerDataObject(triggerDataService.createDataObject(triggerDTO.getTriggerData(), TriggerData.class));
      
      return triggerDTO;
   }
   
   @Override
   public List<TriggerDTO> selectDTOByExample(TriggerExample example) {
      return mapper.selectDTOByExample(example);
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int delete(IDatosSesion datosSesion, String triggerUid) {
      // delete associated actions
      triggersActionsService.deleteByTriggerUid(datosSesion, triggerUid);        
      
      // delete executions
      triggersExecutionsService.deleteByTriggerUid(datosSesion, triggerUid);
      
      return mapper.deleteByPrimaryKey(new TriggerKey(datosSesion.getUidActividad(), triggerUid));
   }
      
   @Override
   public int updateByPrimaryKey(IDatosSesion datosSesion, Trigger record) {
      record.setUidActividad(datosSesion.getUidActividad());
      return mapper.updateByPrimaryKeyWithBLOBs(record); 
   }
}
