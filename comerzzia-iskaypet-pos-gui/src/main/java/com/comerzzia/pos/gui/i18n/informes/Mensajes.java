/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.gui.i18n.informes;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Mensajes {
	private static Logger log = Logger.getLogger(Mensajes.class);
	
	/**Llamadas manuales para pruebas*/
	public static void main(String[] args) throws MensajesException {
		List<String> textos = new Mensajes().generarTextos();
		System.out.println(textos);
	}
	
	/**Llamado desde el gettext-maven-plugin*/
	public static List<String> getTextos() throws MensajesException {
		return new Mensajes().generarTextos();
	}
	
	/* constructor */
	public Mensajes() {
		initLogger();
		setTimeZone();
		
		@SuppressWarnings({ "resource", "unused" })
		ApplicationContext rootContext = new ClassPathXmlApplicationContext(new String[] {"/com/comerzzia/core/servicios/i18n/comerzzia-mensajes-context.xml"});		
		
		log.info("Carga de Spring correcta");
	}
	
	/**Llamado desde el gettext-maven-plugin*/
	private List<String> generarTextos() throws MensajesException {
		List<String> textos = new LinkedList<String>();		

		try {
			Map<String, String> mapGettextInformes = MensajesInformes.obtenerCadenas();
			
			for (Entry<String, String> entrada : mapGettextInformes.entrySet()) {
				String value = entrada.getValue();
				textos.add(value);
			}
			
		}
		catch (Exception e) {
			showError(e);
		}
		finally{
		}
		
		log.info("Cadenas extraidas correctamente");
		
		return textos;
	}
	
	private void initLogger() {

	}
	
	public void setTimeZone() {
		// Se fuerza el timezone porque la ejecución del plugin desde Jenkins da un error ORA-01882: timezone region not found
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	private static void showError(Exception e){
		System.err.println("\nSe ha producido un error: "+e.getMessage());
		System.err.println("\nDetalles:\n");
		e.printStackTrace();
		System.err.println("===================================================================");
	}
}