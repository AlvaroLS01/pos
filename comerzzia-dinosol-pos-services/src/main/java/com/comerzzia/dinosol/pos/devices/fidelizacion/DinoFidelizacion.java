package com.comerzzia.dinosol.pos.devices.fidelizacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.log4j.Logger;

import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;
import com.comerzzia.dinosol.api.client.loyalty.CouponsApi;
import com.comerzzia.dinosol.api.client.loyalty.model.CouponDTO;
import com.comerzzia.dinosol.librerias.cryptoutils.CryptoUtils;
import com.comerzzia.dinosol.librerias.sherpa.client.segmentos.dto.BasicRequestDTO;
import com.comerzzia.dinosol.librerias.sherpa.client.segmentos.dto.FidelizadoDto;
import com.comerzzia.dinosol.librerias.sherpa.client.segmentos.servicios.SherpaService;
import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.documents.LocatorManagerImpl;
import com.comerzzia.dinosol.pos.services.sherpa.SherpaApiBuilder;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoCallback;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.ConsultaTarjetaFidelizadoException;
import com.comerzzia.pos.core.dispositivos.dispositivo.fidelizacion.IFidelizacion;
import com.comerzzia.pos.dispositivo.fidelizacion.Fidelizacion;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;

import javafx.stage.Stage;

public class DinoFidelizacion extends Fidelizacion {
	
	private static final String FIDELIZADO_EMAIL = "EMAIL";
	private static final String FIDELIZADO_TELEFONO = "TELEFONO";
	public static final String FIDELIZADO_TICKET_DIGITAL = "DinoFidelizacion.TicketDigital";
	public static final String FIDELIZADO_TARJETAS = "DinoFidelizacion.Tarjetas";

	private Logger log = Logger.getLogger(DinoFidelizacion.class);
	
	/* Para almacenar el código del porte y poder usarlo en varios sitios */
	private String codigoArticuloPorte;
	/* Para activar la reserva o no de SAD */
	private String integracionRutas;
	
	@Override
	protected void cargaConfiguracion(ConfiguracionDispositivo config) {
		super.cargaConfiguracion(config);
		try {
			XMLDocumentNode configNode = config.getConfiguracionModelo().getConfigConexion().getNodo(IFidelizacion.TAG_CONFIG_FIDELIZACION);
		    					    
			codigoArticuloPorte = configNode.getNodo("codigoArticuloPorte").getValue();
			integracionRutas = configNode.getNodo("integracionRutas").getValue();
		}
		catch (XMLDocumentNodeNotFoundException ex) {
			log.error("Error recuperando la información de configuración de fidelización:" + ex.getMessage(), ex);
		}
	}
	
