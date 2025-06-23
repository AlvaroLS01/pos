package com.comerzzia.bimbaylola.pos.services.core.sesion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.ByL.backoffice.rest.path.ByLBackofficeWebservicesPath;
import com.comerzzia.bimbaylola.pos.services.dispositivofirma.ByLDispositivosFirma;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionAplicacion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.variables.VariablesServices;


@Component
@Primary
public class ByLSesionAplicacion extends SesionAplicacion {
	
	@Autowired
	private VariablesServices variablesServices;
	
	protected Sesion sesion;
	
	@Override
	public void init() throws SesionInitException {
	    super.init();
	    
	    String basePath = variablesServices.getVariableAsString(VariablesServices.REST_URL);
	    if(basePath.endsWith("/")) {
	    	basePath = basePath.substring(0, basePath.length() - 1);
	    }
	    basePath = basePath + "-byl";
	    ByLBackofficeWebservicesPath.initPath(basePath);
	    
	    
	    // Cargamos los dispositivos de firma
	    ByLDispositivosFirma.getInstance().cargarDispositivosFirma();
	}
}
