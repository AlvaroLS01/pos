package com.comerzzia.dinosol.pos.services.sherpa;

import org.apache.log4j.Logger;

import com.comerzzia.dinosol.librerias.sad.client.AccesoApi;
import com.comerzzia.dinosol.librerias.sad.client.ApiClient;
import com.comerzzia.dinosol.librerias.sad.client.BultosApi;
import com.comerzzia.dinosol.librerias.sad.client.PedidosApi;
import com.comerzzia.dinosol.librerias.sad.client.VehiculosApi;
import com.comerzzia.dinosol.librerias.sad.client.model.LoginData;
import com.comerzzia.dinosol.librerias.sad.client.model.LoginResponse;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.DomicilioApi;
import com.comerzzia.dinosol.librerias.sherpa.client.segmentos.servicios.SherpaService;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

public class SherpaApiBuilder {
	private static Logger log = Logger.getLogger(SherpaApiBuilder.class);
	
	/* Nombre de variables de BD */
	public static final String RUTAS_API_URL = "RUTAS.API_URL";
	public static final String RUTAS_USUARIO = "RUTAS.USUARIO";
	public static final String RUTAS_PASSWORD = "RUTAS.PASSWORD";	
	public static final String RUTAS_AUTORIZACION = "sad_jwt_auth";
	public static final String SHERPA_API_URL = "SHERPA.API_URL";
	public static final String SHERPA_API_URL_FIDELIZADOS = "SHERPA.API_URL_FIDELIZADOS";
	public static final String SHERPA_API_KEY = "SHERPA.API_KEY";
	public static final String SHERPA_POS_TYPE = "SHERPA.POS_TYPE";

	private static VariablesServices variablesServicesCache;
	
	private static SherpaService service;
	
	// API sherpa domicilios de fidelizado
	private static com.comerzzia.dinosol.librerias.sherpa.client.domicilio.ApiClient domicilioApiClient;	
	private static DomicilioApi domicilioApiService;
	
	// API servicio a domicilio (RUTAS)
	private static final long TOKEN_TIMEOUT_MS = 3600000L; // 1 hora
	private static long lastTokenTTL = 0L;
	private static com.comerzzia.dinosol.librerias.sad.client.ApiClient sadApiClient;
	private static AccesoApi accesoSadService;
	private static BultosApi sadBultosService;
	private static PedidosApi sadPedidosService;
	private static VehiculosApi sadVehiculosService;			
	
	private static String getVariable(String idVariable) {
		if (variablesServicesCache == null) {
			variablesServicesCache = SpringContext.getBean(VariablesServices.class);			
		}
		
		String valor = variablesServicesCache.getVariableAsString(idVariable);
		
		if (valor.isEmpty()) {
		   log.error("SherpaApiBuilder() - Variable " + idVariable + " no configurada");
		} else {
		   log.debug("SherpaApiBuilder() - Variable " + idVariable + "=" + valor);
		}
		
		return valor;		
	}

	public static SherpaService getSherpaApiService() {
		if (service != null) {
			return service;
		}
				
		log.info("Inicializando API Sherpa");
		String url = getVariable(SHERPA_API_URL_FIDELIZADOS);
		String apikey = getVariable(SHERPA_API_KEY);

		service = new SherpaService();
		service.setApikey(apikey);		
        // se concatena con /promosegment porque se construyo el servicio para esa URL
	    service.setServiceUrl(url + "/promosegment");

		return service;
	}
	
	public static DomicilioApi getSherpaDomicilioApiService() {
		if (domicilioApiService != null) {
			return domicilioApiService;
		}
		
		log.info("Inicializando API Sherpa - domicilios");
		/* Cargamos al "ApiClient" de Sherpa el tipo de autorización */
		domicilioApiClient = new com.comerzzia.dinosol.librerias.sherpa.client.domicilio.ApiClient(RUTAS_AUTORIZACION);
		/* Cargamos el token en los servicios */
		domicilioApiClient.setBasePath(getVariable(SHERPA_API_URL));
		domicilioApiClient.setBearerToken(getVariable(SHERPA_API_KEY));			
		domicilioApiService = domicilioApiClient.buildClient(DomicilioApi.class);
		
		return domicilioApiService;
	}
	
	public static String getSherpaPosType() {
		return getVariable(SHERPA_POS_TYPE);
	}
	
	private static void checkSadApi() throws Exception {
		if (sadApiClient == null) {		
			log.info("Inicializando API servicio a domicilio");
			/* Cargamos al "ApiClient" de Rutas el tipo de autorización */
			sadApiClient = new ApiClient(RUTAS_AUTORIZACION);		
			sadApiClient.setBasePath(getVariable(RUTAS_API_URL));
			
			accesoSadService = sadApiClient.buildClient(AccesoApi.class);						
			sadBultosService = sadApiClient.buildClient(BultosApi.class);
			sadPedidosService = sadApiClient.buildClient(PedidosApi.class);
			sadVehiculosService = sadApiClient.buildClient(VehiculosApi.class);
		}
		
		// control para refrescar el token
		if (System.currentTimeMillis() - lastTokenTTL > TOKEN_TIMEOUT_MS) {
			cargarTokenSad();
			lastTokenTTL = System.currentTimeMillis();
		}
	}
	
	/**
	 * Realiza la petición a Rutas para traer un token a partir del email y password.
	 * @return LoginResponse
	 * @throws RutasTokenException 
	 * @throws ConexionServicioRutasException
	 */
	private static void cargarTokenSad() throws Exception  {
		LoginData datosToken = new LoginData();
		datosToken.setEmail(getVariable(RUTAS_USUARIO));
		datosToken.setPassword(getVariable(RUTAS_PASSWORD));
		
		LoginResponse loginResponse = null;
		try{
			log.debug("cargarToken() POST - " + datosToken);
			loginResponse = accesoSadService.jwtTokenPost(datosToken);
						
			log.debug("cargarToken() RESPUESTA - " + loginResponse);
		}catch(Exception e){
			log.error("cargarToken() - " + e.getMessage(), e);
			
			throw new Exception(I18N.getTexto("Se ha producido un error al solicitar el Token"), e);
		}
		
		/* Cargamos el token en los servicios */
		sadApiClient.setBearerToken(loginResponse.getToken());		
    }
	
	public static BultosApi getSadBultosService() throws Exception {
		checkSadApi();
		
		return sadBultosService;
	}
	
	public static PedidosApi getSadPedidosService() throws Exception {
		checkSadApi();
		
		return sadPedidosService;
	}
	
	public static VehiculosApi getSadVehiculosService() throws Exception {
		checkSadApi();
		
		return sadVehiculosService;
	}	
}
