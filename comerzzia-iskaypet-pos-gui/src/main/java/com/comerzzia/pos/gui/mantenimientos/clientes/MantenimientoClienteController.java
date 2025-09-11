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
package com.comerzzia.pos.gui.mantenimientos.clientes;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.rest.client.clientes.ClientesRest;
import com.comerzzia.api.rest.client.clientes.ConsultarClienteRequestRest;
import com.comerzzia.api.rest.client.clientes.ResponseGetClienteRest;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.core.util.base.Estado;
import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.gui.view.View;
import com.comerzzia.pos.gui.mantenimientos.clientes.codPostales.SeleccionPoblacionController;
import com.comerzzia.pos.gui.mantenimientos.clientes.codPostales.SeleccionPoblacionView;
import com.comerzzia.pos.gui.ventas.identificada.cliente.IdentificacionClienteView;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ClienteGui;
import com.comerzzia.pos.gui.ventas.tickets.factura.paises.PaisesController;
import com.comerzzia.pos.gui.ventas.tickets.factura.paises.PaisesView;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.codPostales.CodigoPostalBean;
import com.comerzzia.pos.persistence.core.impuestos.tratamientos.TratamientoImpuestoBean;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.clientes.ClienteConstraintViolationException;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.clientes.ClientesServiceException;
import com.comerzzia.pos.services.codPostales.CodPostalesException;
import com.comerzzia.pos.services.codPostales.CodPostalesServices;
import com.comerzzia.pos.services.core.impuestos.tratamientos.TratamientoImpuestoService;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class MantenimientoClienteController extends Controller implements IContenedorBotonera {

	public static final String INDICE_CLIENTE_SELECCIONADO = "INDICE_CLIENTE_SELECCIONADO";
	public static final String LISTA_CLIENTES = "LISTA_CLIENTES";
	public static final String MODO_EDICION = "MODO_EDICION";
	public static final String ESTADO_CLIENTE = "ESTADO_CLIENTE";
	public static final String CLIENTE_EDITADO = "CODCLI_EDITADO";

	private static Logger log = Logger.getLogger(MantenimientoClienteController.class);

	@FXML
	protected AnchorPane panelMenuTabla;
	protected BotoneraComponent botoneraAccionesTabla;
	@FXML
	protected ComboBox<TiposIdentBean> cbTipoDocIdent;
	@FXML
	protected ComboBox<TratamientoImpuestoBean> cbTratamientoImpuestos;
	protected ObservableList<TiposIdentBean> tiposIdent;
	protected ObservableList<TratamientoImpuestoBean> tratamientosImp;
	@FXML
	protected TabPane tabPane;
	@FXML
	protected Tab tabBanco;
	@FXML
	protected Tab tabGeneral;
	@FXML
	protected TextField tfRazonSocial, tfDomicilio, tfPoblacion, tfCP, tfProvincia, tfTelefono, tfTelefono2, 
	tfDesPais, tfCodPais, tfNumDocIdent, tfLocalidad, tfTarifa, tfDescripcion, tfFax, tfEmail;
	@FXML
	protected TextField tfBanco, tfBancoDomicilio, tfBancoPoblacion, tfBancoCCC;
	@FXML
	protected TextArea taObservaciones;
	@FXML
	protected CheckBox cbActivo;
	@FXML
	protected Button btBuscarCentral, btBuscarPais, btnValidarDocumento;

	protected ObservableList<ClienteGui> clientes;
	protected int indexCliente;
	protected ClienteBean cliente;
	protected boolean modoEdicion = false;
	protected int estadoCliente;

	@FXML
	protected Label lbError;

	protected FormularioMantenimientoClientesBean frDatosCliente;
	protected FormularioBusquedaCentralBean frBusqCentral;
	
	@Autowired
	private VariablesServices variablesServices = SpringContext.getBean(VariablesServices.class);
	@Autowired
	private Sesion sesion = SpringContext.getBean(Sesion.class);
	@Autowired
	private TiposIdentService tiposIdentService = SpringContext.getBean(TiposIdentService.class);
	@Autowired
	private ClientesService clientesService = SpringContext.getBean(ClientesService.class);
	@Autowired
	private PaisService paisService = SpringContext.getBean(PaisService.class);
	@Autowired
	private CodPostalesServices codPostalesServices = SpringContext.getBean(CodPostalesServices.class);
	@Autowired
	private TratamientoImpuestoService tratamientoImpuestoService = SpringContext.getBean(TratamientoImpuestoService.class);
	
	protected boolean seHaEliminadoCliente;
	
	protected String codPais;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		frDatosCliente = SpringContext.getBean(FormularioMantenimientoClientesBean.class);
		frBusqCentral = SpringContext.getBean(FormularioBusquedaCentralBean.class);

		frDatosCliente.setFormField("domicilio", tfDomicilio);
		frDatosCliente.setFormField("provincia", tfProvincia);
		frDatosCliente.setFormField("poblacion", tfPoblacion);
		frDatosCliente.setFormField("cPostal", tfCP);
		frDatosCliente.setFormField("telefono", tfTelefono);
		frDatosCliente.setFormField("telefono2", tfTelefono2);
		frDatosCliente.setFormField("numDocIdent", tfNumDocIdent);
		frDatosCliente.setFormField("razonSocial", tfRazonSocial);
		frDatosCliente.setFormField("pais", tfCodPais);
		frDatosCliente.setFormField("idTratImpuesto", cbTratamientoImpuestos);
		frDatosCliente.setFormField("descripcion", tfDescripcion);
		frDatosCliente.setFormField("fax", tfFax);
		frDatosCliente.setFormField("banco", tfBanco);
		frDatosCliente.setFormField("bancoDomicilio", tfBancoDomicilio);
		frDatosCliente.setFormField("bancoPoblacion", tfBancoPoblacion);
		frDatosCliente.setFormField("bancoCCC", tfBancoCCC);
		frDatosCliente.setFormField("observaciones", taObservaciones);
		frDatosCliente.setFormField("localidad", tfLocalidad);
		frDatosCliente.setFormField("email", tfEmail);
		
		frBusqCentral.setFormField("codCliente", tfNumDocIdent);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		tfCP.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
		    	if(!newValue) {
		    		buscarCodigoPostal();
		    	}
		    }
		});
		
		tfCP.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				tfTelefono.requestFocus();
			}
		});

		tfCodPais.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
				tfCodPais.setText(tfCodPais.getText().toUpperCase());
				if (!newValue && !codPais.equals(tfCodPais.getText())) {
					PaisBean pais = null;
					if(StringUtils.isNotBlank(tfCodPais.getText())) {
	                    try {
	                        pais = paisService.consultarCodPais(tfCodPais.getText());
	                    }
	                    catch (PaisNotFoundException e) {
	                    	log.debug("initializeComponents() - No se ha encontrado el país con código: " + tfCodPais.getText());
	                    }
	                    catch (PaisServiceException e) {
	                    	log.debug("initializeComponents() - Ha habido un error al buscar el país con código " + tfCodPais.getText() + ": " + e.getMessage());
	                    }
					}
                    
					if(pais != null) {
						tfDesPais.setText(pais.getDesPais());
					}
					else {
						tfDesPais.clear();
					}
					codPais = tfCodPais.getText();
					loadTiposIdentificacion();
				}
			}
		});
		
		try {
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = BotoneraComponent.cargarAccionesTablaSimple();
			listaAccionesAccionesTabla.add(0, new ConfiguracionBotonBean("iconos/back.png", null, null, "VOLVER", "REALIZAR_ACCION"));
			listaAccionesAccionesTabla.add(1, new ConfiguracionBotonBean("iconos/add.png", null, null, "AÑADIR", "REALIZAR_ACCION"));
			listaAccionesAccionesTabla.add(2, new ConfiguracionBotonBean("iconos/edit.png", null, null, "EDITAR", "REALIZAR_ACCION"));
			listaAccionesAccionesTabla.add(3, new ConfiguracionBotonBean("iconos/delete.png", null, null, "ELIMINAR", "REALIZAR_ACCION"));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/aceptar.png", null, null, "ACEPTAR", "REALIZAR_ACCION"));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/cancelar.png", null, null, "CANCELAR", "REALIZAR_ACCION"));

			log.debug("inicializarComponentes() - Configurando botonera");
			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
			panelMenuTabla.getChildren().add(botoneraAccionesTabla);
		}
		catch (CargarPantallaException ex) {
			log.error("inicializarComponentes() - Error creando botonera. Error : " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(ex.getMessageI18N(), getStage());
		}
	}
	
	protected void loadTiposIdentificacion() {
        try {
            tiposIdent.clear();
            String codPais = "";
            if(cliente == null &&  StringUtils.isEmpty(tfCodPais.getText())){
            	codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
            }else if(cliente == null &&  !StringUtils.isEmpty(tfCodPais.getText())){
            	codPais = tfCodPais.getText();
            }else{
            	codPais = cliente.getCodpais();
            }
            List<TiposIdentBean> tiposIdentificacion = tiposIdentService.consultarTiposIdent(null, true, codPais);
            if (tiposIdentificacion.isEmpty()) {
                //Añadimos elemento vacío
                tiposIdent.add(new TiposIdentBean());
            } else {
                tiposIdent.addAll(tiposIdentificacion);
            }
        }
        catch (TiposIdentNotFoundException ex) {
        }
        catch (TiposIdentServiceException ex) {
            log.error("Error consultando los tipos de identificación.",ex);
            VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error consultando los documentos de identificación de la tienda."), this.getStage());
        }
        catch(Exception ex){
            log.error("Se produjo un error en el tratamiento de los tipos de identificacion",ex);
        }
        updateSelectedTipoIdentificacion();
    }

	@SuppressWarnings("unchecked")
    @Override
	public void initializeForm() throws InitializeGuiException {
		//si no tenemos el parámetro modo edición no hacemos nada, ya está abierta
		if(!getDatos().containsKey(MODO_EDICION)){
			return;
		}
		
		seHaEliminadoCliente = false;
		
		if(modoEdicion){
			return;
		}
		
		frDatosCliente.clearErrorStyle();
		lbError.setText("");
		
		if(getDatos().containsKey(INDICE_CLIENTE_SELECCIONADO)){
			indexCliente = (int) getDatos().remove(INDICE_CLIENTE_SELECCIONADO);
			clientes = (ObservableList<ClienteGui>) getDatos().remove(LISTA_CLIENTES);
			cliente = clientes.get(indexCliente).getCliente();
		}
		else{
			clientes = null;
			cliente = null;
		}
		modoEdicion = (boolean) getDatos().get(MODO_EDICION);
		estadoCliente = (int ) getDatos().remove(ESTADO_CLIENTE);

		tiposIdent = FXCollections.observableArrayList();

		loadTiposIdentificacion();
		cbTipoDocIdent.setItems(tiposIdent);


		try {
			List<TratamientoImpuestoBean> tratamientosImpuesto = tratamientoImpuestoService.consultarTratamientosImpuesto(
					sesion.getAplicacion().getTienda().getCliente().getCodpais(), 
					sesion.getAplicacion().getUidActividad());

			this.tratamientosImp = FXCollections.observableArrayList();
			tratamientosImp.add(null);
			//Añadimos elemento vacío
			tratamientosImp.addAll(tratamientosImpuesto);
			cbTratamientoImpuestos.setItems(tratamientosImp);
		}
		catch(Exception ex){
			log.error("initializeForm() - Se produjo un error en el tratamiento de los tipos de identificacion", ex);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error consultando los documentos de identificación de la tienda."), this.getStage());
		}

		if(cliente!=null){
			String codPais = cliente.getCodpais();
			if(codPais!=null && !codPais.isEmpty()){
				PaisBean paisCliente = obtenerPais(codPais);
				if(paisCliente!=null){
					cliente.setPais(paisCliente.getDesPais());
				}
			}
			else{
				tfCodPais.setText(AppConfig.pais);
				this.codPais = AppConfig.pais.toUpperCase();
				PaisBean pais = obtenerPais(AppConfig.pais);
				tfDesPais.setText(pais.getDesPais());
			}
			refrescarDatosPantalla();
		}
		else{
			limpiarCampos();
			tfCodPais.setText(sesion.getAplicacion().getTienda().getCliente().getCodpais());
			codPais = tfCodPais.getText();
			PaisBean pais = obtenerPais(sesion.getAplicacion().getTienda().getCliente().getCodpais());
			if(pais != null) {
				tfDesPais.setText(pais.getDesPais());
			}
			loadTiposIdentificacion();
			cbTipoDocIdent.getSelectionModel().selectFirst();
		}

		setModoEdicion(modoEdicion);
	}        

	@Override
	public void initializeFocus() {
		if(estadoCliente == Estado.NUEVO) {
			tfNumDocIdent.requestFocus();
		}
		else {
			tfRazonSocial.requestFocus();
		}
	}

	public void refrescarDatosPantalla(){
		tfRazonSocial.setText(cliente.getNombreComercial());
		tfProvincia.setText(cliente.getProvincia());
		tfDomicilio.setText(cliente.getDomicilio());
		tfLocalidad.setText(cliente.getLocalidad());
		tfTelefono.setText(cliente.getTelefono1());
		tfTelefono2.setText(cliente.getTelefono2());
		tfNumDocIdent.setText(cliente.getCif());
		tfCodPais.setText(cliente.getCodpais());
		codPais = cliente.getCodpais().toUpperCase();
		tfDesPais.setText(cliente.getPais());
		tfPoblacion.setText(cliente.getPoblacion());
		tfCP.setText(cliente.getCp());

		tfBanco.setText(cliente.getBanco());
		tfBancoDomicilio.setText(cliente.getBancoDomicilio());
		tfBancoPoblacion.setText(cliente.getBancoPoblacion());
		tfBancoCCC.setText(cliente.getCcc());
		
		updateSelectedTipoIdentificacion();
		
		cbActivo.setSelected(cliente.getActivo());
		tfTarifa.setText(cliente.getCodtar());
		tfDescripcion.setText(cliente.getDesCliente());
		tfEmail.setText(cliente.getEmail());
		tfFax.setText(cliente.getFax());
		TratamientoImpuestoBean tratamientoImp = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), cliente.getIdTratImpuestos());
		TratamientoImpuestoBean shopTrat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos());
		TratamientoImpuestoBean tratamientoSeleccion = null;
		for(TratamientoImpuestoBean tratImpuesto : tratamientosImp){
			if(tratImpuesto!=null){
				if(Objects.equals(tratImpuesto.getIdTratImpuestos(), tratamientoImp.getIdTratImpuestos())){
					tratamientoSeleccion = tratImpuesto;
					if(!tratImpuesto.getIdTratImpuestos().equals(shopTrat.getIdTratImpuestos())) {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}.",tratImpuesto.getDestratimp(),shopTrat.getDestratimp()), this.getStage());
					}
					break;
				}
			}
		}
		
		ocultarCampo(true, tfCodPais);

		cbTratamientoImpuestos.getSelectionModel().select(tratamientoSeleccion);
		taObservaciones.setText(cliente.getObservaciones());

		if(cliente.getTipoIdentificacion()!=null && !cliente.getTipoIdentificacion().isEmpty()){
			updateSelectedTipoIdentificacion();
		}
		
	}
	
	protected void updateSelectedTipoIdentificacion() {
        if (cliente != null) {
            updateSelectedTipoIdentificacion(cliente.getTipoIdentificacion());
        } else {
            cbTipoDocIdent.getSelectionModel().clearSelection();
        }
    }
	
	protected void updateSelectedTipoIdentificacion(String selectedTipoIdent) {
        cbTipoDocIdent.getSelectionModel().clearSelection();

        if (tiposIdent == null) {
            return;
        }

        TiposIdentBean tipoIdentSeleccionado = null;
        if (selectedTipoIdent != null) {
            for (TiposIdentBean tipoIdent : tiposIdent) {
                if (tipoIdent != null && tipoIdent.getCodTipoIden() != null && tipoIdent.getCodTipoIden().equals(selectedTipoIdent)) {
                    tipoIdentSeleccionado = tipoIdent;
                }
            }
        }
        if(tipoIdentSeleccionado != null) {
            cbTipoDocIdent.getSelectionModel().select(tipoIdentSeleccionado);
        } else {
            cbTipoDocIdent.getSelectionModel().select(0);
        }
    }

	protected void limpiarCampos(){
		tfRazonSocial.setText("");
		tfProvincia.setText("");
		tfDomicilio.setText("");
		tfLocalidad.setText("");
		tfTelefono.setText("");
		tfTelefono2.setText("");
		tfNumDocIdent.setText("");
		tfCodPais.setText("");
		codPais = "";
		tfDesPais.setText("");
		tfPoblacion.setText("");
		tfCP.setText("");
		updateSelectedTipoIdentificacion();
		cbActivo.setSelected(true);
		taObservaciones.setText("");
		tfDescripcion.setText("");
		tfEmail.setText("");
		tfFax.setText("");
		tfTarifa.setText("");
		cbTratamientoImpuestos.getSelectionModel().select(null);
		tfBanco.setText("");
		tfBancoDomicilio.setText("");
		tfBancoPoblacion.setText("");
		tfBancoCCC.setText("");
	}
	
	public void accionAceptar(){
		// Si el foco está en el input de código postal lo pasamos a otro sitio para que muestre la ventana de códigos postales si es necesario,
		// y evitar el bloqueo con otros mensajes que puedan salir
		if(tfCP.isFocused()) {
			tfDescripcion.requestFocus();
		}
		
		ClienteBean datosClienteSalvar = new ClienteBean();
		
		if(!(cliente == null)){
			BeanUtils.copyProperties(cliente, datosClienteSalvar);	
		}
			
		
		if(validarDatos()){
			if(estadoCliente == Estado.NUEVO){
				if(cliente == null){
					//cliente = new ClienteBean();
					datosClienteSalvar.setIdGrupoImpuestos(sesion.getAplicacion().getTienda().getCliente().getIdGrupoImpuestos());
					//El código de cliente no se puede editar, así que el que traiga el cliente se le queda, si no, el cif del cliente
					datosClienteSalvar.setCodCliente(tfNumDocIdent.getText());
				}
			}
			
			//datosClienteSalvar.setEstadoBean(cliente.getEstadoBean());
			//datosClienteSalvar.setIdGrupoImpuestos(cliente.getIdGrupoImpuestos());
			//datosClienteSalvar.setCodCliente(cliente.getCodCliente());	
			datosClienteSalvar.setEstadoBean(estadoCliente);
			datosClienteSalvar.setCodpais(tfCodPais.getText());
			datosClienteSalvar.setCif(tfNumDocIdent.getText());
			datosClienteSalvar.setCp(tfCP.getText());
			datosClienteSalvar.setDomicilio(tfDomicilio.getText());
			datosClienteSalvar.setPoblacion(tfPoblacion.getText());
			datosClienteSalvar.setLocalidad(tfLocalidad.getText());
			datosClienteSalvar.setTelefono1(tfTelefono.getText());
			datosClienteSalvar.setTelefono2(tfTelefono2.getText());
			datosClienteSalvar.setPais(tfDesPais.getText());
			if (cbTipoDocIdent.getSelectionModel().getSelectedItem() != null) {
				datosClienteSalvar.setTipoIdentificacion(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden());
			}
			datosClienteSalvar.setNombreComercial(tfRazonSocial.getText());
			datosClienteSalvar.setProvincia(tfProvincia.getText());
			datosClienteSalvar.setActivo(cbActivo.isSelected());
			datosClienteSalvar.setObservaciones(taObservaciones.getText());
			datosClienteSalvar.setDesCliente(tfDescripcion.getText());
			datosClienteSalvar.setFax(tfFax.getText());
			datosClienteSalvar.setEmail(tfEmail.getText());
			datosClienteSalvar.setIdTratImpuestos(cbTratamientoImpuestos.getSelectionModel().getSelectedItem().getIdTratImpuestos());

			datosClienteSalvar.setBanco(tfBanco.getText());
			datosClienteSalvar.setBancoDomicilio(tfBancoDomicilio.getText());
			datosClienteSalvar.setBancoPoblacion(tfBancoPoblacion.getText());
			datosClienteSalvar.setCcc(tfBancoCCC.getText());
			
			String codTar = tfTarifa.getText();
			
			if(StringUtils.isBlank(codTar)){
				codTar = null;
			}
			
			datosClienteSalvar.setCodtar(codTar);
			
			if(checkClientTaxes(datosClienteSalvar)) {
				if(datosClienteSalvar.getEstadoBean() == Estado.NUEVO){
					try {
						boolean bSalvar = true;
						String sResultado = validarDocumento();
						if(sResultado != null) {
							bSalvar = VentanaDialogoComponent.crearVentanaConfirmacion(sResultado, getStage());
							if(!bSalvar) {
								tfNumDocIdent.requestFocus();
								return;
							}
						}

						int res = clientesService.salvar(datosClienteSalvar);
						if(res == 0){
	                        VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo crear el cliente."), this.getStage());
	                    }
					} catch(ClienteConstraintViolationException e){
						log.error("Error actualizando el cliente.",e);
						VentanaDialogoComponent.crearVentanaError(this.getStage(), "Lo sentimos, ya existe un cliente con el  documento " +datosClienteSalvar.getCif() + " en el sistema ", e);
						return;
					} catch (ClientesServiceException e) {
						log.error("Error actualizando el cliente.",e);
						VentanaDialogoComponent.crearVentanaError(this.getStage(), e);
						return;
					}
				}
				else{
					boolean bSalvar = true;
					String sResultado = validarDocumento();
					if(sResultado != null) {
						bSalvar = VentanaDialogoComponent.crearVentanaConfirmacion(sResultado, getStage());
						if(!bSalvar) {
							tfNumDocIdent.requestFocus();
							return;
						}
					}

					if(!guardarDatosCliente(datosClienteSalvar))
	                    return;
				}

				if(cliente == null){
					cliente = new ClienteBean();
				}
				BeanUtils.copyProperties(datosClienteSalvar, cliente);
				
				for(View vista : getApplication().getMainView().getSubViews()) {
					if(vista instanceof IdentificacionClienteView) {
						HashMap<String, Object> mapDatos = new HashMap<String, Object>();
						mapDatos.put(CLIENTE_EDITADO, cliente);
						setDatos(mapDatos);
						vista.getController().setDatos(mapDatos);
					}
					else if(vista instanceof BuscarClienteView) {
						((BuscarClienteController) vista.getController()).accionBuscar();
					}
				}	
				
				setModoEdicion(false);
				getApplication().getMainView().close();
			}
		}
	}
	
	protected boolean checkClientTaxes(ClienteBean cliente) {
		TratamientoImpuestoBean trat = null;
		TratamientoImpuestoBean shopTrat = null;
		try {
			trat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), cliente.getIdTratImpuestos());
			shopTrat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos());
		}catch(Exception ignore) {}
		if(trat == null || !trat.getCodpais().equals(sesion.getAplicacion().getTienda().getCliente().getCodpais())) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No es posible seleccionar este cliente al tener un tratamiento de impuestos no disponible para el país asociado a la tienda actual."), this.getStage());
			return false;
		}else if(sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos().equals(trat.getIdTratImpuestos()) || VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}. Confirme si desea continuar.",trat.getDestratimp(),shopTrat.getDestratimp()), this.getStage())) {		
			return true;
		}
		return false;
	}

	protected void setModoEdicion(boolean modoEdicion){
		this.modoEdicion = modoEdicion;
		
		tfNumDocIdent.setEditable(modoEdicion);
		tfRazonSocial.setEditable(modoEdicion);
		tfProvincia.setEditable(modoEdicion);
		tfDomicilio.setEditable(modoEdicion);
		tfLocalidad.setEditable(modoEdicion);
		tfTelefono.setEditable(modoEdicion);
		tfTelefono2.setEditable(modoEdicion);
		tfFax.setEditable(modoEdicion);
		tfEmail.setEditable(modoEdicion);
		tfDescripcion.setEditable(modoEdicion);
		tfCodPais.setEditable(modoEdicion);
		tfDesPais.setEditable(modoEdicion);
		tfPoblacion.setEditable(modoEdicion);
		tfCP.setEditable(modoEdicion);
		cbTipoDocIdent.setDisable(!modoEdicion);
		cbActivo.setDisable(!modoEdicion);
		taObservaciones.setEditable(modoEdicion);
		cbTratamientoImpuestos.setDisable(!modoEdicion);
		btBuscarCentral.setDisable(!modoEdicion);
		btBuscarPais.setDisable(!modoEdicion);

		tfBanco.setEditable(modoEdicion);
		tfBancoDomicilio.setEditable(modoEdicion);
		tfBancoPoblacion.setEditable(modoEdicion);
		tfBancoCCC.setEditable(modoEdicion);

		botoneraAccionesTabla.getBotonClave("VOLVER").setDisable(modoEdicion);
		botoneraAccionesTabla.getBotonClave("AÑADIR").setDisable(modoEdicion);
		botoneraAccionesTabla.getBotonClave("EDITAR").setDisable(modoEdicion);
		botoneraAccionesTabla.getBotonClave("ELIMINAR").setDisable(modoEdicion);
		botoneraAccionesTabla.getBotonClave("ACCION_TABLA_PRIMER_REGISTRO").setDisable(modoEdicion);
		botoneraAccionesTabla.getBotonClave("ACCION_TABLA_ANTERIOR_REGISTRO").setDisable(modoEdicion);
		botoneraAccionesTabla.getBotonClave("ACCION_TABLA_SIGUIENTE_REGISTRO").setDisable(modoEdicion);
		botoneraAccionesTabla.getBotonClave("ACCION_TABLA_ULTIMO_REGISTRO").setDisable(modoEdicion);

		botoneraAccionesTabla.getBotonClave("ACEPTAR").setDisable(!modoEdicion);
		botoneraAccionesTabla.getBotonClave("CANCELAR").setDisable(!modoEdicion);
	}

	@FXML
	public void accionBuscarPais(){
		getApplication().getMainView().showModalCentered(PaisesView.class, getDatos(), this.getStage());

		if(getDatos()!=null && getDatos().containsKey(PaisesController.PARAMETRO_SALIDA_PAIS)){
			PaisBean pais = (PaisBean)getDatos().get(PaisesController.PARAMETRO_SALIDA_PAIS);
			tfDesPais.setText(pais.getDesPais());
			tfCodPais.setText(pais.getCodPais());
			codPais = pais.getCodPais().toUpperCase();
			loadTiposIdentificacion();
			cbTipoDocIdent.getSelectionModel().selectFirst();

		}
	}

	protected PaisBean obtenerPais(String codPais){
		PaisBean pais = null;

		try {
			pais = paisService.consultarCodPais(codPais);
		}
		catch (PaisNotFoundException ex) {
			log.error("No se encontró el código del cliente.");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda, país no encontrado"), getStage());
			tfCodPais.setText("");
			this.codPais = "";
			tfDesPais.setText("");
		}
		catch (PaisServiceException ex) {
			log.error("Error buscando el código del cliente.", ex);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error en la búsqueda del país del cliente."), getStage());
			tfCodPais.setText("");
			this.codPais = "";
			tfDesPais.setText("");
		}

		return pais;
	}

	protected boolean guardarDatosCliente(ClienteBean cliente){
		
		try {
			int actualizados = clientesService.salvar(cliente);
			if( actualizados  == 0){
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se encontró el cliente cuyos datos se desean modificar."), this.getStage());
			}
		} catch (ClientesServiceException e) {
			log.error("Error actualizando el cliente.",e);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo crear el cliente."), this.getStage());
			return false;
		} catch (ClienteConstraintViolationException e) {
			log.error("Error actualizando el cliente.",e);
			VentanaDialogoComponent.crearVentanaAviso(e.getMessage(), this.getStage());
			return false;
		}
		return true;
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		switch (botonAccionado.getClave()) {
		case "VOLVER":
			try {
				if(seHaEliminadoCliente) {
					for(View vista : getApplication().getMainView().getSubViews()) {
						if(vista instanceof IdentificacionClienteView) {
							HashMap<String, Object> mapDatos = new HashMap<String, Object>();
							mapDatos.put(CLIENTE_EDITADO, cliente);
							vista.getController().setDatos(mapDatos);
						}
						else if(vista instanceof BuscarClienteView) {
							((BuscarClienteController) vista.getController()).accionBuscar();
						}
					}
				}
				getView().previousSubView();
			} catch (InitializeGuiException e) {
				log.error("realizarAccion() - " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
			break;
		case "AÑADIR":
			estadoCliente = Estado.NUEVO;
			nuevoCliente();
			break;
		case "EDITAR":
			estadoCliente = Estado.MODIFICADO;
			setModoEdicion(true);
			break;
		case "ELIMINAR":
			if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se borrará el cliente seleccionado. ¿Está seguro?"), getStage())){
				MantenimientoClienteTasks.executeEliminarTask(cliente, clientes, getStage(), new Callback<Boolean, Void>() {
					@Override
					public Void call(Boolean result) {
						if(result){
							if(clientes.isEmpty()){
								try {
									getView().previousSubView();
								} catch (InitializeGuiException e) {
									log.error("realizarAccion() - " + e.getMessage(), e);
									VentanaDialogoComponent.crearVentanaError(getStage(), e);
								}
							}else{
								seHaEliminadoCliente = true;
								indexCliente--;
								if(indexCliente < 0){
									indexCliente = 0;
								}
								cliente = clientes.get(indexCliente).getCliente();
								refrescarDatosPantalla();
							}
						}
						return null;
					}
				});
			}
			break;
		case "ACCION_TABLA_PRIMER_REGISTRO":
			indexCliente = 0;
			cliente = clientes.get(indexCliente).getCliente();
			refrescarDatosPantalla();
			break;
		case "ACCION_TABLA_ANTERIOR_REGISTRO":
			indexCliente--;
			if(indexCliente < 0){
				indexCliente = 0;
			}
			cliente = clientes.get(indexCliente).getCliente();
			refrescarDatosPantalla();
			break;
		case "ACCION_TABLA_SIGUIENTE_REGISTRO":
			indexCliente++;
			if(indexCliente >= clientes.size()){
				indexCliente = clientes.size() - 1;
			}
			cliente = clientes.get(indexCliente).getCliente();
			refrescarDatosPantalla();
			break;
		case "ACCION_TABLA_ULTIMO_REGISTRO":
			indexCliente = clientes.size() - 1;
			cliente = clientes.get(indexCliente).getCliente();
			refrescarDatosPantalla();
			break;
		case "ACEPTAR":
			accionAceptar();
			
			break;
		case "CANCELAR":
			setModoEdicion(false);
			if(clientes == null){
				getApplication().getMainView().close();
			}
			else{
				if(cliente == null){
					cliente = clientes.get(indexCliente).getCliente();
				}
				cliente = clientes.get(indexCliente).getCliente();
				refrescarDatosPantalla();
				estadoCliente = Estado.SIN_MODIFICAR;
			}
			break;
		}
	}

	public void nuevoCliente(){
		setModoEdicion(true);
		limpiarCampos();
		cliente = null;
	}

	public void cargarClienteCentral(){

		if(validarBusCentral()){
			String codPais = null;
			if(cbTipoDocIdent.getSelectionModel().getSelectedItem() != null && StringUtils.isNotBlank(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden())) {
				if(StringUtils.isNotBlank(tfCodPais.getText())) {
					codPais = tfCodPais.getText();
				}
				else {
					codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
				}
			}
			
			ConsultarClienteRequestRest consultaCliente = new ConsultarClienteRequestRest(
					variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY), 
					sesion.getAplicacion().getUidActividad(), tfNumDocIdent.getText(), codPais, cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden());

			try {
				ResponseGetClienteRest res = ClientesRest.getClientePais(consultaCliente);
				if(res.getCodError()!= 0){
					VentanaDialogoComponent.crearVentanaAviso(res.getMensaje(), this.getStage());
				}
				else{
					limpiarCampos();
					cliente = new ClienteBean();
					cliente.setIdGrupoImpuestos(sesion.getAplicacion().getTienda().getCliente().getIdGrupoImpuestos());

					cliente.setActivo(res.isActivo());
					cliente.setBanco(res.getBanco());
					cliente.setCcc(res.getCcc());
					cliente.setTipoIdentificacion(res.getCodTipoIden());
					cliente.setCif(res.getCif());
					cliente.setCodCliente(res.getCodCliente());
					cliente.setCodpais(res.getCodPais());
					cliente.setCodsec(res.getCodSec());
					cliente.setCodtar(res.getCodTar());
					cliente.setCp(res.getCp());
					cliente.setUidActividad(sesion.getAplicacion().getUidActividad());
					cliente.setProvincia(res.getProvincia());
					cliente.setPoblacion(res.getPoblacion());
					cliente.setTelefono1(res.getTelefono1());
					cliente.setTelefono2(res.getTelefono2());
					cliente.setRiesgoConcedido(res.getRiesgoConcedido());
					cliente.setPersonaContacto(res.getPersonaContacto());
					cliente.setPais(res.getDesPais());
					cliente.setObservaciones(res.getObservaciones());
					cliente.setEmail(res.getEmail());
					cliente.setDomicilio(res.getDomicilio());
					cliente.setFax(res.getFax());
					cliente.setNombreComercial(res.getNombreComercial());
					cliente.setIdTratImpuestos(res.getIdTratImp());
					cliente.setIdMedioPagoVencimiento(res.getIdMedioPagoVencimiento());
					cliente.setFechaBaja(res.getFechaBaja());
					cliente.setFechaAlta(res.getFechaAlta());
					cliente.setDesCliente(res.getDesCliente());

					refrescarDatosPantalla();
				}
			} catch (RestException | RestHttpException e) {
				log.error("Error consultando el cliente en central.",e);
				String msg = e.getMessage();
				
				if(msg ==null || msg.isEmpty()){
					msg = I18N.getTexto("La petición no se procesó correctamente.");
				}
				else if (msg.contains("HTTP 404")){
					msg = (I18N.getTexto("Error. dirección de servicio rest no encontrada (HTTP 404)"));
	            }
	            else if (msg.contains("HTTP 4")){
	            	msg = (I18N.getTexto("No se pudo conectar con los servicios rest (HTTP 400)"));
	            }
	            else if (msg.contains("HTTP 5")){
	            	msg = (I18N.getTexto("No se pudo conectar con los servicios rest (HTTP 500)"));
	            }
	            else if (msg.contains("Connection refused")){
	            	msg = (I18N.getTexto("No se pudo conectar con los servicios rest"));
	            }
	            else if(msg.equals("El cliente consultado no existe en el sistema")) {
	            	if(cbTipoDocIdent.getSelectionModel().getSelectedItem() != null && StringUtils.isNotBlank(cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden())) {
	            		msg = I18N.getTexto("El cliente con el documento {0}, el tipo de documento {1} y el país {2} no existe en el sistema", tfNumDocIdent.getText(), cbTipoDocIdent.getSelectionModel().getSelectedItem().getCodTipoIden(), codPais);
	            	}
	            	else {
	            		msg = I18N.getTexto("El cliente con el documento {0} no existe en el sistema", tfNumDocIdent.getText());
	            	}
	            }
				
				VentanaDialogoComponent.crearVentanaAviso(msg, this.getStage());
			}
		}
	}

	public boolean validarDatos(){
		log.debug("validarFormularioDatosFactura()");

		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frBusqCentral.clearErrorStyle();
		frDatosCliente.clearErrorStyle();
		
		frDatosCliente.setDomicilio(tfDomicilio.getText());
		frDatosCliente.setcPostal(tfCP.getText());
		frDatosCliente.setNumDocIdent(tfNumDocIdent.getText());
		frDatosCliente.setProvincia(tfProvincia.getText());
		frDatosCliente.setRazonSocial(tfRazonSocial.getText());
		frDatosCliente.setPoblacion(tfPoblacion.getText());
		frDatosCliente.setLocalidad(tfLocalidad.getText());
		frDatosCliente.setPais(tfCodPais.getText());
		frDatosCliente.setTelefono(tfTelefono.getText());
		frDatosCliente.setTelefono2(tfTelefono2.getText());
		frDatosCliente.setDescripcion(tfDescripcion.getText());
		frDatosCliente.setFax(tfFax.getText());
		frDatosCliente.setEmail(tfEmail.getText());

		frDatosCliente.setBanco(tfBanco.getText());
		frDatosCliente.setBancoDomicilio(tfBancoDomicilio.getText());
		frDatosCliente.setBancoPoblacion(tfBancoPoblacion.getText());
		frDatosCliente.setBancoCCC(tfBancoCCC.getText());
		
		frDatosCliente.setObservaciones(taObservaciones.getText());
		
		try {
            paisService.consultarCodPais(tfCodPais.getText());
        }
        catch (PaisNotFoundException e) {
        	PathImpl path = PathImpl.createPathFromString("pais");
        	frDatosCliente.setFocus(path);
        	frDatosCliente.setErrorStyle(path, true);
        	return false;
        }
        catch (PaisServiceException e) {
        	log.debug("validarDatos() - Ha habido un error al buscar el país con código " + tfCodPais.getText() + ": " + e.getMessage());
        	return false;
        }
		
		TratamientoImpuestoBean tratImpuesto = cbTratamientoImpuestos.getSelectionModel().getSelectedItem();
		if(tratImpuesto == null){
			frDatosCliente.setIdTratImpuesto("");
		}
		else{
			frDatosCliente.setIdTratImpuesto(tratImpuesto.getCodtratimp());
		}
		
		//Limpiamos el posible error anterior
		lbError.setText("");

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioMantenimientoClientesBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frDatosCliente);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioMantenimientoClientesBean> next = constraintViolations.iterator().next();
			Path path = next.getPropertyPath();
			if (path.toString().toLowerCase().contains("banco")) {
				tabPane.getSelectionModel().select(tabBanco);
			} else {
				tabPane.getSelectionModel().select(tabGeneral);
			}
			frDatosCliente.setErrorStyle(path, true);
			frDatosCliente.setFocus(path);
			lbError.setText(next.getMessage());
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}
	
	public String validarDocumento() {
		TiposIdentBean tipoIden = cbTipoDocIdent.getSelectionModel().getSelectedItem();
		String sResultado = null;
		if(tipoIden != null && StringUtils.isNotBlank(tipoIden.getCodTipoIden()) && StringUtils.isNotBlank(tipoIden.getClaseValidacion()) && StringUtils.isNotBlank(tfNumDocIdent.getText())) {
			String claseValidacion = tipoIden.getClaseValidacion();
			String cif = tfNumDocIdent.getText();
			
			try {
				IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
				if(!validadorDocumentoIdentificacion.validarDocumentoIdentificacion(cif)){
					sResultado = I18N.getTexto("El documento indicado no es válido, ¿desea guardar el cliente de todas formas?");
				}
			}
			catch(Exception e) {
				sResultado = I18N.getTexto("Ha habido un error al intentar validar el documento, ¿desea guardar el cliente de todas formas?");
				log.error("validarDocumento() - Ha habido un error al intentar validar el documento: " + e.getMessage());
			}
		}
		return sResultado;
	}
	
	public boolean validarBusCentral(){
		log.debug("validarFormularioDatosFactura()");

		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frBusqCentral.clearErrorStyle();
		frDatosCliente.clearErrorStyle();
		
		frBusqCentral.setCodCliente(tfNumDocIdent.getText().trim());
		
		//Limpiamos el posible error anterior
		lbError.setText("");

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioBusquedaCentralBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBusqCentral);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioBusquedaCentralBean> next = constraintViolations.iterator().next();
			frBusqCentral.setErrorStyle(next.getPropertyPath(), true);
			frBusqCentral.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}

	public void ocultarCampo(boolean ocultar, Node elemento){
		
		Node parent = elemento.getParent();
		parent.prefHeight(0.0);
	}

	protected void buscarCodigoPostal() {
		if(modoEdicion) {
	        String codigoPostal = tfCP.getText();
	    	if(codigoPostal != null && !codigoPostal.equals("")) {
	    		try {
	                List<CodigoPostalBean> codigosPostales = codPostalesServices.obtieneCodigoPostal(codigoPostal);
	                if(codigosPostales != null && !codigosPostales.isEmpty()) {
	                	if(codigosPostales.size() == 1) {
	                		CodigoPostalBean cp = codigosPostales.get(0);
	                		tfLocalidad.setText(cp.getLocalidad());
	                		tfPoblacion.setText(cp.getPoblacion());
	                		tfProvincia.setText(cp.getProvincia());
	                	} else {
	                		HashMap<String, Object> parametros= new HashMap<>();
	                		parametros.put(SeleccionPoblacionController.PARAMETRO_LISTA_CODIGOS_POSTALES, codigosPostales);
	                		getApplication().getMainView().showModalCentered(SeleccionPoblacionView.class, parametros, this.getStage());
	                		
	                		CodigoPostalBean cp = (CodigoPostalBean) parametros.get(SeleccionPoblacionController.PARAMETRO_CODIGO_POSTAL_SELECCIONADO);
	                		if(cp != null) {
		                		tfLocalidad.setText(cp.getLocalidad());
		                		tfPoblacion.setText(cp.getPoblacion());
		                		tfProvincia.setText(cp.getProvincia());
	                		}
	                	}
	                }
	            }
	            catch (CodPostalesException e) {
	            	log.error("inicializarComponentes() - Error buscando código postal : " + e.getMessage(), e);
	    			VentanaDialogoComponent.crearVentanaError(e.getMessageI18N(), getStage());
	            }
	    	}
		}
    }
	
	@FXML
 	public void accionValidarDocumento() {
		if (cbTipoDocIdent.getSelectionModel().getSelectedItem() != null) {
			String claseValidacion = cbTipoDocIdent.getSelectionModel().getSelectedItem().getClaseValidacion();
			try{
				IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
				
				if(validadorDocumentoIdentificacion.validarDocumentoIdentificacion(tfNumDocIdent.getText())){
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Documento valido"), getStage());
				}else{
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Documento no valido"), getStage());
				}
			}
			catch(ClassCastException e) {
				String mensaje = I18N.getTexto("La clase {0} no implementa la interfaz {1}", claseValidacion, IValidadorDocumentoIdentificacion.class);
				VentanaDialogoComponent.crearVentanaError(mensaje, getStage());
			}
			catch(ClassNotFoundException e) {
				String mensaje = I18N.getTexto("No se ha encontrado la clase {0}", claseValidacion);
				VentanaDialogoComponent.crearVentanaError(mensaje, getStage());
			}
			catch(Exception e) {
				String mensaje = I18N.getTexto("Error validando el documento de identificación: ") + e.getMessage();
				VentanaDialogoComponent.crearVentanaError(mensaje, getStage());
			}	
		}
 	}
	
	@Override
	public boolean canClose() {
	    if(super.canClose()){
	    	setModoEdicion(false);
	    	return true;
	    }else{
	    	return false;
	    }
	}
	
}
