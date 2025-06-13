package com.comerzzia.bimbaylola.pos.services.apartados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.apartados.ByLApartadosCabecera;
import com.comerzzia.bimbaylola.pos.persistence.apartados.ByLApartadosCabeceraKey;
import com.comerzzia.bimbaylola.pos.persistence.apartados.ByLApartadosCabeceraMapper;
import com.comerzzia.bimbaylola.pos.persistence.movimientostarjeta.CajaMovimientoTarjeta;
import com.comerzzia.bimbaylola.pos.services.apartados.exception.ReservaDevolucionCancelacionException;
import com.comerzzia.bimbaylola.pos.services.movimientos.CajaMovimientoTarjetaService;
import com.comerzzia.bimbaylola.pos.services.reservas.exception.TicketReservaException;
import com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas.DatosRespuestaTarjetaReservaTicket;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraExample;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.services.apartados.ApartadosService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServices;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@Service
@Primary
public class ByLApartadosService extends ApartadosService{

	public static final String CODCONCEPTO_MOV_ANTICIPO_RESERVA = "22";
	public static final String CODCONCEPTO_MOV_DEVOLUCION_RESERVA = "23";

	@Autowired
	private CajaMovimientoTarjetaService movimientoTarjetaService;
	
	@Autowired
	private ByLApartadosCabeceraMapper bylApartadosCabeceraMapper;
	
