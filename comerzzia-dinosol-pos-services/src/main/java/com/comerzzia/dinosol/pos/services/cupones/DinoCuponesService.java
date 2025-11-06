package com.comerzzia.dinosol.pos.services.cupones;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.api.client.loyalty.LoyaltyOperationsApi;
import com.comerzzia.dinosol.api.client.loyalty.model.CouponRedeemData;
import com.comerzzia.dinosol.api.client.loyalty.model.LoyaltySaleOperation;
import com.comerzzia.dinosol.pos.persistence.promociones.DinoPromocionesMapper;
import com.comerzzia.dinosol.pos.services.codbarrasesp.DinoCodBarrasEspecialesServices;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionPromociones;
import com.comerzzia.dinosol.pos.services.core.sesion.coupons.types.CouponTypeDTO;
import com.comerzzia.dinosol.pos.services.cupones.offline.CouponRedeemOfflineDto;
import com.comerzzia.dinosol.pos.services.cupones.offline.LoyaltySaleOperationOfflineDto;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.cupones.CuponGeneradoDto;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.cupones.CuponesServices;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesBuilder;
import com.comerzzia.pos.services.promociones.PromocionesBuilderException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@SuppressWarnings("deprecation")
@Component
@Primary
public class DinoCuponesService extends CuponesServices {
	
	private Logger log = Logger.getLogger(DinoCuponesService.class);
	
	public static String ID_PROMOCION_SAP = "ID_PROMOCION_SAP";

	private static Long ID_TIPO_DOC_REDENCION_CUPON_OFFLINE = 1000040L;
	private static String ID_CONTADOR_REDENCION_CUPON_OFFLINE = "ID_REDENCION";
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private DinoCodBarrasEspecialesServices codBarrasEspecialesServices;
	
	@Autowired
	private DinoPromocionesMapper dinoPromocionesMapper;
	
	@Autowired
	private PromocionesBuilder promocionesBuilder;
	
	@Autowired
	private ComerzziaApiManager apiManager;
	
	@Autowired
	private TicketsService ticketsService;
	
	@Override
	public Long getIdPromocionAplicacionDtoFuturo(String codigo) throws CuponesServiceException {
		if (codigo == null || codigo.length() < 15) {
            return null;
        }
		
        if (!codigo.substring(0, 2).equals("95")) {
            return null;
        }
        
        Long idPromocionSap = Long.parseLong(codigo.substring(2, 14));
        
        log.debug("getIdPromocionAplicacionDtoFuturo() - Buscando la promoción de comerzzia con el ID de SAP: " + idPromocionSap);
        
        Long idPromocionComerzzia = getIdPromocionCzzDesdeIdSap(idPromocionSap);
        
        return idPromocionComerzzia;
	}

	public Long getIdPromocionCzzDesdeIdSap(Long idPromocionSap) throws CuponesServiceException {
		log.debug("getIdPromocionCzzDesdeIdSap() - Buscando la promoción de comerzzia asociada al ID de SAP: " + idPromocionSap);
		
		Long idPromocionComerzzia = null;
        
        for(Promocion promocion : sesion.getSesionPromociones().getPromocionesActivas().values()) {
        	String extension = promocion.getExtension(ID_PROMOCION_SAP);
			if(StringUtils.isNotBlank(extension) && extension.equals(idPromocionSap.toString())) {
        		idPromocionComerzzia = promocion.getIdPromocion();
        		break;
        	}
        }
        
        if(idPromocionComerzzia == null) {
        	throw new CuponesServiceException(I18N.getTexto("La promoción asociada al cupón no existe."));
        }

		log.debug("getIdPromocionCzzDesdeIdSap() - ID de la promoción de comerzzia encontrada: " + idPromocionComerzzia);
		return idPromocionComerzzia;
	}
	
