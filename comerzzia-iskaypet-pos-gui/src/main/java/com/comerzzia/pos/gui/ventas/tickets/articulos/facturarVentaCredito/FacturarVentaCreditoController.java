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
package com.comerzzia.pos.gui.ventas.tickets.articulos.facturarVentaCredito;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.rest.client.exceptions.HttpServiceRestException;
import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.client.exceptions.ValidationDataRestException;
import com.comerzzia.api.rest.client.exceptions.ValidationRequestRestException;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.clientes.ClientesServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Controller
public class FacturarVentaCreditoController extends WindowController{
		
	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(getClass());
	
	@Autowired
	private Sesion sesion;
	
	@FXML
	protected TextField tfCodCaja, tfIdDoc, tfCodTienda, tfCodDoc, tfDesDoc, tfLocalizador, tfCodServicio, tfCodPedido;
	
	@FXML
	protected Button btAceptar, btCancelar, btBuscarDoc;
	
	@FXML
	protected Label lbError;
	
	protected FormularioDatosVentaCreditoBean formDatos;
	
	protected TicketManager ticketManager;
	
	protected TipoDocumentoBean docOrigenFactura;

	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private Documentos documentos;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
		formDatos = SpringContext.getBean(FormularioDatosVentaCreditoBean.class);
		
		formDatos.setFormField("codCaja", tfCodCaja);
		formDatos.setFormField("codTienda", tfCodTienda);
		formDatos.setFormField("idDoc", tfIdDoc);
		formDatos.setFormField("tipoDoc", tfCodDoc);
		
