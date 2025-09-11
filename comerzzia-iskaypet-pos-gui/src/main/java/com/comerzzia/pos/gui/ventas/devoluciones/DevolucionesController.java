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


package com.comerzzia.pos.gui.ventas.devoluciones;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.core.servicios.documents.LocatorParseException;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class DevolucionesController extends Controller{

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DevolucionesController.class.getName());
    
    final IVisor visor = Dispositivos.getInstance().getVisor();
    
    @Autowired
    private Sesion sesion;
    @Autowired
    private VariablesServices variablesServices;
    
    @FXML
    protected TextField tfOperacion, tfTienda, tfCodCaja, tfCodDoc, tfDesDoc;
    
    @FXML
    protected Label lbMensajeError;
    
    @FXML
    protected Button btAceptar, btDoc;
    
    protected FormularioConsultaTicketBean frConsultaTicket;
    
    protected TicketManager ticketManager;
    
    @Autowired
    protected Documentos documentos;
    
    @Autowired
    private LocatorManager locatorManager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        frConsultaTicket = SpringContext.getBean(FormularioConsultaTicketBean.class);

        frConsultaTicket.setFormField("codTienda", tfTienda);
        frConsultaTicket.setFormField("codOperacion", tfOperacion);
        frConsultaTicket.setFormField("codCaja", tfCodCaja);
        frConsultaTicket.setFormField("tipoDoc", tfCodDoc);
    }

    @Override
    public void initializeComponents() {
        
        tfCodDoc.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                    procesarTipoDoc();
                }
            }
        });
		addSeleccionarTodoEnFoco(tfOperacion);
		addSeleccionarTodoEnFoco(tfCodCaja);
		addSeleccionarTodoEnFoco(tfTienda);
		addSeleccionarTodoEnFoco(tfCodDoc);
    }

    @Override
    public void initializeForm() throws InitializeGuiException {
	    
	    ticketManager = SpringContext.getBean(TicketManager.class);
    	
    	// Realizamos las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
    	try {
	        comprobarAperturaPantalla();
        }
        catch (CajasServiceException | CajaEstadoException e) {
        	log.error("initializeForm() - Error inicializando pantalla:" + e.getMessageI18N(), e);
			throw new InitializeGuiException(e.getMessageI18N(), e);
        }

        visor.escribirLineaArriba(I18N.getTexto("--NUEVA DEVOLUCION--"));
    	
        tfTienda.setText(sesion.getAplicacion().getTienda().getCodAlmacen());
        tfCodCaja.setText(sesion.getAplicacion().getCodCaja());
        tfOperacion.setText("");

        List<String> tiposDocumentoAbonables = documentos.getTiposDocumentoAbonables();
        if (tiposDocumentoAbonables.isEmpty()) {
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está configurado el tipo de documento nota de crédito en el entorno."), this.getStage());
            btAceptar.setDisable(true);
        } else {
            btAceptar.setDisable(false);
        }

        for(String tipoDoc : tiposDocumentoAbonables) {
            try {
                if(documentos.getDocumento(tipoDoc) != null) {
                    TipoDocumentoBean docPreseleccion = documentos.getDocumento(tipoDoc);
                    tfCodDoc.setText(docPreseleccion.getCodtipodocumento());
                    tfDesDoc.setText(docPreseleccion.getDestipodocumento());
                    break;
                }
            }
            catch (DocumentoException ex) {
                log.error("No se ha encontrado el documento asociado",ex);
            }
        }

        lbMensajeError.setText("");
    }

    /**
	 * Realiza las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
	 * @throws CajasServiceException
	 * @throws CajaEstadoException
	 * @throws InitializeGuiException
	 */
	protected void comprobarAperturaPantalla() throws CajasServiceException, CajaEstadoException, InitializeGuiException {
	    if (!sesion.getSesionCaja().isCajaAbierta()) {
	    	Boolean aperturaAutomatica = variablesServices.getVariableAsBoolean(VariablesServices.CAJA_APERTURA_AUTOMATICA, true);
	    	if(aperturaAutomatica){
	    		VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No hay caja abierta. Se abrirá automáticamente."), getStage());
	    		sesion.getSesionCaja().abrirCajaAutomatica();
	    	}else{
	    		VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."), getStage());
	    		throw new InitializeGuiException(false);
	    	}
	    }
	    
	    if(!ticketManager.comprobarCierreCajaDiarioObligatorio()){
	    	String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
	    	String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
	    	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual), getStage());
	    	throw new InitializeGuiException(false);
	    }
    }
    
    @Override
    public void initializeFocus() {
        tfOperacion.requestFocus();
    }

    @FXML
    public void keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !btAceptar.isDisable()) {
        	accionAceptar();
        }
    }
    
    @FXML
    public void accionAceptar(){
        lbMensajeError.setText("");
        if(validarFormularioConsultaCliente()){
            
        	ticketManager = SpringContext.getBean(TicketManager.class);
            String codTienda = frConsultaTicket.getCodTienda();
            String codCaja = frConsultaTicket.getCodCaja();
            String codigo = frConsultaTicket.getCodOperacion();
            String codDoc = frConsultaTicket.getCodDoc();
            
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
    }
    
    public class RecuperarTicketDevolucion extends BackgroundTask<Boolean>{
    	private String codigo;
    	private String codTienda, codCaja;
    	private Long idTipoDoc;
    	
		public RecuperarTicketDevolucion(String codigo, String codTienda, String codCaja, Long idTipoDoc) {
			this.codigo = codigo;
			this.codTienda = codTienda;
			this.codCaja = codCaja;
            this.idTipoDoc = idTipoDoc;
		}

		@Override
		protected Boolean call() throws Exception {
			return ticketManager.recuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDoc);
		}

		@Override
		protected void failed() {
			super.failed();
			if(getException() instanceof com.comerzzia.pos.util.exception.Exception){
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}else{
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), getException());
			}
		}

		@Override
		protected void succeeded() {
			boolean res = getValue();
			recuperarTicketDevolucionSucceeded(res);
			super.succeeded();
		}
    	
    }
    
    protected void recuperarTicketDevolucionSucceeded(boolean encontrado) {
		if (encontrado) {
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
			
			boolean recoveredOnline = ticketManager.getTicket().getCabecera().getDatosDocOrigen().isRecoveredOnline();
			if(!recoveredOnline) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se han podido recuperar las líneas devueltas desde la central. Por favor, compruebe el ticket impreso para ver las líneas ya devueltas."), getStage());
			}
		} else {
			lbMensajeError.setText(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"));
		}
    }
    
    @FXML
    public void accionCancelar(){
        getApplication().getMainView().close();
    }

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
			frConsultaTicket.setErrorStyle(next.getPropertyPath(), true);
			frConsultaTicket.setFocus(next.getPropertyPath());
			lbMensajeError.setText(next.getMessage());
			valido = false;
		} else {
			valido = true;
		}

		return valido;
	}
    
    @FXML
    public void accionBuscarTipoDoc(){
    	datos = new HashMap<String, Object>();
        datos.put(TipoDocumentoController.PARAMETRO_ENTRADA_POSIBLES_DOCS, documentos.getTiposDocumentoAbonables());

        getApplication().getMainView().showModalCentered(TipoDocumentoView.class, datos, this.getStage());

        if(datos.containsKey(TipoDocumentoController.PARAMETRO_SALIDA_DOC)){
            TipoDocumentoBean o = (TipoDocumentoBean)datos.get(TipoDocumentoController.PARAMETRO_SALIDA_DOC);
            tfCodDoc.setText(o.getCodtipodocumento());
            tfDesDoc.setText(o.getDestipodocumento());
        }
    }
    
    protected void procesarTipoDoc(){
        String codDoc = tfCodDoc.getText();
        
        if(!codDoc.trim().isEmpty()){
            try {
                TipoDocumentoBean documento = documentos.getDocumento(codDoc);
                if(!documentos.getTiposDocumentoAbonables().contains(documento.getCodtipodocumento())){
                    log.warn("Se seleccionó un tipo de documento no válido para la devolución.");
                    VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El documento seleccionado no es válido."), getStage());
                    tfCodDoc.setText("");
                    tfDesDoc.setText("");
                }
                else{
                    tfCodDoc.setText(documento.getCodtipodocumento());
                    tfDesDoc.setText(documento.getDestipodocumento());
                }
            }
            catch (DocumentoException ex) {
                log.error("Error obteniendo el tipo de documento", ex);
                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El tipo de documento indicado no existe en la base de datos."), getStage());
                tfCodDoc.setText("");
                tfDesDoc.setText("");
            }
            catch(NumberFormatException nfe){
                log.error("El id de documento introducido no es válido.", nfe);
                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El id introducido no es válido."), getStage());
                tfCodDoc.setText("");
                tfDesDoc.setText("");
            }
        }
        else{
            tfDesDoc.setText("");
            tfCodDoc.setText("");
        }
    }
    
    @Override
    public boolean canClose() {
		visor.escribirLineaArriba(I18N.getTexto("---CAJA CERRADA---"));
		visor.modoEspera();
    	return super.canClose();
    }
    
    @SuppressWarnings("rawtypes")
	protected void addSeleccionarTodoEnFoco(final TextField campo) {
		campo.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						if (campo.isFocused() && !campo.getText().isEmpty()) {
							campo.selectAll();
						}
					}
				});
			}
		});
	}
}
