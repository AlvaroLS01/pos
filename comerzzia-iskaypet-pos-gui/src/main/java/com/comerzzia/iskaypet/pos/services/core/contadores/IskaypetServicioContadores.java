package com.comerzzia.iskaypet.pos.services.core.contadores;

import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.services.core.config.configContadores.parametros.ConfigContadoresParametrosException;
import com.comerzzia.pos.services.core.config.configContadores.parametros.ConfigContadoresParametrosInvalidException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Service
@Primary
public class IskaypetServicioContadores extends ServicioContadores {
	
	@Override
	protected Map<String, ConfigContadorParametroBean> obtenerParametrosEfectivos(String idContador, Map<String, String> parametrosContador) throws ConfigContadoresParametrosException, ConfigContadoresParametrosInvalidException {
		
		Map<String, ConfigContadorParametroBean> mapConfigParametrosEfectivos = new HashMap<>();
		
		Map<String, ConfigContadorParametroBean> mapConfigParametrosPermitidos = servicioConfigContadoresParametros.consultarMap(idContador);
		
		for(Entry<String, String> entry: parametrosContador.entrySet()) {
			if(mapConfigParametrosPermitidos.containsKey(entry.getKey())) {
				ConfigContadorParametroBean parametro = mapConfigParametrosPermitidos.get(entry.getKey());
				if(parametro.getLongitudMaxima() != null && parametro.getLongitudMaxima() < entry.getValue().length()){
					log.error("Error en la configuración del parámetro "+entry.getKey()+" del contador "+idContador);
					throw new ConfigContadoresParametrosInvalidException();
				}
				String valorParametro = entry.getValue();
				
				// ISK-298 GAP 134 Numeración tickets
				if (idContador.contains("REP_NUMFAC")) {
					if(entry.getKey().equals("PERIODO")) {
						valorParametro = valorParametro.substring(2,4);
					}
					if (entry.getKey().equals("CODCAJA")) {
						valorParametro = String.valueOf(Integer.parseInt(valorParametro));
					}
				}
				parametro.setValorParametro(valorParametro);
				mapConfigParametrosEfectivos.put(entry.getKey(), parametro);
			}
		}
		
		return mapConfigParametrosEfectivos;
	}

	public void disminuirContador(ContadorBean ticketContador) {
		ticketContador.setValor(ticketContador.getValor() - 1L);
		contadorMapper.updateByPrimaryKey(ticketContador);
	}

}
