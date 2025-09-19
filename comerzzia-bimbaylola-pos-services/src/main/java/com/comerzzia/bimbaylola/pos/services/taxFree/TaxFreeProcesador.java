package com.comerzzia.bimbaylola.pos.services.taxFree;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TaxFreeProcesador {

	protected String rutaCarpeta;
	protected String rutaEjecutable;

	public TaxFreeProcesador(String rutaCarpeta, String rutaEjecutable) {
		super();
		this.rutaCarpeta = rutaCarpeta;
		this.rutaEjecutable = rutaEjecutable;
	}

	public void ejecutarComando(String comando) {
		run(comando);
	}

	/** Creates a new instance of PruebaRuntime */
	public String run(String comando) {
		String aux = null;

		try {
			// Se lanza el ejecutable.
			// Process p=Runtime.getRuntime().exec ("cmd /c dir");

			Process p = Runtime.getRuntime().exec(rutaEjecutable + " " + comando, null, new File(rutaCarpeta));

			p.waitFor();

			// Se obtiene el stream de salida del programa
			InputStream is = p.getInputStream();

			/* Se prepara un bufferedReader para poder leer la salida más comodamente. */
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			// Se lee la primera linea

			// Mientras se haya leido alguna linea
			while (br.ready()) {
				aux = br.readLine();
			}

		}
		catch (Exception e) {

			e.printStackTrace();
		}

		return aux;
	}
}
