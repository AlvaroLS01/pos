package com.comerzzia.cardoso.pos.services.rest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class PosRestService {
	
	public static final String VARIABLE_URL_APIV1 = "REST.URL";
	
	private Logger log = Logger.getLogger(PosRestService.class);
	
	@Autowired
	private VariablesServices variablesServices;
	
	public String getUrlApiV1() {
		log.debug("getUrlApiV1() : GAP - PERSONALIZACIONES V3 - PROMOCIÓN EMPLEADO");
		String url = "";
		try {
			url = variablesServices.getVariableAsString(VARIABLE_URL_APIV1);
		} catch (Exception e) {
			String msgError = I18N.getTexto("La variable '" + VARIABLE_URL_APIV1
					+ "' no se encuentra o no está bien configurada en el sistema.");
			log.error("getUrlApiV1() - " + msgError + " - " + e.getMessage(), e);
		}
		return url;
	}

}
