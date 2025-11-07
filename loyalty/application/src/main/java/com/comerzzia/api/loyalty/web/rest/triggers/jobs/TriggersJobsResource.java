package com.comerzzia.api.loyalty.web.rest.triggers.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.persistence.triggers.Trigger;
import com.comerzzia.api.loyalty.persistence.triggers.executions.TriggerExecution;
import com.comerzzia.api.loyalty.service.triggers.TriggersService;
import com.comerzzia.api.loyalty.service.triggers.executions.TriggersExecutionsService;
import com.comerzzia.api.loyalty.web.rest.triggers.TriggersResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

//@Path("/triggers/jobs")
//@Tag(name = "Loyalty triggers", description = "Loyalty triggers services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class TriggersJobsResource {
   public static final String JOB_GROUP = "triggers";
  
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
     
   @Autowired
   TriggersResource triggersResource;
   
   @Autowired
   Scheduler scheduler;
   
   @Autowired
   TriggersService triggerService;
   
   @Autowired
   TriggersExecutionsService triggerExecutionsService;   
   
   @GET
   @Path("/{triggerUid}")
   @Operation( summary = "Get trigger job state",
               description = "Get trigger job state",
               responses = { @ApiResponse(description = "Trigger execution data"), 
                              @ApiResponse(responseCode = "404", description = "Trigger not found"),
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public TriggerExecution getTriggerJobState(@PathParam("triggerUid") 
                                     @Parameter(description = "The trigger id") 
                                     String triggerUid) throws ApiException {
      Trigger trigger = triggersResource.getTrigger(triggerUid);
      
      try {
         JobDetail jobDetail = scheduler.getJobDetail(new JobKey(datosSesionRequest.getActivityUid() + "-" + triggerUid, JOB_GROUP));
         
         String newTriggerExecUid;
         
         if (jobDetail != null) {
            // currently runnig
            
            // get new trigger execution uid from job
            newTriggerExecUid = jobDetail.getJobDataMap().getString("newTriggerExecUid");
         } else {
            newTriggerExecUid = trigger.getLastTriggerExecUid();
            
            if (newTriggerExecUid == null) {
               throw new BadRequestException("El trigger no se ha ejecutado nunca");
            }
         }         
         
         // get execution trace record
         TriggerExecution triggerExecution = triggerExecutionsService.selectByPrimaryKey(datosSesionRequest.getDatosSesionBean(), newTriggerExecUid);
         
         if (triggerExecution == null) {
            if (jobDetail == null) {
               throw new BadRequestException("El trabajo de ejecución no ha sido encontrado");
            } else {
               // if the job is pending of execution and the TriggerExecution record is not created yet
               triggerExecution = new TriggerExecution();
               triggerExecution.setTriggerUid(triggerUid);
               triggerExecution.setTriggerExecUid(newTriggerExecUid);
               triggerExecution.setStatusText("Pendiente de ejecución....");
               triggerExecution.setStartDate((Date)jobDetail.getJobDataMap().get("startDate"));
            }
         }
         
         return triggerExecution;
      } catch (SchedulerException e) {
         throw new ApiException(e.getMessage(), e);
      }  
   }
   
   @POST
   @Operation( summary = "Create job for trigger",
               description = "Create job for trigger",
               responses = { @ApiResponse(description = "Trigger execution data"), 
                              @ApiResponse(responseCode = "404", description = "Trigger not found"),
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public TriggerExecution createTriggerJob(@QueryParam("triggerUid") 
                                @Parameter(description = "The trigger id") 
                                String triggerUid) throws ApiException {
      
      Trigger trigger = triggersResource.getTrigger(triggerUid);
      
      try {
         if (scheduler.checkExists(new JobKey(datosSesionRequest.getActivityUid() + "-" + triggerUid, JOB_GROUP))) {
            throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, "El trigger ya se está executando en este momento");
         }
         
         // create triggerExecUid for tracing
         String newTriggerExecUid = UUID.randomUUID().toString();
         
         // create job
         JobDetail jobDetail = buildJobDetail(trigger, newTriggerExecUid);
         
         // create trigger
         org.quartz.Trigger jobTrigger = buildJobTrigger(jobDetail, trigger);
         
         // add to scheduler
         scheduler.scheduleJob(jobDetail, jobTrigger);
         
         // if the job is pending of execution and the TriggerExecution record is not created yet
         TriggerExecution triggerExecution = new TriggerExecution();
         triggerExecution.setTriggerUid(triggerUid);
         triggerExecution.setTriggerExecUid(newTriggerExecUid);
         triggerExecution.setStatusText("Pendiente de ejecución....");
         triggerExecution.setStartDate((Date)jobDetail.getJobDataMap().get("startDate"));
         
         return triggerExecution;
      } catch (SchedulerException e) {
         throw new ApiException(e.getMessage(), e);
      }                        
   }
   
   private JobDetail buildJobDetail(Trigger trigger, String newTriggerExecUid) {
      JobDataMap jobDataMap = new JobDataMap();

      jobDataMap.put("triggerUid", trigger.getTriggerUid());
      jobDataMap.put("newTriggerExecUid", newTriggerExecUid);
      jobDataMap.put("uidActividad", trigger.getUidActividad());
      jobDataMap.put("uidInstancia", datosSesionRequest.getInstanceUid());
      jobDataMap.put("startDate", new Date());

      return JobBuilder.newJob(TriggerExecutionJob.class)
              .withIdentity(trigger.getUidActividad() + "-" + trigger.getTriggerUid(), JOB_GROUP)
              .withDescription("Job " + trigger.getTriggerCode() + "/" + trigger.getTriggerDescription())
              .usingJobData(jobDataMap)      
              .build();
  }

  private org.quartz.Trigger buildJobTrigger(JobDetail jobDetail, Trigger trigger) {
     
     return TriggerBuilder.newTrigger()
              .forJob(jobDetail)
              .withIdentity(jobDetail.getKey().getName(), JOB_GROUP)
              .withDescription("Trigger " + trigger.getTriggerCode() + "/" + trigger.getTriggerDescription())
              .startNow()
              .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
              .build();
  }
  
  @GET
  @Path("/list")
  @Operation( summary = "List trigger jobs",
            description = "List trigger jobs",
               responses = { @ApiResponse(description = "The triggers job list"), 
                             @ApiResponse(responseCode = "400", description = "Invalid input data") })
  public List<JobKey> getTriggersJobs() throws ApiException {                  
     List<JobKey> jobs = new ArrayList<JobKey>();
     
     try {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(JOB_GROUP))) {
           jobs.add(jobKey);
        }
     } catch (SchedulerException e) {
        throw new ApiException(e.getMessage(), e);
     }

     return jobs;
  } 
}
