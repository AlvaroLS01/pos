package com.comerzzia.dinosol.pos.gui.ventas.devoluciones;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuarioController;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.gui.ventas.devoluciones.IntroduccionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosView;
import com.comerzzia.pos.services.payments.PaymentsManager;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionCambioException;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionNuevoArticuloException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class DinoIntroduccionArticulosController extends IntroduccionArticulosController{
	
	private static final Logger log = Logger.getLogger(DinoIntroduccionArticulosController.class.getName());
	
	final IVisor visor = Dispositivos.getInstance().getVisor();
	
	@Autowired
	private PaymentsManager paymentsManager;
	
	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	
	@Override
	public void initializeForm() throws InitializeGuiException {
	    super.initializeForm();
	    
	    initializePaymentsManager();
	}
	
	protected void initializePaymentsManager() {
		paymentsManager = ContextHolder.get().getBean(PaymentsManager.class);
		paymentsManager.setPaymentsMethodsConfiguration(paymentsMethodsConfiguration);
		paymentsManager.setTicketData(ticketManager.getTicket(), ticketManager.getTicketOrigen());
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public void abrirPagos() {
		BigDecimal amountTotal = ticketManager.getTicket().getTotales().getTotalAPagar();
		BigDecimal amountPayments = BigDecimal.ZERO;

		for (PagoTicket pagoTicket : (List<PagoTicket>) ticketManager.getTicketOrigen().getPagos()) {
			if (paymentsManager.isPaymentMethodAvailableForReturn(pagoTicket.getCodMedioPago())) {
				amountPayments = amountPayments.add(pagoTicket.getImporte());
			}
		}

		if (BigDecimalUtil.isMenor(amountPayments.abs(), amountTotal.abs())) {
			String message = I18N.getTexto("No se puede realizar la devolución por los medios de pago empleados en la venta original.")
					+ System.lineSeparator()
					+ System.lineSeparator()
					+ I18N.getTexto("Deberá quitar artículos para poder realizar la devolución.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(message), getStage());
			return;
		}
		
	    super.abrirPagos();
	}
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		super.initializeComponents();
		
		tfCantidadIntro.setFocusTraversable(false);
		tfCantidadIntro.setEditable(false);
		tfCantidadIntro.setDisable(true);
		tfCodigoIntro.requestFocus();
		tfCodigoIntro.setFocusTraversable(true);
		tbLineas.setFocusTraversable(false);

	}
	
	public void TraspasarCantidadToCasillaCantidad() {
		String strCantidad = tfCodigoIntro.getText().replace("*", "").trim();
		if (strCantidad.equals("")) {
			tfCodigoIntro.setText("");
			return;
		}
		try {
			Double intCantidad = Double.parseDouble(strCantidad.replace(",", "."));

			DecimalFormat df = new DecimalFormat("0.000");
			strCantidad = df.format(intCantidad).replace(".", ",");
			tfCantidadIntro.setText(strCantidad);
			tfCodigoIntro.setText("");
		}
		catch (NumberFormatException e) {
			log.debug("Se ha pulsado * sin cantidad previa valida.");
		}
	}
	
	private void resetearCantidad() {
		tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
	}
	
	
	@Override
	public void actionTfCodigoIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.MULTIPLY) {
			TraspasarCantidadToCasillaCantidad();
		}
		if (event.getCode() == KeyCode.ENTER) {
			nuevoCodigoArticulo();
		}
	}
	
	@Override
	public void nuevoCodigoArticulo() {
		log.debug("nuevoCodigoArticulo() - Creando línea de artículo");

		// Validamos los datos
		if (!tfCodigoIntro.getText().trim().isEmpty()) {
			frValidacion.setCantidad(tfCantidadIntro.getText().trim());
			frValidacion.setCodArticulo(tfCodigoIntro.getText().trim().toUpperCase());
			tfCodigoIntro.clear();
			resetearCantidad();
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));

			if (accionValidarFormulario()) {
				log.debug("nuevoCodigoArticulo()- Formulario validado");

				new NuevoCodigoArticuloTask(frValidacion.getCodArticulo(), frValidacion.getCantidadAsBigDecimal()).start();
			}
		}
	}

	@Override
	protected void accionTablaEliminarRegistro() {
		log.debug("accionTablaEliminarRegistro() - ");
		try {
			LineaTicketGui selectedItem = getLineaSeleccionada();
			if (!tbLineas.getItems().isEmpty() && selectedItem != null) {
				super.compruebaPermisos(PERMISO_BORRAR_LINEA);
				boolean confirmar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar esta línea del ticket?"), getStage());
				if (!confirmar) {
					return;
				}
				ticketManager.eliminarLineaArticulo(selectedItem.getIdLinea());
				seleccionarSiguienteLinea();
				resetearCantidad();
				refrescarDatosPantalla();
			}
		}
		catch (SinPermisosException ex) {
			log.debug("accionTablaEliminarRegistro() - El usuario no tiene permisos para eliminar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para borrar una línea."), getStage());
		}
	}
	

	@Override
	public void abrirBusquedaArticulos() {
		log.debug("abrirBusquedaArticulos()");

		// Validamos el campo cantidad antes de iniciar la búsqueda. Si el campo es vacío lo seteamos a 1 sin devolver
		// error
		if (tfCantidadIntro.getText().trim().equals("")) {
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
		}
		// Validamos que hay introducida una cantidad válida de artículos . Nota : También valida el campo código
		// introducido. Podemos crear otro metodo de validación para que no lo haga
		frValidacionBusqueda.setCantidad(tfCantidadIntro.getText());
		if (!accionValidarFormularioBusqueda()) {
			return; // Si la validación de la cantidad no es satisfactoria, no realizamos la búsqueda
		}

		datos = new HashMap<>();
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CLIENTE, ticketManager.getTicket().getCliente());
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CODTARIFA, ticketManager.getTarifaDefault());
		getDatos().put(BuscarArticulosController.PARAM_MODAL, Boolean.TRUE);
		getDatos().put(SeleccionUsuarioController.PARAMETRO_ES_STOCK, Boolean.FALSE);
		getApplication().getMainView().showModal(BuscarArticulosView.class, datos);
		initializeFocus();	

		if (datos.containsKey(BuscarArticulosController.PARAMETRO_SALIDA_CODART)) {
			log.debug("realizarAccion() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
			String codArt = (String) datos.get(BuscarArticulosController.PARAMETRO_SALIDA_CODART);
			String desglose1 = (String) datos.get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE1);
			String desglose2 = (String) datos.get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE2);

			frValidacionBusqueda.setCantidad(tfCantidadIntro.getText());
			try {
				if (accionValidarFormularioBusqueda()) {
					LineaTicket lineaTicket = nuevaLineaArticulo(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());
					ticketManager.getTicket().getTotales().recalcular();
					resetearCantidad();
					refrescarDatosPantalla();
					visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));

					asignarNumerosSerie(lineaTicket);

					visor.escribir(lineaTicket.getDesArticulo(), lineaTicket.getCantidadAsString() + " X " + FormatUtil.getInstance().formateaImporte(lineaTicket.getPrecioTotalConDto()));
				}
			}
			catch (LineaTicketException ex) {
				if (ex instanceof LineaDevolucionCambioException && tienePermisosCambioArticulo()) {
					introducirArticuloCambio(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());
				}
				else if (ex instanceof LineaDevolucionNuevoArticuloException && tienePermisosIntroducirNuevoArticulo()) {
					introducirNuevoArticulo(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());
				}
				else {
					log.error("realizarAccion() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
					VentanaDialogoComponent.crearVentanaError(ex.getLocalizedMessage(), this.getScene().getWindow());
				}

			}
		}
	}
	
	protected void formateaCantidad() {
		nuevaCantidad();
		frValidacion.setCantidad(tfCantidadIntro.getText().trim());
		frValidacion.setCodArticulo(tfCodigoIntro.getText().trim());
		if (accionValidarFormulario()) {
			BigDecimal bigDecimal = FormatUtil.getInstance().desformateaBigDecimal(tfCantidadIntro.getText().trim());
			bigDecimal = bigDecimal.abs();
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(bigDecimal, 3));
		}
		else {
			resetearCantidad();
			cambiarCantidad();
		}
	}


	
}
