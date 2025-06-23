package com.comerzzia.bimbaylola.pos.services.clientes.co;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.core.util.tipoidentificacion.ValidadorDocumentoIdentificacionException;

public class ValidadorNITColombia implements IValidadorDocumentoIdentificacion{

	@Override
	public boolean validarDocumentoIdentificacion(String numDocIdent) throws ValidadorDocumentoIdentificacionException {

		String dv = "";
		String numero = "";
		if(StringUtils.isBlank(numDocIdent) || !numDocIdent.matches("[0-9]+")) {
			return false;
		}
		//Validación de que sea mayor a 5 propia porque no se especifica longitud
		if(numDocIdent.length() > 5) {
			numero = numDocIdent.substring(0, numDocIdent.length() -1 );
			dv = numDocIdent.substring(numDocIdent.length() - 1);
		} else {
			return false;
		}
		//Formateamos para que tenga 15 dígitos
		numero = String.format("%0" + 15 + "." + 0 +"f", new BigDecimal(numero));
		int numeroCalculado = calculateControlNumber(numero);

 		return numeroCalculado == Integer.valueOf(dv).intValue();
	 		 		
	}
	
	private int calculateControlNumber(String nit){
		char[] n = nit.toCharArray();
		Integer sum =  Integer.valueOf(String.valueOf(n[0])) * 71
				+  Integer.valueOf(String.valueOf(n[1])) * 67
				+  Integer.valueOf(String.valueOf(n[2])) * 59
				+  Integer.valueOf(String.valueOf(n[3])) * 53
				+  Integer.valueOf(String.valueOf(n[4])) * 47
				+  Integer.valueOf(String.valueOf(n[5])) * 43
				+  Integer.valueOf(String.valueOf(n[6])) * 41
				+  Integer.valueOf(String.valueOf(n[7])) * 37
				+  Integer.valueOf(String.valueOf(n[8])) * 29
				+  Integer.valueOf(String.valueOf(n[9])) * 23
				+  Integer.valueOf(String.valueOf(n[10])) * 19
				+  Integer.valueOf(String.valueOf(n[11])) * 17
				+  Integer.valueOf(String.valueOf(n[12])) * 13
				+  Integer.valueOf(String.valueOf(n[13])) * 7
				+  Integer.valueOf(String.valueOf(n[14])) * 3;

		sum %= 11;
		
		if(sum > 1){
		    sum = 11 - sum;
		}
		
		return sum;
	}
}
