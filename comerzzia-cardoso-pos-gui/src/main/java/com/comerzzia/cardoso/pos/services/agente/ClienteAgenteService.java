package com.comerzzia.cardoso.pos.services.agente;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.agente.ClienteAgenteExample;
import com.comerzzia.cardoso.pos.persistence.agente.ClienteAgenteKey;
import com.comerzzia.cardoso.pos.persistence.agente.ClienteAgenteMapper;
import com.comerzzia.cardoso.pos.services.agente.exception.ClienteAgenteException;

/**
 * GAP - AGENTES
 * TABLA : X_CLIENTE_AGENTES_TBL
 */
@Service
public class ClienteAgenteService{

	protected static final Logger log = Logger.getLogger(ClienteAgenteService.class);
	
	@Autowired
    protected ClienteAgenteMapper agenteMapper;
	
	public String getClienteAgente(String codCli) throws ClienteAgenteException{
		log.debug("getClienteAgente() :  GAP - AGENTES");
		
		if(StringUtils.isBlank(codCli)){
			return null;
		}
		List<ClienteAgenteKey> listResult =	null;
		try{
			ClienteAgenteExample example = new ClienteAgenteExample();
			example.or().andCodcliEqualTo(codCli);
			
			listResult =	agenteMapper.selectByExample(example);
			if(listResult == null || listResult.isEmpty()){
				return "";
			}
		}
		catch(Exception e){
			String mensajeError = "Error al consultar el agente para el cliente " + codCli;
			log.error("getClienteAgente() - " + mensajeError + " : " + e.getMessage(), e);
			throw new ClienteAgenteException(mensajeError, e);
		}
		return listResult.get(0).getCodage();
	}
	
}
