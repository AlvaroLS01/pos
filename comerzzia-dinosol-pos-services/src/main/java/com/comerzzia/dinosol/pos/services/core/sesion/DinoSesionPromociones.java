package com.comerzzia.dinosol.pos.services.core.sesion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.api.errorhandlers.ApiClientException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.dinosol.api.client.loyalty.CouponsApi;
import com.comerzzia.dinosol.api.client.loyalty.model.CouponDTO;
import com.comerzzia.dinosol.pos.services.core.sesion.coupons.types.CouponTypeDTO;
import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionPromocionesDto;
import com.comerzzia.dinosol.pos.services.promociones.opciones.OpcionesPromocionService;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.PromocionPuntosBPBean;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.cupones.PromoAvailableByCoupon;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.cupones.DinoCuponEmitidoTicket;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.cupones.CuponAplicationException;
import com.comerzzia.pos.services.cupones.CuponGeneradoDto;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.promociones.ComparadorTipoPromocion;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.promociones.tipos.DatosCuponDtoFuturo;
import com.comerzzia.pos.services.promociones.tipos.PromocionLinea;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPrecioDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocion;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import feign.FeignException;
import com.comerzzia.pos.util.format.FormatUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import system.io.IOException;

@Component
@Primary
public class DinoSesionPromociones extends SesionPromociones {
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private ComerzziaApiManager apiManager;

    private Integer lastCouponValidationStatus;
    private String lastCouponValidationMessage;
	
	@Autowired
	private OpcionesPromocionService opcionesPromocionService;
	
	protected Map<String, CouponTypeDTO> couponsType;

	@Override
    public void aplicarPromociones(DocumentoPromocionable documento, boolean limpiarCupones) {
		 // Reseteamos promociones
		documento.getPromociones().clear();
        documento.resetPuntos();
        if (limpiarCupones){
        	documento.getCuponesAplicados().clear();
            for (Long idPromocion : promocionesTemporalesPorCupon){
            	removePromocionActiva(idPromocion);
            	documento.getExtensiones().remove(idPromocion.toString());
            }
            promocionesTemporalesPorCupon.clear();
        }
        for (LineaDocumentoPromocionable lineaTicket : documento.getLineasDocumentoPromocionable()) {
            lineaTicket.resetPromociones();
            lineaTicket.resetPromocionesAplicables();
            lineaTicket.resetPromocionesCandidatas();
        }

        // Aplicamos primero promociones de precio
        aplicarPromocionesPrecio(documento, promocionesPrecioExclusivas);
        aplicarPromocionesPrecio(documento, promocionesPrecio);
        
        // Aplicamos de forma óptima promociones de línea
        aplicarPromocionesLineasOptimas(documento, promocionesLineasExclusivas);
        aplicarPromocionesLineasOptimas(documento, promocionesLineas);

        // Aplicamos promociones de cabecera
        aplicarPromocionesCabecera(documento, promocionesCabeceraExclusiva);
        aplicarPromocionesCabecera(documento, promocionesCabecera);
    }
	
