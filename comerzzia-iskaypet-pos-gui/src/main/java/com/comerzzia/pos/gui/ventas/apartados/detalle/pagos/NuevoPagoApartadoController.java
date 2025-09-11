/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.gui.ventas.apartados.detalle.pagos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.medioPago.BotonBotoneraTextoComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.gui.ventas.apartados.ApartadosManager;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager.SalvarTicketCallback;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.NoCerrarPantallaException;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagoTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.events.PaymentErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.GiftCardManager;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;

/**
 * 
 * @author ABRH
 *
 */
@Component
public class NuevoPagoApartadoController extends PagosController{
	
	public static final String PARAMETRO_APARTADO_MANAGER = "MANAGER_APARTADO";
	
	protected ApartadosManager apartadoManager;
	
	@Autowired
	protected VariablesServices variablesServices;
	@Autowired
	protected MediosPagosService mediosPagosService;
	
	@Autowired
	private Sesion sesion;
	
	final IVisor visor = Dispositivos.getInstance().getVisor();
	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;
	
	@Autowired
	private GiftCardService giftCardService;
	
	private boolean enProceso = false;
	
	public void initializeForm() throws InitializeGuiException {
		enProceso = false;
		
		apartadoManager = (ApartadosManager) getDatos().remove(PARAMETRO_APARTADO_MANAGER);
		ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
		
		cargarBotoneraDatosAdicionales();
		List<ConfiguracionBotonBean> listaAccionesMPAux = new ArrayList<ConfiguracionBotonBean>();

		for(ConfiguracionBotonBean cmg : botoneraMediosPagoContado.getListaAcciones()){
			listaAccionesMPAux.add(cmg);
		}
		try{
			panelPagoContado.getChildren().clear();
			botoneraMediosPagoContado = new BotoneraComponent(3, 4, this, listaAccionesMPAux, null, panelPagoContado.getPrefHeight(), BotonBotoneraTextoComponent.class.getName());
			panelPagoContado.getChildren().add(botoneraMediosPagoContado);
		}catch(Exception e){}

		lbDocActivo.setText(ticketManager.getDocumentoActivo().getDestipodocumento());

		lbTitulo.setText(I18N.getTexto("Pagos"));
		
		panelPagos.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				if (t.booleanValue() == false && t1.booleanValue() == true){
					medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
					lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
					lbSaldo.setText("");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							tfImporte.requestFocus();
						}
					});  
				}
			}
		});

		panelEntregaCuenta.setVisible(false);

		// Establecemos el medio de pago por defecto
		medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
		lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());

		panelPagos.getSelectionModel().select(panelPestanaPagoEfectivo);
		lbSaldo.setText("");

		ticketManager.getTicket().getTotales().recalcular();
		
		visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
		escribirVisor();
		
		initializePaymentsManager();
		
		refrescarDatosPantalla();
	}
	
	protected void incluirPagoTicket(BigDecimal importe) {
		String desMedioPagoUsado = null;
		
		if(!((TicketVenta)ticketManager.getTicket()).getPagos().isEmpty()){
			desMedioPagoUsado = ((IPagoTicket)((TicketVenta)ticketManager.getTicket()).getPagos().get(0)).getDesMedioPago();
		}

		if(desMedioPagoUsado == null || desMedioPagoUsado.equals(medioPagoSeleccionado.getDesMedioPago())){
//			ticketManager.nuevaLineaPago(medioPagoSeleccionado.getCodMedioPago(), importe, true, true, null, true);
//			apartadoManager.getTicketApartado().calcularTotales();
//			refrescarDatosPantalla();
			PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(medioPagoSeleccionado.getCodMedioPago());
			if(ticketManager.getTicket().getCabecera().esVenta()){
				pay(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
			}
			else {
				returnAmount(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
			}
			apartadoManager.getTicketApartado().calcularTotales();
			refrescarDatosPantalla();
		}
		else{
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se permite el uso de diferentes medios de pago."), getStage());
		}
	}
	
	/**
	 * Acción borrar registro seleccionado de la tabla
	 */
	@FXML
	protected void accionBorrarRegistroTabla() {
		log.debug("accionBorrarRegistroTabla() - Acción ejecutada");
		if (tbPagos.getItems() != null && tbPagos.getItems().size() > 0) {
			PagoTicketGui gui = tbPagos.getSelectionModel().getSelectedItem();
			
			if(gui == null) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay ningún pago seleccionado."), getStage());
				return;
			}
			
			if(!gui.isRemovable()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El pago seleccionado no se puede borrar."), getStage());
				return;
			}
			log.debug("accionBorrarRegistroTabla() - Usuario solicita eliminar pagos.");
			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Confirme operación"), I18N.getTexto("¿Desea eliminar los registros?"), getApplication().getStage())){
				log.debug("accionBorrarRegistroTabla() - Se confirma la operación");
				// Eliminamos el registro
//				ticketManager.eliminarPagos();
				String codMedioPago = paymentsManager.getCurrentPayments().get(gui.getPaymentId()).getPaymentCode();
				PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(codMedioPago);
				
				if(isDevolucion()) {
					cancelReturn(paymentMethodManager, gui.getPaymentId());
				}
				else {
					cancelPay(paymentMethodManager, gui.getPaymentId());
				}
				visor.modoPago(visorConverter.convert(((TicketVenta) ticketManager.getTicket())));
//				ticketManager.guardarCopiaSeguridadTicket();
//				if(tarjetaRegalo!=null){
//					tarjetaRegalo = null;
//					ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
//					ticketManager.setEsOperacionTarjetaRegalo(false);
//				}

				// Refrescar datos pantalla recarga la lista de pagos en pantalla.
				refrescarDatosPantalla();
			}			
		}
		else {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Borrar pago"), I18N.getTexto("Sin pagos para eliminar."), getApplication().getStage());
		}

		tfImporte.requestFocus();
	}

	@Override
	public void borrarDatosPago(){		
//		ticketManager.getTicket().getPagos().clear();
		for(PagoTicketGui gui : tbPagos.getItems()) {
			ticketManager.deletePayment(gui.getPaymentId());
		}
		
		ticketManager.getTicket().getTotales().recalcular();

		if(ticketManager.isEsRecargaTarjetaRegalo()){
			try {
				ticketManager.eliminarTicketCompleto();
			} catch (TicketsServiceException | PromocionesServiceException | DocumentoException e) {
				log.error("accionCancelar() - Excepción capturada" + e.getMessage(), e);
			}
		}
		visor.modoPago(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
		escribirVisor();
		getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
		apartadoManager.getTicketApartado().calcularTotales();
		
		getStage().close();
	}
	
	/**
	 * Acción aceptar
	 */
	@FXML
	public void aceptar() {
		if(!enProceso){
			log.debug("aceptar()");
			if(superaImporteMaximoEfectivo()){
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. La cantidad que se quiere pagar en efectivo supera el máximo permitido ({0})", importeMaxEfectivo),getStage());
				enProceso = false;
				return;
			}
			if(!ticketManager.comprobarCierreCajaDiarioObligatorio()){
				String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
				String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar el pago. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual), getStage());
				enProceso = false;
				return;
			}
			enProceso = true;
			try {
				if ((((TicketVenta)ticketManager.getTicket()).isPagosCubiertos() && ticketManager.getDocumentoActivo().getRequiereCompletarPagos()) || !ticketManager.getDocumentoActivo().getRequiereCompletarPagos()) {
					ticketManager.salvarTicketApartado(apartadoManager.getTicketApartado().getCabecera(), getStage(), new SalvarTicketCallback() {
						
						@Override
						public void onSucceeded() {
							try {                                                   
								Map<String,Object> mapaParametros= new HashMap<String,Object>();
								mapaParametros.put("ticket",ticketManager.getTicket());
								mapaParametros.put("apartado", apartadoManager.getTicketApartado().getCabecera());
								mapaParametros.put("fecha", FormatUtil.getInstance().formateaFechaHora(new Date()));
								mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
								mapaParametros.put("cajero", sesion.getSesionUsuario().getUsuario().getDesusuario());
								mapaParametros.put("pagos", ((TicketVenta)ticketManager.getTicket()).getPagos());
								mapaParametros.put("totales", ((TicketVenta)ticketManager.getTicket()).getTotales());
	
								ServicioImpresion.imprimir(ticketManager.getTicket().getCabecera().getFormatoImpresion(), mapaParametros);
	
								if(mediosPagosService.isCodMedioPagoVale(ticketManager.getTicket().getTotales().getCambio().getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento())
										&& !BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())){
				                    Map<String,Object> mapaParametrosTicket = new HashMap<String,Object>();
				                    mapaParametrosTicket.put("ticket",ticketManager.getTicket());
				                    mapaParametrosTicket.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
				                    mapaParametrosTicket.put("importeVale", ticketManager.getTicket().getTotales().getCambio().getImporteAsString());
				                    mapaParametrosTicket.put("esCopia", Boolean.FALSE);
									ServicioImpresion.imprimir(PLANTILLA_VALE, mapaParametrosTicket);
									mapaParametrosTicket.put("esCopia", Boolean.TRUE);
									ServicioImpresion.imprimir(PLANTILLA_VALE, mapaParametrosTicket);
								}
							} catch (Exception e) {
								VentanaDialogoComponent.crearVentanaError(getApplication().getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), e);
							}
	
							//Mostramos la ventana con la información de importe pagado, cambio...
							if(ticketManager.getTicket().getTotales().getCambio().getImporte().compareTo(BigDecimal.ZERO) != 0){
								mostrarVentanaCambio();
							}
	
							ticketManager.finalizarTicket();
	
							getStage().close();
							
							enProceso = false;
						}
						
						@Override
						public void onFailure(Exception e) {
							VentanaDialogoComponent.crearVentanaError(getStage(), "Error al salvar el ticket.", e);					    
							enProceso = false;
							getStage().close();
						}
					});
				}
				else {
					log.debug("aceptar() - Pagos no cubiertos");
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Los pagos han de cubrir el importe a pagar."), this.getStage());
					enProceso = false;
				}
			}
			catch (TicketsServiceException ex) {
				VentanaDialogoComponent.crearVentanaError(getApplication().getStage(), ex.getMessageI18N(), ex);
			}
		}else{
			log.warn("aceptar() - Pago en proceso");
		}
	}

	@Override
	protected void escribirVisor() {
		visor.escribir(I18N.getTexto("TOTAL:     ") + ticketManager.getTicket().getTotales().getTotalAPagarAsString(),
					   I18N.getTexto("PENDIENTE: ") + ticketManager.getTicket().getTotales().getPendienteAsString());
	}
	
	@Override
	protected void addPayment(PaymentOkEvent eventOk) {
		BigDecimal amount = eventOk.getAmount();
		String paymentCode = ((PaymentMethodManager) eventOk.getSource()).getPaymentCode();
		Integer paymentId = eventOk.getPaymentId();
		boolean removable = eventOk.isRemovable();
		
		MedioPagoBean paymentMethod = mediosPagosService.getMedioPago(paymentCode);
		
		boolean cashFlowRecorded = ((PaymentMethodManager) eventOk.getSource()).recordCashFlowImmediately();
		
		PagoTicket payment = ticketManager.nuevaLineaPago(paymentCode, amount, true, removable, paymentId, true);
		payment.setMovimientoCajaInsertado(cashFlowRecorded);
		
		if(ticketManager.getTicket().getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO)<0){
		       amount = amount.negate();
		}
		
		if(eventOk.getSource() instanceof GiftCardManager) {
			GiftCardBean giftCard = (GiftCardBean) eventOk.getExtendedData().get(GiftCardManager.PARAM_TARJETA);
			payment.addGiftcardBean(amount, giftCard);
			
			GiftCardBean tarjetaRegaloPago = obtenerPagoTarjetaRegalo(giftCard);
			asociarPagoTarjetaRegalo(ticketManager.getTicket().getCabecera().esVenta(), tarjetaRegaloPago);
		}
		else {
			addCustomPaymentData(eventOk, payment);
		}
		
		if(paymentMethod.getTarjetaCredito() != null && paymentMethod.getTarjetaCredito()) {
			if(eventOk.getExtendedData().containsKey(BasicPaymentMethodManager.PARAM_RESPONSE_TEF)) {
				DatosRespuestaPagoTarjeta datosRespuestaPagoTarjeta = (DatosRespuestaPagoTarjeta) eventOk.getExtendedData().get(BasicPaymentMethodManager.PARAM_RESPONSE_TEF);
				payment.setDatosRespuestaPagoTarjeta(datosRespuestaPagoTarjeta);
				for(String key : eventOk.getExtendedData().keySet()) {
					payment.addExtendedData(key, eventOk.getExtendedData().get(key));
				}
			}
		}
		