	@Autowired
	protected CajaConceptosServices cajaConceptosServices;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void registrarMovimientosPagosApartado(Ticket ticket, List<PagoTicket> pagos, ApartadosCabeceraBean cabeceraApartado) 
			throws TicketsServiceException{
    	log.debug("registrarMovimientosPagosApartado() - Procesando movimientos... ");
    	
    	SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
    	Date fecha = new Date();
    	ApartadosPagoBean pagoApartado;
    	try{
    		sqlSession.openSession(SessionFactory.openSession());
    		Integer idLineaCaja = cajasService.consultarProximaLineaDetalleCaja(sqlSession);
    		
    		for(IPagoTicket pago : pagos){
    			CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
				CajaConceptoBean concepto = cajaConceptosServices.getConceptoCaja(CODCONCEPTO_MOV_ANTICIPO_RESERVA);
				if (concepto == null) {
					log.error("registrarMovimientosPagosApartado() - Se está intentando insertar un movimiento con concepto nulo. Código concepto: " + CODCONCEPTO_MOV_ANTICIPO_RESERVA);
					throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"));
				}
				detalleCaja.setCodConceptoMovimiento(concepto.getCodConceptoMovimiento());
				detalleCaja.setConcepto(concepto.getDesConceptoMovimiento());
    			detalleCaja.setLinea(idLineaCaja);
    			detalleCaja.setFecha(fecha);
    			/* Si es positivo se crea el movimiento como cargo, si no, como abono */
    			if(pago.getImporte().compareTo(BigDecimal.ZERO)>0){
    				detalleCaja.setCargo(pago.getImporte());
    				detalleCaja.setAbono(BigDecimal.ZERO);
    			}else if(pago.getImporte().compareTo(BigDecimal.ZERO)<0){
    				detalleCaja.setCargo(BigDecimal.ZERO);
    				detalleCaja.setAbono(pago.getImporte().negate());
    			}
    			detalleCaja.setCodConceptoMovimiento(CODCONCEPTO_MOV_ANTICIPO_RESERVA);
    			detalleCaja.setConcepto("Apartado nº " + cabeceraApartado.getIdApartado());
    			detalleCaja.setCodMedioPago(pago.getCodMedioPago());
				cajasService.crearMovimiento(sqlSession, detalleCaja);
    			    			
    			pagoApartado = new ApartadosPagoBean();
    			pagoApartado.setLinea(idLineaCaja);
    			pagoApartado.setUidActividad(sesion.getAplicacion().getUidActividad());
    			pagoApartado.setUidApartado(cabeceraApartado.getUidApartado());
    			pagoApartado.setUidDiarioCaja(sesion.getSesionCaja().getUidDiarioCaja());
    			pagoApartado.setFechaActualizacion(fecha);
    			pagoApartado.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
    			nuevoPagoApartado(pagoApartado, sqlSession);

    			if(pago.getDatosRespuestaPagoTarjeta() != null){
	    			if(pago.isMedioPagoTarjeta()){
		    			/* Generamos el movimiento de la Tarjeta */
		            	CajaMovimientoTarjeta movimientoTarjeta = new CajaMovimientoTarjeta();
		            	movimientoTarjeta.setUidActividad(pagoApartado.getUidActividad());
		            	movimientoTarjeta.setUidDiarioCaja(pagoApartado.getUidDiarioCaja());
		            	movimientoTarjeta.setLinea(pagoApartado.getLinea());
		        		try{
		        			/* Generamos el XML del objeto de movimiento de Tarjeta */
		        			DatosRespuestaTarjetaReservaTicket datosRespuesta = new DatosRespuestaTarjetaReservaTicket();
		        			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		        			BeanUtils.copyProperties(datosRespuesta, pago.getDatosRespuestaPagoTarjeta());

							/* Se añade este control para que no falle al realizar el Marshall */
							if (datosRespuesta.getAdicionales() == null) {
								datosRespuesta.setAdicionales(new HashMap<String, String>());
							}
							
							byte[] result = MarshallUtil.crearXML(datosRespuesta);
		        			movimientoTarjeta.setRespuestaTarjeta(result);
		        			
							movimientoTarjeta.setBase(ticket.getTotales().getBase());
							movimientoTarjeta.setImpuestos(ticket.getTotales().getImpuestos());
		        			
		        			movimientoTarjetaService.crear(movimientoTarjeta, sqlSession);
		        		}catch(MarshallUtilException e){
		        			String mensajeError = "Error al generar el XML de los Datos de Respuesta de una Tarjeta";
		        			log.error("generarTicketReserva() - " + mensajeError + " - " + e.getMessage());
		        			throw new TicketReservaException(mensajeError, e);
		        		}
	    			}
    			}
    			idLineaCaja++;
    		}
    		
    		/* Añadimos el movimiento de cambio */
    		IPagoTicket cambio = ticket.getTotales().getCambio();
    		if(!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getCambio().getImporte())){
	            CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
				CajaConceptoBean concepto = cajaConceptosServices.getConceptoCaja(CODCONCEPTO_MOV_ANTICIPO_RESERVA);
				if (concepto == null) {
					log.error("registrarMovimientosPagosApartado() - Se está intentando insertar un movimiento con concepto nulo. Código concepto: " + CODCONCEPTO_MOV_ANTICIPO_RESERVA);
					throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"));
				}
				detalleCaja.setCodConceptoMovimiento(concepto.getCodConceptoMovimiento());
				detalleCaja.setConcepto(concepto.getDesConceptoMovimiento());
	            detalleCaja.setLinea(idLineaCaja);
	            detalleCaja.setFecha(ticket.getFecha());
	            /* Cargo o abono al revés de lo normal */
	            if(BigDecimalUtil.isMayorOrIgualACero(ticket.getCabecera().getTotales().getTotal())){
	                detalleCaja.setCargo(cambio.getImporte().negate());
	                detalleCaja.setAbono(BigDecimal.ZERO);
	            }else{
	                detalleCaja.setCargo(BigDecimal.ZERO);
	                detalleCaja.setAbono(cambio.getImporte().negate());
	            }
	            detalleCaja.setCodConceptoMovimiento(CODCONCEPTO_MOV_ANTICIPO_RESERVA);
	            detalleCaja.setConcepto("Apartado nº " + cabeceraApartado.getIdApartado() + " (cambio)");
	            detalleCaja.setCodMedioPago(cambio.getCodMedioPago());
	            cajasService.crearMovimiento(sqlSession, detalleCaja);
	            
	            pagoApartado = new ApartadosPagoBean();
    			pagoApartado.setLinea(idLineaCaja);
    			pagoApartado.setUidActividad(sesion.getAplicacion().getUidActividad());
    			pagoApartado.setUidApartado(cabeceraApartado.getUidApartado());
    			pagoApartado.setUidDiarioCaja(sesion.getSesionCaja().getUidDiarioCaja());
    			pagoApartado.setFechaActualizacion(fecha);
    			pagoApartado.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
    			nuevoPagoApartado(pagoApartado, sqlSession);
    			
	            idLineaCaja++;
            }
            
            /* Añadimos el cambio como un pago */
            if(!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getCambio().getImporte())){
	            IPagoTicket pagoCodMedPagoCambio = ((TicketVenta)ticket).getPago(cambio.getMedioPago().getCodMedioPago());
	        	MedioPagoBean medioPagoCambio = ticket.getCabecera().getTotales().getCambio().getMedioPago();
	        	
	        	if(pagoCodMedPagoCambio == null){
	        		pagoCodMedPagoCambio = createPago();
	        		pagoCodMedPagoCambio.setEliminable(false);
	        		pagoCodMedPagoCambio.setModificable(true);
	        		pagoCodMedPagoCambio.setMedioPago(medioPagoCambio);
	        		
	        		((TicketVenta)ticket).addPago(pagoCodMedPagoCambio);
	        	}
	        	
	        	if(BigDecimalUtil.isMayorOrIgualACero(ticket.getCabecera().getTotales().getTotal())){
	        		pagoCodMedPagoCambio.setImporte(pagoCodMedPagoCambio.getImporte().subtract(((TicketVenta)ticket).getTotales().getCambio().getImporte()));
	        	}else{
	        		pagoCodMedPagoCambio.setImporte(pagoCodMedPagoCambio.getImporte().add(((TicketVenta)ticket).getTotales().getCambio().getImporte()));
	        	}
            }
    		
