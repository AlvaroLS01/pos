package com.comerzzia.pos.core.gui.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.core.facade.model.ActionDetail;
import com.comerzzia.core.facade.service.permissions.EffectiveActionPermissionsDto;
import com.comerzzia.core.service.audit.ComerzziaAuditEventBuilder;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.POSUserSession;

import lombok.extern.log4j.Log4j;

@Log4j
public abstract class ActionSceneController extends SceneController {
	protected ActionDetail action;
		
	@Autowired
	protected ApplicationSession applicationSession;
	
	@Autowired
	protected POSUserSession userSession;

	public ActionDetail getAction() {
		return action;
	}

	public void setAction(ActionDetail action) {
		this.action = action;
	}
	
	public void checkOperationPermissions(String operacion) throws PermissionDeniedException {
		log.trace("compruebaPermisos() - Comprobando permisos efectivos para la operación: " + operacion);
		EffectiveActionPermissionsDto permisosUsuarioAccion = userSession.getEffectiveActionPermissions(action);
        
        Boolean hasPermission = permisosUsuarioAccion.isCheckOperationExecution(operacion);
        	        
        auditOperation(new ComerzziaAuditEventBuilder()        		       
        		           .addOperation(operacion)
         		           .addField("permission", hasPermission));
        
        if(!hasPermission){
        	log.warn("checkPermisoEjecucion() - intentando ejecutar acción sin permiso de ejecución");
        	throw new PermissionDeniedException();
        }
	}

	public void checkExecutionPermission() throws PermissionDeniedException{
		// Consultamos los permisos efectivos del usuario
        EffectiveActionPermissionsDto permisosUsuarioAccion = userSession.getEffectiveActionPermissions(action);

        Boolean hasPermission = permisosUsuarioAccion.isCheckExecution();
        
        auditOperation(new ComerzziaAuditEventBuilder()        		       
		           .addOperation("EXECUTE")		           
		           .addField("permission", hasPermission));
        
        		
        // COMPROBAMOS SI USUARIO TIENE PERMISO DE EJECUCIÓN
        if (!hasPermission) {
            log.warn("checkPermisoEjecucion() - intentando ejecutar acción sin permiso de ejecución");
        	throw new PermissionDeniedException();
        }
	}
	
	public void auditOperation(ComerzziaAuditEventBuilder event) {
		event.addAction(action.getAction());
		super.auditOperation(event);
	}	
}
