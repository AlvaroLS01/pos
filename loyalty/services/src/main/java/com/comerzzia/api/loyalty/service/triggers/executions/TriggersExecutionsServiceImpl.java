package com.comerzzia.api.loyalty.service.triggers.executions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerData;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecution;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecutionExample;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecutionKey;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecutionMapper;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.persistence.triggers.executions.dto.TriggerExecutionDTO;
import com.comerzzia.api.loyalty.service.triggers.TriggersService;
import com.comerzzia.api.loyalty.service.triggers.data.TriggerDataService;
import com.comerzzia.api.loyalty.service.triggers.executions.data.TriggersExecutionsDataService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

@Service
public class TriggersExecutionsServiceImpl implements TriggersExecutionsService {
   @Autowired
   private MessageSourceAccessor messageSourceAccessor;
   
   @Autowired
   TriggerExecutionMapper mapper;
   
   @Autowired
   TriggersExecutionsDataService triggersExecutionsDataService;

   @Autowired
   TriggersService triggersService;
   
   @Autowired
   TriggerDataService triggerDataService;
      
   @Override
   @Transactional(rollbackFor=Exception.class)
   public TriggerExecution insert(IDatosSesion datosSesion, TriggerExecution newRecord) throws ApiException {
      newRecord.setUidActividad(datosSesion.getUidActividad());
      if (newRecord.getTriggerExecUid() == null) {
         newRecord.setTriggerExecUid(UUID.randomUUID().toString());
      }
      mapper.insert(newRecord);
      
      return newRecord;
   }
   
   @Override
   public TriggerExecution selectByPrimaryKey(IDatosSesion datosSesion, String triggerExecUid) {
      return mapper.selectByPrimaryKey(new TriggerExecutionKey(datosSesion.getUidActividad(), triggerExecUid));
   }
   
   @Override
   public List<TriggerExecution> selectByExample(TriggerExecutionExample example) {
      return mapper.selectByExample(example);
   }   

   @Override
   public TriggerExecutionDTO selectDTOByPrimaryKey(IDatosSesion datosSesion, String triggerExecUid) {
      return mapper.selectDTOByPrimaryKey(new TriggerExecutionKey(datosSesion.getUidActividad(), triggerExecUid));
   }
   
   @Override
   public List<TriggerExecutionDTO> selectDTOByExample(TriggerExecutionExample example) {
      return mapper.selectDTOByExample(example);
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int delete(IDatosSesion datosSesion, String triggerExecUid) {
      // delete execution data
      triggersExecutionsDataService.delete(datosSesion, triggerExecUid);
      
      // delete record
      return mapper.deleteByPrimaryKey(new TriggerExecutionKey(datosSesion.getUidActividad(), triggerExecUid));
   }
   
   @Override
   @Transactional(rollbackFor=Exception.class)
   public int deleteByTriggerUid(IDatosSesion datosSesion, String triggerUid) {
      TriggerExecutionExample example = new TriggerExecutionExample(datosSesion);
      example.or().andTriggerUidEqualTo(triggerUid);
      
      List<TriggerExecution> records = selectByExample(example);
      
      for (TriggerExecution record : records) {
         delete(datosSesion, record.getTriggerExecUid());
      }
      
      return records.size();
   }
   
   @Override
   public int updateByPrimaryKey(IDatosSesion datosSesion, TriggerExecution record) {
      record.setUidActividad(datosSesion.getUidActividad());
      return mapper.updateByPrimaryKey(record); 
   }
   
   protected long getLastExecutionId(IDatosSesion datosSesion, String triggerUid) {
      TriggerExecutionExample example = new TriggerExecutionExample(datosSesion);
      example.or().andTriggerUidEqualTo(triggerUid);
      
      return mapper.countByExample(example) +1;
   }
   
   @Override
   public TriggerExecution executeTrigger(IDatosSesion datosSesion, String triggerUid, String newTriggerExecUid) throws ApiException {
      Trigger trigger = triggersService.selectByPrimaryKey(datosSesion, triggerUid);
      
      TriggerExecution triggerExecution = new TriggerExecution();
      
      triggerExecution.setTriggerExecUid(newTriggerExecUid);
      triggerExecution.setStatusText(messageSourceAccessor.getMessage(LY_TRIGGER_EXECUTING));
      triggerExecution.setUserId(datosSesion.getUserId());
      triggerExecution.setStatusId(0L);
      triggerExecution.setStartDate(new Date());
      triggerExecution.setTriggerUid(triggerUid);
      triggerExecution.setExecutionId(getLastExecutionId(datosSesion, triggerUid));
      triggerExecution.setTriggerData(trigger.getTriggerData());

      triggerExecution = insert(datosSesion, triggerExecution);
      
      // execute query
      try {
         triggerExecution.setAffectedRecords(new Long(executeSQL(datosSesion, trigger, triggerExecution)));
         
         triggerExecution.setEndDate(new Date());
         triggerExecution.setStatusId(100L);
         triggerExecution.setStatusText(messageSourceAccessor.getMessage(LY_TRIGGER_ENDED));
      } catch (Exception e) {
         triggerExecution.setEndDate(new Date());
         triggerExecution.setStatusId(200L);
         triggerExecution.setStatusText(StringUtils.left("Error: " + e.getMessage(), 2000));
      }
      
      // update trigger execution end
      updateByPrimaryKey(datosSesion, triggerExecution);
      
      // update last execution of trigger
      trigger.setLastTriggerExecUid(triggerExecution.getTriggerExecUid());      
      triggersService.updateByPrimaryKey(datosSesion, trigger);
      
      return triggerExecution;
   }
   
   protected int executeSQL(IDatosSesion datosSesion, Trigger trigger, TriggerExecution triggerExecution) throws ApiException {      
      try {
         TriggerData triggerData = triggerDataService.createDataObject(trigger.getTriggerData(), TriggerData.class);
         
         // set dynamic execution params
         triggerData.setUidActividad(datosSesion.getUidActividad());
         triggerData.setUidInstancia(datosSesion.getUidInstancia());
         
         // set dinamic date fields
         if (triggerData.getStartPurchaseDate() == null && triggerData.getStartPurchaseDays() != null) {
            triggerData.setStartPurchaseDate(DateUtils.addDays(DateUtils.round(new Date(), Calendar.DAY_OF_MONTH), triggerData.getStartPurchaseDays()*-1));
         }
         
         if (triggerData.getEndPurchaseDate() == null && triggerData.getEndPurchaseDays() != null && triggerData.getStartPurchaseDate() != null) {
            triggerData.setEndPurchaseDate(DateUtils.addDays(triggerData.getStartPurchaseDate(), triggerData.getEndPurchaseDays()));
         }
         
         if (StringUtils.isEmpty(triggerData.getSql())) {
            triggerData.setSql(triggerDataService.buildSql(datosSesion, triggerData));
         }         
         
         List<TriggerExecutionDataKey> records = mapper.executeTriggerQuery(triggerData);
                  
         triggersExecutionsDataService.insert(datosSesion, triggerExecution.getTriggerExecUid(), records);
         
         return records.size();
      }  catch (Exception e) {
         e.printStackTrace();
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL,
                                LY_TRIGGER_QUERY_ERROR, new String[] {e.getMessage()}, e);
      }
   }
}
