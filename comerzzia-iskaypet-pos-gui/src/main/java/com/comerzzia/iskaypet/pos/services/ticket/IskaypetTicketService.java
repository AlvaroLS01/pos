package com.comerzzia.iskaypet.pos.services.ticket;

import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.servicios.ventas.tickets.ServicioTicketsImpl;
import com.comerzzia.core.servicios.ventas.tickets.TicketNotFoundException;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.persistence.ticket.IskaypetTicketBean;
import com.comerzzia.iskaypet.pos.persistence.ticket.gestion.documentos.ticket.contratos.POSTicketContrato;
import com.comerzzia.iskaypet.pos.persistence.ticket.gestion.documentos.ticket.contratos.POSTicketContratoExample;
import com.comerzzia.iskaypet.pos.persistence.ticket.gestion.documentos.ticket.contratos.POSTicketContratoExample.Criteria;
import com.comerzzia.iskaypet.pos.persistence.ticket.gestion.documentos.ticket.contratos.POSTicketContratoMapper;
import com.comerzzia.iskaypet.pos.services.core.contadores.IskaypetServicioContadores;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.config.configContadores.ContadoresConfigException;
import com.comerzzia.pos.services.core.config.configContadores.ContadoresConfigNotFoundException;
import com.comerzzia.pos.services.core.config.configContadores.parametros.ConfigContadoresParametrosException;
import com.comerzzia.pos.services.core.config.configContadores.rangos.CounterRangeParamDto;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.listeners.ListenersExecutor;
import com.comerzzia.pos.services.fiscaldata.FiscalData;
import com.comerzzia.pos.services.fiscaldata.FiscalDataException;
import com.comerzzia.pos.services.fiscaldata.FiscalDataService;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.ticket.*;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.config.SpringContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**Oa
 * GAP 117 RECUPERACIÓN DE CONTRATOS DESDE EL POS
 */

@Primary
@Service
@SuppressWarnings({"rawtypes", "deprecation", "unchecked"})
public class IskaypetTicketService extends TicketsService {

	@Autowired
	protected POSTicketContratoMapper ticketContratoMapper;
	
	@Autowired
	protected IskaypetTicketManager ticketManager;

	@Autowired
	protected IskaypetServicioContadores iskaypetServicioContadores;

	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;

	@Autowired
    private ListenersExecutor listenersExecutor;

	@Override
	public List<TicketBean> consultarTicketLocalizador(String localizador, List<Long> idTiposDocValidos) throws TicketsServiceException {
		return consultarTicketLocalizadorIskaypet(localizador, idTiposDocValidos);
	}

	@Override
	public List<TicketBean> consultarTickets(String caja, Long idTicket, Date fecha, Long idDoc, List<Long> idTiposDocValidos) throws TicketsServiceException {

		return consultarTicketIskaypet(caja, idTicket, fecha, idDoc, idTiposDocValidos);
	}

	private List<TicketBean> consultarTicketLocalizadorIskaypet(String localizador, List<Long> idTiposDocValidos) {

		log.debug("consultarTicketLocalizadorContrato() - Consultando ticket en base de datos...");

		List<TicketBean> tickets = new ArrayList<>();

		POSTicketContratoExample ticketLocatorKey = new POSTicketContratoExample();
		Criteria criteria = ticketLocatorKey.createCriteria();
		criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
		criteria.andLocatorIdEqualTo(localizador);

		for (Long idTipoDocValido : idTiposDocValidos) {
			criteria.andIdTipoDocumentoEqualTo(idTipoDocValido);
			List<POSTicketContrato> ticketsEncontrado = ticketContratoMapper.selectByExample(ticketLocatorKey);
			if (ticketsEncontrado != null && !ticketsEncontrado.isEmpty()) {
				POSTicketContrato ticket = ticketsEncontrado.get(0);
				try {
					// Variables que no se pueden copiar por llamarse diferente en el padre.
					String codAlm = ticket.getCodalm();

					IskaypetTicketBean responseFinal = new IskaypetTicketBean();
					BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
					BeanUtils.copyProperties(responseFinal, ticket);
					tickets.add(responseFinal);

					responseFinal.setCodAlmacen(codAlm);

				}
				catch (IllegalAccessException | InvocationTargetException e) {
					log.error("");
				}
			}
		}
		return tickets;
	}

