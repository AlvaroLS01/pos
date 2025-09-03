package com.comerzzia.iskaypet.pos.services.cajas;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraTipoPagosEnum;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.iskaypet.pos.persistence.cajas.cierre.ConsultaCierreMapper;
import com.comerzzia.iskaypet.pos.persistence.cajas.controlretiradas.ControlRetiradas;
import com.comerzzia.iskaypet.pos.persistence.cajas.controlretiradas.ControlRetiradasMapper;
import com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS.MovimientoEyS;
import com.comerzzia.iskaypet.pos.services.closingday.ClosingEndDayService;
import com.comerzzia.iskaypet.pos.services.ventas.cajas.apuntes.IskaypetApuntesService;
import com.comerzzia.pos.persistence.cajas.CajaBean;
import com.comerzzia.pos.persistence.cajas.CajaExample;
import com.comerzzia.pos.persistence.cajas.CajaExample.Criteria;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoExample;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import static com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController.IMPRIMIR_LOGO;
import static com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController.requierImprimirLogo;

/**
 * GAP 73 ENTRADAS Y SALIDAS DE CAJAS
 * ISK-221 GAP 27.2 Ampliación del cierre de fin de día
 */
@SuppressWarnings("deprecation")
@Primary
@Component
public class IskaypetCajasService extends CajasService {
	
	
	@Autowired
	protected IskaypetApuntesService iskaypetApuntesService;
	@Autowired
	protected Sesion sesion;
	@Autowired
	protected ControlRetiradasMapper controlRetiradasMapper;
	@Autowired
	protected ClosingEndDayService closingEndDayService;
	
	@Autowired
	protected ConsultaCierreMapper cierreMapper;

	@Autowired
	protected VariablesServices variablesServices;
	
