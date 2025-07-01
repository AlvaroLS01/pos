package com.comerzzia.pos.gui.sales.retail.customer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.model.CountryIdType;
import com.comerzzia.core.facade.service.countryidentificationtype.CountryIdentificationTypeFacade;
import com.comerzzia.omnichannel.facade.model.PaginatedRequest;
import com.comerzzia.omnichannel.facade.model.PaginatedResponse;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.model.sale.OmnichannelCustomerFilter;
import com.comerzzia.omnichannel.facade.service.sale.customer.CustomerServiceFacade;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.log4j.Log4j;

@Controller
@SuppressWarnings("rawtypes")
@CzzScene
@Log4j
public class CustomerSearchController extends SceneController implements Initializable, ButtonsGroupController {
	
	@Autowired
	protected Session session;

    //  - lista de clientesBuscados
    protected ObservableList<CustomerDto> queriedCustomers;
    
    //  - cliente seleccionado
    protected Customer selectedCustomer;
    
    //  - botonera de acciones de tabla
    protected ButtonsGroupComponent buttonsGroupComponent;
    
    //  - acciones de pantalla al aceptar o accionCancelar    
	protected EventHandler actionHandlerCancel;

    // Componentes
    @FXML
    protected TextField tfVatNumber, tfDescription;

    @FXML
	protected TableView<CustomerDto> tbCustomers;
    @FXML
    protected TableColumn<CustomerDto, String> tcCustomersDescription;
    @FXML
    protected TableColumn<CustomerDto, String> tcCustomersVatNumber;
    @FXML
    protected TableColumn<CustomerDto, String> tcCustomersCity;
    @FXML
    protected TableColumn<CustomerDto, String> tcCustomersProvince;

    @FXML
    protected VBox menuPanelTable;

    @FXML
	protected ComboBox<CountryIdType> cbIdentType;
    protected ObservableList<CountryIdType> identTypes;
    
    @FXML
    protected Label lbError;
    
    @FXML
    protected HBox footerHBox;

    protected CustomerSearchForm frQueryCustomer;
    
    protected Boolean modalMode = false;
    
    @Autowired
    protected CountryIdentificationTypeFacade identTypeService;
    
    @Autowired
    protected CustomerServiceFacade customersService;

    /**
     * Inicializa el componente tras su creación. No hay acceso al application
     * desde este método.
     *
     * @param url
     * @param rb
     */
    @SuppressWarnings("unchecked")
	@Override
    public void initialize(URL url, ResourceBundle rb) {
        log.debug("initialize()");

        tbCustomers.setPlaceholder(new Text(""));
        
        queriedCustomers = FXCollections.observableArrayList(new ArrayList<CustomerDto>());
        tbCustomers.setItems(queriedCustomers);

        // Inicializamos la tabla
        tcCustomersDescription.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCustomers", "tcCustomersDescription", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCustomersVatNumber.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCustomers", "tcCustomersVatNumber", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCustomersCity.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCustomers", "tcCustomersCity", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCustomersProvince.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCustomers", "tcCustomersProvince", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        
        tcCustomersDescription.setCellValueFactory(new PropertyValueFactory<CustomerDto, String>("description"));
        tcCustomersVatNumber.setCellValueFactory(new PropertyValueFactory<CustomerDto, String>("vatNumber"));
        tcCustomersCity.setCellValueFactory(new PropertyValueFactory<CustomerDto, String>("city"));
        tcCustomersProvince.setCellValueFactory(new PropertyValueFactory<CustomerDto, String>("province"));

        //Creamos el formulario que validará los parámetros para la búsqueda de clientes
        frQueryCustomer = SpringContext.getBean(CustomerSearchForm.class);
        frQueryCustomer.setFormField("identTypeCode", tfVatNumber);
        frQueryCustomer.setFormField("customerDes", tfDescription);
    }

    @Override
    public void onSceneOpen() {
		List<CountryIdType> countryIdentTypes = identTypeService.findByCountryCode(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode(), null);

		identTypes = FXCollections.observableArrayList();
		//Añadimos elemento vacío
		identTypes.add(new CountryIdType());

		for(CountryIdType countryIdentType: countryIdentTypes){
//				tiposIdent.add(tipoIdent.getCodTipoIden());
			identTypes.add(countryIdentType);
		}
		cbIdentType.setItems(identTypes);
		
		tfVatNumber.setText("");
		tfDescription.setText("");
		cbIdentType.getSelectionModel().clearSelection();
    }

    @Override
    public void initializeFocus() {
        tfVatNumber.requestFocus();
    }

    /**
     * Inicializa los componentes establecer la configuración de la ventana
     */
    @Override
    public void initializeComponents() {
        log.debug("initializeComponents()");
        //Se registra el evento para salir de la pantalla pulsando la tecla escape.
    }

