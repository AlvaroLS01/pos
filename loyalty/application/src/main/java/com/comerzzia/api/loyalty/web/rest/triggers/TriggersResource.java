package com.comerzzia.api.loyalty.web.rest.triggers;

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
import com.comerzzia.api.loyalty.persistence.triggers.dto.TriggerDTO;
import com.comerzzia.api.loyalty.service.triggers.TriggersService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

//@Path("/triggers")
//@Tag(name = "Loyalty triggers", description = "Loyalty triggers services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class TriggersResource {
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
        
   @Autowired
   TriggersService triggerService;

   @GET
   @Path("/{triggerUid}")
   @Operation( summary = "Get trigger from key",
               description = "Get trigger from key",
               responses = { @ApiResponse(description = "The trigger data"), 
                             @ApiResponse(responseCode = "404", description = "Trigger not found") })
   public TriggerDTO getTrigger(@PathParam("triggerUid") @Parameter(description = "The trigger uid") 
                               String triggerUid) throws ApiException {
      TriggerDTO record = triggerService.selectDTOByPrimaryKey(datosSesionRequest.getDatosSesionBean(), triggerUid);
      
      if (record == null) {
         throw new NotFoundException();   
      }
      
      return record;
   }
}
