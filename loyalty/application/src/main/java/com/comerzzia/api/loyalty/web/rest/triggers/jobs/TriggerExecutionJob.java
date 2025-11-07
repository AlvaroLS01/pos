package com.comerzzia.api.loyalty.web.rest.triggers.jobs;

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

import com.comerzzia.api.loyalty.service.triggers.executions.TriggersExecutionsService;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;

@Component
public class TriggerExecutionJob implements Job {
   private final Logger log = LoggerFactory.getLogger(TriggerExecutionJob.class);
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
   
   @Autowired
   TriggersExecutionsService triggerExecutionsService;
   
   @Override
   public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
      String triggerUid = jobDataMap.getString("triggerUid");
      String uidActividad = jobDataMap.getString("uidActividad");
      String uidInstancia = jobDataMap.getString("uidInstancia");
      String newTriggerExecUid = jobDataMap.getString("newTriggerExecUid");

      log.info("The current time is: " + dateFormat.format(new Date()) + " for trigger " + triggerUid + " and action " + jobExecutionContext.getJobDetail().getKey().getName());
      
      DatosSesionBean datosSesion;
      try {
         Assert.notNull(triggerUid, "triggerUid must not be null");
         Assert.notNull(newTriggerExecUid, "newTriggerExecUid must not be null");
         
         datosSesion = new DatosSesionBean();
         datosSesion.setUidActividad(uidActividad);
         datosSesion.setUidInstancia(uidInstancia);
         datosSesion.setUserId(0L);
         
         triggerExecutionsService.executeTrigger(datosSesion, triggerUid, newTriggerExecUid);
      } catch (Exception e1) {
         throw new JobExecutionException(e1);
      }
      
   }

}
