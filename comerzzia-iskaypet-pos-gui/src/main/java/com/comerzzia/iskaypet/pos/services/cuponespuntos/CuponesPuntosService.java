package com.comerzzia.iskaypet.pos.services.cuponespuntos;

import com.comerzzia.iskaypet.pos.services.cupones.generation.IskaypetCouponsCodeGeneratorService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.cupones.IskaypetCuponEmitidoTicket;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ISK-216 GAP 50b Generación de cupones en POS
 * ISK-217 GAP 51b Cupón con diferencia
 * IER-176 - NO SE MUESTRA EN LA IMPRESIÓN DE CUPONES CÓDIGO DE BARRAS LARGOS
 */
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class CuponesPuntosService {
	
	private Logger log = Logger.getLogger(CuponesPuntosService.class);
	
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	SimpleDateFormat dfDescripcion = new SimpleDateFormat("dd/MM/yyyy");
	
	@Autowired
	protected VariablesServices variablesServices;
	@Autowired
    protected Sesion sesion;
	// IER-176 - NO SE MUESTRA EN LA IMPRESIÓN DE CUPONES CÓDIGO DE BARRAS LARGOS
	@Autowired
	protected IskaypetCouponsCodeGeneratorService couponsCodeGeneratorService;
	
	public static final String PUNTOS_CUPON_DIAS_VIGENCIA = "X_POS.PUNTOS_CUPON_DIAS_VIGENCIA";
	public static final String PUNTOS_EUROS = "X_POS.PUNTOS_EUROS";
	public static final String PUNTOS_GENERACION_CUPON = "X_POS.PUNTOS_GENERACION_CUPON";
	public static final String PUNTOS_PERIODO_CARENCIA = "X_POS.PUNTOS_PERIODO_CARENCIA";
	
	//LYT KWK EMISIÓN DE CUPONES A EMPLEADOS
	public static final String COLECTIVOS_FIDELIZADOS = "X_POS.COD_COLECTIVOS_FIDELIZADOS";
	
	// ISK-217 GAP 51b Cupón con diferencia
	public void gestionCuponesPorDiferenciaFidelizado(ITicket ticketPrincipal) throws Exception {
		BigDecimal eurosNoUtilizadosDelCupon = BigDecimal.ZERO;
		CustomerCouponDTO cuponOriginal = null;

		for(CuponAplicadoTicket cupon : (List<CuponAplicadoTicket>) ticketPrincipal.getCuponesAplicados()) {
			for (CustomerCouponDTO cuponFidelizado : ticketPrincipal.getCabecera().getDatosFidelizado().getActiveCoupons()) {
				if (cupon.getCodigo().equals(cuponFidelizado.getCouponCode())) {
					if (BigDecimalUtil.isMayorACero(cupon.getImporteTotalAhorrado()) && cupon.getImporteTotalAhorrado().compareTo(cuponFidelizado.getBalance()) < 0) {
						eurosNoUtilizadosDelCupon = BigDecimalUtil.redondear(cuponFidelizado.getBalance().subtract(cupon.getImporteTotalAhorrado()));
						cuponOriginal = cuponFidelizado;
						break;
					}
				}
			}
			if (cuponOriginal != null) {
				break;
			}
		}
		
		if (eurosNoUtilizadosDelCupon != null && BigDecimalUtil.isMayorACero(eurosNoUtilizadosDelCupon) && cuponOriginal != null) {
			log.info("Han sobrado " + eurosNoUtilizadosDelCupon + " del cupón utilizado con código " + cuponOriginal.getCouponCode() + ". Se procederá a crear un cupón con esta diferencia");

			String couponTypeCode = "DEFAULT";
			String codigoCupon = couponsCodeGeneratorService.getCouponCode(ticketPrincipal.getUidTicket(), ticketPrincipal.getCabecera().getDatosFidelizado().getIdFidelizado(), couponTypeCode);

			IskaypetCuponEmitidoTicket cupon = new IskaypetCuponEmitidoTicket();
			cupon.setTituloCupon(I18N.getTexto("CUPÓN POR DIFERENCIA"));
			cupon.setImporteCupon(eurosNoUtilizadosDelCupon);
			cupon.setFechaInicio(cuponOriginal.getStartDate());
			cupon.setFechaFin(cuponOriginal.getEndDate());
			cupon.setTipoCupon(couponTypeCode);
			cupon.setCodigoCupon(codigoCupon);
			cupon.setIdPromocionAplicacion(cuponOriginal.getPromotionId());
			cupon.setMaximoUsos(1);
			cupon.setImagenCupon(cuponOriginal.getImageUrl());

			//CAMPOS PERSONALIZADOS
			cupon.setCodCuponOrigen(cuponOriginal.getCouponCode());

			((IskaypetTicketVentaAbono) ticketPrincipal).addCuponEmitido(cupon);
		}
	}
	
	// ISK-216 GAP 50b Generación de cupones en POS
	public void gestionCuponesPuntosFidelizado(ITicket ticketPrincipal) throws Exception {
		String uidInstancia = sesion.getAplicacion().getUidInstancia();
		String periodoCarencia = getValorVariable(uidInstancia, PUNTOS_PERIODO_CARENCIA);
		BigDecimal puntosObtenidos = ticketPrincipal.getTotales().getPuntos();
		if (StringUtils.isNotBlank(periodoCarencia)) {
			puntosObtenidos = BigDecimal.ZERO;
		}

		BigDecimal saldoFidelizado = BigDecimal.ZERO;
		if (ticketPrincipal.getCabecera().getDatosFidelizado().getSaldo() != null) {
			saldoFidelizado = ticketPrincipal.getCabecera().getDatosFidelizado().getSaldo();
		}

		BigDecimal puntosDisponibles;
		puntosDisponibles = (saldoFidelizado/*.subtract(puntosUtilizados)*/).add(puntosObtenidos);

        String valorPuntosGenCupon = getValorVariable(uidInstancia, PUNTOS_GENERACION_CUPON);
        if (StringUtils.isBlank(valorPuntosGenCupon)) {
            return;
        }

        BigDecimal puntosGenCupon = new BigDecimal(valorPuntosGenCupon);
		if (puntosDisponibles.compareTo(puntosGenCupon) >= 0) {
			String puntosEuros = getValorVariable(uidInstancia, PUNTOS_EUROS);
			if (StringUtils.isBlank(puntosEuros)) {
				throw new Exception("No se ha podido generar el cupón porque la variable " + PUNTOS_EUROS + " no se encuentra definida o no tiene valor para la instancia " + uidInstancia);
			}

			if(comprobarColectivoGeneraCupon(ticketPrincipal, uidInstancia)) {
                String divisa = "";
                if(StringUtils.isNotBlank(((IskaypetCabeceraTicket)ticketPrincipal.getCabecera()).getDivisa()) && ((IskaypetCabeceraTicket)ticketPrincipal.getCabecera()).getDivisa().equals("EUR")) {
                    divisa = "€";
                }
                BigDecimal importeCupon = BigDecimalUtil.redondear(puntosDisponibles.multiply(new BigDecimal(puntosEuros)));
				CuponEmitidoTicket cupon = generarCuponPuntos(importeCupon, ticketPrincipal, divisa);
				((IskaypetTicketVentaAbono) ticketPrincipal).addCuponEmitido(cupon);
			}else {
				log.info("El cupón no puede generarse porque el colectivo del fidelizado " + ticketPrincipal.getCabecera().getDatosFidelizado().getIdFidelizado() +" no está definido en la variable "+ COLECTIVOS_FIDELIZADOS);
			}
		}
	}


	public CuponEmitidoTicket generarCuponPuntos(BigDecimal importeCupon, ITicket ticket, String divisa) throws Exception {
		boolean esDevolucion = ticket.isEsDevolucion();
		Date fechainicioCupon = getFechaInicioCupon(esDevolucion);
		Date fechaFinCupon = getFechaFinCupon(fechainicioCupon);
		Long idFidelizado = ticket.getCabecera().getDatosFidelizado().getIdFidelizado();
		String uidTicket = ticket.getUidTicket();

		String couponTypeCode = "DEFAULT";
		String codigoCupon = couponsCodeGeneratorService.getCouponCode(uidTicket, idFidelizado, couponTypeCode);
		
		IskaypetCuponEmitidoTicket cupon = new IskaypetCuponEmitidoTicket();

		cupon.setTituloCupon(I18N.getTexto("CUPÓN MUNDO KIWOKO"));
		cupon.setImporteCupon(importeCupon);
		cupon.setFechaInicio(fechainicioCupon);
		cupon.setFechaFin(fechaFinCupon);
		cupon.setTipoCupon(couponTypeCode);
		cupon.setCodigoCupon(codigoCupon);
		cupon.setIdPromocionOrigen(0L);
		cupon.setMaximoUsos(1);

		if(esDevolucion) {
			cupon.setTipoCupon("DEVOLUCION");
			cupon.setTituloCupon(I18N.getTexto("CUPÓN DEVOLUCIÓN"));
			cupon.setIdPromocionOrigen(null);
			cupon.setFechaInicio(new Date());
		}
		
		return cupon;
	}
	
	public String getValorVariable (String uidInstancia, String idVariable) {
		log.info("Consultando valor de la variable " + idVariable + " para la instancia " + uidInstancia);
		String valor = "";
		try {
			valor = variablesServices.getVariableAsString(idVariable);
			if (StringUtils.isNotBlank(valor)) {
				valor = formateaYExtraeValor(uidInstancia, valor);
			}
			if (StringUtils.isBlank(valor)) {
				throw new Exception();
			}
		} catch (Exception e) {
			log.info("La variable " + idVariable + " no se encuentra definida o no tiene valor para la instancia " + uidInstancia);
		}
		return valor;
	}

	private String formateaYExtraeValor(String uidInstancia, String valor) {
		// Ejemplo valor de las variables -> TIENDA_ANIMAL:30;KIWOKO:15;
		String valorFinal = "";
		String[] instanciaValor = valor.split(";");
		for (int i = 0; i < instanciaValor.length; i++) {
			if (instanciaValor[i].split(":")[0].equals(uidInstancia)) {
				valorFinal = instanciaValor[i].split(":")[1];
				BigDecimal valorBd = new BigDecimal(valorFinal);
				if (BigDecimalUtil.isMayorACero(valorBd)) {
					break;
				} else {
					valorFinal = "";
				}
			}
		}
		return valorFinal;
	}
	
	private Date getFechaInicioCupon(boolean esDevolucion) {
		Date dt = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(dt);
		if(!esDevolucion) {
			c.add(Calendar.DATE, 1);
		}
		dt = c.getTime();
		return dt;
	}
	
	private Date getFechaFinCupon(Date fechainicioCupon) {
		String valorDiasVigencia = getValorVariable(sesion.getAplicacion().getUidInstancia(), PUNTOS_CUPON_DIAS_VIGENCIA);
		int diasVigencia = 0;
		if (StringUtils.isNotBlank(valorDiasVigencia)) {
			diasVigencia = Integer.parseInt(valorDiasVigencia);
		}
		
		Date fechaFinCupon;
		Calendar c = Calendar.getInstance(); 
		c.setTime(fechainicioCupon); 
		c.add(Calendar.DATE, diasVigencia);
		fechaFinCupon = c.getTime();
		return fechaFinCupon;
	}
	
	//LYT KWK EMISIÓN DE CUPONES A EMPLEADOS
	public boolean comprobarColectivoGeneraCupon(ITicket ticketPrincipal, String uidInstancia) throws Exception {
		boolean esColectivoApto = false;
		String valorColectivoQueGeneraCupon = variablesServices.getVariableAsString(COLECTIVOS_FIDELIZADOS);
		if (StringUtils.isNotBlank(valorColectivoQueGeneraCupon)) {
			List<String> colectivosList = Arrays.asList(valorColectivoQueGeneraCupon.split(";"));
			for (String codColectivo : ticketPrincipal.getCabecera().getDatosFidelizado().getCodColectivos()) {
				if(colectivosList.contains(codColectivo)) {
					esColectivoApto = true;
					break;
				}
			}
		}else {
			throw new Exception(I18N.getTexto("No se ha podido generar el cupón porque la variable {1} no se encuentra definida o no tiene valor para la instancia {2}",COLECTIVOS_FIDELIZADOS, uidInstancia));
		}
		return esColectivoApto;
	}
	
	

}
