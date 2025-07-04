package com.comerzzia.cardoso.pos.services.promociones.empleado;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.gui.ventas.tickets.pagos.rest.empleados.CARDOSOClientRestPromocionEmpleados;
import com.comerzzia.cardoso.pos.persistence.promociones.PromoCuentaBean;
import com.comerzzia.cardoso.pos.services.rest.PosRestService;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.POSTicketMapper;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.xml.MarshallUtil;

@Component
public class ServicioAnulacionPromocionEmpleado {

	private Logger log = Logger.getLogger(ServicioAnulacionPromocionEmpleado.class);

	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private Sesion sesion;
    @Autowired
    protected POSTicketMapper ticketMapper;
	@Autowired
	private TicketsService ticketsService;
	@Autowired
	private ServicioContadores servicioContadores;
	@Autowired
	private PosRestService posRestService;

	public void enviar(AnulacionPromocionEmpleadoBean parametrosAnulacionPromocionEmpleado) throws PromocionEmpleadoException {
		try {
			validarDatos(parametrosAnulacionPromocionEmpleado);
			String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
			String uidActividad = sesion.getAplicacion().getUidActividad();
			PromoCuentaBean promo = new PromoCuentaBean();
			promo.setIdPromocion(parametrosAnulacionPromocionEmpleado.getIdPromocion());
			promo.setNumeroTarjeta(parametrosAnulacionPromocionEmpleado.getNumTarjeta());
			promo.setUidTransaccion(parametrosAnulacionPromocionEmpleado.getUidTransaccion());
			promo.setReferenciaUso(parametrosAnulacionPromocionEmpleado.getReferenciaUso());
			promo.setFechaUso(new Date());
			promo.setImporteUso(parametrosAnulacionPromocionEmpleado.getImporteUso());
			promo.setImporteTotal(parametrosAnulacionPromocionEmpleado.getImporteTotal());
			/*String restURL = variablesServices.getVariableAsString(VariablesServices.REST_URL);
			restURL = restURL.replaceAll("ws", "cardoso-ws");
			BackofficeWebservicesPath.initPath(restURL);
			PromoFidelizadoRequestRest request = new PromoFidelizadoRequestRest(apiKey, uidActividad, promo);
			ResponseDeleteRest response = FidelizadosRest.anularMovimientoProvisionalTarjetaRegaloDefinitivo(request);
			if (response.getCodError() == 7) {
				throw new Exception("Ha habido un error al enviar al REST la anulación de la promoción de empleado");
			}*/
			CARDOSOClientRestPromocionEmpleados.anularUso(posRestService.getUrlApiV1(), apiKey, uidActividad, promo.getIdPromocion(), promo.getNumeroTarjeta(), promo.getUidTransaccion());
		}
		catch (Exception e) {
			throw new PromocionEmpleadoException("enviar() - Ha habido un error al enviar al REST la anulación de la promoción de empleado: " + e.getMessage(), e);
		}
	}

	private void validarDatos(AnulacionPromocionEmpleadoBean parametros) throws Exception {
		if (!(parametros.getIdPromocion() != null && StringUtils.isNotBlank(parametros.getNumTarjeta()) && StringUtils.isNotBlank(parametros.getUidTransaccion())
		        && StringUtils.isNotBlank(parametros.getReferenciaUso()) && BigDecimalUtil.isMayorACero(parametros.getImporteUso()) && BigDecimalUtil.isMayorACero(parametros.getImporteTotal()))) {
			throw new Exception("Los datos del fichero no están completos");
		}
	}

	public void crearDocumento(AnulacionPromocionEmpleadoBean anulacion) {
		log.debug("crearDocumentoAnulacionBonos() - Creando documento de anulación de pagos con Bonopoly");

		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());

			ContadorBean ticketContador = servicioContadores.obtenerContador("ID_ANULA_EMP", null, sesion.getAplicacion().getUidActividad());

			TicketBean ticketBean = new TicketBean();
			ticketBean.setUidActividad(sesion.getAplicacion().getUidActividad());
			ticketBean.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
			ticketBean.setCodcaja(sesion.getAplicacion().getCodCaja());
			ticketBean.setFecha(new Date());
			ticketBean.setIdTicket(ticketContador.getValor());
			ticketBean.setUidTicket(UUID.randomUUID().toString());
			ticketBean.setIdTipoDocumento(7000L);
			ticketBean.setCodTicket("*");
			ticketBean.setSerieTicket("*");
			ticketBean.setFirma("*");
			byte[] xmlTicket = MarshallUtil.crearXML(anulacion);
			ticketBean.setTicket(xmlTicket);
			
			/* Necesario ya que la versión 3.0 no tiene este campo, y en la versión 4.8 es obligatorio. */
			if(StringUtils.isBlank(ticketBean.getLocatorId())){
				ticketBean.setLocatorId(ticketBean.getUidTicket());
			}

			log.debug("crearDocumento() - Creando ticket de anulación de promoción de empleado para el ticket " + anulacion.getUidTransaccion());
			ticketMapper.insert(ticketBean);
		}
		catch (Exception e) {
			log.error("crearDocumentoAnulacionBonos() - Ha habido un error al guardar el movimiento del apartado: " + e.getMessage(), e);
		}
		finally {
			sqlSession.close();
		}
	}

}
