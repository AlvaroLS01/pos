package com.comerzzia.pos.gui.sales.customer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
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
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.retail.customer.CustomerDto;
import com.comerzzia.pos.gui.sales.retail.customer.CustomerSearchForm;
import com.comerzzia.pos.util.base.Status;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

@Controller
@CzzActionScene
public class CustomerManagementSearchController extends ActionSceneController implements Initializable, ButtonsGroupController{

	private static Logger log = Logger.getLogger(CustomerManagementSearchController.class);
	@FXML
	protected HBox hbHeader;
	
	//Consulta cliente controller
	@FXML
    protected HBox footerHBox;
    //  - botonera de acciones de tabla
    protected ButtonsGroupComponent buttonsGroupComponent;
    @FXML
    protected VBox tableMenuPane;
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
    protected ObservableList<CustomerDto> queriedCustomers;
    protected CustomerSearchForm frCustomerSearch;
    @Autowired
    protected Session session;
    @Autowired
    protected CountryIdentificationTypeFacade identTypesService;
    @FXML
	protected ComboBox<CountryIdType> cbIdentType;
    protected ObservableList<CountryIdType> identTypes;
    @FXML
    protected Label lbError;
    @Autowired
    protected CustomerServiceFacade customersService;
	
    @Override
    public void initializeComponents() {
        try {
        	
            List<ButtonConfigurationBean> tableActionsList = new ArrayList<ButtonConfigurationBean>();
            tableActionsList.add(0, new ButtonConfigurationBean("icons/view.png", null, null, "VER", "REALIZAR_ACCION"));
            tableActionsList.add(1, new ButtonConfigurationBean("icons/add.png", null, null, "AÑADIR", "REALIZAR_ACCION"));
            tableActionsList.add(2, new ButtonConfigurationBean("icons/edit.png", null, null, "EDITAR", "REALIZAR_ACCION"));
            tableActionsList.add(3, new ButtonConfigurationBean("icons/delete.png", null, null, "ELIMINAR", "REALIZAR_ACCION"));

            log.debug("initializeComponents() - Configurando botonera");
            buttonsGroupComponent = new ButtonsGroupComponent(4, 1, this, tableActionsList, tableMenuPane.getPrefWidth(), tableMenuPane.getPrefHeight(), ActionButtonSimpleComponent.class.getName());
            tableMenuPane.getChildren().add(buttonsGroupComponent);


        }
        catch (LoadWindowException ex) {
            log.error("initializeComponents() - Error creando botonera para la consulta de clientes. error : " + ex.getMessage(), ex);
            DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(ex.getMessage());
        }
    }
    
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
        frCustomerSearch = SpringContext.getBean(CustomerSearchForm.class);
        frCustomerSearch.setFormField("identTypeCode", tfVatNumber);
        frCustomerSearch.setFormField("customerDes", tfDescription);
    }

    @Override
    public void onSceneOpen() {
    	List<CountryIdType> identificationTypes = identTypesService.findByCountryCode(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode(), null);
    	
    	identTypes = FXCollections.observableArrayList();
    	//Añadimos elemento vacío
    	identTypes.add(new CountryIdType());
    	
    	for(CountryIdType identificationType: identificationTypes){
    		identTypes.add(identificationType);
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
	

	protected void processSelectedCustomer() {
		int index = tbCustomers.getSelectionModel().getSelectedIndex();
		sceneData.put(CustomerManagementController.INDEX_SELECTED_CUSTOMER, index);
		sceneData.put(CustomerManagementController.CUSTOMERS_LIST, queriedCustomers);
		sceneData.put(CustomerManagementController.EDITION_MODE, false);
		sceneData.put(CustomerManagementController.CUSTOMER_STATUS, Status.UNMODIFIED);
		openScene(CustomerManagementController.class);
	}
	
    @Override
    public void executeAction(ActionButtonComponent pressedButton) {
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
        case "VER":
    		viewCustomer(false);
        	break;
        case "AÑADIR":
        	createCustomer();
        	break;
        case "EDITAR":
        	viewCustomer(true);
        	break;
        case "ELIMINAR":
        	eliminarCliente();
        	break;
        }
    }
    
    public void eliminarCliente(){
    	if(tbCustomers.getSelectionModel().getSelectedItem()!=null){
    		if(DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se borrará el cliente seleccionado. ¿Está seguro?"))){
    			CustomerManagementTasks.executeDeleteTask(tbCustomers.getSelectionModel().getSelectedItem().getCustomer(), queriedCustomers, getStage(), null);
    			actionSearch();
    		}
    	}
    }
	
	public void viewCustomer(boolean editionMode){
    	int index = tbCustomers.getSelectionModel().getSelectedIndex();
		if(index > -1){
			sceneData.put(CustomerManagementController.INDEX_SELECTED_CUSTOMER, index);
			sceneData.put(CustomerManagementController.CUSTOMERS_LIST, queriedCustomers);
			sceneData.put(CustomerManagementController.EDITION_MODE, editionMode);
			if(editionMode){
				sceneData.put(CustomerManagementController.CUSTOMER_STATUS, Status.MODIFIED);
			}
			else{
				sceneData.put(CustomerManagementController.CUSTOMER_STATUS, Status.UNMODIFIED);
			}
			openScene(CustomerManagementController.class);
		}
    }
	
	public void createCustomer(){
		sceneData.put(CustomerManagementController.EDITION_MODE, true);
		sceneData.put(CustomerManagementController.CUSTOMER_STATUS, Status.NEW);
		openScene(CustomerManagementController.class);
	}
	
	
	//ConsultaClienteController
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
        
        frCustomerSearch.setIdentTypeCode(tfVatNumber.getText());
        frCustomerSearch.setCustomerDes(tfDescription.getText());
        
        if(validateCustomerSearchForm()){
	        String description = tfDescription.getText();
	        String ident = tfVatNumber.getText();
	        String identTypeCode = null;
	        if(cbIdentType.getValue() != null) {
	        	identTypeCode = cbIdentType.getValue().getIdentificationTypeCode();
	        }
	        else {
	        	identTypeCode = "";
	        }
	        new SearchCustomersTask(identTypeCode, ident, description).start();
        }
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
     * Valida los parámetros de búsqueda introducidos
     *
     * @return
     */
    protected boolean validateCustomerSearchForm() {

        boolean valid;

        // Limpiamos los errores que pudiese tener el formulario
        frCustomerSearch.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        // Validamos el formulario de login
        Set<ConstraintViolation<CustomerSearchForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frCustomerSearch);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<CustomerSearchForm> next = constraintViolations.iterator().next();
            frCustomerSearch.setErrorStyle(next.getPropertyPath(), true);
            frCustomerSearch.setFocus(next.getPropertyPath());
            lbError.setText(next.getMessage());
            valid = false;
        }
        else {
            valid = true;
        }

        return valid;
    }
    
    public class SearchCustomersTask extends BackgroundTask<List<Customer>>{
    	private String identTypeCode;
    	private String ident;
		private String description;
		public SearchCustomersTask(String identTypeCode, String ident, String description) {
			this.identTypeCode = identTypeCode;
			this.ident = ident;
			this.description = description;
		}

		@Override
		protected List<Customer> execute() throws Exception {
			PaginatedRequest request = new PaginatedRequest(0, 100);
			PaginatedResponse<Customer> response = customersService.findPage(new OmnichannelCustomerFilter(ident, identTypeCode, null, description), request);
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
    
    public void acceptCustomerKeyboard(KeyEvent event) {
        log.trace("acceptCustomerKeyboard(KeyEvent event) - Aceptar");
        if (event.getCode() == KeyCode.ENTER) {
        	processSelectedCustomer();
        }
    }
}