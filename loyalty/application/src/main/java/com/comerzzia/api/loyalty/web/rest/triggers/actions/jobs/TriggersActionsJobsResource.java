package com.comerzzia.api.loyalty.web.rest.triggers.actions.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.BadRequestException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerActionExample;
import com.comerzzia.api.loyalty.persistence.triggers.actions.executions.TriggerActionExecution;
import com.comerzzia.api.loyalty.service.triggers.actions.TriggersActionsService;
import com.comerzzia.api.loyalty.service.triggers.actions.executions.TriggersActionsExecutionsService;
import com.comerzzia.api.loyalty.web.rest.triggers.actions.TriggersActionsResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

//@Path("/triggers/actions/jobs")
//@Tag(name = "Loyalty triggers", description = "Loyalty triggers services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class TriggersActionsJobsResource {
   public static final String JOB_GROUP = "triggers-action";
   public static final String JOB_GROUP_CRON = "triggers-action-cron";   
   
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
     
   @Autowired
   Scheduler scheduler;
   
   @Autowired
   TriggersActionsService triggersActionsService;
   
   @Autowired
   TriggersActionsResource triggersActionsResource;
   
   @Autowired
   TriggersActionsExecutionsService triggersActionsExecutionsService;
   
   @DELETE
   @Path("/deleteAll")
   @Operation( summary = "Delete all jobs",
             description = "Delete all jobs",
                responses = { @ApiResponse(responseCode = "204", description = "Empty response") })
   public Response clearActionsJobs() throws ApiException {
      try {
         scheduler.deleteJobs(new ArrayList<JobKey>(scheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(JOB_GROUP))));
      } catch (SchedulerException e) {
         throw new ApiException(e.getMessage(), e);
      }  
      
      return Response.ok().status(Status.NO_CONTENT).build();
   }
   
   @GET
   @Path("/list")
   @Operation( summary = "List jobs",
             description = "List jobs",
                responses = { @ApiResponse(description = "The job list"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public List<JobKey> getActionsJobs() throws ApiException {                  
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
   
   @GET
   @Path("/verify")
   @Operation( summary = "Verify jobs for scheduled actions",
               description = "Verify jobs for scheduled actions. Synchronization between scheduled actions and jobs",
               responses = { @ApiResponse(description = "The job list"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public List<JobKey> verifyActionsJobs() throws ApiException {                  
      try {
         checkScheduledActionsJobs();         
      } catch (SchedulerException e) {
         throw new ApiException(e.getMessage(), e);
      }

      return getActionsJobs();
   }   
   
   private JobDetail buildJobDetail(TriggerAction triggerAction) {
      JobDataMap jobDataMap = new JobDataMap();

      jobDataMap.put("actionUid", triggerAction.getActionUid());
      jobDataMap.put("triggerUid", triggerAction.getTriggerUid());
      jobDataMap.put("uidActividad", triggerAction.getUidActividad());
      jobDataMap.put("uidInstancia", datosSesionRequest.getInstanceUid());

      return JobBuilder.newJob(TriggerActionExecutionJob.class)
              .withIdentity(triggerAction.getUidActividad() + "-" + triggerAction.getActionUid(), JOB_GROUP_CRON)
              .withDescription("Job for trigger " + triggerAction.getTriggerUid())
              .usingJobData(jobDataMap)
              //.storeDurably()              
              .build();
  }
   
   private Trigger buildJobTrigger(JobDetail jobDetail, TriggerAction triggerAction) {
      Date startAt = triggerAction.getNextExecutionDate();
      
      if (startAt == null || (startAt != null && startAt.before(new Date()))) {
         startAt = new Date();
      }
      
      return TriggerBuilder.newTrigger()
               .forJob(jobDetail)
               .withIdentity(jobDetail.getKey().getName(), JOB_GROUP_CRON)
               .withDescription("Trigger for action " + triggerAction.getActionUid())
               .startAt(startAt) 
               .withSchedule(CronScheduleBuilder.cronSchedule(triggerAction.getCronInterval()).withMisfireHandlingInstructionDoNothing())
               .build();
   }   
   
  private JobDetail buildManualJobDetail(TriggerAction triggerAction, String newAccExecutionUid) {
      JobDataMap jobDataMap = new JobDataMap();

      jobDataMap.put("actionUid", triggerAction.getActionUid());
      jobDataMap.put("triggerUid", triggerAction.getTriggerUid());
      jobDataMap.put("uidActividad", triggerAction.getUidActividad());
      jobDataMap.put("uidInstancia", datosSesionRequest.getInstanceUid());
      jobDataMap.put("newAccExecutionUid", newAccExecutionUid);
      jobDataMap.put("startDate", new Date());

      return JobBuilder.newJob(TriggerActionManualExecutionJob.class)
              .withIdentity(triggerAction.getUidActividad() + "-" + triggerAction.getActionUid(), JOB_GROUP)
              .withDescription("Job for trigger " + triggerAction.getTriggerUid())
              .usingJobData(jobDataMap)
              .build();
  }   
  
  private org.quartz.Trigger buildManualJobTrigger(JobDetail jobDetail, TriggerAction triggerAction) {
     
     return TriggerBuilder.newTrigger()
              .forJob(jobDetail)
              .withIdentity(jobDetail.getKey().getName(), JOB_GROUP)
              .withDescription("Trigger for action " + triggerAction.getActionUid())
              .startNow()
              .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
              .build();
  }
   
  protected void checkScheduledActionsJobs() throws SchedulerException {
     // get scheduled actions
     TriggerActionExample example = new TriggerActionExample(datosSesionRequest.getDatosSesionBean());
     example.or().andManualExecutionEqualTo(false);
     
     List<TriggerAction> triggerActions = triggersActionsService.selectByExample(example);
     
     // check every action job & cron trigger
     for (TriggerAction triggerAction : triggerActions) {
        JobDetail jobDetail = scheduler.getJobDetail(new JobKey(triggerAction.getUidActividad() + "-" + triggerAction.getActionUid(), JOB_GROUP_CRON));
        Trigger trigger = scheduler.getTrigger(new TriggerKey(triggerAction.getUidActividad() + "-" + triggerAction.getActionUid(), JOB_GROUP_CRON));
        
        // check current trigger if exists
        if (trigger != null) {
           // cron expression changed
           if (!StringUtils.equals(((CronTrigger)trigger).getCronExpression(), triggerAction.getCronInterval())) {
              // delete job & triggers
              scheduler.unscheduleJob(trigger.getKey());
              trigger = null;
           }
        }

        // check create job & trigger if the cron interval is defined
        if (trigger == null && !StringUtils.isEmpty(triggerAction.getCronInterval())) {
           // create job
           jobDetail = buildJobDetail(triggerAction);
           
           // create trigger
           trigger = buildJobTrigger(jobDetail, triggerAction);
           
           // add to scheduler
           scheduler.scheduleJob(jobDetail, trigger);
        }
     }     
  }
  
  @DELETE
  @Path("/{actionUid}")
  @Operation( summary = "delete job for an trigger action",
              description = "delete job for an trigger action",
              responses = { @ApiResponse(responseCode = "204", description = "Empty response"), 
                            @ApiResponse(responseCode = "404", description = "Trigger not found") })
  public Response deleteTriggerActionJob(@PathParam("actionUid") 
                               @Parameter(description = "The action id") 
                               String actionUid) throws ApiException {
     
     TriggerAction triggerAction = triggersActionsResource.getTriggerAction(actionUid);
          
     try {
    	JobKey key = new JobKey(datosSesionRequest.getActivityUid() + "-" + actionUid, JOB_GROUP);
    	
    	// delete current job schedule if exits
        if (scheduler.checkExists(key)) {
        	Trigger trigger = scheduler.getTrigger(new TriggerKey(triggerAction.getUidActividad() + "-" + triggerAction.getActionUid(), JOB_GROUP_CRON));
        	
        	if (trigger == null) {
        		throw new NotFoundException();
        	}
        	
        	scheduler.unscheduleJob(trigger.getKey());
        }                
     } catch (SchedulerException e) {
        throw new ApiException(e.getMessage(), e);
     }                  
     
     return Response.ok().status(Status.NO_CONTENT).build();
  }
  
  @PUT
  @Path("/{actionUid}")
  @Operation( summary = "update job for an trigger action",
              description = "update job for an trigger action",
              responses = { @ApiResponse(description = "Action execution data"), 
                             @ApiResponse(responseCode = "404", description = "Trigger not found"),
                             @ApiResponse(responseCode = "400", description = "Invalid input data") })
  public TriggerActionExecution updateTriggerActionJob(@PathParam("actionUid") 
                               @Parameter(description = "The action id") 
                               String actionUid) throws ApiException {
     
     TriggerAction triggerAction = triggersActionsResource.getTriggerAction(actionUid);
     
     if (StringUtils.isEmpty(triggerAction.getCronInterval())) {
    	 throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_BAD_REQUEST, "La acción no tiene asignado intervalo de tiempo");
     }
     
     try {
    	JobKey key = new JobKey(datosSesionRequest.getActivityUid() + "-" + actionUid, JOB_GROUP);
    	
    	// delete current job schedule if exits
        if (scheduler.checkExists(key)) {
        	Trigger trigger = scheduler.getTrigger(new TriggerKey(triggerAction.getUidActividad() + "-" + triggerAction.getActionUid(), JOB_GROUP_CRON));
        	scheduler.unscheduleJob(trigger.getKey());
        }
        
        // create triggerExecUid for tracing
        String newAccExecutionUid = UUID.randomUUID().toString();
        
        // create job
        JobDetail jobDetail = buildManualJobDetail(triggerAction, newAccExecutionUid);
        
        // create trigger
        org.quartz.Trigger jobTrigger = buildManualJobTrigger(jobDetail, triggerAction);
        
        // add to scheduler
        scheduler.scheduleJob(jobDetail, jobTrigger);
                
        return getActionJobState(actionUid);
     } catch (SchedulerException e) {
        throw new ApiException(e.getMessage(), e);
     }                        
  }
  
  @POST
  @Path("/{actionUid}")
  @Operation( summary = "create job for an trigger action",
              description = "create job for an trigger action",
              responses = { @ApiResponse(description = "Action execution data"), 
                             @ApiResponse(responseCode = "404", description = "Trigger not found"),
                             @ApiResponse(responseCode = "400", description = "Invalid input data") })
  public TriggerActionExecution createTriggerActionJob(@PathParam("actionUid") 
                               @Parameter(description = "The action id") 
                               String actionUid) throws ApiException {
     
     TriggerAction triggerAction = triggersActionsResource.getTriggerAction(actionUid);
     
     try {
        if (scheduler.checkExists(new JobKey(datosSesionRequest.getActivityUid() + "-" + actionUid, JOB_GROUP))) {
           throw new BadRequestException(ApiException.STATUS_RESPONSE_ERROR_CONFLICT_STATE, "La acción ya se está executando en este momento");
        }
        
        // create triggerExecUid for tracing
        String newAccExecutionUid = UUID.randomUUID().toString();
        
        // create job
        JobDetail jobDetail = buildManualJobDetail(triggerAction, newAccExecutionUid);
        
        // create trigger
        org.quartz.Trigger jobTrigger = buildManualJobTrigger(jobDetail, triggerAction);
        
        // add to scheduler
        scheduler.scheduleJob(jobDetail, jobTrigger);
                
        return getActionJobState(actionUid);
     } catch (SchedulerException e) {
        throw new ApiException(e.getMessage(), e);
     }                        
  }
  
  @GET
  @Path("/{actionUid}")
  @Operation( summary = "Get action job state",
              description = "Get action job state",
              responses = { @ApiResponse(description = "Action execution data"), 
                             @ApiResponse(responseCode = "404", description = "Action not found"),
                             @ApiResponse(responseCode = "400", description = "Invalid input data") })
  public TriggerActionExecution getActionJobState(@PathParam("actionUid") 
                                    @Parameter(description = "The action id") 
                                    String actionUid) throws ApiException {
     TriggerAction triggerAction = triggersActionsResource.getTriggerAction(actionUid);
     
     try {
        JobDetail jobDetail = scheduler.getJobDetail(new JobKey(datosSesionRequest.getActivityUid() + "-" + actionUid, JOB_GROUP));
        
        String newAccExecutionUid;
        
        if (jobDetail != null) {
           // currently runnig
           
           // get new trigger execution uid from job
           newAccExecutionUid = jobDetail.getJobDataMap().getString("newAccExecutionUid");
        } else {
           newAccExecutionUid = triggerAction.getLastActionExecUid();
           
           if (newAccExecutionUid == null) {
              throw new BadRequestException("La accion no se ha ejecutado nunca");
           }
        }         
        
        // get execution trace record
        TriggerActionExecution triggerActionExecution = triggersActionsExecutionsService.selectByPrimaryKey(datosSesionRequest.getDatosSesionBean(), newAccExecutionUid);
        
        if (triggerActionExecution == null) {
           if (jobDetail == null) {
              throw new BadRequestException("El trabajo de ejecución no ha sido encontrado");
           } else {
              // if the job is pending of execution and the TriggerExecution record is not created yet
              triggerActionExecution = new TriggerActionExecution();
              triggerActionExecution.setManualExecution(true);
              triggerActionExecution.setActionUid(actionUid);
              triggerActionExecution.setAccExecutionUid(newAccExecutionUid);
              triggerActionExecution.setStatusText("Pendiente de ejecución....");
              triggerActionExecution.setStartDate((Date)jobDetail.getJobDataMap().get("startDate"));
           }
        }
        
        return triggerActionExecution;
     } catch (SchedulerException e) {
        throw new ApiException(e.getMessage(), e);
     }  
  }

     
}