	@Override
	protected void aplicarPromocionesPrecio(DocumentoPromocionable documento, Map<Long, PromocionPrecioDetalles> promoCandidatas) {
		for (LineaDocumentoPromocionable lineaTicket : documento.getLineasDocumentoPromocionable()) {
	        if (!lineaTicket.isAdmitePromociones()) {
		        continue;
	        }
        	if (lineaTicket.tieneDescuentoManual()){
            	continue;
            }
            if (lineaTicket.tieneCambioPrecioManual()){ 
            	continue;
            }
            
        	DetallePromocion detallePromocionPrecioOptima = null;
        	
        	for (PromocionPrecioDetalles promocion : promoCandidatas.values()) {
		        if (!promocion.isAplicable(documento)) {
			        log.trace("aplicarPromocionesPrecio() - Promoción no aplicable por condiciones generales de activación y cabecera - " + promocion);
			        continue;
		        }else{
			        log.trace("aplicarPromocionesPrecio() - Promoción aplicable - " + promocion);
		        }

		        try {
	            	DetallePromocion detalleCandidato = promocion.getDetalleAplicable(lineaTicket.getCodArticulo(), lineaTicket.getDesglose1(), lineaTicket.getDesglose2());
	            	if (detalleCandidato == null || !promocion.isAplicable(documento) ||  !detalleCandidato.isAplicable()){ // el artículo no está registrado en esta promoción
	            		continue;
	            	}
	            	if (detallePromocionPrecioOptima == null // por ahora no tenemos otro candidato
	            			|| esMejorPromocion(detallePromocionPrecioOptima, detalleCandidato)){ // el candidato es mejor que el anterior 
	            		detallePromocionPrecioOptima = detalleCandidato;
	            	}
	            } catch (Exception e) {
	            	log.error("aplicarPromocionesPrecio() Error aplicando promoción " + promocion.getIdPromocion() + ": " + e.getMessage(), e);
	            }	            	
            }
        	if (detallePromocionPrecioOptima != null){
        		((PromocionPrecioDetalles)detallePromocionPrecioOptima.getPromocion()).aplicaPromocionPrecioLinea(documento, lineaTicket);
        	}
        }
	}

	protected boolean esMejorPromocion(DetallePromocion detallePromocionPrecioOptima, DetallePromocion detalleCandidato) {
		int diasMejorPromocion = calcularDiasEntre(detallePromocionPrecioOptima.getFechaInicio(), new Date());
		int diasPromocionActual = calcularDiasEntre(detalleCandidato.getFechaInicio(), new Date());
		
		if(diasPromocionActual < diasMejorPromocion) {
			return true;
		}
		else if(diasPromocionActual > diasMejorPromocion) {
			return false;
		}
		else {
			return BigDecimalUtil.isMenor(detalleCandidato.getPrecioTotal(), detallePromocionPrecioOptima.getPrecioTotal());
		}
	}
	
