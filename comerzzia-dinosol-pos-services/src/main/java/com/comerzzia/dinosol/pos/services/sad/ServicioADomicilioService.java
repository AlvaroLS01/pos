package com.comerzzia.dinosol.pos.services.sad;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.librerias.sad.client.model.Vehiculos;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasTipoBultoBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class ServicioADomicilioService {
	
	private Logger log = Logger.getLogger(ServicioADomicilioService.class);
	
	private List<Vehiculos> tiposVehiculos;
	
	private List<RutasTipoBultoBean> tiposBulto;
	
	public List<Vehiculos> getTiposVehiculos() {
		if(tiposVehiculos == null) {
			tiposVehiculos = new ArrayList<Vehiculos>();
			try {
				String filePath = "entities/sad_tipos_vehiculos.json";
				log.debug("getTiposVehiculos() - Leyendo fichero: " + filePath);
	
				Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
				ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
				URL url = classLoader.getResource(filePath);
	
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	
				String json = "";
				String line = "";
				while ((line = in.readLine()) != null) {
					json = json + System.lineSeparator() + line;
				}
				in.close();
				
				Type listType = new TypeToken<ArrayList<Vehiculos>>(){}.getType();
				tiposVehiculos = new Gson().fromJson(json, listType);
			}
			catch(Exception e) {
				log.error("getTiposVehiculos() - Error al leer JSON de tipos de vehículos: " + e.getMessage(), e);
			}
		}
		return tiposVehiculos;
	}
	
	public List<RutasTipoBultoBean> getTiposBultos() {
		if(tiposBulto == null) {
			tiposBulto = new ArrayList<RutasTipoBultoBean>();
			try {
				String filePath = "entities/sad_tipos_bultos.json";
				log.debug("getTiposBultos() - Leyendo fichero: " + filePath);
	
				Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
				ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
				URL url = classLoader.getResource(filePath);
	
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	
				String json = "";
				String line = "";
				while ((line = in.readLine()) != null) {
					json = json + System.lineSeparator() + line;
				}
				in.close();
				
				Type listType = new TypeToken<ArrayList<RutasTipoBultoBean>>(){}.getType();
				tiposBulto = new Gson().fromJson(json, listType);
			}
			catch(Exception e) {
				log.error("getTiposBultos() - Error al leer JSON de tipos de bultos: " + e.getMessage(), e);
			}
		}
		return tiposBulto;
	}

}
