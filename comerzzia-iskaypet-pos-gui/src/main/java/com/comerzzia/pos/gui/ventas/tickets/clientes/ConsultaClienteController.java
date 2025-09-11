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

package com.comerzzia.pos.gui.ventas.tickets.clientes;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Controller
public class ConsultaClienteController extends WindowController implements Initializable, IContenedorBotonera {
	
	@Autowired
	private Sesion sesion;

    // <editor-fold desc="Declaración de variables">
    // Variables
    private static final Logger log = Logger.getLogger(ConsultaClienteController.class.getName());

    public static final String PARAMETRO_SALIDA_CLIENTE = "PARAMETRO_SALIDA_CLIENTE";
    public static final String MODO_MODAL = "MODAL";

    //  - lista de clientesBuscados
    protected ObservableList<ClienteGui> clientesBuscados;
    
    //  - cliente seleccionado
    protected ClienteBean objetoSeleccionado;
    
    //  - botonera de acciones de tabla
    protected BotoneraComponent botoneraAccionesTabla;
    
    //  - acciones de pantalla al aceptar o accionCancelar
    protected EventHandler actionHandlerCancelar;

    // Componentes
    @FXML
    protected TextField tfNumDocIdent, tfDescripcion;

    @FXML
	protected TableView<ClienteGui> tbClientes;
    @FXML
    protected TableColumn<ClienteGui, String> tcClientesDescripcion;
    @FXML
    protected TableColumn<ClienteGui, String> tcClientesCif;
    @FXML
    protected TableColumn<ClienteGui, String> tcClientesPoblacion;
    @FXML
    protected TableColumn<ClienteGui, String> tcClientesProvincia;

    @FXML
    protected AnchorPane panelMenuTabla;

    @FXML
	protected ComboBox<TiposIdentBean> cbTipoDocIdent;
    protected ObservableList<TiposIdentBean> tiposIdent;
    
    @FXML
    protected Label lbError;
    
    @FXML
    protected HBox footerHBox;

    protected FormularioConsultaClienteBean frConsultaCliente;
    
    protected Boolean modoModal = false;
    
    @Autowired
    private TiposIdentService tiposIdentService;
    
    @Autowired
    private ClientesService clientesService;

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

        tbClientes.setPlaceholder(new Text(""));
        
        clientesBuscados = FXCollections.observableArrayList(new ArrayList<ClienteGui>());
        tbClientes.setItems(clientesBuscados);

        // Inicializamos la tabla
        tcClientesDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbClientes", "tcClientesDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcClientesCif.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbClientes", "tcClientesCif", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcClientesPoblacion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbClientes", "tcClientesPoblacion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcClientesProvincia.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbClientes", "tcClientesProvincia", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        
        tcClientesDescripcion.setCellValueFactory(new PropertyValueFactory<ClienteGui, String>("descripcion"));
        tcClientesCif.setCellValueFactory(new PropertyValueFactory<ClienteGui, String>("cif"));
        tcClientesPoblacion.setCellValueFactory(new PropertyValueFactory<ClienteGui, String>("poblacion"));
        tcClientesProvincia.setCellValueFactory(new PropertyValueFactory<ClienteGui, String>("provincia"));

