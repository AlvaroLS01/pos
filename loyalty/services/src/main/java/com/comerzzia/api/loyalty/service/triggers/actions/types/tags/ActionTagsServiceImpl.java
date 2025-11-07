package com.comerzzia.api.loyalty.service.triggers.actions.types.tags;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.loyalty.persistence.triggers.actions.TriggerAction;
import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;
import com.comerzzia.api.loyalty.service.triggers.TriggersService;
import com.comerzzia.api.loyalty.service.triggers.actions.types.ActionTypeAbstractService;
import com.comerzzia.core.model.etiquetas.categorias.EtiquetaBean;
import com.comerzzia.core.model.etiquetas.enlaces.EtiquetaEnlaceBean;
import com.comerzzia.core.model.etiquetas.enlaces.EtiquetaEnlaceExample;
import com.comerzzia.core.model.etiquetas.enlaces.EtiquetaEnlaceExample.Criteria;
import com.comerzzia.core.servicios.etiquetas.EtiquetasConstraintViolationException;
import com.comerzzia.core.servicios.etiquetas.EtiquetasException;
import com.comerzzia.core.servicios.etiquetas.EtiquetasService;
import com.comerzzia.core.servicios.etiquetas.enlaces.EtiquetasEnlacesConstraintViolationException;
import com.comerzzia.core.servicios.etiquetas.enlaces.EtiquetasEnlacesException;
import com.comerzzia.core.servicios.etiquetas.enlaces.EtiquetasEnlacesService;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.comerzzia.core.util.base.Estado;


@Component("ActionTagsService")
@Scope("prototype")
public class ActionTagsServiceImpl extends ActionTypeAbstractService {
   private final Logger log = LoggerFactory.getLogger(ActionTagsServiceImpl.class);
   
   protected ActionTagsData data;
   
   @Autowired
   TriggersService triggerService;
   
   @Autowired
   EtiquetasService etiquetasService;
   
   @Autowired
   EtiquetasEnlacesService etiquetasEnlacesService;
   
   @Override
   public void setTriggerAction(TriggerAction triggerAction) {
      super.setTriggerAction(triggerAction);
      data = createDataObject(ActionTagsData.class);
   }   
   
   public Object getDataObject() {
      return data;
   }
      
   @Transactional(rollbackFor = Exception.class)
   public void executeAction(IDatosSesion sessionData) throws ApiException {
	  log.info("Executing action: " + triggerAction.getActionUid() + " Trigger: " + triggerAction.getTriggerUid());
      if (data == null) {
         throw new ApiException(ApiException.STATUS_RESPONSE_ERROR_INTERNAL, 
                                LY_TRIGGER_ACTIION_TYPE_NULL_DATA, 
                                new String[] {});         
      }
      
      // query customers from trigger execution data
      List<TriggerExecutionDataKey> triggerExecutionData = getTriggerExecutionData(sessionData);
      try {
	      if(data.isOperationDeassignTags()) {
	    	  deassignTags(triggerExecutionData, sessionData);	    	  
	      }
	      else if(data.isOperationAssignTags()) {
	    	  assignTags(triggerExecutionData, sessionData);	    		  
	      }   
      }catch(Exception e) {
		  throw new ApiException(e.getMessage(), e);
	  }
   }

	protected void assignTags(List<TriggerExecutionDataKey> triggerExecutionData, IDatosSesion sessionData) throws EtiquetasException, EtiquetasConstraintViolationException, EtiquetasEnlacesConstraintViolationException, EtiquetasEnlacesException {
		for(String stringTag: data.getTags()) {
			EtiquetaBean tagBean = etiquetasService.consultarEtiquetaEnlazada(sessionData, stringTag);
	    	if(tagBean.getEtiqueta()==null) {	
	    		String uidTag = UUID.randomUUID().toString();
	    		
	    		tagBean.setEstadoBean(Estado.NUEVO);
	    		tagBean.setUidEtiqueta(uidTag);
	    		tagBean.setEtiqueta(stringTag);
	    		
				etiquetasService.salvar(tagBean, sessionData);				
	    	}
	    	for(TriggerExecutionDataKey triggerExecutionDataKey: triggerExecutionData) {
	    		   		  
	    		EtiquetaEnlaceExample example = new EtiquetaEnlaceExample();
      		  	Criteria criteria = example.or();
      		  	criteria.andIdClaseEqualTo("F_FIDELIZADOS_TBL.ID_FIDELIZADO");
      		  	criteria.andIdObjetoEqualTo(triggerExecutionDataKey.getIdFidelizado().toString());

				if (etiquetasEnlacesService.consultar(example).isEmpty()){
					EtiquetaEnlaceBean etiquetaEnlace = new EtiquetaEnlaceBean();
					etiquetaEnlace.setIdClase("F_FIDELIZADOS_TBL.ID_FIDELIZADO");
					etiquetaEnlace.setEstadoBean(Estado.NUEVO);
					etiquetaEnlace.setUidEtiqueta(tagBean.getUidEtiqueta());
					etiquetaEnlace.setIdObjeto(triggerExecutionDataKey.getIdFidelizado().toString());
					etiquetasEnlacesService.salvar(etiquetaEnlace, sessionData);
				}
	    	}    	    	  
		}
	}
	
	protected void deassignTags(List<TriggerExecutionDataKey> triggerExecutionData, IDatosSesion sessionData) throws EtiquetasException, EtiquetasEnlacesConstraintViolationException, EtiquetasEnlacesException {
		for(String stringTag : data.getTags()) {
  		  	EtiquetaBean tagBean = etiquetasService.consultarEtiquetaEnlazada(sessionData, stringTag);
  		  	if(tagBean.getEtiqueta() != null) {
  		  		for(TriggerExecutionDataKey triggerExecutionDataKey: triggerExecutionData) {
  		  			EtiquetaEnlaceBean etiquetaEnlace = new EtiquetaEnlaceBean();
  		  			etiquetaEnlace.setUidEtiqueta(tagBean.getUidEtiqueta());
  		  			etiquetaEnlace.setIdClase("F_FIDELIZADOS_TBL.ID_FIDELIZADO");
  		  			etiquetaEnlace.setIdObjeto(triggerExecutionDataKey.getIdFidelizado().toString());
  		  			etiquetasEnlacesService.eliminar(etiquetaEnlace, sessionData);
  		  		}  			 
  		  	}
  	  	}
	}
}
