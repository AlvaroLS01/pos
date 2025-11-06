package com.comerzzia.dinosol.pos.util.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.comerzzia.pos.util.text.TextUtils;

public class DinoTextUtils extends TextUtils {
	
	@Override
	public List<String> divideLines(String texto, int maxCaracteres, String separador) {
		List<String> lineas = new ArrayList<String>();
		
		if(StringUtils.isBlank(texto)) {
			return lineas;
		}

		if(StringUtils.isBlank(separador)) {
			separador = System.lineSeparator();
		}
		
		texto = texto.replaceAll("#QR#", "|#QR#");
		texto = texto.replaceAll("#/QR#", "#/QR#|");
		
		if(separador.equals("|")) {
			separador = "\\|";
		}
		
		String[] lineasIntroducidas = texto.split(separador);
		for (int i = 0; i < lineasIntroducidas.length; i++) {
			String lineaIntroducida = lineasIntroducidas[i];
			if (lineaIntroducida.length() <= maxCaracteres || lineaIntroducida.startsWith("#QR#")) {
				lineas.add(lineaIntroducida);
			}
			else {
				String[] palabrasLinea = lineaIntroducida.split(" ");
				String linea = "";
				for (int j = 0; j < palabrasLinea.length; j++) {
					String palabra = palabrasLinea[j];
					
					if (linea.length() + 1 + palabra.length() < maxCaracteres) {
						if(StringUtils.isNotBlank(linea)) {
							linea = linea + " " + palabra;
						}
						else {
							linea = palabra;
						}
					}
					else {
						lineas.add(linea);
						linea = palabra;
					}
					if(j==palabrasLinea.length-1) {
						lineas.add(linea);
					}
				}
			}
		}

		return lineas;
	}
	
	public String maskCardNumber(String cardNumber, int asterisks, int lastDigits) {
		String maskedCardNumber = "";
		
		for(int i = 0 ; i < asterisks ; i++) {
			maskedCardNumber = maskedCardNumber + "*";
		}
		
		String cardNumberLastDigits = cardNumber.substring(cardNumber.length() - lastDigits, cardNumber.length());
		maskedCardNumber = maskedCardNumber + cardNumberLastDigits;
		
		return maskedCardNumber;
	}
	
	public String capitalizeText(String text) {
		return WordUtils.capitalizeFully(text);
	}

}
