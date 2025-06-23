package com.comerzzia.bimbaylola.pos.services.clientes;

import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.core.util.tipoidentificacion.ValidadorDocumentoIdentificacionException;

public class ValidadorRutRunChile implements IValidadorDocumentoIdentificacion {

	@Override
	public boolean validarDocumentoIdentificacion(String documentoIdentificacion) throws ValidadorDocumentoIdentificacionException {
		try {
			return validarRut(documentoIdentificacion);
		}
		catch (Exception e) {
			throw new ValidadorDocumentoIdentificacionException(e.getMessage(), e);
		}
	}

	/**
	 * Método que valida un RUT
	 */
	public static boolean validarRut(String rut) {
		boolean validacion = false;
		try {
			rut = rut.toUpperCase();
			rut = rut.replace(".", "");
			rut = rut.replace("-", "");
			int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

			char dv = rut.charAt(rut.length() - 1);

			int m = 0, s = 1;
			for (; rutAux != 0; rutAux /= 10) {
				s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
			}
			if (dv == (char) (s != 0 ? s + 47 : 75)) {
				validacion = true;
			}

		}
		catch (java.lang.NumberFormatException e) {
		}
		catch (Exception e) {
		}
		return validacion;
	}
}
