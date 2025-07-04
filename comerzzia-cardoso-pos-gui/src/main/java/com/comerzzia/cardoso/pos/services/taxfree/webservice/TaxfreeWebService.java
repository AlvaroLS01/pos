package com.comerzzia.cardoso.pos.services.taxfree.webservice;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeVariablesService;

@Service
public class TaxfreeWebService {

	protected Logger log = Logger.getLogger(getClass());

	@Autowired
	protected TaxfreeVariablesService taxfreeVariablesService;

	public String llamadaTaxfree(String json) throws Exception {
		String response = null;
		log.debug("llamadaTaxfree() - Solicitando Taxfree con el json : " + json);
		try {
			String url = taxfreeVariablesService.getUrlServicio();
			log.debug("llamadaTaxfree() - Solicitando Taxfree a la url : " + url);
			String user = taxfreeVariablesService.getUsuario();
			log.debug("llamadaTaxfree() - Solicitando Taxfree con el user : " + user);
			String pass = taxfreeVariablesService.getPassword();
			log.debug("llamadaTaxfree() - Solicitando Taxfree con la pass : " + pass);

			response = TaxfreeWebServiceClient.llamadaWsConsulta(url, user, pass, json);

		} catch (Exception e) {
			log.error("llamadaTaxfree() - Error haciendo la llamada a Innova WS:" + e.getMessage(), e);
			throw new Exception();
		}

		return response;

	}
}
