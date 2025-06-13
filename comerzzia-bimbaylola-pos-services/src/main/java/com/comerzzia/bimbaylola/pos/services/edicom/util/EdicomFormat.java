package com.comerzzia.bimbaylola.pos.services.edicom.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class EdicomFormat {

	public static String rellenaEspaciosAlfanumericos(String cadena, int longitud) {
		// Añade espacios en blanco a la derecha hasta rellenar la longitud total
		//return StringUtils.isNotBlank(cadena) ? String.format("%-" + longitud + "s", cadena) : "";
		return StringUtils.isNotBlank(cadena) ? cadena : "";
	}

	public static String rellenaCerosNumericos(BigDecimal numero, int longitud, int decimales) {
		if (numero != null) {
			String numeroFormateado = "";
			// Si tiene decimales debe añadirse ceros delante y detrás, si no se añade solo el número 
			if (decimales != 0) {
				String formato = "%0" + (longitud) + "." + decimales + "f";
				numeroFormateado = String.format(formato, numero);
			} else {
				numeroFormateado = String.valueOf(numero.intValue());
			}
			return numeroFormateado;
		}
		else {
			return "";
		}
	}

	public static String devuelveFechaFormateada(Date fecha) {
		if (fecha == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		return sdf.format(fecha);
	}

	public static String devuelveHoraFormateada(Date fecha) {
		if (fecha == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(fecha);
	}

	public static String devuelveDvNIT(String nit) {
		String cadena = "";
		if (nit.contains("-")) {
			cadena = nit.substring(nit.indexOf("-") + 1, nit.length());
		} else {
			cadena = nit.substring(nit.length() - 1);
		}
		return cadena;
	}
	
	public static String devuelveNITSinDv(String nit) {
		String cadena = "";
		if (nit.contains("-")) {
			cadena = nit.substring(0, nit.indexOf("-"));
		} else {
			cadena = nit.substring(0, nit.length() - 1);
		}
		return cadena;
	}
	
	public static String devuelveCpDosPrimerosDigitos(String cp) {
		return cp.substring(0, 2);
	}

    public static String truncarRegistro(String registro) {
		int contador = 1;
        while (Character.compare(registro.charAt(registro.length() - contador), '|') == 0) {
            contador++;
        }
        contador--;
        int cuenta = registro.length() - contador + 1;

        return registro.substring(0, cuenta);
    }
    
    public static List<String> dividirEnPartes(String texto, int maxLongitud) {
        List<String> partes = new ArrayList<>();

        int inicio = 0;
        while (inicio < texto.length()) {
            int fin = Math.min(inicio + maxLongitud, texto.length());
            if (fin < texto.length() && texto.charAt(fin) != ' ') {
                int ultimoEspacio = texto.lastIndexOf(' ', fin);
                if (ultimoEspacio > inicio) {
                    fin = ultimoEspacio;
                }
            }
            partes.add(texto.substring(inicio, fin).trim());
            inicio = fin + 1;
        }

        return partes;
    }

}
