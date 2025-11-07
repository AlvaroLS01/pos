package com.comerzzia.api.loyalty.service.triggers.actions.executions;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecution;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecutionExample;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecutionKey;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecutionMapper;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecution;
import com.comerzzia.api.loyalty.service.triggers.actions.TriggersActionsService;
import com.comerzzia.api.loyalty.service.triggers.actions.types.TriggerActionsTypeFactory;
import com.comerzzia.api.loyalty.service.triggers.executions.TriggersExecutionsService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class TriggersActionsExecutionsServiceImpl implements TriggersActionsExecutionsService {
   @Autowired
   private MessageSourceAccessor messageSourceAccessor;
   
   @Autowired
   TriggerActionExecutionMapper mapper;
   
   @Autowired
   TriggersExecutionsService triggerExecutionsService;
   
   @Autowired
   TriggersActionsService triggersActionsService;
   
   @Autowired
   TriggerActionsTypeFactory triggerActionsTypeFactory;
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public TriggerActionExecution insert(IDatosSesion datosSesion, TriggerActionExecution newRecord) throws ApiException {
      newRecord.setUidActividad(datosSesion.getUidActividad());
      if (newRecord.getAccExecutionUid() == null) {
         newRecord.setAccExecutionUid(UUID.randomUUID().toString());
      }
      
      mapper.insert(newRecord);
      
      return newRecord;
   }
   
   @Override
   public TriggerActionExecution selectByPrimaryKey(IDatosSesion datosSesion, String accExecutionUid) {
      return mapper.selectByPrimaryKey(new TriggerActionExecutionKey(datosSesion.getUidActividad(), accExecutionUid));
   }
   
   @Override
   public List<TriggerActionExecution> selectByExample(TriggerActionExecutionExample example) {
      return mapper.selectByExample(example);
   }   
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int delete(IDatosSesion datosSesion, String accExecutionUid) {            
      return mapper.deleteByPrimaryKey(new TriggerActionExecutionKey(datosSesion.getUidActividad(), accExecutionUid));
   }
   
   @Override
   public int updateByPrimaryKey(IDatosSesion datosSesion, TriggerActionExecution record) {
      record.setUidActividad(datosSesion.getUidActividad());
      return mapper.updateByPrimaryKey(record); 
   }   
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int deleteByActionUid(IDatosSesion datosSesion, String actionUid) {            
      TriggerActionExecutionExample example = new TriggerActionExecutionExample(datosSesion);
      example.setDatosSesion(datosSesion);
      example.or().andActionUidEqualTo(actionUid);
      return mapper.deleteByExample(example );
   }
   
   
   @Override
   public void executeTriggerAction(IDatosSesion datosSesion, String triggerUid, String actionUid, String triggerExecUid, String newAccExecutionUid) throws ApiException {
      TriggerExecution triggerExecution;
      
      // execute trigger if the execution uid is null
      if (triggerExecUid == null) {
         triggerExecution = triggerExecutionsService.executeTrigger(datosSesion, triggerUid, null);
         triggerExecUid = triggerExecution.getTriggerExecUid();
      } else {
         triggerExecution = triggerExecutionsService.selectByPrimaryKey(datosSesion, triggerExecUid);
      }
      
      // get trigger action
      TriggerAction triggerAction = triggersActionsService.selectByPrimaryKey(datosSesion, actionUid);
      
      // execute action begin
      TriggerActionExecution triggerActionExecution = new TriggerActionExecution();
      triggerActionExecution.setAccExecutionUid(newAccExecutionUid);
      triggerActionExecution.setActionUid(actionUid);
      triggerActionExecution.setStartDate(new Date());
      triggerActionExecution.setManualExecution(false);
      triggerActionExecution.setTriggerExecUid(triggerExecUid);
      triggerActionExecution.setStatusId(0L);
      triggerActionExecution.setUserId(datosSesion.getUserId());
      triggerActionExecution.setStatusText(messageSourceAccessor.getMessage(LY_TRIGGER_ACTION_EXECUTING));
      
      triggerActionExecution = insert(datosSesion, triggerActionExecution);
      
      // update trigger execution last action execution
      triggerExecution.setLastAccExecutionUid(triggerActionExecution.getAccExecutionUid());
      triggerExecutionsService.updateByPrimaryKey(datosSesion, triggerExecution);
      
      // update trigger last execution
      triggerAction.setLastActionExecUid(triggerActionExecution.getAccExecutionUid());      
      triggersActionsService.updateByPrimaryKey(datosSesion, triggerAction);     
      
      // execute action logic
      try {
         // call action type implementation
         triggerActionsTypeFactory.getActionImplementation(triggerAction).executeAction(datosSesion);
         
         triggerActionExecution.setEndDate(new Date());
         triggerActionExecution.setStatusId(100L);
         triggerActionExecution.setStatusText(messageSourceAccessor.getMessage(LY_TRIGGER_ACTION_ENDED));
      } catch (Exception e) {
         triggerActionExecution.setEndDate(new Date());
         triggerActionExecution.setStatusId(200L);
         triggerActionExecution.setStatusText(StringUtils.left("Error: " + e.getMessage(), 2000));
      }
      
      // update trigger action execution end
      updateByPrimaryKey(datosSesion, triggerActionExecution);       
   }
         
}
