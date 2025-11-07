package com.comerzzia.api.loyalty.web.rest.triggers.actions;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.exception.NotFoundException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.persistence.triggers.actions.dto.TriggerActionDTO;
import com.comerzzia.api.loyalty.service.triggers.actions.TriggersActionsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

//@Path("/triggers/actions")
//@Tag(name = "Loyalty triggers", description = "Loyalty triggers services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class TriggersActionsResource {
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
        
   @Autowired
   TriggersActionsService service;

   @GET
   @Path("/{actionUid}")
   @Operation( summary = "Get action from key",
               description = "Get action from key",
               responses = { @ApiResponse(description = "The action data"), 
                             @ApiResponse(responseCode = "404", description = "Trigger not found") })
   public TriggerActionDTO getTriggerAction(@PathParam("actionUid") 
                                         @Parameter(description = "The action uid") 
                                         String actionUid) throws ApiException {
      TriggerActionDTO record = service.selectDTOByPrimaryKey(datosSesionRequest.getDatosSesionBean(), actionUid);
      
      if (record == null) {
         throw new NotFoundException();   
      }
      
      return record;
   }
}
