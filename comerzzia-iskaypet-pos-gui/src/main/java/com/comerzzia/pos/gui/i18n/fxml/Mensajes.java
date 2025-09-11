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
package com.comerzzia.pos.gui.i18n.fxml;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
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
			Map<String, String> mapGettextFxml = MensajesFXML.obtenerCadenas();
			
			Map<String, String> mapGettextxml = MensajesBotonera.obtenerCadenas();
			
			Map<String, String> mapGettextValidacion = MensajesValidacion.obtenerCadenas();
			
			List<String> cadenasBD = MensajesBaseDatos.obtenerCadenas();
			
			List<String> cadenasDefecto = MensajesDefecto.obtenerCadenas();


			for (String cadena : cadenasDefecto) {
				textos.add(cadena);
			}
			
			for (Entry<String, String> entrada : mapGettextFxml.entrySet()) {
				String value = entrada.getValue();
				textos.add(value);
			}
			
			for (Entry<String, String> entrada : mapGettextxml.entrySet()) {
				String value = entrada.getValue();
				if(value.startsWith("<texto>")){
					value = value.substring(7, value.length()-7);
				}
				textos.add(value);
			}
			
			for (Entry<String, String> entrada : mapGettextValidacion.entrySet()) {
				String value = entrada.getValue();
				textos.add(value);
			}
			
			for (String cadena : cadenasBD) {
				textos.add(cadena);
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
		// configure console appender
//		if (Logger.getRootLogger().getAppender("console") == null) {
//			ConsoleAppender console = new ConsoleAppender(); //create appender
//		    //configure the appender		    
//		    console.setName("console");
//		    console.setLayout(new PatternLayout("[%p] [%c] %m%n")); 
//		    console.setThreshold(Level.INFO);		    
//		    console.activateOptions();
//	  	    //add appender to any Logger (here is root)
//		    Logger.getRootLogger().addAppender(console);
//		}
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