        //Creamos el formulario que validará los parámetros para la búsqueda de clientes
        frConsultaCliente = SpringContext.getBean(FormularioConsultaClienteBean.class);
        frConsultaCliente.setFormField("numDocIdent", tfNumDocIdent);
        frConsultaCliente.setFormField("descCliente", tfDescripcion);
    }

    @Override
    public void initializeForm() {
    	AnchorPane pane = (AnchorPane) getView().getViewNode().getChildrenUnmodifiable().get(0);
    	Boolean modal = (Boolean) getDatos().get(MODO_MODAL);
    	if(modal != null && modal){
    		modoModal = true;
    		pane.getStyleClass().add("pantalla-modal");
    		footerHBox.setPrefHeight(60.0);
    		footerHBox.setVisible(true);
    	}else{
    		modoModal = false;
    		pane.getStyleClass().remove("pantalla-modal");
    		footerHBox.setPrefHeight(0.0);
    		footerHBox.setVisible(false);
    	}	
        
		try {
			List<TiposIdentBean> tiposIdentificacion = tiposIdentService.consultarTiposIdent(null, true, 
					sesion.getAplicacion().getTienda().getCliente().getCodpais());

			tiposIdent = FXCollections.observableArrayList();
			//Añadimos elemento vacío
			tiposIdent.add(new TiposIdentBean());

			for(TiposIdentBean tipoIdent: tiposIdentificacion){
//				tiposIdent.add(tipoIdent.getCodTipoIden());
				tiposIdent.add(tipoIdent);
			}
			cbTipoDocIdent.setItems(tiposIdent);
			
			tfNumDocIdent.setText("");
			tfDescripcion.setText("");
			cbTipoDocIdent.getSelectionModel().clearSelection();
		}
		catch (TiposIdentNotFoundException ex) {
			log.error("initializeForm() - No se encontró ningún tipo de identificación.", ex);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se encontraron documentos de identificación configurados para la tienda."), this.getStage());
		}
		catch (TiposIdentServiceException ex) {
			log.error("initializeForm() - Error consultando los tipos de identificación.",ex);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error consultando los documentos de identificación de la tienda."), this.getStage());
		}
		catch(Exception ex){
			log.error("initializeForm() - Se produjo un error en el tratamiento de los tipos de identificacion", ex);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error consultando los documentos de identificación de la tienda."), this.getStage());
		}
    }

    @Override
    public void initializeFocus() {
        tfNumDocIdent.requestFocus();
    }

    /**
     * Inicializa los componentes establecer la configuración de la ventana
     */
    @Override
    public void initializeComponents() {
        log.debug("inicializarComponentes()");
        try {
            List<ConfiguracionBotonBean> listaAccionesAccionesTabla = BotoneraComponent.cargarAccionesTablaSimple();

            log.debug("inicializarComponentes() - Configurando botonera");
            botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
            panelMenuTabla.getChildren().add(botoneraAccionesTabla);

            //Se registra el evento para salir de la pantalla pulsando la tecla escape.
            registrarAccionCerrarVentanaEscape();

        }
        catch (CargarPantallaException ex) {
            log.error("inicializarComponentes() - Error creando botonera para la consulta de clientes. error : " + ex.getMessage(), ex);
            VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla pagos."), getStage());
        }
    }

    /**
     * Establece la acción que será ejecutada tras accionCancelar en la ventana
     *
     * @param actionHandlerCancelar
     */
    public void setActionHandlerCancelar(EventHandler actionHandlerCancelar) {
        this.actionHandlerCancelar = actionHandlerCancelar;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Funciones relacionadas con interfaz GUI y manejo de pantalla">
    /**
     * Función que refresca los totales en pantalla
     */
    /**
     * Acción aceptar
     */
    @FXML
    public void accionBotonAceptarCliente(ActionEvent event) {
        log.debug("accionBotonAceptarCliente() - Acción aceptar");
        tratarClienteSeleccionado();
    }

    /**
     * Accion para tratar el doble click en una de las filas de la pantalla
     *
     * @param event
     */
    @FXML
    public void accionTablaAceptarCliente(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                tratarClienteSeleccionado();
            }
        }
    }

    /**
     * Método para establecer el cliente seleccionado y notificarlo
     */
    protected void tratarClienteSeleccionado() {
        if(establecerClienteSeleccionado()){
            //getStage().close();
            cierraVentana();
        }
    }

    /**
     * Valida los parámetros de búsqueda introducidos
     *
     * @return
     */
    protected boolean validarFormularioConsultaCliente() {

        boolean valido;

        // Limpiamos los errores que pudiese tener el formulario
        frConsultaCliente.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<FormularioConsultaClienteBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frConsultaCliente);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<FormularioConsultaClienteBean> next = constraintViolations.iterator().next();
            frConsultaCliente.setErrorStyle(next.getPropertyPath(), true);
            frConsultaCliente.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            valido = false;
        }
        else {
            valido = true;
        }

        return valido;
    }

    /**
     * Acción accionCancelar
     */
    public void accionCancelar() {
    	log.debug("accionCancelar() - Cancelar");
    	
        // Limpiamos los errores que pudiese tener el formulario
        frConsultaCliente.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");
        //getStage().close();
        cierraVentana();
        // Ejecutamos el código de aceptación de la ventana padre que abre esta
        if (actionHandlerCancelar != null) {
            actionHandlerCancelar.handle(null);
        }
    }

    @FXML
    public void accionBuscarTeclado(KeyEvent event) {
        log.trace("accionBuscarTeclado()");
        
        if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
            accionBuscar();
        }
        else if(event.getCode() == KeyCode.DOWN && !event.isControlDown() && tbClientes.getItems().size() > 0) {
			tbClientes.requestFocus();
		}
    }

    /**
     * Busca un cliente en función de los campos de búsqueda de la pantalla.
     */
    @FXML
    public void accionBuscar() {
        log.trace("accionBuscar()");
        
        clientesBuscados.clear();
        
        frConsultaCliente.setNumDocIdent(tfNumDocIdent.getText());
        frConsultaCliente.setDescCliente(tfDescripcion.getText());
        
        if(validarFormularioConsultaCliente()){
	        String descripcion = tfDescripcion.getText();
	        String ident = tfNumDocIdent.getText();
	        String codTipoIdent = null;
	        if(cbTipoDocIdent.getValue() != null) {
	        	codTipoIdent = cbTipoDocIdent.getValue().getCodTipoIden();
	        }
	        else {
	        	codTipoIdent = "";
	        }
	        new BuscarClientesTask(codTipoIdent, ident, descripcion).start();
        }
    }

    class BuscarClientesTask extends BackgroundTask<List<ClienteBean>>{
    	private String codTipoIdent;
    	private String ident;
		private String descripcion;
		public BuscarClientesTask(String codTipoIdent, String ident, String descripcion) {
			this.codTipoIdent = codTipoIdent;
			this.ident = ident;
			this.descripcion = descripcion;
		}

		@Override
		protected List<ClienteBean> call() throws Exception {
			return clientesService.consultarClientes(codTipoIdent, ident, descripcion);
		}

		@Override
		protected void failed() {
			log.error(getCMZException().getLocalizedMessage(), getCMZException());
            VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessageI18N(), getCMZException());
			super.failed();
		}

		@Override
		protected void succeeded() {
			List<ClienteBean> clientesF = getValue();
			List<ClienteGui> clientesTabla = new ArrayList<ClienteGui>();
		
			for(ClienteBean c : clientesF){
				clientesTabla.add(new ClienteGui(c));
			}
            
            if (clientesTabla.isEmpty()) {
                tbClientes.setPlaceholder(new Text(I18N.getTexto("No hay registros para los parámetros de búsqueda seleccionados")));
            }
            else{
            	clientesBuscados.addAll(clientesTabla);
            	tbClientes.getSelectionModel().selectFirst();
            }
            super.succeeded();
		}
    }
    
    /**
     * Funcion auxiliar que llamaremos para establecer como cliente seleccionado de la pantalla, el cliente seleccionado en la tabla
     */
    protected boolean establecerClienteSeleccionado() {
        int indice = tbClientes.getSelectionModel().getSelectedIndex();
        if (indice >= 0) {
            objetoSeleccionado = tbClientes.getItems().get(indice).getCliente();
            getDatos().put(PARAMETRO_SALIDA_CLIENTE, objetoSeleccionado);
            return true;
        }
        else {
            objetoSeleccionado = null;
            VentanaDialogoComponent.crearVentanaAviso("", I18N.getTexto("Debe seleccionar el cliente que desee establecer para la venta."), getStage());
            return false;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="AccionesMenu">

    /**
     * Método de control de acciones de página de clientesBuscados
     *
     * @param botonAccionado botón pulsado
     */
    @Override
    public void realizarAccion(BotonBotoneraComponent botonAccionado) {
        log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
        switch (botonAccionado.getClave()) {

            case "ACCION_TABLA_PRIMER_REGISTRO":
                log.debug("Acción seleccionar primer registro de la tabla");
                accionTablaPrimerRegistro(tbClientes);
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                log.debug("Acción seleccionar registro anterior de la tabla");
                accionTablaIrAnteriorRegistro(tbClientes);
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                log.debug("Acción seleccionar siguiente registro de la tabla");
                accionTablaIrSiguienteRegistro(tbClientes);
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                log.debug("Acción seleccionar último registro de la tabla");
                accionTablaUltimoRegistro(tbClientes);
                break;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Getters de objetos de la pantalla">
    /**
     * Devuelve el cliente seleccionado de la pantalla
     *
     * @return
     */
    public ClienteBean getObjetoSeleccionado() {
        return objetoSeleccionado;
    }

    // </editor-fold>
    
    
    protected void cierraVentana(){
    	clientesBuscados.clear();
    	getStage().close();
    }
    
    public void aceptarClienteTeclado(KeyEvent event) {
        log.trace("aceptarClienteTeclado(KeyEvent event) - Aceptar");
        if (event.getCode() == KeyCode.ENTER) {
        	tratarClienteSeleccionado();
        }
    }
}