	// GAP 73 ENTRADAS Y SALIDAS DE CAJAS
	@Override
	public void cerrarCaja(Caja caja, Date fechaCierre) throws CajasServiceException {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			log.debug("cerrarCaja() - Cerrando caja con fecha: " + fechaCierre);
			sqlSession.openSession(SessionFactory.openSession());

			//Antes de crear el cierre de la caja comprobamos los movimientos manuales que pueden haber para la caja
			List<MovimientoEyS> lstManuales = iskaypetApuntesService.consultarMovmientoCajaEyS(sesion.getAplicacion().getUidActividad(), caja.getUidDiarioCaja());
			((IskaypetCaja)caja).setLstMovimientoEyS(lstManuales);
			
			cerrarCaja(sqlSession, caja, fechaCierre);

			sqlSession.commit();
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error cerrando caja con fecha de cierre" + fechaCierre + " :" + e.getMessage();
			log.error("cerrarCaja() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error realizando cierre de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}
	
	@Override
	public Caja crearCaja(Date fechaApertura) throws CajasServiceException, CajaEstadoException {
		SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
		try {
			sqlSession.openSession(SessionFactory.openSession());
			String uidActividad = sesion.getAplicacion().getUidActividad();
			String codAlmacen = sesion.getAplicacion().getCodAlmacen();
			String codCaja = sesion.getAplicacion().getCodCaja();

			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(uidActividad).andCodAlmacenEqualTo(codAlmacen).andCodcajaEqualTo(codCaja).andFechaCierreIsNull();
			log.debug("crearCaja() - consultado caja con codAlmacen" + codAlmacen + "  y codCaja " + codCaja);
			List<CajaBean> cajasBean = cajaMapper.selectByExample(exampleCaja);

			if (!cajasBean.isEmpty()) {
				log.warn("crearCaja() - Error creando caja. La caja esta marcada como abierta");
				throw new CajaEstadoException(I18N.getTexto("Ya existe una caja abierta en el sistema "));
			}
			CajaBean cajaBean = new CajaBean();
			cajaBean.setUidActividad(uidActividad);
			cajaBean.setUidDiarioCaja(UUID.randomUUID().toString());
			cajaBean.setCodAlmacen(codAlmacen);
			cajaBean.setCodCaja(codCaja);
			cajaBean.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
			cajaBean.setFechaApertura(fechaApertura);
			cajaBean.setFechaCierre(null);
			cajaBean.setUsuarioCierre(null);
			cajaBean.setFechaEnvio(null);
			
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaContable = formatter.parse(formatter.format(fechaApertura));

			
			cajaBean.setFechaContable(fechaContable);
			log.debug("crearCaja() - Insertando caja con codAlmacen " + codAlmacen + "  y codCaja " + codCaja);

			// Dependiendo si la fecha tiene hora o no, llamamos a un método u otro del mapper
			Calendar calendarApertura = Calendar.getInstance();
			calendarApertura.setTime(fechaApertura);
			if (calendarApertura.get(Calendar.HOUR_OF_DAY) == 0 && calendarApertura.get(Calendar.MINUTE) == 0 && calendarApertura.get(Calendar.SECOND) == 0
			        && calendarApertura.get(Calendar.MILLISECOND) == 0) {
				cajaMapper.insertFechaAperturaDate(cajaBean);
			}
			else {
				cajaMapper.insertFechaAperturaDateTime(cajaBean);
			}

			sqlSession.commit();
			return new IskaypetCaja(cajaBean);
		}
		catch (CajaEstadoException e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error insertando caja con fecha de apertura" + fechaApertura + " :" + e.getMessage();
			log.error("crearCaja() - " + msg);
			throw e;
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error insertando caja con fecha de apertura" + fechaApertura + " :" + e.getMessage();
			log.error("crearCaja() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error realizando apertura de caja"), e);
		}
		finally {
			sqlSession.close();
		}
	}
	@Override
	public Caja consultarCajaAbierta() throws CajasServiceException, CajaEstadoException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
			        .andCodcajaEqualTo(sesion.getAplicacion().getCodCaja()).andFechaCierreIsNull();
			log.debug("consultarCajaAbierta() - Consultado caja abierta en sesion");
			List<CajaBean> cajasBean = cajaMapper.selectByExample(exampleCaja);

			if (cajasBean.isEmpty()) {
				throw new CajaEstadoException(I18N.getTexto("No existe caja abierta en el sistema"));
			}
			return new IskaypetCaja(cajasBean.get(0));
		}
		catch (CajaEstadoException e) {
			throw e;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando caja abierta en sesion :" + e.getMessage();
			log.error("consultarCajaAbierta() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar caja abierta en sesión del sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void impresionDeCierre(Caja caja) throws CajasServiceException, DeviceException {

		log.debug("impresionDeCierre() - Imprimiendo ticket: " + caja.getUidDiarioCaja());
		// String printTicket = VelocityServices.getInstance().getPrintCierreCaja(caja);

		// Rellenamos los parametros
		EmpresaBean empresa = sesion.getAplicacion().getEmpresa();
		Date fechaImpresion = new Date();
		//GAP 79 Gestión de promociones selección múltiples
		BigDecimal retiradas = BigDecimal.ZERO;
		BigDecimal salidaCaja = BigDecimal.ZERO;
		BigDecimal saldoInicial = BigDecimal.ZERO;
		BigDecimal entradaEfectivo = BigDecimal.ZERO;

		for (CajaMovimientoBean lineaMov : caja.getMovimientos()) {
			if (lineaMov.getCodConceptoMovimiento() != null) {
				switch (lineaMov.getCodConceptoMovimiento()) {
					// SALDO INICIAL
					case "00":
						saldoInicial = saldoInicial.add(lineaMov.getCargo().subtract(lineaMov.getAbono()));
						break;
					// RETIRADAS
					case "01":
						retiradas = retiradas.add(lineaMov.getCargo().subtract(lineaMov.getAbono()));
						break;
					// SALIDA DE CAJA
					case "02":
						salidaCaja = salidaCaja.add(lineaMov.getCargo().subtract(lineaMov.getAbono()));
						break;
					// ENTRADA EFECTIVO
					case "03":
						entradaEfectivo = entradaEfectivo.add(lineaMov.getCargo().subtract(lineaMov.getAbono()));
						break;
					default:
						break;
				}
			}
		}

		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("empresa", empresa);
		parametros.put("caja", caja);

		parametros.put("saldoInicial", FormatUtil.getInstance().formateaImporte(saldoInicial));
		parametros.put("retiradas", FormatUtil.getInstance().formateaImporte(retiradas));
		parametros.put("salidaCaja", FormatUtil.getInstance().formateaImporte(salidaCaja));
		parametros.put("entradaEfectivo", FormatUtil.getInstance().formateaImporte(entradaEfectivo));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(caja.getFechaApertura());
		if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0) {
			parametros.put("fechaApertura", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaApertura()));
		} else {
			parametros.put("fechaApertura", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaApertura()) + " " + FormatUtil.getInstance().formateaHora(caja.getFechaApertura()));
		}
		calendar.setTime(caja.getFechaCierre());
		if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0) {
			parametros.put("fechaCierre", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaCierre()));
		} else {
			parametros.put("fechaCierre", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaCierre()) + " " + FormatUtil.getInstance().formateaHora(caja.getFechaCierre()));
		}
		parametros.put("fechaImpresion", FormatUtil.getInstance().formateaFechaCorta(fechaImpresion) + " " + FormatUtil.getInstance().formateaHora(fechaImpresion));
		parametros.put("acumulados", new ArrayList(caja.getAcumulados().values()));

		// Indicamos si se debe imprimir el logo
		parametros.put(IMPRIMIR_LOGO, requierImprimirLogo(variablesServices));

		// Llamamos al servicio de impresión
		ServicioImpresion.imprimir(ServicioImpresion.PLANTILLAS_CIERRE_CAJA, parametros);
	}
	
	// ISK-221 GAP 27.2 Ampliación del cierre de fin de día
	public CajaMovimientoBean consultarUltimoMovimientoConVentas() throws CajasServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			log.debug("consultarUltimoMovimientoConVentas() - Consultando último movimiento de caja con ventas");
			sqlSession.openSession(SessionFactory.openSession());
			
			Date fechaActual = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(fechaActual);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			fechaActual = c.getTime();
			
			CajaMovimientoExample exampleCajaMovimiento = new CajaMovimientoExample();
			exampleCajaMovimiento.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
			.andFechaLessThan(fechaActual)
			.andIdDocumentoIsNotNull();
			
			exampleCajaMovimiento.setOrderByClause(CajaMovimientoExample.ORDER_BY_FECHA_DESC);
			List<CajaMovimientoBean> movimientosVentas = cajaMovimientoMapper.selectByExample(exampleCajaMovimiento);

			if (movimientosVentas != null && !movimientosVentas.isEmpty()) {
				return movimientosVentas.get(0);
			}
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando el último movimiento de caja con ventas: " + e.getMessage();
			log.error("consultarUltimoMovimientoConVentas() - " + msg);
			throw new CajasServiceException(I18N.getTexto("Error al consultar el último movimiento de caja con ventas en el sistema"), e);
		}
		finally {
			sqlSession.close();
		}
		return null;
	}

    @Override
    public void crearMovimientoApertura(BigDecimal importe, Date fecha) throws CajasServiceException {
        log.debug("crearMovimientoApertura() - Registrando movimiento de apertura de caja por importe: " + importe);
        SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
        try {
            sqlSession.openSession(SessionFactory.openSession());
            CajaMovimientoBean movimiento = new CajaMovimientoBean();
            movimiento.setCargo(importe);
            movimiento.setConcepto(I18N.getTexto("SALDO INICIAL"));
            movimiento.setCodMedioPago(ContadoraTipoPagosEnum.EFECTIVO.getCodPago());
            movimiento.setCodConceptoMovimiento(CODIGO_CONCEPTO_APERTURA);
            movimiento.setFecha(fecha);
            movimiento.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());

            crearMovimiento(sqlSession, movimiento);
            sqlSession.commit();
        }
        catch (CajasServiceException e) {
            sqlSession.rollback();
            throw e;
        }
        catch (Exception e) {
            sqlSession.rollback();
            String msg = "Se ha producido un error insertando movimiento de caja por apertura de caja: " + e.getMessage();
            log.error("crearMovimientoApertura() - " + msg, e);
            throw new CajasServiceException(I18N.getTexto("Error al insertar movimiento de caja"), e);
        }
        finally {
            sqlSession.close();
        }
    }

    @Override
	public CajaMovimientoBean crearMovimientoManual(BigDecimal importe, String codConcepto, String documento,
			String descConcepto) throws CajasServiceException {
		CajaConceptoBean concepto = cajaConceptosServices.getConceptoCaja(codConcepto);
		
		//Comprobamos que el movimiento sea de salida
		if(CajaConceptoBean.MOV_SALIDA.equals(concepto.getInOut())) {
			//Consultamos para saber cual es el saldo incial
			BigDecimal saldoIncial= controlRetiradasMapper.consultarSaldoInicial(sesion.getAplicacion().getUidActividad(), sesion.getSesionCaja().getUidDiarioCaja());
			
			//Luego consultamos para obtener el total de las entradas y salidas en efectivo de la tienda
			ControlRetiradas controlRetiradas = controlRetiradasMapper.consultarTotalesRecuentoEfectivo(sesion.getAplicacion().getUidActividad(), sesion.getSesionCaja().getUidDiarioCaja(), sesion.getAplicacion().getTienda().getTiendaBean().getCodMedioPagoPorDefecto());
			
			//si cumple llamamos al super
			if(!BigDecimalUtil.isMenor(controlRetiradas.getSaldoEfectivo().subtract(importe), saldoIncial)) {
				return super.crearMovimientoManual(importe, codConcepto, documento, descConcepto);
			}else {
				//si no lanzamos una excepcion

				FormatUtil.getInstance().formateaImporte(importe);
				String msg = I18N.getTexto("No puede realizarse la retirada porque el resultante queda por debajo del saldo inicial (")+ FormatUtil.getInstance().formateaImporte(saldoIncial)+
						").\n" + rPad(I18N.getTexto("Total en caja:"), ' ', 20) + lPad(FormatUtil.getInstance().formateaImporte(controlRetiradas.getSaldoEfectivo()),' ',8) +
						"\n" + rPad(I18N.getTexto("Importe a retirar:"), ' ', 20) + lPad(FormatUtil.getInstance().formateaImporte(importe),' ',8)+
						"\n" + rPad(I18N.getTexto("Resultante:"), ' ', 20) + lPad(FormatUtil.getInstance().formateaImporte(controlRetiradas.getSaldoEfectivo().subtract(importe)),' ',8);
				log.error("crearMovimientoManual() - " + msg);
				throw new CajasServiceException(msg);
			}
		}else {
			return super.crearMovimientoManual(importe, codConcepto, documento, descConcepto);
		}
		
	}
	
	public String rPad(String input, char ch, int L)
    {  
        String result = String.format("%" + (-L) + "s", input).replace(' ', ch);

        return result;
    }
	
	public String lPad(String input, char ch, int L)
    {
        String result = String.format("%" + L + "s", input).replace(' ', ch);
  
        return result;
    }
	
	