            actualizarCabeceraApartado(cabeceraApartado, sqlSession);
    		
    		sqlSession.commit();
    	}catch(Exception e){
    		sqlSession.rollback();
    		String mensajeError = "Se ha producido un error procesando los movimientos";
    		log.error("registrarMovimientosPagosApartado() - " + mensajeError + " - " + e.getMessage());
    		throw new TicketsServiceException(mensajeError, e);
    	}finally{
    		sqlSession.close();
    	}
    }
	
	/**
	 * Inserta un pago que contiene la devolución del exceso de saldo al cancelar una Reserva.
	 * @param apartado
	 * @return
	 * @throws ReservaDevolucionCancelacionException
	 */
	public ApartadosPagoBean crearPagoReservaCancelacion(ApartadosCabeceraBean apartado) throws ReservaDevolucionCancelacionException{
	    SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
	    try{
	        Integer idLineaCaja = cajasService.consultarProximaLineaDetalleCaja(sqlSession);
	        
	        ApartadosPagoBean pagoApartado = new ApartadosPagoBean();
	    	pagoApartado.setLinea((idLineaCaja - 1));
	    	pagoApartado.setUidActividad(sesion.getAplicacion().getUidActividad());
	    	pagoApartado.setUidApartado(apartado.getUidApartado());
	    	pagoApartado.setUidDiarioCaja(sesion.getSesionCaja().getUidDiarioCaja());
	    	pagoApartado.setFechaActualizacion(new Date());
	    	pagoApartado.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
	    	nuevoPagoApartado(pagoApartado, sqlSession);
	    	
	    	return pagoApartado;
	    }
	    catch(Exception e){
	        String mensajeError = "Error al crear el movimiento de devolución que se genera por la cancelación de la Reserva";
	        log.error("cancelarApartado() - " + mensajeError, e);
	        throw new ReservaDevolucionCancelacionException(mensajeError, e);
	    }
    }
	
	
	public List<ApartadosCabeceraBean> consultarApartados(String cliente, Long numApartado, boolean verTodos, String uidActividad) {

		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("consultarApartados() - Consultando ticket en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			ApartadosCabeceraExample example = new ApartadosCabeceraExample();
			ApartadosCabeceraExample.Criteria criteria = example.createCriteria();
			example.setOrderByClause(ApartadosCabeceraExample.ORDER_BY_ID_APARTADO_DESC);
			criteria.andUidActividadEqualTo(uidActividad);
			if (cliente != null && !cliente.isEmpty()) {
				criteria.andDesClienteLikeInsensitive("%" + cliente + "%");
			}
			if (numApartado != null) {
				criteria.andIdApartadoEqualTo(numApartado);
			}
			if (!verTodos) {
				criteria.andEstadoApartadoEqualTo(ApartadosCabeceraBean.ESTADO_DISPONIBLE);
			}
			List<ApartadosCabeceraBean> selectByExample = apartadosCabeceraMapper.selectByExample(example);

			log.debug("consultarApartados() - Consulta terminada, encontrados " + selectByExample.size() + " apartados");

			return selectByExample;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando apartados en base de datos con parámetros indicados: cliente: " + cliente + ", apartado: " + numApartado;
			log.error("consultarApartados() - " + msg, e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}

	public ByLApartadosCabecera consultarXApartadoCabecera(String uidActividad, String uidApartado) {

		ByLApartadosCabeceraKey key = new ByLApartadosCabeceraKey();
		key.setUidActividad(uidActividad);
		key.setUidApartado(uidApartado);

		ByLApartadosCabecera apartadoCabecera = bylApartadosCabeceraMapper.selectByPrimaryKey(key);

		return apartadoCabecera;
	}
	
	public void insertarNuevoXApartadoCabecera(ApartadosCabeceraBean apartadoCabeceraBean, String codCaja) {
		ByLApartadosCabecera apartadoCabecera = new ByLApartadosCabecera();
		apartadoCabecera.setUidActividad(apartadoCabeceraBean.getUidActividad());
		apartadoCabecera.setUidApartado(apartadoCabeceraBean.getUidApartado());
		apartadoCabecera.setCodCaja(codCaja);

		bylApartadosCabeceraMapper.insert(apartadoCabecera);
	}
}