	@SuppressWarnings("rawtypes")
    @Override
	public CuponGeneradoDto getCuponDtoFuturo(Long idPromocionGeneracion, Long idPromocionAplicacion, BigDecimal importeAhorro, ITicket ticket) throws CuponesServiceException {
		log.debug("getCuponDtoFuturo() - Generando cupón personalizado para Dinosol.");
		Promocion promocion = sesion.getSesionPromociones().getPromocionActiva(idPromocionAplicacion);
		
		if(promocion == null) {
			PromocionBean key = new PromocionBean();
			key.setUidActividad(sesion.getAplicacion().getUidActividad());
			key.setIdPromocion(idPromocionAplicacion);
			PromocionBean promocionBean = dinoPromocionesMapper.selectByPrimaryKey(key);
			if(promocionBean != null) {
				try {
					promocion = promocionesBuilder.create(promocionBean);
				}
				catch (PromocionesBuilderException e) {
					log.error("getPromocionSap() - No se ha podido generar la promoción: " + e.getMessage(), e);
				}
			}
		}
		
		if(promocion == null) {
			throw new CuponesServiceException("No se ha encontrado la promoción " + idPromocionAplicacion);
		}
		
		GeneratedCouponDto newCoupon = new GeneratedCouponDto(null, importeAhorro);
		newCoupon.setPromotionId(promocion.getIdPromocion());
		newCoupon.setStartDate(new Date());
		newCoupon.setEndDate(promocion.getFechaFin());
		newCoupon.setCouponTypeCode("DEFAULT");
		
		String codCupon = getCouponCodeAleatory(newCoupon);
		
		log.debug("getCuponDtoFuturo() - Generado el siguiente cupón: " + codCupon);
		
		importeAhorro = BigDecimalUtil.redondear(importeAhorro);
		
	    return new CuponGeneradoDto(codCupon, importeAhorro);
	}
	
	@Override
	public BigDecimal getImporteDescuentoCupon(String codigo) {
		if (StringUtils.isBlank(codigo)) {
            return null;
        }
		
        if (!codigo.substring(0, 2).equals("95")) {
            return null;
        }
        
        String importe = codigo.substring(14, 19);
        
		return new BigDecimal(importe).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
	}
	
	@SuppressWarnings("rawtypes")
    @Override
	public CuponGeneradoDto getCodigoCuponAutogenerado(Long idPromocionGeneracion, Long idPromocionAplicacion, DocumentoPromocionable documento) throws CuponesServiceException {
		return getCuponDtoFuturo(idPromocionGeneracion, idPromocionAplicacion, null, (ITicket) documento);
	}
	
	public boolean isCupon(String codidoCupon) {
		
		boolean resultado = false;
		
		try {
			CodigoBarrasBean codBarrasEspecial = codBarrasEspecialesServices.esCodigoBarrasEspecial(codidoCupon);
			if(codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("FLYER") && codidoCupon.length() == 13) {
				resultado = true;
			}
			
			if(!resultado) {
				boolean esCuponNuevo = ((DinoSesionPromociones) sesion.getSesionPromociones()).isCouponWithPrefix(codidoCupon);
				boolean esCuponAntiguo = esCuponAntiguo(codidoCupon);
				
				resultado = esCuponNuevo || esCuponAntiguo;
			}
		}
		catch(Exception e) {
			log.error("isCupon() - Ha habido un error al comprobar si el código es un cupón: " + e.getMessage(), e);
		}
		
		return resultado;
	}
	
