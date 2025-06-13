package com.comerzzia.bimbaylola.pos.services.clientes.pa;

import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.core.util.tipoidentificacion.ValidadorDocumentoIdentificacionException;

public class ValidadorRucPanama implements IValidadorDocumentoIdentificacion {

	@Override
	public boolean validarDocumentoIdentificacion(String numDocumento) throws ValidadorDocumentoIdentificacionException {
		String[] trozos = numDocumento.split("-");
		if(trozos.length != 3) {
			return false;
		}
		if(comprobarNumAlfanumerico(trozos)) {
			return false;
		}
		if(trozos[0].length() != 3 || trozos[1].length() != 3 || trozos[2].length() != 6) {
			return false;
		}
		return true;
	}

	private boolean comprobarNumAlfanumerico(String[] trozos) {
		for(int i = 0; i < trozos.length; i++) {
			if(!trozos[i].matches("[0-9]+")) {
				return true;
			}
		}
		return false;
	}

}
