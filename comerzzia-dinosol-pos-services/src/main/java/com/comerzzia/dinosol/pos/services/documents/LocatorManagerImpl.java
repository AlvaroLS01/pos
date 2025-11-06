package com.comerzzia.dinosol.pos.services.documents;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.core.servicios.documents.LocatorParseException;
import com.comerzzia.dinosol.librerias.cryptoutils.CryptoUtils;

@Primary
@Service
public class LocatorManagerImpl implements LocatorManager {
	protected static final Logger log = Logger.getLogger(LocatorManagerImpl.class);
	
	public static final String secretKey = "dinosol";	

	@Override
	public String encode(HashMap<String, Object> params) {
		if (params.containsKey("codTicket")) {
			String codTicket = (String)params.get("codTicket");
			
			// eliminar la barra de la serie
			codTicket = codTicket.replace("/", "");
			
			// codificar
			return CryptoUtils.encrypt(codTicket, secretKey);
		} else {
			throw new RuntimeException("No se ha podido obtener el codigo del ticket");
		}			
	}

	@Override
	public HashMap<String, Object> decode(String locator) throws LocatorParseException {		
		HashMap<String, Object> output = new HashMap<>();		
		return output;
	}

}