	@Override
	public void cargarTarjetaFidelizado(String numTarjeta, Stage stage, DispositivoCallback<FidelizacionBean> callback) {
		String numeroTarjetaDesencriptado = CryptoUtils.decrypt(numTarjeta, LocatorManagerImpl.secretKey);
		String[] split = numeroTarjetaDesencriptado.split("-");
		numTarjeta = split[0];
		log.debug("cargarTarjetaFidelizado() - Número de tarjeta leído: " + numTarjeta);
		
	    super.cargarTarjetaFidelizado(numTarjeta, stage, callback);
	}

	
	@Override
	public FidelizacionBean consultarTarjetaFidelizado(String numeroTarjeta, String uidActividad) throws ConsultaTarjetaFidelizadoException {
		try {			
			FidelizacionBean tarjetaFidelizacion = null;
		    
		    Sesion sesion = ContextHolder.get().getBean(Sesion.class);
		    
		    SherpaService service = SherpaApiBuilder.getSherpaApiService();
		    
		    BasicRequestDTO request = new BasicRequestDTO();
		    request.setTipoCodigoTarjeta("qr");
		    request.setCodigoTarjeta(numeroTarjeta);
		    request.setCodigoTienda(sesion.getAplicacion().getCodAlmacen());
		    request.setTipoPOS(SherpaApiBuilder.getSherpaPosType());
		    request.setCodigoPOS(sesion.getAplicacion().getCodCaja());
		    
		    FidelizadoDto response = service.serviceCall(request);
		    
		    tarjetaFidelizacion = new FidelizacionBean();
		    tarjetaFidelizacion.setNombre(response.getNombre());
		    tarjetaFidelizacion.setApellido(response.getApellidos());
		    tarjetaFidelizacion.setDocumento(response.getNif());
		    tarjetaFidelizacion.setNumTarjetaFidelizado(response.getCodigoSherpa());
		    List<String> colectivos = response.getColectivos();
		    log.debug("consultarTarjetaFidelizado() - Colectivos del fidelizado: " + colectivos);
			tarjetaFidelizacion.setCodColectivos(colectivos);
		    
		    Map<String, Object> mapaAdicionales = new HashMap<String, Object>();
		    if(response.getTelefono() != null && !response.getTelefono().isEmpty()){
		    	mapaAdicionales.put(FIDELIZADO_TELEFONO, response.getTelefono());
		    }
		    if(response.getEmail() != null && !response.getEmail().isEmpty()){
		    	mapaAdicionales.put(FIDELIZADO_EMAIL, response.getEmail());
		    }
		    if(StringUtils.isNotBlank(response.getTipoTicket()) && response.getTipoTicket().equals("digital")) {
		    	mapaAdicionales.put(FIDELIZADO_TICKET_DIGITAL, true);
		    }
		    else {
		    	mapaAdicionales.put(FIDELIZADO_TICKET_DIGITAL, false);
		    }
		    List<String> tarjetas = response.getTarjetas();
			if(tarjetas != null && !tarjetas.isEmpty()){
				List<String> tarjetasTransformadas = new ArrayList<String>();
				for(String tarjeta : tarjetas) {
					tarjetasTransformadas.add(transformarCodigoTarjetaBp(tarjeta));
				}
		    	mapaAdicionales.put(FIDELIZADO_TARJETAS, tarjetasTransformadas);
		    	
		    	log.debug("consultarTarjetaFidelizado() - Tarjetas del fidelizado: " + tarjetas);
		    	log.debug("consultarTarjetaFidelizado() - Tarjetas (transformadas) del fidelizado: " + tarjetasTransformadas);
		    }
		    
		    if(!mapaAdicionales.isEmpty()){
		    	tarjetaFidelizacion.setAdicionales(mapaAdicionales);
		    }
		    if(response.getDireccion() != null && !response.getDireccion().isEmpty()){
		    	tarjetaFidelizacion.setDomicilio(response.getDireccion());
		    }
		    if(response.getMunicipio() != null && !response.getMunicipio().isEmpty()){
		    	tarjetaFidelizacion.setPoblacion(response.getMunicipio());
		    }
		    if(response.getCodigoPostal() != null && !response.getCodigoPostal().isEmpty()){
		    	tarjetaFidelizacion.setCp(response.getCodigoPostal());
		    }
		    if(response.getProvincia() != null && !response.getProvincia().isEmpty()){
		    	tarjetaFidelizacion.setProvincia(response.getProvincia());
		    }
		    
		    getCustomerCoupons(tarjetaFidelizacion);
		    
		    return tarjetaFidelizacion;
		}
		catch (Exception e) {
			throw new ConsultaTarjetaFidelizadoException(e);
		}
	}

