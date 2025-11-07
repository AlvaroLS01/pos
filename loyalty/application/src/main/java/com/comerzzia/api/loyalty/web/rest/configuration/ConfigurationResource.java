package com.comerzzia.api.loyalty.web.rest.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.core.model.config.variables.ConfigVariableBean;
import com.comerzzia.core.model.config.variables.ConfigVariableExample;
import com.comerzzia.core.persistencia.config.variables.ConfigVariableMapper;
import com.comerzzia.core.servicios.variables.VariableException;
import com.comerzzia.core.servicios.variables.VariableNotFoundException;
import com.comerzzia.core.servicios.variables.VariablesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/configuration")
@Tag(name = "Configuration", description = "Loyalty module configuration")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class ConfigurationResource {
   @Resource(name = "datosSesionRequest")
   ComerzziaDatosSesion datosSesionRequest;
        
   @Autowired
   ConfigVariableMapper configVariableMapper;
   
   @Autowired
   VariablesService variablesService;

   @GET
   @Operation( summary = "Get loyalty module configuration",
             description = "Get loyalty module configuration",
                responses = { @ApiResponse(description = "The loyalty module variables list"), 
                              @ApiResponse(responseCode = "400", description = "Invalid input data") })
   public List<VariableDTO> getConfiguration() throws ApiException {
      ConfigVariableExample configVariableExample = new ConfigVariableExample();
      configVariableExample.or().andIdVariableLike("FIDELIZACION.%");
      
      List<ConfigVariableBean> configVariables = configVariableMapper.selectByExample(configVariableExample);
      List<VariableDTO> variables = new ArrayList<VariableDTO>();
            
      for (ConfigVariableBean configVariable : configVariables) {
         VariableDTO variable = new VariableDTO();
         variable.setVariableId(configVariable.getIdVariable());
         try {
            variable.setValue(variablesService.consultarValor(datosSesionRequest.getDatosSesionBean(), configVariable.getIdVariable()));
            variables.add(variable);            
         } catch (VariableException | VariableNotFoundException ignore) {
         }
      }
            
      return variables;
   }   
      
}
