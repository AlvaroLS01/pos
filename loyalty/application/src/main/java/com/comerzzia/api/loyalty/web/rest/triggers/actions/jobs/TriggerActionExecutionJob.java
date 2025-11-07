package com.comerzzia.api.loyalty.web.rest.triggers.actions.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.comerzzia.api.loyalty.service.triggers.actions.executions.TriggersActionsExecutionsService;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;

@Component
public class TriggerActionExecutionJob implements Job {
   private final Logger log = LoggerFactory.getLogger(TriggerActionExecutionJob.class);
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
   
   @Autowired
   TriggersActionsExecutionsService triggersActionsExecutionsService;
   
   @Override
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
      String triggerUid = jobDataMap.getString("triggerUid");
      String actionUid = jobDataMap.getString("actionUid");
      String uidActividad = jobDataMap.getString("uidActividad");
      String uidInstancia = jobDataMap.getString("uidInstancia");           

      log.info("The current time is: " + dateFormat.format(new Date()) + " for trigger " + triggerUid + " and action " + jobExecutionContext.getJobDetail().getKey().getName());
      
      DatosSesionBean datosSesion;
      try {
         Assert.notNull(triggerUid, "triggerUid must not be null");
         Assert.notNull(actionUid, "actionUid must not be null");
         
         datosSesion = new DatosSesionBean();
         datosSesion.setUidActividad(uidActividad);
         datosSesion.setUidInstancia(uidInstancia);
         datosSesion.setUserId(0L);
         
         triggersActionsExecutionsService.executeTriggerAction(datosSesion, triggerUid, actionUid, null, null);
      } catch (Exception e1) {
         throw new JobExecutionException(e1);
      }
      
   }

}
