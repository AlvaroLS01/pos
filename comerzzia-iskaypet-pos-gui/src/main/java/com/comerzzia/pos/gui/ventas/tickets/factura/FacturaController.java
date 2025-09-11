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

package com.comerzzia.pos.gui.ventas.tickets.factura;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.rest.client.clientes.ClientesRest;
import com.comerzzia.api.rest.client.clientes.ConsultarClienteRequestRest;
import com.comerzzia.api.rest.client.clientes.ResponseGetClienteRest;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.mantenimientos.clientes.FormularioBusquedaCentralBean;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.datosCliente.CambiarDatosClienteController;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class FacturaController extends CambiarDatosClienteController {
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private TiposIdentService tiposIdentService;
	
	@Autowired
	private ClientesService clientesService;
	
	@Autowired
	private VariablesServices variablesServices;

    // <editor-fold desc="Declaración de variables">   
    // Variables

    //  - cliente seleccionado
    protected DatosFactura datosFactura;
    //FacturacionBean facturacion;  <- Objeto que se corresponda con lo contenido en la pantalla
    //  - botonera de acciones de tabla
    protected BotoneraComponent botoneraAccionesTabla;

    protected FormularioDatosFacturaBean frDatosFactura;
    
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Creación e inicialización"> 
    /**
     * Inicializa el componente tras su creación. No hay acceso al application
     * desde este método.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.debug("initialize()");
        
        lbTitulo.setText(I18N.getTexto("Datos del cliente para la factura"));
        
        frDatosFactura = SpringContext.getBean(FormularioDatosFacturaBean.class);
        frDatosFactura.setFormField("cif", tfNumDocIdent);
        frDatosFactura.setFormField("cPostal", tfCP);
        frDatosFactura.setFormField("telefono", tfTelefono);
        frDatosFactura.setFormField("provincia", tfProvincia);
        frDatosFactura.setFormField("poblacion", tfPoblacion);
        frDatosFactura.setFormField("domicilio", tfDomicilio);
        frDatosFactura.setFormField("razonSocial", tfRazonSocial);
        frDatosFactura.setFormField("numDocIdent", tfNumDocIdent);
        frDatosFactura.setFormField("pais", tfCodPais);
        frDatosFactura.setFormField("localidad", tfLocalidad);
        
        frBusquedaCentral = SpringContext.getBean(FormularioBusquedaCentralBean.class);
        frBusquedaCentral.setFormField("codCliente", tfNumDocIdent);
    }
    
    @Override
    public void initializeForm() {
    	limpiarCampos();
    	
    	log.debug("initializeForm()");
    	ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);
        datosFactura = ((TicketVenta)ticketManager.getTicket()).getCliente().getDatosFactura();
        
        frDatosFactura.clearErrorStyle();
        
        clienteTicket = new ClienteBean();
        clienteTicket.setIdGrupoImpuestos(sesion.getAplicacion().getTienda().getCliente().getIdGrupoImpuestos());

        if (datosFactura !=null){
        	clienteTicket.setCif(datosFactura.getCif());
        	clienteTicket.setCp(datosFactura.getCp());
        	clienteTicket.setDomicilio(datosFactura.getDomicilio());
        	clienteTicket.setProvincia(datosFactura.getProvincia());
        	clienteTicket.setTelefono1(datosFactura.getTelefono());
        	clienteTicket.setDesCliente(datosFactura.getNombre());
        	clienteTicket.setPoblacion(datosFactura.getPoblacion());
        	clienteTicket.setCodpais(datosFactura.getPais());
        	clienteTicket.setTipoIdentificacion(datosFactura.getTipoIdentificacion());
        	clienteTicket.setLocalidad(datosFactura.getLocalidad());
        }
        else{
        	clienteTicket = ticketManager.getTicket().getCliente().clone();
        }
        
        isClienteGenerico = clienteTicket.getCodCliente() != null && clienteTicket.getCodCliente().equals(sesion.getAplicacion().getTienda().getCliente().getCodCliente()) && clienteTicket.getDesCliente().equals(sesion.getAplicacion().getTienda().getCliente().getDesCliente());

		String codPais = clienteTicket.getCodpais();
		if(codPais!=null && !codPais.isEmpty()){
			PaisBean paisCliente = obtenerPais(codPais);
			if(paisCliente!=null){
				tfCodPais.setText(paisCliente.getCodPais());
				this.codPais = paisCliente.getCodPais().toUpperCase();
				clienteTicket.setPais(paisCliente.getDesPais());
			}
		}
		else{
			tfCodPais.setText(AppConfig.pais);
			this.codPais = AppConfig.pais.toUpperCase();
			PaisBean pais = obtenerPais(AppConfig.pais);
			tfDesPais.setText(pais.getDesPais());
		}

		this.tiposIdent = FXCollections.observableArrayList();
		loadTiposIdentificacion();
		cbTipoDocIdent.setItems(tiposIdent);

        refrescarDatosPantalla();
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Funciones relacionadas con interfaz GUI y manejo de pantalla"> 
    /**
     * Gestiona el evento de presionar la tecla intro para establecer el cliente de la factura
     *
     * @param event
     */
    @FXML
    public void accionAceptarIntro(KeyEvent event) {
    	log.debug("accionAceptarIntro()");

        if (event.getCode() == KeyCode.ENTER) {
            establecerClienteFactura((Event) event);
        }
    }

    /**
     * Acción aceptar
     */
    @FXML
    public void accionAceptar(ActionEvent event) {
        log.debug("aceptar() - Acción aceptar");

        establecerClienteFactura((Event) event);
    }

    protected void establecerClienteFactura(Event event) {
    	log.debug("establecerClienteFactura()");
    	
        frDatosFactura.setNumDocIdent(frDatosFactura.trimTextField(tfNumDocIdent));
        frDatosFactura.setDomicilio(frDatosFactura.trimTextField(tfDomicilio));
        frDatosFactura.setProvincia(frDatosFactura.trimTextField(tfProvincia));
        frDatosFactura.setLocalidad(frDatosFactura.trimTextField(tfLocalidad));
        frDatosFactura.setPoblacion(frDatosFactura.trimTextField(tfPoblacion));
        frDatosFactura.setRazonSocial(frDatosFactura.trimTextField(tfRazonSocial));
        frDatosFactura.setcPostal(frDatosFactura.trimTextField(tfCP));
        frDatosFactura.setTelefono(frDatosFactura.trimTextField(tfTelefono));
        frDatosFactura.setPais(frDatosFactura.trimTextField(tfCodPais));
        frDatosFactura.setBanco(frDatosFactura.trimTextField(tfBanco));
        frDatosFactura.setBancoDomicilio(frDatosFactura.trimTextField(tfBancoDomicilio));
        frDatosFactura.setBancoPoblacion(frDatosFactura.trimTextField(tfBancoPoblacion));
        frDatosFactura.setBancoCCC(frDatosFactura.trimTextField(tfBancoCCC));

        if (validarFormularioDatosFactura()) {
            
            DatosFactura datosFactura = new DatosFactura();
            datosFactura.setCif(tfNumDocIdent.getText());
            datosFactura.setCp(tfCP.getText());
            datosFactura.setDomicilio(tfDomicilio.getText());
            datosFactura.setProvincia(tfProvincia.getText());
            datosFactura.setTelefono(tfTelefono.getText());
            datosFactura.setNombre(tfRazonSocial.getText());
            datosFactura.setPoblacion(tfPoblacion.getText());
            datosFactura.setLocalidad(tfLocalidad.getText());
            datosFactura.setPais(tfCodPais.getText());
            datosFactura.setBanco(tfBanco.getText());
            datosFactura.setBancoDomicilio(tfBancoDomicilio.getText());
            datosFactura.setBancoPoblacion(tfBancoPoblacion.getText());
            datosFactura.setCcc(tfBancoCCC.getText());

            if(cbTipoDocIdent.getSelectionModel().getSelectedItem() != null){
            	datosFactura.setTipoIdentificacion(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden());
            }
            
            ((TicketVenta)ticketManager.getTicket()).setDatosFacturacion(datosFactura);
            try {
                if(!ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA)){
                    ticketManager.setDocumentoActivo(sesion.getAplicacion().getDocumentos().getDocumento(ticketManager.getDocumentoActivo().getTipoDocumentoFacturaDirecta()));
                }
                getStage().close();
            }
            catch (DocumentoException ex) {
                log.error("No se pudo establecer el tipo documento factura",ex);
                VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error al establecer el tipo de documento factura."), this.getStage());
            }
        }
    }

    /**
     * Valida los campos editables
     *
     * @return
     */
    protected boolean validarFormularioDatosFactura() {
    	log.debug("validarFormularioDatosFactura()");

    	String sResultado = validarDocumento();
		if(sResultado != null) {
			boolean guardar = VentanaDialogoComponent.crearVentanaConfirmacion(sResultado, getStage());
			if(!guardar) {
				tfNumDocIdent.requestFocus();
				return false;
			}
		}

        boolean valido = true;

        // Limpiamos los errores que pudiese tener el formulario
        frDatosFactura.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        final Set<ConstraintViolation<FormularioDatosFacturaBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frDatosFactura);
        Iterator<ConstraintViolation<FormularioDatosFacturaBean>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            final ConstraintViolation<FormularioDatosFacturaBean> next = iterator.next();
            frDatosFactura.setErrorStyle(next.getPropertyPath(), true);
            if(valido){ //Ponemos el foco en el primero que da error
            	lbError.setText(next.getMessage());
            	Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
    					frDatosFactura.setFocus(next.getPropertyPath());
    				}
    			});
            }
            valido = false;
        }

        return valido;
    }
    
    @Override
    public void cargarClienteCentral() {
		if (validarFormularioBusquedaCentral()) {
			String codPais = null;
			String codTipoIden = null;
			if (cbTipoDocIdent.getSelectionModel().getSelectedItem() != null && StringUtils.isNotBlank(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden())) {
				codTipoIden = cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden();
				if (StringUtils.isNotBlank(tfCodPais.getText())) {
					codPais = tfCodPais.getText();
				}
				else {
					codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
				}
			}

			try {
				List<ClienteBean> consultarClientes = clientesService.consultarClientes(codTipoIden, tfNumDocIdent.getText());
				if (consultarClientes != null && !consultarClientes.isEmpty()) {
					rellenarDatosFacturacion(consultarClientes.get(0));
				}
				else {
					new BuscarClienteEnCentralTask(codPais).start();
				}
			}
			catch (Exception e) {
				log.error("cargarClienteCentral() - Ha habido un error al buscar en la base de datos el cliente para facturar: " + e.getMessage(), e);
				new BuscarClienteEnCentralTask(codPais).start();
			}
		}
    }
    
	protected boolean validarFormularioBusquedaCentral() {
		log.debug("validarFormularioDatosFactura()");

		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frDatosFactura.clearErrorStyle();
		frBusquedaCentral.clearErrorStyle();

		frBusquedaCentral.setCodCliente(tfNumDocIdent.getText().trim());

		// Limpiamos el posible error anterior
		lbError.setText("");

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioBusquedaCentralBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusquedaCentral);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioBusquedaCentralBean> next = constraintViolations.iterator().next();
			frBusquedaCentral.setErrorStyle(next.getPropertyPath(), true);
			frBusquedaCentral.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}

	protected class BuscarClienteEnCentralTask extends BackgroundTask<ResponseGetClienteRest> {

		private String codPais;

		public BuscarClienteEnCentralTask(String codPais) {
			super();
			this.codPais = codPais;
		}

		@Override
		protected ResponseGetClienteRest call() throws Exception {

			ConsultarClienteRequestRest consultaCliente = new ConsultarClienteRequestRest(variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), sesion.getAplicacion()
			        .getUidActividad(), tfNumDocIdent.getText(), codPais, cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden());

			return ClientesRest.getClientePais(consultaCliente);
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			try {
				ResponseGetClienteRest res = get();

				if (res.getCodError() != 0) {
					VentanaDialogoComponent.crearVentanaAviso(res.getMensaje(), getStage());
				}
				else {
					limpiarCampos();
					rellenarDatosFacturacion(res);
				}
			}
			catch (Exception e) {
				log.error("BuscarClienteEnCentralTask() - Ha habido un error al recuperar el cliente de la central: " + e.getMessage(), e);
			}
		}

		@Override
		protected void failed() {
			super.failed();

			log.error("BuscarClienteEnCentralTask() - Error consultando el cliente en central: " + getException().getMessage(), getException());
			String msg = getException().getMessage();

			if (msg == null || msg.isEmpty()) {
				msg = I18N.getTexto("La petición no se procesó correctamente.");
			}
			else if (msg.contains("HTTP 404")) {
				msg = (I18N.getTexto("Error. Dirección de servicio rest no encontrada (HTTP 404)"));
			}
			else if (msg.contains("HTTP 4")) {
				msg = (I18N.getTexto("No se pudo conectar con los servicios REST (HTTP 400)"));
			}
			else if (msg.contains("HTTP 5")) {
				msg = (I18N.getTexto("No se pudo conectar con los servicios REST (HTTP 500)"));
			}
			else if (msg.contains("Connection refused")) {
				msg = (I18N.getTexto("No se pudo conectar con los servicios REST de la central"));
			}
			else if (msg.equals("El cliente consultado no existe en el sistema")) {
				if (cbTipoDocIdent.getSelectionModel().getSelectedItem() != null && StringUtils.isNotBlank(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden())) {
					msg = I18N.getTexto("El cliente con el documento {0}, el tipo de documento {1} y el país {2} no existe en el sistema", tfNumDocIdent.getText(), cbTipoDocIdent.getSelectionModel()
					        .getSelectedItem().getCodTipoIden(), codPais);
				}
				else {
					msg = I18N.getTexto("El cliente con el documento {0} no existe en el sistema", tfNumDocIdent.getText());
				}
			}

			VentanaDialogoComponent.crearVentanaAviso(msg, getStage());
		}
	}

	protected void rellenarDatosFacturacion(ClienteBean cliente) {
		ResponseGetClienteRest res = new ResponseGetClienteRest();
		res.setDesCliente(cliente.getDesCliente());
		res.setNombreComercial(cliente.getNombreComercial());
		res.setDomicilio(cliente.getDomicilio());
		res.setPoblacion(cliente.getPoblacion());
		res.setProvincia(cliente.getProvincia());
		res.setLocalidad(cliente.getLocalidad());
		res.setCp(cliente.getCp());
		res.setTelefono1(cliente.getTelefono1());
		res.setCif(cliente.getCif());
		res.setCodPais(cliente.getCodpais());
		res.setCodTipoIden(cliente.getTipoIdentificacion());
		rellenarDatosFacturacion(res);
	}

	protected void rellenarDatosFacturacion(ResponseGetClienteRest respuestaRest) {
		tfRazonSocial.setText(respuestaRest.getDesCliente());
		tfDomicilio.setText(respuestaRest.getDomicilio());
		tfPoblacion.setText(respuestaRest.getPoblacion());
		tfProvincia.setText(respuestaRest.getProvincia());
		tfLocalidad.setText(respuestaRest.getLocalidad());
		tfCP.setText(respuestaRest.getCp());
		tfTelefono.setText(respuestaRest.getTelefono1());
		tfNumDocIdent.setText(respuestaRest.getCif());
		tfCodPais.setText(respuestaRest.getCodPais());
		codPais = respuestaRest.getCodPais() != null ? respuestaRest.getCodPais().toUpperCase() : "";
		tfDesPais.setText(respuestaRest.getDesPais());
		updateSelectedTipoIdentificacion(respuestaRest.getCodTipoIden());
	}
    
}
