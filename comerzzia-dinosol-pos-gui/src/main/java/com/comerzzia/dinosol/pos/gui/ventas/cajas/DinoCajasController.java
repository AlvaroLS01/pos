package com.comerzzia.dinosol.pos.gui.ventas.cajas;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.ticket.aparcados.DinoTicketsAparcadosService;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.ventas.cajas.CajaVentasGui;
import com.comerzzia.pos.gui.ventas.cajas.CajasController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.scene.control.Button;

@Component
@Primary
public class DinoCajasController extends CajasController {
	
	private Logger log = Logger.getLogger(DinoCajasController.class);

	private static final String PERMISO_APUNTE = "INSERTAR APUNTE";
	private static final String PERMISO_ABRIR_CAJON = "ABRIR CAJON";
	public static final String PERMISO_ARQUEO_VISIBLE = "ARQUEO VISIBLE";
	public static final String PERMISO_IMPRIMIR_TICKETS = "IMPRIMIR TICKETS";
	private static final String PERMISO_IMPRIMIR_ULTIMO_CIERRE = "IMPRIMIR ÚLTIMO CIERRE";
	private static final String PERMISO_ARQUEO_DE_CAJA = "ABRIR ARQUEO DE CAJA";
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	TicketManager ticketManager;
	
	@Autowired
	private AuditoriasService auditoriasService;
	
	@Autowired
	private DinoTicketsAparcadosService ticketsAparcadosService;
	
	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;
	
	
	@Override
	public void comprobarPermisosUI() {
		Button botonApunte = null;
		Button botonUltimoCierre = null;
		Button botonArqueoDeCaja = null;
		for(Button boton : botoneraMenu.getListaBotones()) {
			String clave = ((ConfiguracionBotonBean) boton.getUserData()).getClave();
			if(clave.equals("insertarApunte")) {
				botonApunte = boton;
			}
			if(clave.equals("imprimirUltimoCierre")) {
				botonUltimoCierre = boton;
			}
			if(clave.equals("abrirRecuentoCaja")) {
				botonArqueoDeCaja = boton;
			}
		}
		
		if(botonApunte != null) {
			if(sesion.getSesionCaja().isCajaAbierta()) {
				try {
					super.compruebaPermisos(PERMISO_APUNTE);
					botonApunte.setDisable(false);
				}
				catch (SinPermisosException ex) {
					botonApunte.setDisable(true);
				}
			}
			else {
				botonApunte.setDisable(true);
			}
		}
		
		controlarPermisosImprimirUltimoCierre(botonUltimoCierre);
		
		controlarPermisosArqueoDeCaja(botonArqueoDeCaja);
		
		Button botonAbrirCajon = null;
		for(Button boton : botoneraMenu.getListaBotones()) {
			String clave = ((ConfiguracionBotonBean) boton.getUserData()).getClave();
			if(clave.equals("abrirCajon")) {
				botonAbrirCajon = boton;
			}
		}
		try {
			super.compruebaPermisos(PERMISO_ABRIR_CAJON);
			botonAbrirCajon.setDisable(false);
		}
		catch (SinPermisosException ex) {
			botonAbrirCajon.setDisable(true);
		}
		
		try {
			super.compruebaPermisos(PERMISO_ARQUEO_VISIBLE);
			tcVentasEntrada.setVisible(true);
			tcVentasSalida.setVisible(true);			
		}
		catch (SinPermisosException ex) {
			log.debug("comprobarPermisosUI() - El usuario no puede ver las columnas entrada y salida porque tiene arqueo ciego");
			tcVentasEntrada.setVisible(false);
			tcVentasSalida.setVisible(false);
		}
		
		Button botonReimprimirTicket = null;
		for(Button boton : botoneraAccionesTablaVen.getListaBotones()) {
			String clave = ((ConfiguracionBotonBean) boton.getUserData()).getClave();
			if(clave.equals("ACCION_TABLA_VEN_IMPRIMIR")) {
				botonReimprimirTicket = boton;
			}
		}
		try {
			super.compruebaPermisos(PERMISO_IMPRIMIR_TICKETS);
			botonReimprimirTicket.setDisable(false);
		}
		catch (SinPermisosException ex) {
			botonReimprimirTicket.setDisable(true);
		}
	}



	private void controlarPermisosImprimirUltimoCierre(Button botonUltimoCierre) {
		if(botonUltimoCierre != null) {
			if(sesion.getSesionCaja().isCajaAbierta()) {
				try {
					super.compruebaPermisos(PERMISO_IMPRIMIR_ULTIMO_CIERRE);
					botonUltimoCierre.setDisable(false);
				}
				catch (SinPermisosException ex) {
					botonUltimoCierre.setDisable(true);
				}
			}
			else {
				botonUltimoCierre.setDisable(true);
			}
		}
	}
	
	@Override
	public void abrirCaja() {
	    super.abrirCaja();
	    
	    comprobarPermisosUI();
	}
	
