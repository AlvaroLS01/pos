package com.comerzzia.dinosol.pos.gui.ventas.solicitudfacturacion;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.core.servicios.documents.LocatorParseException;
import com.comerzzia.dinosol.librerias.sap.client.RespuestaComprobacionActualizacionClienteSAP;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

@Component
public class SolicitudFacturacionController extends Controller{

	private static final Logger log = Logger.getLogger(SolicitudFacturacionController.class.getName());
	
	public static final String PARAM_CODTICKET = "SolicitudFacturacionController.CodTicket";
	private static final String PERMISO_ALTA_CLIENTE = "ALTA CLIENTE";
	
	@FXML
	protected TextField tfOperacion;

	@FXML
	private FlowPane fpDocumento;

	@FXML
	private VBox vbContenedor;

	@FXML
	protected Label lbMensajeError;
	
	@FXML
	protected Button btAceptar;
	
	@Autowired
	protected Documentos documentos;
	
	@Autowired
    private Sesion sesion;
	
    @Autowired
    private LocatorManager locatorManager;
    
    @Autowired
	private TicketManager ticketManager;

	protected FormularioConsultaTicketBean frConsultaTicket;
	
	
	private String codigoTienda;
	private String codigoCaja;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		codigoTienda = sesion.getAplicacion().getCodAlmacen();
		codigoCaja = sesion.getAplicacion().getCodCaja();
		
        frConsultaTicket = SpringContext.getBean(FormularioConsultaTicketBean.class);

        frConsultaTicket.setFormField("codOperacion", tfOperacion);
		
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfOperacion.setUserData(keyboardDataDto);
		
		addSeleccionarTodoEnFoco(tfOperacion);
		
	}

    @Override
    public void initializeFocus() {
        tfOperacion.requestFocus();
    }
		

	@Override
	public void initializeForm() throws InitializeGuiException {
	    
	    ticketManager = SpringContext.getBean(TicketManager.class);
	    
	    tfOperacion.setText("");  
        lbMensajeError.setText("");
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
            String codigo = tfOperacion.getText();
            
            try {
				if(ticketManager.comprobarConfigContador(documentos.getDocumentoAbono(documentos.FACTURA_SIMPLIFICADA).getCodtipodocumento())){
					Long idTipoDocumento = documentos.getDocumento(documentos.FACTURA_SIMPLIFICADA).getIdTipoDocumento();
					new RecuperarTicketDevolucion(codigo, codigoTienda, codigoCaja, idTipoDocumento).start();
				}else{
					ticketManager.crearVentanaErrorContador(getStage());
				}
			} catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), documentos.FACTURA_SIMPLIFICADA), e);
			}
        }
    }
    
    protected boolean validarFormularioConsultaCliente() {
		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frConsultaTicket.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbMensajeError.setText("");

		frConsultaTicket.setCodCaja(codigoCaja);
		frConsultaTicket.setCodOperacion(tfOperacion.getText());
		frConsultaTicket.setCodTienda(codigoTienda);
		frConsultaTicket.setCodDoc(documentos.FACTURA_SIMPLIFICADA);
		
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
			frConsultaTicket.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(next.getMessage()), getStage());
			tfOperacion.requestFocus();
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
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
			return ticketManager.recuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDoc, false);
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
			getApplication().getMainView().showModalCentered(ComprobacionClienteFacturaView.class, getDatos(), getStage());
			if (getDatos().containsKey(ComprobacionClienteFacturaController.ACCION_CANCELAR)) {
				return;
			}
			if (getDatos().containsKey(ComprobacionClienteFacturaController.CLIENTE_FACTURACION_SAP)) {
				RespuestaComprobacionActualizacionClienteSAP clienteFacturacion = (RespuestaComprobacionActualizacionClienteSAP) getDatos().get(ComprobacionClienteFacturaController.CLIENTE_FACTURACION_SAP);

				if (clienteFacturacion != null || (clienteFacturacion == null && compruebaPermisoAltaCliente())) {
					
					if (getDatos().containsKey(ComprobacionClienteFacturaController.NIF_BUSQUEDA)) {
						String nifBusqueda = (String) getDatos().get(ComprobacionClienteFacturaController.NIF_BUSQUEDA);

						getDatos().put(ComprobacionClienteFacturaController.NIF_BUSQUEDA, nifBusqueda);
					}

					getDatos().put(ComprobacionClienteFacturaController.CLIENTE_FACTURACION_SAP, clienteFacturacion);
					getDatos().put(PARAM_CODTICKET, ticketManager.getTicketOrigen().getCabecera().getCodTicket());

					getApplication().getMainView().showModal(EnvioSolicitudFacturaView.class, getDatos(), getStage());
					tfOperacion.setText("");
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se encuentra ningún cliente con el NIF proporcionado y no tiene permiso para dar de alta"), getStage());
				}
			}

		}
		else {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"), getStage());
		}
    }

	

	private boolean compruebaPermisoAltaCliente() {
		try {
			compruebaPermisos(PERMISO_ALTA_CLIENTE);
		}
		catch (SinPermisosException e) {
			return false;
		}

		return true;
	}
	
}
