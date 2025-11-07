package com.comerzzia.api.loyalty.service.triggers.actions;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerActionExample;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerActionKey;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerActionMapper;
import com.comerzzia.api.loyalty.persistence.triggers.actions.dto.TriggerActionDTO;
import com.comerzzia.api.loyalty.service.triggers.actions.executions.TriggersActionsExecutionsService;
import com.comerzzia.api.loyalty.service.triggers.actions.types.TriggerActionsTypeFactory;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class TriggersActionsServiceImpl implements TriggersActionsService {
   @Autowired
   TriggerActionMapper mapper;
   
   @Autowired
   TriggersActionsExecutionsService triggersActionsExecutionsService;
   
   @Autowired
   TriggerActionsTypeFactory triggerActionsTypeFactory;
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public TriggerAction insert(IDatosSesion datosSesion, TriggerAction newRecord) throws ApiException {
      newRecord.setUidActividad(datosSesion.getUidActividad());
      newRecord.setActionUid(UUID.randomUUID().toString());
      mapper.insert(newRecord);
      
      return newRecord;
   }
   
   @Override
   public TriggerAction selectByPrimaryKey(IDatosSesion datosSesion, String actionUid) {
      return mapper.selectByPrimaryKey(new TriggerActionKey(datosSesion.getUidActividad(), actionUid));
   }
   
   @Override
   public List<TriggerAction> selectByExample(TriggerActionExample example) {
      return mapper.selectByExample(example);
   }   

   @Override
   public TriggerActionDTO selectDTOByPrimaryKey(IDatosSesion datosSesion, String actionUid) {
      return mapper.selectDTOByPrimaryKey(new TriggerActionKey(datosSesion.getUidActividad(), actionUid));
   }
   
   @Override
   public List<TriggerActionDTO> selectDTOByExample(TriggerActionExample example) {
      return mapper.selectDTOByExample(example);
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int delete(IDatosSesion datosSesion, String actionUid) {      
      // delete executions
      triggersActionsExecutionsService.deleteByActionUid(datosSesion, actionUid);
      
      return mapper.deleteByPrimaryKey(new TriggerActionKey(datosSesion.getUidActividad(), actionUid));
   }
   
   @Override
   public int updateByPrimaryKey(IDatosSesion datosSesion, TriggerAction record) {
      record.setUidActividad(datosSesion.getUidActividad());
      return mapper.updateByPrimaryKeyWithBLOBs(record); 
   }  
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int deleteByTriggerUid(IDatosSesion datosSesion, String triggerUid) {
      TriggerActionExample example = new TriggerActionExample(datosSesion);
      example.or().andTriggerUidEqualTo(triggerUid);
      
      List<TriggerAction> records = selectByExample(example);
      
      for (TriggerAction record : records) {
         delete(datosSesion, record.getActionUid());
      }
      
      return records.size();
   }
   
   @Override
   public Object getDataObject(TriggerAction triggerAction) {
      try {
         return triggerActionsTypeFactory.getActionImplementation(triggerAction).getDataObject();
      } catch (ApiException e) {
         throw new RuntimeException(e);
      }
   }
}