	private String transformarCodigoTarjetaBp(String numeroTarjeta) {
		try {
			if(numeroTarjeta.startsWith("705690")) {
				numeroTarjeta = "002" + numeroTarjeta.substring(6, numeroTarjeta.length() - 1);
				numeroTarjeta = numeroTarjeta + new EAN13CheckDigit().calculate(numeroTarjeta);
			}
			else if (numeroTarjeta.startsWith("242")) {
				numeroTarjeta = "002" + numeroTarjeta.substring(3, numeroTarjeta.length() - 1);
				numeroTarjeta = numeroTarjeta + new EAN13CheckDigit().calculate(numeroTarjeta);
			}
		}
		catch (Exception e) {
			log.error("transformarCodigoTarjeta() - Ha habido un error al generar el número de tarjeta correcto de BP: " + e.getMessage(), e);
		}
		
		return numeroTarjeta;
	}

    public String getCodigoArticuloPorte() {
    	return codigoArticuloPorte;
    }
	
    public String getIntegracionRutas() {
    	return integracionRutas;
    }
    
	protected void getCustomerCoupons(FidelizacionBean fidelizado) {
		try {
			log.debug("getCustomerCoupons() - Querying coupons for customer " + fidelizado.getNumTarjetaFidelizado());

			Sesion sesion = SpringContext.getBean(Sesion.class);
			ComerzziaApiManager apiManager = SpringContext.getBean(ComerzziaApiManager.class);

			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
			datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
			datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
			CouponsApi api = apiManager.getClient(datosSesion, "CouponsApi");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("active", true);
			params.put("used", false);
			params.put("valid", true);
			params.put("validInFuture", false);
			params.put("includeAnonymousCoupons", false);
			params.put("manualSelection", true);
			
			List<CouponDTO> customerCoupons = api.getCustomerCoupons(fidelizado.getNumTarjetaFidelizado(), params);

			log.debug("getCustomerCoupons() - Result: " + customerCoupons);

			List<CustomerCouponDTO> availableCoupons = new ArrayList<CustomerCouponDTO>();
			for (CouponDTO coupon : customerCoupons) {
				DinoCuponesService cuponesServices = SpringContext.getBean(DinoCuponesService.class);
				Promocion promotion = null;
				try {
					Long idPromocion = cuponesServices.getIdPromocionCzzDesdeIdSap(coupon.getPromotionId());
					promotion = sesion.getSesionPromociones().getPromocionActiva(idPromocion);
				}
				catch(Exception e) {
					log.error("getCustomerCoupons() - Ha habido un error al consultar la promoción: " + e.getMessage());
				}
				if (promotion != null && promotion.isActiva()) {
					CustomerCouponDTO customerCouponDTO = new CustomerCouponDTO(coupon.getCouponCode(), false, CustomerCouponDTO.CUPON_NUEVO);

					customerCouponDTO.setBalance(coupon.getBalance());
					customerCouponDTO.setCouponDescription(coupon.getCouponDescription());
					customerCouponDTO.setCouponName(coupon.getCouponName());
					customerCouponDTO.setCreationDate(coupon.getCreationDate());
					customerCouponDTO.setCreationtDate(coupon.getCreationtDate());
					customerCouponDTO.setImageUrl(coupon.getImageUrl());
					customerCouponDTO.setManualSelection(coupon.getManualSelection());
					customerCouponDTO.setPriority(coupon.getPriority());
					customerCouponDTO.setPromotionId(coupon.getPromotionId());
					customerCouponDTO.setStartDate(coupon.getStartDate());
					customerCouponDTO.setEndDate(coupon.getEndDate());
					customerCouponDTO.setValidationRequired(true);
					customerCouponDTO.setFromLoyaltyRequest(true);

					availableCoupons.add(customerCouponDTO);
				}
				else {
					log.warn("getCustomerCoupons() - No se ha podido añadir el cupón con código " + coupon.getCouponCode() + " al no estar activa la promoción " + coupon.getPromotionId());
				}

			}

			fidelizado.putAdicional("coupons", availableCoupons);
		}
		catch (Exception e) {
			log.error("getCustomerCoupons() - Error querying coupons for customer " + fidelizado.getIdFidelizado() + ": " + e.getMessage(), e);
		}
	}

    
}
