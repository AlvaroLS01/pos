package com.comerzzia.api.loyalty.web.rest.operations;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.loyalty.service.operations.LoyaltyOperationsService;
import com.comerzzia.api.loyalty.service.operations.dto.LoyaltySaleOperation;
import com.comerzzia.api.loyalty.service.operations.dto.LoyaltySaleOperationResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/operations")
@Tag(name = "Loyalty operations", description = "Loyalty operations")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class OperationsResource {
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
  
   @Autowired
   LoyaltyOperationsService service;
      
   @POST
   @Operation( summary = "Insert loyalty sale operation",
               description = "Insert loyalty sale operation",
                responses = { @ApiResponse(responseCode = "200", description = "Loyalty sale operation results"), 
                              @ApiResponse(responseCode = "404", description = "Invalid data") })
   public LoyaltySaleOperationResult newLoyaltySaleOperation(@Parameter(description = "Loyalty sale operation data") 
                                           LoyaltySaleOperation loyaltySaleOperation) throws ApiException {
      
      return service.registerLoyaltyOperation(datosSesionRequest.getDatosSesionBean(), loyaltySaleOperation); 
   }  
      
}
