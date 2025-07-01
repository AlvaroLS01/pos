package com.comerzzia.pos.core.services.countryidtypevalidator.es;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.comerzzia.pos.core.services.countryidtypevalidator.CountryDocTypeValidator;


@Service("countryDocTypeValidatorES")
public class CountryDocTypeValidatorES implements CountryDocTypeValidator {

	@Override
    public boolean validateVatNumber(String vatNumber) {
		return validateCifNif(vatNumber);
    }
	
	/**
	 * Método que valida un CIF o un NIF según corresponda
	 */
	protected boolean validateCifNif(String cifNif){
		String n = new String(cifNif);
		
		Pattern mask = Pattern.compile("^[a-zA-Z].*");
        Matcher matcher = mask.matcher(n);
        //si empieza por una letra, es un cif
        if (matcher.matches()) {
        	return validarCif(cifNif);
        }
        else{
        	return validateNif(cifNif);
        }
	}
	
	/**
	 * Método que valida un CIF
	 * @param cif
	 * @return true o false dependiendo si el CIF es válido o no
	 */
	protected boolean validarCif(String cif){
		cif = cif.trim().toUpperCase();

		Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
		String CONTROL_ONLY_NUMBERS = "ABEH"; // Sólo admiten números como caracter de control
		String CONTROL_ONLY_LETTERS = "KPQS"; // Sólo admiten letras como caracter de control
		String CONTROL_NUMBER_TO_LETTER = "JABCDEFGHI"; // Conversión de dígito a letra de control.

		try {
			if (!cifPattern.matcher(cif).matches()) {
				// No cumple el patrón
				return false;
			}

			int parA = 0;
			for (int i = 2; i < 8; i += 2) {
				int digit = Character.digit(cif.charAt(i), 10);
				if (digit < 0) {
					return false;
				}
				parA += digit;
			}

			int nonB = 0;
			for (int i = 1; i < 9; i += 2) {
				int digit = Character.digit(cif.charAt(i), 10);
				if (digit < 0) {
					return false;
				}
				int nn = 2 * digit;
				if (nn > 9) {
					nn = 1 + (nn - 10);
				}
				nonB += nn;
			}

			int parcialC = parA + nonB;
			int digitoE = parcialC % 10;
			int digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
			char letraIni = cif.charAt(0);
			char caracterFin = cif.charAt(8);
			
			// ¿el caracter de control es válido como letra o como dígito?
			boolean isValidControl = (CONTROL_ONLY_NUMBERS.indexOf(letraIni) < 0 && CONTROL_NUMBER_TO_LETTER .charAt(digitoD) == caracterFin) || (CONTROL_ONLY_LETTERS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
			
			return isValidControl;

		} catch (Exception e) {
			return false;
		}
	}

	/** Valida un NIF. Devuelve un booleano con el resultado de la validación. */
	protected boolean validateNif(String nif) {
		String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        try {
            if (nif == null) {
                return false;
            }
        
            String n = new String(nif);
            n = n.trim().toUpperCase();
        
            Pattern mask = Pattern.compile("[0-9]{8,8}[A-Z]");
            Matcher matcher = mask.matcher(n);
            if (!matcher.matches()) {
                return false;
            }
            
            String dni = n.substring(0, 8);
            String controlDigit = n.substring(8, 9);
            
            // Comprobamos digito control
            int posModulo = Integer.parseInt(dni)%23;
            String dc = letters.substring(posModulo, posModulo+1);
            if (!controlDigit.equals(dc)) {
                return false;
            }
            
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
	
	/** Valida un NIE. Devuelve un booleano con el resultado de la validación. */
	protected boolean validateNie(String nif){
		//si es NIE, eliminar la x,y,z inicial para tratarlo como nif
		if (nif.toUpperCase().startsWith("X")||nif.toUpperCase().startsWith("Y")||nif.toUpperCase().startsWith("Z")){
			return validateNif(nif.substring(1));
		}
		else{
			return false;
		}
	}
	
}