	private List<TicketBean> consultarTicketIskaypet(String caja, Long idTicket, Date fecha, Long idDoc, List<Long> idTiposDocValidos) throws TicketsServiceException {
		try {
			log.debug("consultarTicketIshaypet() - Consultando ticket en base de datos...");
			POSTicketContratoExample ticketLocatorKey = new POSTicketContratoExample();
			Criteria criteria = ticketLocatorKey.createCriteria();
			criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
			criteria.andCodalmEqualTo(sesion.getAplicacion().getCodAlmacen());
			if (idDoc != null) {
				criteria.andIdTipoDocumentoEqualTo(idDoc);
			}
			if (caja != null && !caja.isEmpty()) {
				criteria.andCodcajaEqualTo(caja);
			}
			if (idTicket != null) {
				criteria.andIdTicketEqualTo(idTicket);
			}
			if (fecha != null) {
				// Se crea la fecha del día siguiente para crear el intervalo válido para el día del filtro
				Fecha diaSuperior = Fecha.getFecha(fecha);
				diaSuperior.sumaDias(1);
				criteria.andFechaBetween(fecha, diaSuperior.getDate());
			}
			if (idTiposDocValidos != null && !idTiposDocValidos.isEmpty()) {
				criteria.andIdTipoDocumentoIn(idTiposDocValidos);
			}

			List<POSTicketContrato> ticketsEncontrado = ticketContratoMapper.selectByExample(ticketLocatorKey);

			List<TicketBean> tickets = new ArrayList<>();
			if (ticketsEncontrado != null && !ticketsEncontrado.isEmpty()) {
				for (POSTicketContrato posTicketContrato : ticketsEncontrado) {
					// Variables que no se pueden copiar por llamarse diferente en el padre.
					String codAlm = posTicketContrato.getCodalm();

					IskaypetTicketBean responseFinal = new IskaypetTicketBean();
					BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
					BeanUtils.copyProperties(responseFinal, posTicketContrato);

					responseFinal.setCodAlmacen(codAlm);
					tickets.add(responseFinal);
				}
			}

			return tickets;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando tickets en base de datos con parámetros indicados: Fecha: " + fecha + " IdTicket: " + idTicket + " Caja: " + caja + " - "
			        + e.getMessage();
			log.error("consultarTickets() - " + msg, e);
			throw new TicketsServiceException(e);
		}

	}

	@Override
	protected void setFiscalData(ITicket ticket) throws FiscalDataException {

		try {

			FiscalDataService fiscalService = (FiscalDataService) ContextHolder.getBean("IskaypetFiscalDataService" + sesion.getAplicacion().getTienda().getCliente().getCodpais());
			if (fiscalService == null) {
				fiscalService = (FiscalDataService) ContextHolder.getBean("FiscalDataService" + sesion.getAplicacion().getTienda().getCliente().getCodpais());
			}

			FiscalData fiscalData = fiscalService.getFiscalData(ticket);

			ticket.getCabecera().setFiscalData(fiscalData);
		}
		catch (ClassNotFoundException | NoSuchBeanDefinitionException e) {
			log.debug("setFiscalData() - No hay configurado un servicio fiscal para el país '" + sesion.getAplicacion().getTienda().getCliente().getCodpais() + "'");
		}

	}

	@Override
	public void saveEmptyTicket(ITicket ticketPrincipal, TipoDocumentoBean documentoActivo, TipoDocumentoBean documentoOrigen) {
		log.debug("saveEmptyTicket  -- No guardamos, ni registramos nada...");
	}

