package com.comerzzia.bimbaylola.pos.services.clientes;

import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.core.util.tipoidentificacion.ValidadorDocumentoIdentificacionException;

public class ValidadorNIPPolonia implements IValidadorDocumentoIdentificacion {

	@Override
	public boolean validarDocumentoIdentificacion(String documentoIdentificacion)
			throws ValidadorDocumentoIdentificacionException {
		try {
			return validarNIP(documentoIdentificacion);
		} catch (Exception e) {
			throw new ValidadorDocumentoIdentificacionException(e.getMessage(), e);
		}
	}

	public boolean validarNIP(String number) {
		try {
			char[] n = number.toCharArray();

			Integer controlNumber = calculateControlNumber(number);

			if (controlNumber.equals(Integer.valueOf(String.valueOf(n[9])))) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;

	}

	private int calculateControlNumber(String nip){
		char[] n = nip.toCharArray();
		Integer sum =  Integer.valueOf(String.valueOf(n[0])) * 6
				+  Integer.valueOf(String.valueOf(n[1])) * 5
				+  Integer.valueOf(String.valueOf(n[2])) * 7
				+  Integer.valueOf(String.valueOf(n[3])) * 2
				+  Integer.valueOf(String.valueOf(n[4])) * 3
				+  Integer.valueOf(String.valueOf(n[5])) * 4
				+  Integer.valueOf(String.valueOf(n[6])) * 5
				+  Integer.valueOf(String.valueOf(n[7])) * 6
				+  Integer.valueOf(String.valueOf(n[8])) * 7;

		sum %=11;
		return sum;
	}
}
