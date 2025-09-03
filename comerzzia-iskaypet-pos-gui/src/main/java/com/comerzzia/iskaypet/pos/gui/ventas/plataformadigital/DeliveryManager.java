package com.comerzzia.iskaypet.pos.gui.ventas.plataformadigital;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;

/*
 * GAP 107 - ISK-262 GLOVO (venta por plataforma digital)
 */
@Component
public class DeliveryManager {
	
	private static final Logger log = Logger.getLogger(DeliveryManager.class);
	
	private static final String X_POS_REPARTO_COLECTIVOS = "X_POS.REPARTO_COLECTIVOS";
	private static final String X_POS_REPARTO_PERMITIDO = "X_POS.REPARTO_PERMITIDO";
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private MediosPagosService mediosPagosService;

	private Map<String, String> mapaColectivosDelivery = null;
	private boolean repartoPermitido;
	
	public void inicializarValores() {
		if(mapaColectivosDelivery==null) {
			cargarMapaColectivosDelivery();
		}
	}
	
	private void cargarMapaColectivosDelivery() {
		log.debug("cargarMapaColectivosDelivery() - Cargando variables de plataformas digitales");
		mapaColectivosDelivery = new HashMap<>();
		
		repartoPermitido = variablesServices.getVariableAsBoolean(X_POS_REPARTO_PERMITIDO, false);
		
		if(!repartoPermitido) {
			log.debug("cargarMapaColectivosDelivery() - No se permiten ventas de plataformas digitales en esta tienda");
			return;
		}
		
		String variableString = variablesServices.getVariableAsString(X_POS_REPARTO_COLECTIVOS);
		
		if(variableString != null) {
			String[] variableDuplas = StringUtils.split(variableString, ";");
			for(String dupla : variableDuplas) {
				String[] claveValor = StringUtils.split(dupla, ":");
				
				
				if(StringUtils.isBlank(dupla)) {
					log.debug("cargarMapaColectivosDelivery() - Error: par clave-valor vacío: " + dupla);
					continue;
				}
				
				if(claveValor.length != 2 || StringUtils.countMatches(dupla, ":") != 1) {
					log.debug("cargarMapaColectivosDelivery() - Error: par clave-valor malformado: " + dupla);
					continue;
				}
				
				//Se descarta si alguno de los dos está vacío
				if(StringUtils.isBlank(claveValor[0]) || StringUtils.isBlank(claveValor[1])) {
					log.debug("cargarMapaColectivosDelivery() - Error: algún valor vacío: " + dupla);
					continue;
				}
				
				//Se descarta si el mapa ya incluye el valor
				if(mapaColectivosDelivery.containsKey(claveValor[0])) {
					log.debug("cargarMapaColectivosDelivery() - Error: Ya se incluye el colectivo indicado: " + dupla);
					continue;
				}
				
				if(mediosPagosService.getMedioPago(claveValor[1]) == null) {
					log.debug("cargarMapaColectivosDelivery() - Error: No existe el medio de pago: " + dupla);
					continue;
				}
				
				mapaColectivosDelivery.put(claveValor[0], claveValor[1]);
				log.debug("cargarMapaColectivosDelivery() - Valor cargado: cod colectivo:["+claveValor[0]+"], medio de pago:["+claveValor[1]+"]");
			}
		}
		log.debug("cargarMapaColectivosDelivery() - Fin de carga de mapa de colectivos y medpag de plataformas de venta digital. Mapa resultante: " + mapaColectivosDelivery);
		
	}
	
	public boolean fidelizadoGeneraVentaDelivery(FidelizacionBean fidelizado) {
		return getCodColectivoVentaDelivery(fidelizado) != null;
	}
	
	public String getCodMedPagDeColectivo(String codColectivo) {
		return mapaColectivosDelivery.get(codColectivo);
	}
	
	public String getCodColectivoVentaDelivery(FidelizacionBean fidelizado) {
		
		if(fidelizado==null || fidelizado.getCodColectivos()==null || fidelizado.getCodColectivos().isEmpty() || mapaColectivosDelivery.isEmpty()) {
			log.debug("getCodColectivoVentaDelivery() - Este fidelizado no genera venta digital");
			return null;
		}
		
		//Nos quedamos con el primer colectivo configurado en la variable que se encuentre en el fidelizado
		for(String codColectivo : mapaColectivosDelivery.keySet()) {
			if(fidelizado.getCodColectivos().contains(codColectivo)) {
				log.debug("getCodColectivoVentaDelivery() - Fidelizado id:["+fidelizado.getIdFidelizado()+"] genera venta digital con colectivo cod:["+codColectivo+"]");
				return codColectivo;
			}
		}
		log.debug("getCodColectivoVentaDelivery() - Fidelizado id:["+fidelizado.getIdFidelizado()+"] no genera venta digital");
		return null;
	}
	
}
