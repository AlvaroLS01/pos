package com.comerzzia.dinosol.pos.gui.ventas.devoluciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.core.servicios.documents.LocatorParseException;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.payments.methods.types.DinoPaymentsManagerImpl;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.devoluciones.DevolucionesController;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.gui.ventas.devoluciones.IntroduccionArticulosView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Primary
public class DinoDevolucionesController extends DevolucionesController{
	
	private static final Logger log = Logger.getLogger(DinoDevolucionesController.class.getName());
	
	final IVisor visor = Dispositivos.getInstance().getVisor();
	
	@Autowired
	private LocatorManager locatorManager;
	
	@Override
	protected boolean validarFormularioConsultaCliente() {

		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frConsultaTicket.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbMensajeError.setText("");

		frConsultaTicket.setCodCaja(tfCodCaja.getText());
		frConsultaTicket.setCodOperacion(tfOperacion.getText());
		frConsultaTicket.setCodTienda(tfTienda.getText());
		frConsultaTicket.setCodDoc(tfCodDoc.getText());
		
		String codigo = frConsultaTicket.getCodOperacion();
		try {
			locatorManager.decode(codigo);
		}
		catch (LocatorParseException e) {
			//No es localizador válido
			if(codigo.length() > 10){
				//No es codDocumento válido
				lbMensajeError.setText(I18N.getTexto("El código {0} no es un localizador o un código de documento válido", codigo));
				valido = false;
				return valido;
			}
		}

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioConsultaTicketBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frConsultaTicket);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioConsultaTicketBean> next = constraintViolations.iterator().next();
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(next.getMessage()), getStage());
			if(StringUtils.isBlank(frConsultaTicket.getCodCaja())){
				tfCodDoc.requestFocus();
				tfCodCaja.requestFocus();
			}else if(StringUtils.isBlank(frConsultaTicket.getCodOperacion())){
				tfCodCaja.requestFocus();
				tfOperacion.requestFocus();
			}else if(StringUtils.isBlank(frConsultaTicket.getCodTienda())){
				tfOperacion.requestFocus();
				tfTienda.requestFocus();
			}else if(StringUtils.isBlank(frConsultaTicket.getCodDoc())){
				tfTienda.requestFocus();
				tfCodDoc.requestFocus();
			}
			valido = false;
		} else {
			valido = true;
		}

		return valido;
	
	}
	
	@Override
	protected void recuperarTicketDevolucionSucceeded(boolean encontrado) {
		if (encontrado) {
			if(!comprobarMediosPago()) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La venta original fue pagada con unas formas de pago que no permiten la devolución."), getStage());
				return;
			}
			
			boolean esMismoTratamientoFiscal = ticketManager.comprobarTratamientoFiscalDev();
			if(!esMismoTratamientoFiscal) {
				try {
					ticketManager.eliminarTicketCompleto();
				} catch (Exception e) {
					log.error("recuperarTicketDevolucionSucceeded() - Ha habido un error al eliminar los tickets: " + e.getMessage(), e);
				}
				
				lbMensajeError.setText(I18N.getTexto("El ticket fue realizando en una tienda con un tratamiento fiscal diferente al de esta tienda. No se puede realizar esta devolución."));
			}
			else {
				try {
					getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
					getView().changeSubView(IntroduccionArticulosView.class);
				} catch (InitializeGuiException e) {
					if(e.isMostrarError()){
						log.error("accionCambiarArticulo() - Error abriendo ventana", e);
						VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), e);
					}
				}
			}
			if(ticketManager.getTicketOrigen().getCabecera() instanceof DinoCabeceraTicket) {
				boolean recuperadoTicketDesdeCentral = ((DinoCabeceraTicket) ticketManager.getTicketOrigen().getCabecera()).isRecuperadoTicketOrigenCentral();
				if(!recuperadoTicketDesdeCentral) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se han podido recuperar las líneas devueltas desde la central. Por favor, compruebe el ticket impreso para ver las líneas devueltas ya."), getStage());
				}
			}
			
		} else {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"), getStage());

		}
    
	}
	  
	@SuppressWarnings("unchecked")
    private boolean comprobarMediosPago() {
		List<String> mediosPagoOriginales = new ArrayList<String>();
		for(PagoTicket pagoOriginal : (List<PagoTicket>) ticketManager.getTicketOrigen().getPagos()) {
			mediosPagoOriginales.add(pagoOriginal.getCodMedioPago());
		}
		
		if(mediosPagoOriginales.contains(DinoPaymentsManagerImpl.PAYMENT_CODE_CASH) 
				|| mediosPagoOriginales.contains(DinoPaymentsManagerImpl.PAYMENT_CODE_CREDIT_CARD) 
				|| mediosPagoOriginales.contains(DinoPaymentsManagerImpl.PAYMENT_CODE_DATAPHONE)) {
			return true;
		}
		
	    return false;
    }

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		visor.modoEspera();
	}

	@Override
	public void initializeComponents() {
	    		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfOperacion.setUserData(keyboardDataDto);
		tfTienda.setUserData(keyboardDataDto);
		tfCodCaja.setUserData(keyboardDataDto);
		tfCodDoc.setUserData(keyboardDataDto);
		
		addSeleccionarTodoEnFoco(tfOperacion);
		addSeleccionarTodoEnFoco(tfTienda);
		addSeleccionarTodoEnFoco(tfCodCaja);
		addSeleccionarTodoEnFoco(tfCodDoc);
				
	}
	
	@Override
	public void accionAceptar() {
        lbMensajeError.setText("");
        if(validarFormularioConsultaCliente()){
            
        	ticketManager = SpringContext.getBean(TicketManager.class);
            String codTienda = tfTienda.getText();
            String codCaja = tfCodCaja.getText();
            String codigo = tfOperacion.getText();
            String codDoc = tfCodDoc.getText();
            
            try {
				if(ticketManager.comprobarConfigContador(documentos.getDocumentoAbono(codDoc).getCodtipodocumento())){
					Long idTipoDocumento = documentos.getDocumento(codDoc).getIdTipoDocumento();
					new RecuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDocumento).start();
				}else{
					ticketManager.crearVentanaErrorContador(getStage());
				}
			} catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), codDoc), e);
			}
        }
        btAceptar.requestFocus();
    }
	
	


	
}
