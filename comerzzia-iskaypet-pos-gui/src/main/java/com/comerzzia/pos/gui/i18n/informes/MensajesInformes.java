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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.SystemUtils;

import com.comerzzia.pos.gui.i18n.fxml.Mensajes;

public class MensajesInformes {
	
	public static final boolean DEBUG_ENABLED = false;
	
	protected static final String EXTENSION_JRXML = ".jrxml";
	
	protected static Map<String, String> mapGettextInformes = new HashMap<String, String>();
	
	public static Map<String, String> obtenerCadenas() throws MensajesException {
		Writer fstreamDebug = null;
		BufferedWriter outDebug = null;
		try {
			
			//=======================================================================================//
			// 								EXTRAER LAS CADENAS									 	 //
			//=======================================================================================//
			if(DEBUG_ENABLED){
				fstreamDebug = new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8"));
				outDebug = new BufferedWriter(fstreamDebug);
			}
			
			//Añadimos los directorios a una lista. De esta forma se podrá definir más de un directorio evitando tener que recorrer
			//el proyecto entero y así ahorrarnos el recorrido de directorios que no interesan como pueden ser las clases java o los
			//propios directorios de subvesion (.svn).
			List<String> listDirectorios = new ArrayList<String>();
			String path = Mensajes.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../../comerzzia-pos-resources/src/main/resources/plantillas/jasper";
			if (SystemUtils.IS_OS_WINDOWS) {
				path = path.substring(1); //En Windows hay que quitar el primer carácter /
			}
			path = path.replace("%20", " ");
			listDirectorios.add(path);
			
			File directorio;
			for(String sDirectorio: listDirectorios){
				directorio = new File(sDirectorio);
				listarDirectorio(directorio, fstreamDebug);
			}
		}
		catch (Exception e) {
			throw new MensajesException(e.getMessage(), e);
		}
		finally {
			try {
				if (outDebug != null) {
					outDebug.close();
				}
			}
			catch (IOException e) {
				throw new MensajesException(e.getMessage());
			}
		}
		
		return mapGettextInformes;
	}
	
	protected static void listarDirectorio(File f, Writer fstreamDebug) throws IOException {
		File[] ficheros = f.listFiles();

		String patternJRXML = "\\$R\\{(.*?)\\}";

		for (int x = 0; x < ficheros.length; x++) {
			if (ficheros[x].isDirectory()) {
				listarDirectorio(ficheros[x], fstreamDebug);
			}
			else {
				if (ficheros[x].getName().endsWith(EXTENSION_JRXML)){
					if(DEBUG_ENABLED){
						fstreamDebug.write("\n\n----------------------------------");
						fstreamDebug.write("\n" +ficheros[x].getPath());
						fstreamDebug.write("\n----------------------------------");
					}
					try {
						Pattern pattern = Pattern.compile(patternJRXML);
						Matcher matcher = pattern.matcher(fromFile(ficheros[x].getPath()));

						while (matcher.find()) {
							String match = matcher.group(1);

							if(DEBUG_ENABLED){
								fstreamDebug.write("\n" + match + " -----> " + match);
							}
							if(!mapGettextInformes.containsKey(match)) {
								mapGettextInformes.put(match, match);
							}
						}
					}
					catch (IOException e) {
					}
				}
			}
		}
	}

	protected static CharSequence fromFile(String filename) throws IOException {
		FileInputStream input = new FileInputStream(filename);
		FileChannel channel = input.getChannel();

		ByteBuffer bbuf = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int) channel.size());
		CharBuffer cbuf = Charset.forName("UTF-8").newDecoder().decode(bbuf);
		input.close();
		return cbuf;
	}
}