    /**
     * Establece la acción que será ejecutada tras accionCancelar en la ventana
     *
     * @param actionHandlerCancelar
     */
    public void setActionHandlerCancel(EventHandler actionHandlerCancel) {
        this.actionHandlerCancel = actionHandlerCancel;
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
    public void actionButtonAcceptCustomer(ActionEvent event) {
        log.debug("actionButtonAcceptCustomer() - Acción aceptar");
        processSelectedCustomer();
    }

    /**
     * Accion para tratar el doble click en una de las filas de la pantalla
     *
     * @param event
     */
    @FXML
    public void actionTableAcceptCustomer(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                processSelectedCustomer();
            }
        }
    }

    /**
     * Método para establecer el cliente seleccionado y notificarlo
     */
    protected void processSelectedCustomer() {
    	int index = tbCustomers.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            selectedCustomer = tbCustomers.getItems().get(index).getCustomer();
            
            closeSuccess(selectedCustomer);
        }
        else {
            selectedCustomer = null;
            DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe seleccionar el cliente que desee establecer para la venta."));
        }
    }

    /**
     * Valida los parámetros de búsqueda introducidos
     *
     * @return
     */
    protected boolean validateQueryCustomerForm() {

        boolean valid;

        // Limpiamos los errores que pudiese tener el formulario
        frQueryCustomer.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<CustomerSearchForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frQueryCustomer);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<CustomerSearchForm> next = constraintViolations.iterator().next();
            frQueryCustomer.setErrorStyle(next.getPropertyPath(), true);
            frQueryCustomer.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            valid = false;
        }
        else {
            valid = true;
        }

        return valid;
    }

    @FXML
    public void actionSearchKeyboard(KeyEvent event) {
        log.trace("actionSearchKeyboard()");
        
        if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
            actionSearch();
        }
        else if(event.getCode() == KeyCode.DOWN && !event.isControlDown() && tbCustomers.getItems().size() > 0) {
			tbCustomers.requestFocus();
		}
    }

    /**
     * Busca un cliente en función de los campos de búsqueda de la pantalla.
     */
    @FXML
    public void actionSearch() {
        log.trace("actionSearch()");
        
        queriedCustomers.clear();
        
        frQueryCustomer.setIdentTypeCode(tfVatNumber.getText());
        frQueryCustomer.setCustomerDes(tfDescription.getText());
        
        if(validateQueryCustomerForm()){
	        String description = tfDescription.getText();
	        String ident = tfVatNumber.getText();
	        String identTypeCode = null;
	        if(cbIdentType.getValue() != null) {
	        	identTypeCode = cbIdentType.getValue().getIdentificationTypeCode();
	        }

	        new QueryCustomersTask(identTypeCode, ident, description).start();
        }
    }

    public class QueryCustomersTask extends BackgroundTask<List<Customer>>{
    	private String identTypeCode;
    	private String vatNumber;
		private String description;
		public QueryCustomersTask(String identTypeCode, String vatNumber, String description) {
			this.identTypeCode = identTypeCode;
			this.vatNumber = vatNumber;
			this.description = description;
		}

		@Override
		protected List<Customer> execute() throws Exception {
			PaginatedRequest request = new PaginatedRequest(0, 100);
			PaginatedResponse<Customer> response = customersService.findPage(new OmnichannelCustomerFilter(vatNumber, identTypeCode, null, description), request);
			return response.getContent();
		}

		@Override
		protected void failed() {
			log.error(getCMZException().getLocalizedMessage(), getCMZException());
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(getCMZException().getMessage(), getCMZException());
			super.failed();
		}

		@Override
		protected void succeeded() {
			List<Customer> customers = getValue();
			List<CustomerDto> customersTable = new ArrayList<CustomerDto>();
		
			for(Customer c : customers){
				customersTable.add(new CustomerDto(c));
			}
            
            if (customersTable.isEmpty()) {
                tbCustomers.setPlaceholder(new Text(I18N.getText("No hay registros para los parámetros de búsqueda seleccionados")));
            }
            else{
            	queriedCustomers.addAll(customersTable);
            	tbCustomers.getSelectionModel().selectFirst();
            }
            super.succeeded();
		}
    }
    

    /**
     * Método de control de acciones de página de clientesBuscados
     *
     * @param pressedButton botón pulsado
     */
    @Override
    public void executeAction(ActionButtonComponent pressedButton) {
        log.debug("executeAction() - Realizando la acción : " + pressedButton.getClave() + " de tipo : " + pressedButton.getTipo());
        switch (pressedButton.getClave()) {

            case "ACCION_TABLA_PRIMER_REGISTRO":
                log.debug("Acción seleccionar primer registro de la tabla");
                actionTableEventFirst(tbCustomers);
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                log.debug("Acción seleccionar registro anterior de la tabla");
                actionTableEventPrevious(tbCustomers);
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                log.debug("Acción seleccionar siguiente registro de la tabla");
                actionTableEventNext(tbCustomers);
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                log.debug("Acción seleccionar último registro de la tabla");
                actionTableEventLast(tbCustomers);
                break;
        }
    }

    /**
     * Devuelve el cliente seleccionado de la pantalla
     *
     * @return
     */
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void closeCancel() {
    	queriedCustomers.clear();
    	super.closeCancel();
    }
    
    public void closeSuccess(Object callbackData){
    	queriedCustomers.clear();
    	super.closeSuccess(callbackData);
    }
    
    public void acceptCustomerKeyboard(KeyEvent event) {
        log.trace("acceptCustomerKeyboard(KeyEvent event) - Aceptar");
        if (event.getCode() == KeyCode.ENTER) {
        	processSelectedCustomer();
        }
    }
    
}