	@Override
	public void imprimirMovimiento(CajaMovimientoBean movimiento) {
		log.trace("imprimirMovimiento()");
		try {
			// Rellenamos los parametros
			Map<String, Object> contextoTicket = new HashMap<String, Object>();

			// Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
			contextoTicket.put("movimiento", movimiento);
			contextoTicket.put("caja", sesion.getSesionCaja().getCajaAbierta().getCodCaja());
			contextoTicket.put("tienda", sesion.getAplicacion().getTienda().getCodAlmacen());
			contextoTicket.put("empleado", sesion.getSesionUsuario().getUsuario().getDesusuario());
			contextoTicket.put("esCopia", true);

			// Llamamos al servicio de impresión

			ServicioImpresion.imprimir(ServicioImpresion.PLANTILLA_MOVIMIENTO_CAJA, contextoTicket);
		}
		catch (DeviceException e) {
			log.error("Error en la impresión del movimiento", e);
		}
	}
	
	@SuppressWarnings("rawtypes")
    @Override
	public void imprimirVenta(CajaVentasGui venta) {

		log.trace("imprimirVenta()");
		try {
			// cargamos el ticket y lo imprimimos
			TicketBean ticketConsultado = ticketsService.consultarTicket(venta.getIdDocumento(), sesion.getAplicacion().getUidActividad());
			if(ticketConsultado != null) {
				TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ticketConsultado.getIdTipoDocumento());
	
				ITicket ticketOperacion = (TicketVenta) MarshallUtil.leerXML(ticketConsultado.getTicket(), getTicketClasses(documento).toArray(new Class[] {}));
	
				// Se reimprime la misma
				Map<String, Object> mapaParametros = new HashMap<String, Object>();
				mapaParametros.put("ticket", ticketOperacion);
				mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
				mapaParametros.put("esCopia", true);
	
				ServicioImpresion.imprimir(ticketOperacion.getCabecera().getFormatoImpresion(), mapaParametros);
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El ticket seleccionado no se ha encontrado en esta caja."), getStage());
			}
		}
		catch (TicketsServiceException ex) {
			log.error("refrescarDatosPantalla() - " + ex.getMessage());
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error leyendo información de ticket"), ex);
		}
		catch (DocumentoException e) {
			log.error("Error recuperando el tipo de documento del ticket.", e);
		}
		catch (DeviceException ex) {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Fallo al imprimir ticket."), getStage());
		}
	}
	
	@Override
	public void insertarApunte() {
		if (!ticketManager.comprobarCierreCajaDiarioObligatorio()) {
			String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
			String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
	    	
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se pueden insertar movimientos. El día de apertura de la caja {0} no coincide con la del sistema {1}", fechaCaja, fechaActual), getStage());
		} else {
			super.insertarApunte();
		}
	}
	
	@Override
	public void abrirCajon() {
		super.abrirCajon();
        
        AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setTipo("ABRIR CAJÓN");
		auditoriasService.guardarAuditoria(auditoria);
	}
	
	@Override
	public void abrirCierreCaja() {
		if (ticketsAparcadosService.isTicketAparcadoRemotoActivo()) {
			try {
				String codUsuario = sesion.getSesionCaja().getCajaAbierta().getUsuario();
				
				List<TicketAparcadoBean> ticketsAparcados = ticketsAparcadosService.consultarTickets(null, null, null, null);
				
				for(TicketAparcadoBean ticket : ticketsAparcados) {
					if(ticket.getUsuario().equals(codUsuario)) {
						log.debug("accionCierreCaja() - Hay tickets aparcados en la caja central para el usuario " + codUsuario);
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La caja no se puede cerrar. Hay tickets aparcados en la caja central."), getStage());
						return;
					}
				}
				
				super.abrirCierreCaja();
			}
			catch (Exception e) {
				log.warn("accionCierreCaja() - Error al consultar los tickets aparcados en la caja central: " + e.getCause(), e);
				
				super.abrirCierreCaja();
			}
		}
		else {
			super.abrirCierreCaja();
		}
	}
	
	private void controlarPermisosArqueoDeCaja(Button botonArqueoCaja) {

		if(botonArqueoCaja != null) {
			if(sesion.getSesionCaja().isCajaAbierta()) {
				try {
					super.compruebaPermisos(PERMISO_ARQUEO_DE_CAJA);
					botonArqueoCaja.setDisable(false);
				}
				catch (SinPermisosException ex) {
					botonArqueoCaja.setDisable(true);
				}
			}
			else {
				botonArqueoCaja.setDisable(true);
			}
		}
	}
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		super.initializeComponents();
		
		consultarCopiaSeguridad();
	}

	protected void consultarCopiaSeguridad() throws InitializeGuiException {
		try {
			TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
			TicketAparcadoBean copiaSeguridad = copiaSeguridadTicketService.consultarCopiaSeguridadTicket(tipoDocumentoActivo);
	
			if (copiaSeguridad != null) {
				TicketVentaAbono ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(copiaSeguridad.getTicket(), ticketManager.getTicketClasses(tipoDocumentoActivo).toArray(new Class[] {}));
	
				if (ticketRecuperado != null) {
					getApplication().getMainView().showActionView(201L, datos);
					throw new InitializeGuiException(false);
				}
			}
		}
		catch (InitializeGuiException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("consultarCopiaSeguridad() - No se ha podido consultar la copia de seguridad: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Ha fallado la apertura de la pantalla al intentar comprobar si hay tickets pendientes. Por favor, contacte con un administrador."), getStage());
			throw new InitializeGuiException(false);
		}
	}
	
}