	public int calcularDiasEntre(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
	
	

	public boolean aplicarCupon(CustomerCouponDTO coupon, DocumentoPromocionable ticket) throws CuponUseException, CuponesServiceException, CuponAplicationException {
		if (coupon.isValidationRequired()) {
			CouponDTO couponValidated = validateCoupon(coupon.getCouponCode());

			if (couponValidated != null) {
				coupon.setCouponCode(couponValidated.getCouponCode());
				
				Long idPromocion = ((DinoCuponesService) cuponesServices).getIdPromocionCzzDesdeIdSap(couponValidated.getPromotionId());
				coupon.setPromotionId(idPromocion);
				coupon.setBalance(couponValidated.getBalance());
			}
			else {
				log.warn("aplicarCupon()- The coupon could not be validated");
				throw new CuponUseException();
			}
		}

		Promocion promocion = getPromocionAplicacionCupon(coupon);
		if (promocion == null) {
			log.error("aplicarCupon() - The coupon promotion does not exist.");
			return false;
		}

		if (!promocion.isAplicable(ticket)) {
			log.debug("aplicarCupon() - La promoción a la que hace referencia el cupón no ha podido ser aplicada.");
			throw new CuponAplicationException();
		}

		// Añadimos la promoción como candidata a aplicarse
		if (promocion instanceof PromoAvailableByCoupon) {
			((PromoAvailableByCoupon) promocion).setCustomerCoupon(coupon);
		}
		else {
			promocion.setCodigoCupon(coupon.getCouponCode());
		}
		addPromocionNoActivaCupon(promocion.getIdPromocion(), promocion);

		// No aplicamos todas las promociones, solo la del cupón, para que no borre las promociones ya aplicadas
		promocion.aplicarPromocion(ticket);
		
		aplicarPromocionSiLinea(ticket, promocion);

		// Comprobamos si la promoción del cupón ha sido aplicada
		PromocionTicket documentoPromocion = ticket.getPromocion(promocion.getIdPromocion(), coupon.getCouponCode());
		
		if (documentoPromocion == null) {
			log.debug("aplicarCupon() - Cupón NO aplicado");
			removePromocionActiva(promocion.getIdPromocion());
			throw new CuponAplicationException();
		}
		
		eliminarCuponesNoAcumulables(coupon, ticket, promocion, documentoPromocion);

		List<CuponAplicadoTicket> cuponesEliminadosConflicto = new LinkedList<>();
		List<CuponAplicadoTicket> cuponesAplicados = ticket.getCuponesAplicados();
		ListIterator<CuponAplicadoTicket> listIterator = cuponesAplicados.listIterator();
		while (listIterator.hasNext()) {
			CuponAplicadoTicket cuponAplicadoTicket = listIterator.next();
			PromocionTicket promoCupon = ticket.getPromocion(cuponAplicadoTicket.getIdPromocion(), cuponAplicadoTicket.getCodigo());
			if (promoCupon == null) {
				cuponesEliminadosConflicto.add(cuponAplicadoTicket);
				listIterator.remove();
			}
			promoCupon = ticket.getPromocion(cuponAplicadoTicket.getIdPromocion());
			if (promoCupon == null) {
				removePromocionActiva(cuponAplicadoTicket.getIdPromocion());
			}
		}
        
		boolean promocionAplicada = seHaAplicadoCupon(coupon, ticket);
		if(promocionAplicada) {
	        CuponAplicadoTicket cupon = new CuponAplicadoTicket(coupon.getCouponCode(), promocion);
	        cupon.setTextoPromocion(documentoPromocion.getTextoPromocion());
	        cupon.setImporteTotalAhorrado(documentoPromocion.getImporteTotalAhorro());
	        ticket.addCuponAplicado(cupon);
		}

		return true;
	}

	private void aplicarPromocionSiLinea(DocumentoPromocionable ticket, Promocion promocion) {
		if(promocion instanceof PromocionLinea) {
			if (promocion.isAplicable(ticket)) {
				((PromocionLinea) promocion).analizarLineasAplicables(ticket);
				
				for (LineaDocumentoPromocionable lineaTicket : ticket.getLineasDocumentoPromocionable()) {
		        	if (!lineaTicket.isAdmitePromociones()) {
				        continue;
			        }
		            if (!lineaTicket.tienePromocionesAplicables()){
		                continue;
		            }
		            if (lineaTicket.getPromocionesAplicables().size()>0) { 
		                for (PromocionLineaCandidataTicket promoCandidata : lineaTicket.getPromocionesAplicables()) {
		                	if(promoCandidata.getPromocion().getIdPromocion().equals(promocion.getIdPromocion())) {
		                		promoCandidata.aplicarPromocion(ticket);
		                	}
		                }
		            }
		        }
			}
		}
	}

	private boolean seHaAplicadoCupon(CustomerCouponDTO coupon, DocumentoPromocionable ticket) {
		boolean promocionAplicada = false;
		for(PromocionTicket promocionTicket : ticket.getPromociones()) {
			if(promocionTicket.getCodAcceso().equals(coupon.getCouponCode())) {
				promocionAplicada = true;
				break;
			}
		}
		return promocionAplicada;
	}

	private void eliminarCuponesNoAcumulables(CustomerCouponDTO coupon, DocumentoPromocionable ticket, Promocion promocion, PromocionTicket documentoPromocion) {
		if(promocion instanceof PromoAvailableByCoupon && !((PromoAvailableByCoupon) promocion).permiteCuponesAcumulables()) {
			List<PromocionTicket> promocionesAplicadas = new ArrayList<>(ticket.getPromociones());
			for(PromocionTicket promocionAplicada : promocionesAplicadas) {
				boolean esMismaPromocion = promocionAplicada.getIdPromocion().equals(promocion.getIdPromocion());
				boolean esOtroCupon = StringUtils.isNotBlank(promocionAplicada.getCodAcceso()) && !promocionAplicada.getCodAcceso().equals(coupon.getCouponCode());
				if(esMismaPromocion && esOtroCupon) {
					BigDecimal importeAhorroCuponAnterior = promocionAplicada.getImporteTotalAhorro();
					BigDecimal importeAhorroCuponActual = documentoPromocion.getImporteTotalAhorro();
					
					if(BigDecimalUtil.isMayorOrIgual(importeAhorroCuponActual, importeAhorroCuponAnterior)) {
						((DinoTicketVentaAbono) ticket).removePromocion(promocionAplicada);
					}
					else {
						((DinoTicketVentaAbono) ticket).removePromocion(documentoPromocion);
					}
				}
			}
		}
	}

	public CouponDTO validateCoupon(String code) throws ApiClientException {
		lastCouponValidationStatus = null;
		lastCouponValidationMessage = null;

		try {
			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
			datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
			datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
			CouponsApi api = apiManager.getClient(datosSesion, "CouponsApi");

			log.debug("validateCoupon() - Consultando en la API el cupón: " + code);

			CouponDTO validation = api.validateCoupon(code, "0");

			log.debug("validateCoupon() - Resultado de la API: " + validation);

			return validation;
		}
		catch (ApiClientException e) {
			log.error("validateCoupon() - Error while validating coupon: " + e.getMessage(), e);
			throw e;
		}
		catch (Exception e) {
                        if (e instanceof FeignException) {
                                FeignException feignException = (FeignException) e;
                                lastCouponValidationStatus = feignException.status();
                                String content = null;
                                try {
                                        content = feignException.contentUTF8();
                                }
                                catch (Exception contentException) {
                                        log.debug("validateCoupon() - Unable to extract coupon validation content: " + contentException.getMessage());
                                }
                                if (StringUtils.isNotBlank(content)) {
                                        lastCouponValidationMessage = content;
                                }
                                else {
                                        lastCouponValidationMessage = feignException.getMessage();
                                }
                        }

			log.error("validateCoupon() - Error while validating coupon: " + e.getMessage(), e);

			return null;
		}
	}

	public Integer getLastCouponValidationStatus() {
		return lastCouponValidationStatus;
	}

	public String getLastCouponValidationMessage() {
		return lastCouponValidationMessage;
	}

	protected Promocion getPromocionAplicacionCupon(CustomerCouponDTO coupon) throws CuponUseException, CuponesServiceException {
		if (coupon.getPromotionId() != null) {
			Promocion promocion = promocionesActivas.get(coupon.getPromotionId());
			return promocion;
		}

		// Comprobamos si el código es un cupón fijo
		log.debug("getPromocionAplicacionCupon() - Intentando aplicar cupón con código: " + coupon.getCouponCode());
		Promocion promocion = promocionesActivasPorCuponFijo.get(coupon.getCouponCode());
		if (promocion != null) {
			log.debug("getPromocionAplicacionCupon() - El código pertenece a cupón fijo de promoción activa. Intentando aplicar promoción: " + promocion);
			return promocion;
		}

		// Comprobamos si el código es un cupón fijo con variables
		for (String expresionRegular : promocionesActivasPorCuponFijoVariable.keySet()) {
			if (coupon.getCouponCode().matches(expresionRegular)) {
				promocion = promocionesActivasPorCuponFijoVariable.get(expresionRegular);
				log.debug("getPromocionAplicacionCupon() - El código pertenece a cupón fijo (con variables) de promoción activa. Intentando aplicar promoción: " + promocion);
				return promocion;
			}
		}

		// Comprobamos si el código tiene formato de código autogenerado por el POS
		Long idPromocion = cuponesServices.getPromocionCodigoCuponAutogenerado(coupon.getCouponCode());
		if (idPromocion == null) {
			idPromocion = cuponesServices.getIdPromocionAplicacionDtoFuturo(coupon.getCouponCode());
			if (idPromocion == null) {
				log.debug("getPromocionAplicacionCupon() - El código no pertenece a cupón fijo de promoción activa ni tiene formato de cupón generado por el POS.");
				return null;
			}
		}
		log.debug("getPromocionAplicacionCupon() - El código tiene formato de cupón autogenerado para la promoción con id:  " + idPromocion);
		// Buscamos e intentamos aplicar la promoción
		promocion = promocionesActivasPorCuponNumerado.get(idPromocion);
		if (promocion == null) {
			log.debug("getPromocionAplicacionCupon() - La promoción a la que hace referencia el cupón autogenerado no existe o no está activa. ");
			return null;
		}

		return promocion;
	}

	@Override
	protected void addPromocionNoActivaCupon(Long idPromocion, Promocion promocion) {		
		super.addPromocionNoActivaCupon(idPromocion, promocion);

		if (promocion instanceof PromoAvailableByCoupon && ((PromoAvailableByCoupon) promocion).getCustomerCoupon() != null) {
			promocionesTemporalesPorCupon.add(promocion.getIdPromocion());
		}
	}

	public CouponTypeDTO getCouponTypeDTO(String couponTypeCode) {
		return couponsType.get(couponTypeCode);
	}

	public CouponTypeDTO getCouponTypeByPrefix(String couponCode) {
		for (CouponTypeDTO couponType : couponsType.values()) {
			if (couponCode.startsWith(couponType.getPrefix())) {
				return couponType;
			}
		}

		return null;
	}
	
	protected void readCouponsTypeFile() {
		try {
			couponsType = new HashMap<String, CouponTypeDTO>();

			String filePath = "entities/ly_coupons_types_tbl.json";
			log.debug("readCouponsTypeFile() - Reading file " + filePath);

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

			List<CouponTypeDTO> coupons = readJson(json.getBytes());

			for (CouponTypeDTO couponType : coupons) {
				couponsType.put(couponType.getCouponTypeCode(), couponType);
			}
		}
		catch (Exception e) {
			log.warn("readCouponsTypeFile() - No se ha encontrado el fichero ly_coupons_types_tbl.json: " + e.getMessage());
		}

	}

	public <T> T readJson(byte[] json) throws java.io.IOException {
		if (json == null) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		// jaxb annotations support
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		objectMapper.registerModule(module);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return objectMapper.readValue(new String(json), new TypeReference<List<CouponTypeDTO>>(){
			});
		}
		catch (JsonParseException | JsonMappingException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void actualizarPromocionesActivas() throws PromocionesServiceException {		
		if(couponsType == null) {
        	readCouponsTypeFile();
        }
		
		super.actualizarPromocionesActivas();
		
		eliminarPromocionesOpcionesSeleccionables();
	}
	
	private void eliminarPromocionesOpcionesSeleccionables() {
		List<OpcionPromocionesDto> opciones = opcionesPromocionService.getOpciones();
		
		if(opciones != null && !opciones.isEmpty()) {
			for(OpcionPromocionesDto opcion : opciones) {
				log.debug("restrigirPromocionesOpcionesSeleccionables() - Se restringen las promociones de la opción: " + opcion.getTitulo());
				for(String idPromocionSap : opcion.getPromociones()) {
					try {
						Long idPromocionCzz = ((DinoCuponesService) cuponesServices).getIdPromocionCzzDesdeIdSap(new Long(idPromocionSap));
						
						removePromocionActiva(idPromocionCzz);
				    	promocionesFinales.remove(idPromocionCzz);
				    	promocionesFinalesExclusivas.remove(idPromocionCzz);
					}
					catch (Exception e) {
						log.error("restrigirPromocionesOpcionesSeleccionables() - Ha habido un error al recuperar la promoción en comerzzia desde el ID de SAP " + idPromocionSap + ": " + e.getMessage(), e);
					}
				}
			}
		}
	}

	public boolean isCouponWithPrefix(String code) {
		boolean isCoupon = false;
		
		if(couponsType != null) {
			for(CouponTypeDTO couponType : couponsType.values()) {
				if(code.startsWith(couponType.getPrefix())) {
					isCoupon = true;
					break;
				}
			}
		}
		
		return isCoupon;
	}
	
	public void aplicarPromocionesBp(TicketVentaAbono ticketPrincipal) {
		aplicarPromocionesTipoBp(ticketPrincipal, promocionesCabecera);
		aplicarPromocionesTipoBp(ticketPrincipal, promocionesCabeceraExclusiva);
	}
    
    protected void aplicarPromocionesTipoBp(DocumentoPromocionable documento, Map<Long, Promocion> promoCandidatas) {
        Collection<Promocion> promociones = promoCandidatas.values();
        List<Promocion> promocionesOrdenadas = new ArrayList<>(promociones);
        Collections.sort(promocionesOrdenadas, new ComparadorTipoPromocion());
		for (Promocion promocion : promociones) {
			if(promocion instanceof PromocionPuntosBPBean) {
	            if (!promocion.isAplicable(documento)) {
	                log.trace(promocion + " aplicarPromocionesTipoBp() - Promoción no aplicable por condiciones generales de Activación y Fecha/Hora");
	                continue;
	            }
	            try {
	               promocion.aplicarPromocion(documento);
	            } catch (Exception e) {
	            	log.error("aplicarPromocionesTipoBp() Error aplicando promoción " + promocion.getIdPromocion() + ": " + e.getMessage(), e);
	            }
			}
        }
    }
    
    public void aplicarOpcionPromociones(OpcionPromocionesDto opcion, DocumentoPromocionable documento) {
    	log.debug("aplicarOpcionPromociones() - Se va a aplicar la opción " + opcion.getTitulo());
    	
    	try {
	    	for(String idPromocionSap : opcion.getPromociones()) {
	    		Long idPromocionCzz = ((DinoCuponesService) cuponesServices).getIdPromocionCzzDesdeIdSap(new Long(idPromocionSap));
	    		Promocion promocion = getPromocionActiva(idPromocionCzz);
	    		
	    		log.debug("aplicarOpcionPromociones() - Aplicando promoción " + promocion.getIdPromocion());
	    		
	    		if(promocion instanceof PromocionLinea) {
    	    		log.debug("aplicarOpcionPromociones() - Promoción de línea. Se añadirá a las candidatas y se aplicará si es la más beneficiosa.");
	    			Map<Long, PromocionLinea> copiaPromocionesLinea = new HashMap<Long, PromocionLinea>(promocionesLineas);
	    			copiaPromocionesLinea.put(promocion.getIdPromocion(), (PromocionLinea) promocion);
	    			aplicarPromocionesLineasOptimas(documento, copiaPromocionesLinea);
	    		}
	    		else {
	    			if(promocion.isAplicable(documento)) {
	    	    		log.debug("aplicarOpcionPromociones() - Promoción de cabecera aplicable. Se aplicará.");
	    				promocion.aplicarPromocion(documento);
	    			}
	    			else {
	    				log.error("aplicarOpcionPromociones() - La promoción " + promocion.getIdPromocion() + " no es aplicable en el ticket");
	    				log.error("aplicarOpcionPromociones() - Colectivos asociados al ticket: " + documento.getDatosFidelizado().getCodColectivos());
	    			}
	    		}
	    	}
	    	documento.getCabecera().getTotales().recalcular();
    	}
    	catch (Exception e) {
    		log.error("aplicarOpcionPromociones() - Ha habido un error al aplicar las promociones de la opción " + opcion.getTitulo() + ": " + e.getMessage(), e);
    	}
    }

    /* Para el SCO */
    @Override
	public boolean aplicarCupon(com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO coupon,
			DocumentoPromocionable ticket) throws CuponUseException, CuponesServiceException, CuponAplicationException {

		CustomerCouponDTO cuponDino = new CustomerCouponDTO();
		
		cuponDino.setActive(coupon.getActive());
		cuponDino.setBalance(coupon.getBalance());
		cuponDino.setCouponCode(coupon.getCouponCode());
		cuponDino.setCouponDescription(coupon.getCouponDescription());
		cuponDino.setCouponName(coupon.getCouponName());
		cuponDino.setCreationDate(coupon.getCreationDate());
		cuponDino.setCreationtDate(coupon.getCreationtDate());
		cuponDino.setEndDate(coupon.getEndDate());
//		cuponDino.setFromLoyaltyRequest();
		cuponDino.setImageUrl(coupon.getImageUrl());
		cuponDino.setLoyalCustomerId(coupon.getLoyalCustomerId());
		cuponDino.setManualSelection(coupon.getManualSelection());
		cuponDino.setPriority(coupon.getPriority());
		cuponDino.setPromotionId(coupon.getPromotionId());
		cuponDino.setStartDate(coupon.getStartDate());
		cuponDino.setValidationRequired(true);
//		cuponDino.setVersion();
		
//		try {
//			BeanUtils.copyProperties(cuponDino, coupon);
//		} catch (IllegalAccessException | InvocationTargetException e) {
//		}

		return aplicarCupon(cuponDino, ticket);
	}
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void generarCuponesDtoFuturo(ITicket ticket) {
		for (PromocionTicket promocion : (List<PromocionTicket>) ticket.getPromociones()) {
			if (promocion.getTipoDescuento().equals(Promocion.TIPO_DTO_A_FUTURO)) {
				DatosCuponDtoFuturo datosCupon = promocion.getPromocion().getDatosCupon();
				CuponGeneradoDto cupon = null;
				try {
					cupon = cuponesServices.getCuponDtoFuturo(promocion.getIdPromocion(), datosCupon.getIdPromocionAplicacion(), promocion.getImporteTotalAhorro(), ticket);
				}
				catch (Exception e) {
					log.error("generarCuponesDtoFuturo() - No se ha podido generar el código de cupón.");
					break;
				}

				DinoCuponEmitidoTicket cuponEmitido = new DinoCuponEmitidoTicket();
				cuponEmitido.setTituloCupon(datosCupon.getTitulo());

				String descripcionCupon = datosCupon.getDescripcion();
				if (StringUtils.isNotBlank(descripcionCupon)) {
					descripcionCupon = descripcionCupon.replaceAll("#IMPORTE#", FormatUtil.getInstance().formateaImporte(promocion.getImporteTotalAhorro()));
					cuponEmitido.setDescripcionCupon(descripcionCupon);
				}

				cuponEmitido.setCodigoCupon(cupon.getCodigoCupon());
				try {
					Date fechaFin = obtenerCuponFechaFin(datosCupon.getIdPromocionAplicacion());
					cuponEmitido.setFechaFin(fechaFin);
				}
				catch (Exception e) {
					log.error("generarCuponesDtoFuturo() - Error obteniendo fecha de fin del cupon: " + e.getMessage(), e);
				}
				cuponEmitido.setIdPromocionOrigen(promocion.getIdPromocion());
				cuponEmitido.setIdPromocionAplicacion(datosCupon.getIdPromocionAplicacion());
				cuponEmitido.setImporteCupon(cupon.getImporteCupon());

				((DinoTicketVentaAbono) ticket).addCuponEmitido(cuponEmitido);
			}
		}
	}

	public Date obtenerCuponFechaFin(Long idPromocionAplicacionCupon) throws PromocionesServiceException {
		log.debug("obtenerCuponFechaFin() - Obteniendo fecha de fin del cupon " + idPromocionAplicacionCupon);

		List<PromocionBean> promociones = promocionesService.consultarPromocionesActivas();
		for (PromocionBean promo : promociones) {
			if (promo.getIdPromocion().equals(idPromocionAplicacionCupon)) {
				return promo.getFechaFin();
			}
		}
		return null;
	}
}