//		ticketManager.guardarCopiaSeguridadTicket();
	}
	
	@Override
	protected void deletePayment(PaymentOkEvent eventOk) {
		Integer paymentId = eventOk.getPaymentId();
		
		Iterator<PagoTicketGui> it = tbPagos.getItems().iterator();
		while(it.hasNext()) {
			PagoTicketGui gui = it.next();
			if(gui.getPaymentId() != null && gui.getPaymentId().equals(paymentId)) {
				if(ticketManager.deletePayment(paymentId)) {
					it.remove();
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido borrar el pago, contacte con el administrador."), getStage());
					return;
				}		
			}
		}
		
		ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
		ticketManager.setEsOperacionTarjetaRegalo(false);
		
		deleteCustomPaymentData(eventOk, paymentId);
		
//		ticketManager.guardarCopiaSeguridadTicket();
		
		Platform.runLater(new Runnable() {
			@Override
            public void run() {
				refrescarDatosPantalla();
            }
		});
	}

	@Override
	protected void deletePayment(PaymentErrorEvent eventError) {
		Integer paymentId = eventError.getPaymentId();
		
		Iterator<PagoTicketGui> it = tbPagos.getItems().iterator();
		while(it.hasNext()) {
			PagoTicketGui gui = it.next();
			if(gui.getPaymentId() != null && gui.getPaymentId().equals(paymentId)) {
				if(ticketManager.deletePayment(paymentId)) {
					it.remove();
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido borrar el pago, contacte con el administrador."), getStage());
					return;
				}		
			}
		}
		
		ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
		ticketManager.setEsOperacionTarjetaRegalo(false);
		
		deleteCustomPaymentData(eventError, paymentId);
		
//		ticketManager.guardarCopiaSeguridadTicket();
		
		Platform.runLater(new Runnable() {
			@Override
            public void run() {
				refrescarDatosPantalla();
            }
		});
	}
	
	@Override
	public void accionCancelar() {		
		boolean hayPagos = false;
		for(IPagoTicket pago : (List<IPagoTicket>) ticketManager.getTicket().getPagos()) {
			if(pago.isEliminable()) {
				hayPagos = true;
				break;
			}
		}
		
		
		if(hayPagos){
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se han efectuados pagos. Debe cancelarlos para volver atrás."), getStage());
		}
		else {
			try {
				realizarComprobacionesTicketCierrePantalla();
			}
			catch(NoCerrarPantallaException e) {
				return;
			}
			
			visor.modoVenta(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
			escribirVisor();
			getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
			getStage().close();
		}
	}
	
}