	@SuppressWarnings("unchecked")
	@Override
	public TicketVentaAbono generateEmptyTicket(ITicket ticketPrincipal, TipoDocumentoBean documentoActivo,
			Boolean lineasPositivas) {
		log.debug("generateEmptyTicket() - generando ticket vacio...");
		// Inicializamos un ticket sin lineas
		TicketVentaAbono ticketVacio = SpringContext.getBean(TicketVentaAbono.class);
		ticketVacio.getCabecera().inicializarCabecera(ticketVacio);
		ticketVacio.inicializarTotales();
		ticketVacio.setCliente(ticketPrincipal.getCliente());
		ticketVacio.setCajero(ticketPrincipal.getCabecera().getCajero());
		IPagoTicket cambio = createPago();
		cambio.setMedioPago(MediosPagosService.medioPagoDefecto);
		ticketVacio.getCabecera().getTotales().setCambio(cambio);
		ticketVacio.getTotales().recalcular();
		ticketVacio.getCabecera().setDocumento(documentoActivo);
		ticketVacio.setEsDevolucion(ticketPrincipal.isEsDevolucion());
		// Añadimos los datos del contador obtenido antes
		ticketVacio.setIdTicket(ticketPrincipal.getIdTicket());
		ticketVacio.getCabecera().setSerieTicket(ticketPrincipal.getCabecera().getSerieTicket());
		ticketVacio.getCabecera().setCodTicket(ticketPrincipal.getCabecera().getCodTicket());
		ticketVacio.getCabecera().setDatosDocOrigen(ticketPrincipal.getCabecera().getDatosDocOrigen());
		ticketVacio.getCabecera().setUidTicket(ticketPrincipal.getCabecera().getUidTicket());

		List<LineaTicket> lineasOriginales = ticketPrincipal.getLineas();
		//solo actuamos sobre la primera linea del documento, no tiene sentido poner todas las lineas en negativo.
		LineaTicket lineaTicketVacio = lineasOriginales.get(0);
		log.debug("generateEmptyTicket() - Antes de resetPromociones: " + lineaTicketVacio.getImporteTotalConDto());
		lineaTicketVacio.resetPromociones();
		log.debug("generateEmptyTicket() - Después de resetPromociones: " + lineaTicketVacio.getImporteTotalConDto());
		log.debug("generateEmptyTicket() - Antes de recalcular: " + lineaTicketVacio.getImporteTotalConDto());
		lineaTicketVacio.recalcularImporteFinal();
		log.debug("generateEmptyTicket() - Después de recalcular: " + lineaTicketVacio.getImporteTotalConDto());

		if(lineasPositivas == null) {
			ticketVacio.addLinea(lineaTicketVacio.clone());
			LineaTicket lineaNegativa = lineaTicketVacio.clone();
			lineaNegativa.setCantidad(lineaNegativa.getCantidad().negate());
			lineaNegativa.recalcularImporteFinal();
			ticketVacio.addLinea(lineaNegativa);
		}
		else {
			LineaTicket lineaNueva = lineaTicketVacio.clone();
			BigDecimal cantidad = lineaNueva.getCantidad();
			if(lineasPositivas) {
				cantidad = cantidad.abs();
			}
			else {
				cantidad = cantidad.abs().negate();
			}
			lineaNueva.setCantidad(cantidad);
			log.debug("generateEmptyTicket() - Antes de recalcular importe final en lineaNueva. Importe total con DTO: " + lineaNueva.getImporteTotalConDto());
			lineaNueva.recalcularImporteFinal();
			log.debug("generateEmptyTicket() - Después de recalcular importe final en lineaNueva. Importe total con DTO: " + lineaNueva.getImporteTotalConDto());
			ticketVacio.addLinea(lineaNueva);
		}
		
		log.debug("generateEmptyTicket() - antes del recalcular totales de la cabecera del ticket vacio" + ticketVacio.getCabecera().getTotales().getTotalAPagar());
		ticketVacio.getCabecera().getTotales().recalcular();
		log.debug("generateEmptyTicket() - despues del recalcular totales de la cabecera del ticket vacio" + ticketVacio.getCabecera().getTotales().getTotalAPagar());
		PagoTicket pagoVacio = createPago();
		pagoVacio.setMedioPago(MediosPagosService.medioPagoDefecto);
		pagoVacio.setImporte(ticketVacio.getTotales().getTotalAPagar());
		log.debug("generateEmptyTicket() - Añadiendo importe de pago al pago vacio: " + pagoVacio.getImporte());
		ticketVacio.addPago(pagoVacio);
		return ticketVacio;
	}