	@Override
	public void compruebaUsoCupon(String codigo) throws CuponUseException, CuponesServiceException {
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void registraUsoCupones(TicketVenta ticket) {
		try {
			if(ticket.getIdTicket() == null) {
				log.debug("registraUsoCupones() - El ticket no tiene ID así que se solicita para poder mantener la traza de los cupones usados.");
				ticketsService.setContadorIdTicket(ticket);
			}
			
			log.debug("registraUsoCupones() - Se van a redimir los cupones usados.");
			
			List<CustomerCouponDTO> cuponesLeidos = ((DinoCabeceraTicket) ticket.getCabecera()).getCuponesLeidos();
			
			List<CouponRedeemData> redeemedCoupons = new ArrayList<CouponRedeemData>();
			for(CuponAplicadoTicket cupon : (List<CuponAplicadoTicket>) ticket.getCuponesAplicados()) {
				String codigoCupon = cupon.getCodigo();
				
				boolean hayQueRedimir = true;
				for(CustomerCouponDTO cuponLeido : cuponesLeidos) {
					if(codigoCupon.equals(cuponLeido.getCouponCode()) && cuponLeido.isCuponAntiguo()) {
						hayQueRedimir = false;
					}
				}
				
				if(hayQueRedimir) {
					CouponRedeemData redeemedCoupon = new CouponRedeemData();
					redeemedCoupon.setCouponCode(codigoCupon);
					redeemedCoupon.setDiscount(cupon.getImporteTotalAhorrado());
					redeemedCoupon.setSaleAmount(ticket.getTotales().getTotal());
					redeemedCoupons.add(redeemedCoupon);
				}
			}
			
			if(!redeemedCoupons.isEmpty()) {
				LoyaltySaleOperation loyaltySaleOperation = new LoyaltySaleOperation();
				
				String codAlmacen = ticket.getTienda().getCodAlmacen();
				loyaltySaleOperation.setDate(ticket.getFecha());
				loyaltySaleOperation.setLockByTerminalId(codAlmacen+ticket.getCodCaja());
				
				FidelizacionBean datosFidelizado = ticket.getCabecera().getDatosFidelizado();
				String numTarjetaFidelizado = datosFidelizado != null ? datosFidelizado.getNumTarjetaFidelizado() : "0";
				loyaltySaleOperation.setLoyalCustomerId(numTarjetaFidelizado);
				
				loyaltySaleOperation.setStoreId(codAlmacen);
				loyaltySaleOperation.setTicketUid(ticket.getUidTicket());
				loyaltySaleOperation.setTillId(sesion.getSesionCaja().getUidDiarioCaja());
				loyaltySaleOperation.setReedemCoupons(redeemedCoupons);
		    	
		    	DatosSesionBean datosSesion = new DatosSesionBean();
				datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
				datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
				datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
				LoyaltyOperationsApi api = apiManager.getClient(datosSesion, "LoyaltyOperationsApi");
				
				log.debug("registraUsoCupones() - Request: " + loyaltySaleOperation);
				
				api.newLoyaltySaleOperation(loyaltySaleOperation);
			}
		}
		catch(Exception e) {
			log.error("registraUsoCupones() - Ha habido un error al redimir los cupones: " + e.getMessage(), e);
			
			saveDocumentOffline(ticket);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private void saveDocumentOffline(TicketVenta ticket) {
		SqlSession sqlSession = new SqlSession();
		
		try {
			log.debug("saveDocumentOffline() - Se va a guardar el documento de procesamiento offline.");
			
			LoyaltySaleOperationOfflineDto loyaltySaleOperation = createLoyaltySalesOperationDto(ticket);
			
			if(loyaltySaleOperation != null) {
				TicketBean ticketOffline = createTicketReedemCoupons(ticket, loyaltySaleOperation);
				saveTicketReedemCoupons(sqlSession, ticketOffline);
			}
		}
		catch(Exception e) {
			log.error("saveDocumentOffline() - Ha habido un error al guardar el documento: " + e.getMessage(), e);
			
			sqlSession.rollback();
		}
		finally {
			sqlSession.close();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected LoyaltySaleOperationOfflineDto createLoyaltySalesOperationDto(TicketVenta ticket) {
		LoyaltySaleOperationOfflineDto loyaltySaleOperation = null;
		List<CouponRedeemOfflineDto> redeemedCoupons = new ArrayList<CouponRedeemOfflineDto>();
		for(CuponAplicadoTicket cupon : (List<CuponAplicadoTicket>) ticket.getCuponesAplicados()) {
			String codigoCupon = cupon.getCodigo();
			
			CouponRedeemOfflineDto redeemedCoupon = new CouponRedeemOfflineDto();
			redeemedCoupon.setCouponCode(codigoCupon);
			redeemedCoupon.setDiscount(cupon.getImporteTotalAhorrado());
			redeemedCoupon.setSaleAmount(ticket.getTotales().getTotal());
			redeemedCoupons.add(redeemedCoupon);
		}
		
		if(!redeemedCoupons.isEmpty()) {
			loyaltySaleOperation = new LoyaltySaleOperationOfflineDto();
			
			String codAlmacen = ticket.getTienda().getCodAlmacen();
			loyaltySaleOperation.setDate(ticket.getFecha());
			loyaltySaleOperation.setLockByTerminalId(codAlmacen+ticket.getCodCaja());
			
			FidelizacionBean datosFidelizado = ticket.getCabecera().getDatosFidelizado();
			String numTarjetaFidelizado = datosFidelizado != null ? datosFidelizado.getNumTarjetaFidelizado() : "";
			numTarjetaFidelizado = StringUtils.isNotBlank(numTarjetaFidelizado) ? numTarjetaFidelizado : "0";
			
			loyaltySaleOperation.setLoyalCustomerId(numTarjetaFidelizado);
			loyaltySaleOperation.setStoreId(codAlmacen);
			loyaltySaleOperation.setTicketUid(ticket.getUidTicket());
			loyaltySaleOperation.setTillId(sesion.getSesionCaja().getUidDiarioCaja());
			loyaltySaleOperation.setReedemCoupons(redeemedCoupons);
			
		}
		return loyaltySaleOperation;
	}

	protected void saveTicketReedemCoupons(SqlSession sqlSession, TicketBean ticketOffline) throws TicketsServiceException {
		sqlSession.openSession(SessionFactory.openSession());
		ticketsService.insertarTicket(sqlSession, ticketOffline, false);
		sqlSession.commit();
	}

	@SuppressWarnings({ "rawtypes" })
	protected TicketBean createTicketReedemCoupons(TicketVenta ticket, LoyaltySaleOperationOfflineDto loyaltySaleOperation) throws ContadorServiceException, MarshallUtilException {
		TicketBean ticketOffline = new TicketBean();
		
		String uidActividad = sesion.getAplicacion().getUidActividad();
		ticketOffline.setUidActividad(uidActividad);
		
		String uid = UUID.randomUUID().toString();
		ticketOffline.setUidTicket(uid);
		ticketOffline.setLocatorId(uid);
		
		String codalm = sesion.getAplicacion().getCodAlmacen();
		ticketOffline.setCodAlmacen(codalm);
		
		String codcaja = sesion.getAplicacion().getCodCaja();
		ticketOffline.setCodcaja(codcaja);
		
		ticketOffline.setIdTipoDocumento(ID_TIPO_DOC_REDENCION_CUPON_OFFLINE);
		
		ticketOffline.setCodTicket("*");
		ticketOffline.setFirma("*");
		ticketOffline.setSerieTicket("*");
		
		Date fecha = new Date();
		ticketOffline.setFecha(fecha);
		
		Long idTicket = servicioContadores.obtenerValorContador(ID_CONTADOR_REDENCION_CUPON_OFFLINE, uidActividad);
		ticketOffline.setIdTicket(idTicket);
		
		byte[] xml = MarshallUtil.crearXML(loyaltySaleOperation);
		log.debug("createTicketReedemCoupons() - XML de procesamiento offline: " + new String(xml));
		ticketOffline.setTicket(xml);
		return ticketOffline;
	}

	protected String getCouponCodeAleatory(GeneratedCouponDto newCoupon) {
		CouponTypeDTO couponType = ((DinoSesionPromociones) sesion.getSesionPromociones()).getCouponTypeDTO(newCoupon.getCouponTypeCode());
		String couponBody = getCouponBody(newCoupon);

		String value = RandomStringUtils.random(8, "0123456789ABCDEFGHJKLMNOPQRSTVWXYZ");
		String couponCode = formatCouponCode(value, couponType.getPrefix(), couponBody, couponType.getMaxLength());

		return couponCode;
	}
	
	protected String getCouponBody(GeneratedCouponDto newCoupon) {
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");

		String promotionId = StringUtils.leftPad(newCoupon.getPromotionId().toString(), 10, "0");
		String balance = StringUtils.leftPad(newCoupon.getImporteCupon() == null ? "0" : newCoupon.getImporteCupon().multiply(new BigDecimal(100)).abs().toBigInteger().toString(), 5, "0");
		String startDate = (newCoupon.getStartDate() != null) ? df.format(newCoupon.getStartDate()) : "000000";
		String endDate = (newCoupon.getEndDate() != null) ? df.format(newCoupon.getEndDate()) : "000000";

		return promotionId + balance + startDate + endDate;
	}

	protected String formatCouponCode(String value, String prefix, String suffix, Integer maxLength) {
		return StringUtils.join(new String[] { prefix, suffix, StringUtils.leftPad(value.toString(), maxLength, "0") });
	}

	public boolean esCuponAntiguo(String codigo) {
		log.debug("esCuponAntiguo() - Comprobando si el código introducido es un código de cupón de los antiguos.");
		
		try {
			log.debug("esCuponAntiguo() - Comprobando si existe la promoción.");
			getIdPromocionAplicacionDtoFuturo(codigo);
	
			log.debug("esCuponAntiguo() - Comprobando el importe.");
			BigDecimal importe = getImporteDescuentoCupon(codigo);
			if(importe == null || BigDecimalUtil.isMenorOrIgualACero(importe)) {
				return false;
			}
	
			log.debug("esCuponAntiguo() - Comprobando las fechas.");
			
			String fechaInicio = codigo.substring(19, 25);		
			String fechaFin = codigo.substring(25, 31);
			
			SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
			
			format.parse(fechaInicio);
			format.parse(fechaFin);
		}
		catch(Exception e) {
			log.debug("esCuponAntiguo() - Validación con error: " + e.getMessage());
			return false;
		}
		
		return true;
	}

}
