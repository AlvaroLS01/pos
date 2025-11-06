package com.comerzzia.dinosol.pos.services.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlaceAutocompleteRequest.SessionToken;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceAutocompleteType;

@Component
public class GoogleMapsService {

	private Logger log = Logger.getLogger(GoogleMapsService.class);
	
	public static final String ID_VARIABLE_API_KEY = "GOOGLE_MAPS.API_KEY";

	@Autowired
	private Sesion sesion;
	
	@Autowired
	private VariablesServices variablesService;

	public synchronized List<String> buscarLocalizaciones(String direccion, SessionToken tokenGoogle) {
		log.trace("buscarLocalizaciones() - Buscando localizaciones: " + direccion);
		
		String apiKey = variablesService.getVariableAsString(ID_VARIABLE_API_KEY);
		
		List<String> routeResult = new ArrayList<String>();

		try {
			log.debug("buscarLocalizaciones() - API Key de Google: " + apiKey);
			log.debug("buscarLocalizaciones() - Session token de Google: " + tokenGoogle.getUUID());
			log.debug("buscarLocalizaciones() - Dirección a buscar: " + direccion);
			
			PlaceAutocompleteRequest geoRequest = PlacesApi.placeAutocomplete(new GeoApiContext.Builder().apiKey(apiKey).build(), formatAddress(direccion, "", ""), tokenGoogle);

			geoRequest.components(ComponentFilter.country("es"));

			Double latitud = sesion.getAplicacion().getTienda().getTiendaBean().getLatitud();
			Double longitud = sesion.getAplicacion().getTienda().getTiendaBean().getLongitud();
			
			if(latitud == null || longitud == null) {
				log.error("buscarLocalizaciones() - La latitud y longitud de la tienda no están configuradas, no se podrá buscar la dirección en Google Maps");
				return routeResult;
			}

			log.debug("buscarLocalizaciones() - Buscando dirección");
			geoRequest.language("es").types(PlaceAutocompleteType.ADDRESS).location(new LatLng(latitud, longitud)).radius(20000);
			AutocompletePrediction[] result = geoRequest.await();

			log.debug("buscarLocalizaciones() - Devueltas " + result.length + " direcciones");
			for (AutocompletePrediction prediction : result) {
				routeResult.add(prediction.description);
			}
			log.debug("buscarLocalizaciones() - Devueltas: " + routeResult);
		}
		catch (Exception e) {
			log.error("buscarLocalizaciones() - Ha habido un error al buscar las localizaciones en Google Maps: " + e.getMessage(), e);
		}

		return routeResult;
	}

	private String formatAddress(String address, String postalCode, String locality) {
		if (locality != null && !locality.isEmpty()) {
			address += (" , " + locality);
		}
		if (postalCode != null && !postalCode.isEmpty()) {
			address += (" , " + postalCode);
		}
		return address;
	}
	
	public boolean validarDireccion(String address, String postalCode, String country, String locality) throws ApiException, InterruptedException, IOException {
		String apiKey = variablesService.getVariableAsString(ID_VARIABLE_API_KEY);
		
		List<ComponentFilter> f = new ArrayList<ComponentFilter>();
		List<String> routeResult = new ArrayList<String>();
		// Building the GeoRequest
		GeocodingApiRequest geoRequest = GeocodingApi.newRequest(new GeoApiContext.Builder().apiKey(apiKey).build()).language("es").region("es");

		// Building Filters
		f.add(ComponentFilter.route(address));
		if (postalCode != null && !postalCode.isEmpty()) {
			f.add(ComponentFilter.postalCode(postalCode));
		}
		if (country != null && !country.isEmpty()) {
			f.add(ComponentFilter.country(country));
		}
		if (locality != null && !locality.isEmpty()) {
			f.add(ComponentFilter.locality(locality));
		}
		geoRequest.components(f.toArray(new ComponentFilter[f.size()]));
		GeocodingResult[] result = geoRequest.await();
		for (GeocodingResult geolocation : result) {
			routeResult.add(geolocation.formattedAddress);
		}
		return result != null && result.length > 0;
	}

}