	public void deshacerContadorIdTicket(ITicket ticketPrincipal, TipoDocumentoBean documentoActivo) {
		log.debug("deshacerContadorIdTicket() - deshaciendo contador para identificador...");

        try {
            Map<String, String> parametrosContador = new HashMap<>();
            Map<String, String> condicionesVigencias = new HashMap<>();

            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODEMP, sesion.getAplicacion().getEmpresa().getCodEmpresa());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODALM,sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODSERIE,sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODCAJA,sesion.getAplicacion().getCodCaja());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODDOC, documentoActivo.getCodtipodocumento());
            parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_PERIODO,((new Fecha()).getAño().toString()));

            condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODCAJA,sesion.getAplicacion().getCodCaja());
            condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODALM,sesion.getAplicacion().getTienda().getCodAlmacen());
            condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODEMP,sesion.getAplicacion().getEmpresa().getCodEmpresa());

            ConfigContadorBean confContador = servicioConfigContadores.consultar(documentoActivo.getIdContador());
            if(!confContador.isRangosCargados()){
                ConfigContadorRangoExample example = new ConfigContadorRangoExample();
                example.or().andIdContadorEqualTo(confContador.getIdContador());
                example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
                        + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
                List<ConfigContadorRangoBean> rangos = servicioConfigContadoresRangos.consultar(example);
                confContador.setRangos(rangos);
                confContador.setRangosCargados(true);
            }

            ContadorBean ticketContador = servicioContadores.consultarContadorActivo(confContador, parametrosContador,condicionesVigencias,sesion.getAplicacion().getUidActividad(), false);
            iskaypetServicioContadores.disminuirContador(ticketContador);

            copiaSeguridadTicketService.eliminarBackup(new SqlSession(), ticketPrincipal.getUidTicket());
            ticketPrincipal.getCabecera().setIdTicket(null);
            ticketPrincipal.getCabecera().setCodTicket(null);
        } catch (TicketsServiceException | ContadorServiceException | ConfigContadoresParametrosException | ContadoresConfigNotFoundException | ContadoresConfigException e) {
            log.error("error deshaciendo contador: " + e);
        }
    }

	@Override
	public synchronized void setContadorIdTicket(Ticket ticket) throws TicketsServiceException {
		try {
			log.debug("setContadorIdTicket() - Obteniendo contador para identificador...");
			Map<String, String> parametrosContador = new HashMap<>();
			Map<String, String> condicionesVigencias = new HashMap<>();

			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODEMP,ticket.getEmpresa().getCodEmpresa());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODALM,ticket.getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODSERIE,ticket.getTienda().getAlmacenBean().getCodAlmacen());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODCAJA,ticket.getCodCaja());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_CODDOC,ticket.getCabecera().getCodTipoDocumento());
			parametrosContador.put(ConfigContadorParametroBean.PARAMETRO_PERIODO,((new Fecha()).getAño().toString()));

			condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODCAJA,ticket.getCabecera().getCodCaja());
			condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODALM,ticket.getCabecera().getTienda().getCodAlmacen());
			condicionesVigencias.put(ConfigContadorRangoBean.VIGENCIA_CODEMP,ticket.getCabecera().getEmpresa().getCodEmpresa());

			TipoDocumentoBean documentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(ticket.getCabecera().getCodTipoDocumento());
			ConfigContadorBean confContador = servicioConfigContadores.consultar(documentoActivo.getIdContador());
			if (!confContador.isRangosCargados()) {
				ConfigContadorRangoExample example = new ConfigContadorRangoExample();
				example.or().andIdContadorEqualTo(confContador.getIdContador());
				example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " +
						ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", " +
						ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " +
						ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
				List<ConfigContadorRangoBean> rangos = servicioConfigContadoresRangos.consultar(example);

				confContador.setRangos(rangos);
				confContador.setRangosCargados(true);
			}

			// Obtener contador y código de ticket válidos
			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());

			ContadorBean ticketContador = obtenerContadorTicketDisponible(confContador, parametrosContador, condicionesVigencias, ticket.getUidActividad(), datosSesion);
			String codTicket = servicioContadores.obtenerValorTotalConSeparador(
					ticketContador.getConfigContador().getValorDivisor3Formateado(), ticketContador.getValorFormateado());

			ticket.setIdTicket(ticketContador.getValor());
			ticket.getCabecera().setSerieTicket(ticketContador.getConfigContador().getValorDivisor3Formateado());
			ticket.getCabecera().setCodTicket(codTicket);
			ticket.getCabecera().setUidTicket(UUID.randomUUID().toString());

			copiaSeguridadTicketService.guardarBackupTicketActivo((TicketVenta) ticket);

			CounterRangeParamDto counterRangeParam = new CounterRangeParamDto();
			counterRangeParam.setCounterId(ticketContador.getIdContador());
			counterRangeParam.setDivisor1(ticketContador.getDivisor1());
			counterRangeParam.setDivisor2(ticketContador.getDivisor2());
			counterRangeParam.setDivisor3(ticketContador.getDivisor3());

			String rangeId = counterRangeManager.findRangeId(counterRangeParam);

			if (StringUtils.isNotBlank(rangeId)) {
				((TicketVentaAbono) ticket).addExtension(EXTENSION_RANGE_ID, rangeId);
			}

		} catch (Exception e) {
			String msg = "Se ha producido un error procesando ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
			log.error("registrarTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
	}

	private ContadorBean obtenerContadorTicketDisponible(ConfigContadorBean confContador, Map<String, String> parametrosContador,
	 Map<String, String> condicionesVigencias, String uidActividad, DatosSesionBean datosSesion) throws ContadorServiceException, ConfigContadoresParametrosException {
		ContadorBean ticketContador;
		String codTicket;

		while (true) {
			ticketContador = servicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, uidActividad, true);
			if (ticketContador == null || ticketContador.getError() != null) {
				throw new ContadorNotFoundException("No se ha encontrado un contador disponible");
			}

			codTicket = servicioContadores.obtenerValorTotalConSeparador(
					ticketContador.getConfigContador().getValorDivisor3Formateado(),
					ticketContador.getValorFormateado());

			try {
				ServicioTicketsImpl.get().consultarTicketCod(datosSesion, codTicket);
				log.debug("El ticket con código " + codTicket + " ya existe. Reintentando...");
			} catch (TicketNotFoundException e) {
				log.debug("Ticket con código " + codTicket + " es válido (no existe aún).");
				break;
			}
		}

		return ticketContador;
	}



        @Override
        @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
        public synchronized void registrarTicket(Ticket ticket, TipoDocumentoBean tipoDocumento, boolean procesarTicket) throws TicketsServiceException {
                log.debug("registrarTicket() - Registrando ticket con id: " + ticket.getIdTicket() + ", total entregado de: " + ticket.getCabecera().getTotales().getEntregadoAsString() + " y un total a pagar de: " + ticket.getCabecera().getTotales().getTotalAPagar());
                log.debug("registrarTicket() - Cantidad total de líneas: " + ticket.getLineas().size());

                BigDecimal totalAPagar = ticket.getCabecera().getTotales().getTotalAPagar();
                if (ticket.getLineas().isEmpty()
                                || (ticket.getPagos().isEmpty() && totalAPagar.compareTo(BigDecimal.ZERO) != 0)) {
                        throw new IllegalStateException("El ticket debe contener líneas y pagos");
                }

                BigDecimal totalEntregado = ticket.getCabecera().getTotales().getEntregado();
                if (totalEntregado.compareTo(totalAPagar) < 0) {
                        throw new IllegalStateException("El total entregado es inferior al total a pagar");
                }

                try {
                        for (Object obj : ticket.getPagos()) {
                                IPagoTicket pago = (IPagoTicket) obj;
                                log.debug("registrarTicket() - Pago del ticket con medio de pago: " + pago.getDesMedioPago() + " y un importe de: " + pago.getImporte());
                        }
                        super.registrarTicket(ticket, tipoDocumento, procesarTicket);
                }
                catch (Exception e) {
                        log.error("registrarTicket() - Error al registrar el ticket", e);
                        throw e;
                }
        }

}