/*	//consultamos la ultima caja cerrada anterior al dia de hoy, ya que este metodo se utiliza para comprobar
	//si tenemos generado el cierreZ para el ultimo dia de venta de la tienda.
	@SuppressWarnings("static-access")
	public Caja consultarUltimaCajaAbiertaAnteriorHoy() throws CajasServiceException, CajaEstadoException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			CajaExample exampleCaja = new CajaExample();
			exampleCaja.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
			        .andFechaAperturaLessThan(closingEndDayService.setFechaPrimeraHora(new Date()));
			exampleCaja.setOrderByClause(exampleCaja.ORDER_BY_FECHA_APERTURA_DESC);
			log.debug("consultarUltimaCajaCerrada() - Consultado caja cerrada en sesion");
			List<CajaBean> cajasBean = cajaMapper.selectByExample(exampleCaja);

			if (cajasBean.isEmpty()) {
				throw new CajaEstadoException(I18N.getTexto("No existen cajas cerrada en el sistema"));
			}
			return new Caja(cajasBean.get(0));
		}
		catch (CajaEstadoException e) {
			throw e;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando caja cerrada en sesion :" + e.getMessage();
			log.error("consultarCajaAbierta() - " + msg, e);
			throw new CajasServiceException(I18N.getTexto("Error al consultar caja abierta en sesión del sistema"), e);
		}
		finally {
			sqlSession.close();
		}
	}*/

	//consultamos la ultima caja cerrada anterior al dia de hoy, ya que este metodo se utiliza para comprobar
		//si tenemos generado el cierreZ para el ultimo dia de venta de la tienda.
	public Date consultarFechaUltimaAperturaAnteriorHoy(){
		return cierreMapper.getFechaAperturaHoy(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getCodAlmacen(), closingEndDayService.setFechaPrimeraHora(new Date()));
	}
}