		tfCodDoc.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(oldValue){
                    procesarTipoDoc();
                }
            }
        });
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
		
		tfLocalizador.clear();
		tfCodServicio.clear();
		
		formDatos.clearErrorStyle();
		
		tfCodCaja.setText(sesion.getAplicacion().getCodCaja());
		tfCodTienda.setText(sesion.getAplicacion().getCodAlmacen());
		tfIdDoc.setText("");
		
		try{
            docOrigenFactura = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
		}catch(DocumentoException e){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está configurado el tipo de documento nota de crédito en el entorno."), this.getStage());
		}

		if(docOrigenFactura!=null){
			try {
				List<String> docsVentaCredito = docOrigenFactura.getTiposDocumentosVentaCredito();
				if(docsVentaCredito==null || docsVentaCredito.isEmpty()){
                                        log.error("initializeForm() - No hay configurado en base de datos un documento de venta de crédito para la aplicación");
					throw new DocumentoException();
				}
				TipoDocumentoBean docVentaCredito = sesion.getAplicacion().getDocumentos().getDocumento(docsVentaCredito.get(0));

				if(docVentaCredito!=null){
					tfCodDoc.setText(docVentaCredito.getCodtipodocumento());
					tfDesDoc.setText(docVentaCredito.getDestipodocumento());
				}
			} catch (DocumentoException e) {
				log.error("Error al buscar el doc venta de credito", e);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda del documento de venta de cr\u00E9dito asociado a la factura."), getStage());
			}
		}
	}

	@Override
	public void initializeFocus() {
		tfCodServicio.requestFocus();
	}
	
	public void accionAceptar(){
		String localizador = tfLocalizador.getText();
		if (localizador != null && !localizador.isEmpty()) {
//			if (localizador.trim().length() != 19) {
//				lbError.setText(I18N.getTexto("El código de localizador introducido no es correcto."));
//				return;
//			}
		} else {
			localizador = null;
		}
		
		String codServicio = tfCodServicio.getText();
		
		if (codServicio != null && codServicio.isEmpty()) {
			codServicio = null;
		}
		
		String codPedido = tfCodPedido.getText();
		
		if(codPedido != null && codPedido.isEmpty()){
			codPedido = null;
		}
			
		String codTienda = tfCodTienda.getText();
		String codCaja = tfCodCaja.getText();
		String idDoc = tfIdDoc.getText();
		String codDoc = tfCodDoc.getText();
		
		formDatos.setCodCaja(codCaja);
		formDatos.setIdDoc(idDoc);
		formDatos.setCodTienda(codTienda);
		formDatos.setTipoDoc(codDoc);
		
		if (localizador != null || codServicio != null || codPedido != null) {
			formDatos.clearErrorStyle();
			lbError.setText("");
			new RecuperarTicketVentaCredito(codTienda, codCaja, idDoc, codDoc, localizador, codServicio, codPedido).start();
		} else {
			if(validarFormularioDatos()){
				new RecuperarTicketVentaCredito(codTienda, codCaja, idDoc, codDoc, localizador, codServicio, codPedido).start();
			}
		}
	}
	
	@FXML
    public void actionTfCodigoIntro(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            btAceptar.requestFocus();
        }
    }
	
	public class RecuperarTicketVentaCredito extends BackgroundTask<Boolean>{

		private String codCaja, codTienda, idDoc, codDoc, localizador, codServicio, codPedido;
		
		public RecuperarTicketVentaCredito(String codTienda, String codCaja, String codOperacion, String codDoc, String localizador, String codServicio, String codPedido) {
			this.codTienda = codTienda;
			this.codCaja = codCaja;
			this.idDoc = codOperacion;
			this.codDoc = codDoc;
			this.localizador = localizador;
			this.codServicio = codServicio;
			this.codPedido = codPedido;
		}

		@Override
		protected Boolean call() throws Exception {
			if(codServicio!= null){
					return ticketManager.obtenerDocumentoVentaCreditoCodServicio(getStage(), codServicio);
			}else if(localizador != null){
				TipoDocumentoBean tipoDocumento = documentos.getDocumento(tfCodDoc.getText());
				Long idTipoDoc = tipoDocumento.getIdTipoDocumento();
				return ticketManager.obtenerDocumentoVentaCreditoLocalizador(getStage(), localizador, idTipoDoc);
			}else if(codPedido != null){
				return ticketManager.obtenerDocumentoVentaCreditoCodPedido(getStage(), codPedido);
			} else {
				return ticketManager.obtenerDocumentoVentaCredito(getStage(), codCaja, codTienda, idDoc, codDoc);
			}
		}
	
		@Override
		protected void failed() {
			Throwable e = getException();
			
			log.error("RecuperarTicketVentaCredito:failed() - Ha habido un error al recuperar la venta a crédito: " + e.getMessage(), e);
			
        	if(e instanceof ValidationRequestRestException || e instanceof ValidationDataRestException || e instanceof HttpServiceRestException){
        		VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
        	}else if(e.getCause() != null && e.getCause() instanceof NotFoundException){
        		VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(e.getMessage()), e);
        	}else if(e instanceof RestHttpException){
        		VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
        	}else if(e instanceof RestConnectException){
        		VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido conectar con el servidor"), e);
        	}else if(e instanceof RestTimeoutException){
        		VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("El servidor ha tardado demasiado tiempo en responder"), e);
        	}else if(e instanceof RestException){
        		VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
        	}else if(e instanceof IllegalStateException && ObjectUtils.equals(e.getMessage(), "distinto-cliente")){
        		VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se puede facturar documentos de distinto cliente"), e);
        	}else if(e instanceof ClientesServiceException){
        		VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha encontrado el cliente asociado al ticket origen"), e);
        	}else{
        		VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
        	}
        	if(!tfCodServicio.getText().isEmpty()){
        		tfCodServicio.requestFocus();
        	}else if(!tfLocalizador.getText().isEmpty()){
        		tfLocalizador.requestFocus();
        	}
        	else if(!tfCodPedido.getText().isEmpty()){
        		tfCodPedido.requestFocus();
        	}
			super.failed();
		}

		@Override
		protected void succeeded() {
			getStage().close();
			super.succeeded();
		}
	}
	
	/**
     * Valida los datos introducidos
     *
     * @return
     */
    private boolean validarFormularioDatos() {
        boolean valido;

        // Limpiamos los errores que pudiese tener el formulario
        formDatos.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<FormularioDatosVentaCreditoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formDatos);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioDatosVentaCreditoBean> next = constraintViolations.iterator().next();
            formDatos.setErrorStyle(next.getPropertyPath(), true);
            formDatos.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            valido = false;
        }
        else {            
            valido = true;
        }

        return valido;
    }
	
    @FXML
    public void accionBuscarTipoDoc(){
    	datos = new HashMap<String, Object>();
    	try {
    		datos.put(TipoDocumentoController.PARAMETRO_ENTRADA_POSIBLES_DOCS, new ArrayList<String>(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA).getTiposDocumentosVentaCredito()));

    		getApplication().getMainView().showModalCentered(TipoDocumentoView.class, datos, this.getStage());

    		if(datos.containsKey(TipoDocumentoController.PARAMETRO_SALIDA_DOC)){
    			TipoDocumentoBean o = (TipoDocumentoBean)datos.get(TipoDocumentoController.PARAMETRO_SALIDA_DOC);
    			tfCodDoc.setText(o.getCodtipodocumento());
    			tfDesDoc.setText(o.getDestipodocumento());
    		}
    	} catch (DocumentoException e) {
    		log.error("Error recuperando los posibles documentos origen de la nota de crédito.",e);
    	}
    }

    protected void procesarTipoDoc(){
    	String tipoDoc = tfCodDoc.getText();

    	if(!tipoDoc.trim().isEmpty()){
    		try {
    			TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(tipoDoc);
    			if(docOrigenFactura!=null){
    				if(!docOrigenFactura.getTiposDocumentosVentaCredito().contains(documento.getCodtipodocumento())){
    					log.warn("Se seleccionó un tipo de documento no válido para la devolución.");
    					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El documento seleccionado no es válido."), getStage());
    					tfDesDoc.setText("");
    					tfCodDoc.setText("");
    				}
    				else{
    					tfCodDoc.setText(documento.getCodtipodocumento());
    					tfDesDoc.setText(documento.getDestipodocumento());
    				}
    			}
    			else{
    				tfCodDoc.setText("");
    				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El documento seleccionado no es válido."), this.getStage());
    			}
    		}
    		catch (DocumentoException ex) {
    			log.error("Error obteniendo el tipo de documento", ex);
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El tipo de documento indicado no existe en la base de datos."), getStage());
    			tfDesDoc.setText("");
    			tfCodDoc.setText("");
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
    
    public void accionTfLocalizadorIntro(KeyEvent e){
    	if(e.getCode() == KeyCode.ENTER){
    		accionAceptar();
    	}
    }

}
