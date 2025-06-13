package com.comerzzia.bimbaylola.pos.services.clientes;

import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.core.util.tipoidentificacion.ValidadorDocumentoIdentificacionException;

public class ValidadorNifNipcPortugal implements IValidadorDocumentoIdentificacion{

	@Override
	public boolean validarDocumentoIdentificacion(String numDocIdent) throws ValidadorDocumentoIdentificacionException {
 		
		final int max=9;
 		//comprueba que tiene 9 numeros
 		if (!numDocIdent.matches("[0-9]+") || numDocIdent.length()!=max) return false;
 		int checkSum=0;
 		//calcula el sumatorio
 		for (int i=0; i<max-1; i++){
 			checkSum+=(numDocIdent.charAt(i)-'0')*(max-i);
 		}
 		int checkDigit=11-(checkSum % 11);
 		//Si el digito de control es mayor que 9 se setea a 0
 		if (checkDigit>9) checkDigit=0;
 		//comapra el digito de control con el ultimo numero del NIF
 		return checkDigit==numDocIdent.charAt(max-1)-'0';
	 		 		
	}

}
