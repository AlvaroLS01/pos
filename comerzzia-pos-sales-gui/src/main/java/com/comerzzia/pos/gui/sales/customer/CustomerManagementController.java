
package com.comerzzia.pos.gui.sales.customer;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.Country;
import com.comerzzia.core.facade.model.CountryIdType;
import com.comerzzia.core.facade.model.PostalCode;
import com.comerzzia.core.facade.model.TaxTreatment;
import com.comerzzia.core.facade.service.country.CountryServiceFacade;
import com.comerzzia.core.facade.service.countryidentificationtype.CountryIdentificationTypeFacade;
import com.comerzzia.core.facade.service.postalcode.PostalCodeServiceFacade;
import com.comerzzia.core.facade.service.tax.TaxTreatmentServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.model.sale.NewCustomer;
import com.comerzzia.omnichannel.facade.model.sale.UpdateCustomer;
import com.comerzzia.omnichannel.facade.service.sale.customer.CustomerServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.countryidtypevalidator.CountryDocTypeValidatorBuilder;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.country.CountriesController;
import com.comerzzia.pos.gui.sales.customer.postalcode.LocationSelectionController;
import com.comerzzia.pos.gui.sales.retail.customer.CustomerDto;
import com.comerzzia.pos.gui.sales.retail.invoice.QueryRemoteCustomerTask;
import com.comerzzia.pos.util.base.Status;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.mybatis.exception.KeyConstraintViolationException;

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
import javafx.scene.layout.VBox;
import javafx.util.Callback;

@Component
@CzzScene
public class CustomerManagementController extends SceneController implements ButtonsGroupController {

	public static final String INDEX_SELECTED_CUSTOMER = "INDEX_SELECTED_CUSTOMER";
	public static final String CUSTOMERS_LIST = "CUSTOMERS_LIST";
	public static final String EDITION_MODE = "EDITION_MODE";
	public static final String CUSTOMER_STATUS = "CUSTOMER_STATUS";
	public static final String EDITED_CUSTOMER = "EDITED_CUSTOMER";

	private static Logger log = Logger.getLogger(CustomerManagementController.class);

	@FXML
	protected VBox tableMenuPane;
	protected ButtonsGroupComponent buttonsGroupComponent;
	@FXML
	protected ComboBox<CountryIdType> cbIdentTypeCode;
	@FXML
	protected ComboBox<TaxTreatment> cbTaxTreatment;
	protected ObservableList<CountryIdType> identTypes;
	protected ObservableList<TaxTreatment> taxTreatments;
	@FXML
	protected TabPane tabPane;
	@FXML
	protected Tab tabBank;
	@FXML
	protected Tab tabGeneral;
	@FXML
	protected TextField tfCompanyName, tfAddress, tfCity, tfPostalCode, tfProvince, tfPhone, tfPhone2, 
	tfCountryDes, tfCountryCode, tfVatNumber, tfLocation, tfRate, tfDescription, tfFax, tfEmail;
	@FXML
	protected TextField tfBank, tfAddressBank, tfCityBank, tfIBANBank;
	@FXML
	protected TextArea taRemarks;
	@FXML
	protected CheckBox cbActive;
	@FXML
	protected Button btSearchCentral, btSearchCountry, btValidateDocument;

	protected ObservableList<CustomerDto> customers;
	protected int indexCustomer;
	protected Customer customer;
	protected boolean editionMode = false;
	protected int customerStatus;

	@FXML
	protected Label lbError;

	protected CustomerManagementForm frCustomerData;
	protected CentralCustomerSearchForm frSearchCentral;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
	
	@Autowired
	protected Session session;
	
	@Autowired
	protected CountryIdentificationTypeFacade identTypeService;
	
	@Autowired
	protected CustomerServiceFacade customerService;
	
	@Autowired
	protected CountryServiceFacade countryService;
	
	@Autowired
	protected PostalCodeServiceFacade postalCodeService;
	
	@Autowired
	protected TaxTreatmentServiceFacade taxTreatmentService;
	
	@Autowired
	protected CountryDocTypeValidatorBuilder documentTypeValidatorBuilder;
	
	@Autowired
	protected ModelMapper modelMapper;
	
	protected boolean customerHasDeleted;
	
	protected String countryCode;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		frCustomerData = SpringContext.getBean(CustomerManagementForm.class);
		frSearchCentral = SpringContext.getBean(CentralCustomerSearchForm.class);

		frCustomerData.setFormField("address", tfAddress);
		frCustomerData.setFormField("province", tfProvince);
		frCustomerData.setFormField("city", tfCity);
		frCustomerData.setFormField("postalCode", tfPostalCode);
		frCustomerData.setFormField("phone", tfPhone);
		frCustomerData.setFormField("phone2", tfPhone2);
		frCustomerData.setFormField("vatNumber", tfVatNumber);
		frCustomerData.setFormField("companyName", tfCompanyName);
		frCustomerData.setFormField("country", tfCountryCode);
		frCustomerData.setFormField("taxTreatId", cbTaxTreatment);
		frCustomerData.setFormField("description", tfDescription);
		frCustomerData.setFormField("fax", tfFax);
		frCustomerData.setFormField("bank", tfBank);
		frCustomerData.setFormField("addressBank", tfAddressBank);
		frCustomerData.setFormField("cityBank", tfCityBank);
		frCustomerData.setFormField("ibanBank", tfIBANBank);
		frCustomerData.setFormField("remarks", taRemarks);
		frCustomerData.setFormField("location", tfLocation);
		frCustomerData.setFormField("email", tfEmail);
		
		frSearchCentral.setFormField("customerCode", tfVatNumber);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		tfPostalCode.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
		    	if(!newValue) {
		    		searchPostalCode();
		    	}
		    }
		});
		
		tfPostalCode.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				tfPhone.requestFocus();
			}
		});

		tfCountryCode.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
				tfCountryCode.setText(tfCountryCode.getText().toUpperCase());
				if (!newValue && !countryCode.equals(tfCountryCode.getText())) {
					Country country = null;
					if(StringUtils.isNotBlank(tfCountryCode.getText())) {
	                    try {
	                        country = countryService.findById(tfCountryCode.getText());
	                    }
	                    catch (NotFoundException e) {
	                    	log.debug("initializeComponents() - No se ha encontrado el país con código: " + tfCountryCode.getText());
	                    }
					}
                    
					if(country != null) {
						tfCountryDes.setText(country.getCountryDes());
					}
					else {
						tfCountryDes.clear();
					}
					countryCode = tfCountryCode.getText();
				}
			}
		});
		
		try {
			List<ButtonConfigurationBean> tableActionsList = ButtonsGroupComponent.buildActionsForSimpleTableNavigation();
			tableActionsList.add(0, new ButtonConfigurationBean("icons/back.png", null, null, "VOLVER", "REALIZAR_ACCION"));
			tableActionsList.add(1, new ButtonConfigurationBean("icons/add.png", null, null, "AÑADIR", "REALIZAR_ACCION"));
			tableActionsList.add(2, new ButtonConfigurationBean("icons/edit.png", null, null, "EDITAR", "REALIZAR_ACCION"));
			tableActionsList.add(3, new ButtonConfigurationBean("icons/garbage-blue.png", null, null, "ELIMINAR", "REALIZAR_ACCION"));
			tableActionsList.add(new ButtonConfigurationBean("icons/aceptar.png", null, null, "ACEPTAR", "REALIZAR_ACCION"));
			tableActionsList.add(new ButtonConfigurationBean("icons/cancelar.png", null, null, "CANCELAR", "REALIZAR_ACCION"));

			log.debug("initializeComponents() - Configurando botonera");
			buttonsGroupComponent = new ButtonsGroupComponent(4, 1, this, tableActionsList, tableMenuPane.getPrefWidth(), tableMenuPane.getPrefHeight(), ActionButtonSimpleComponent.class.getName());
			tableMenuPane.getChildren().add(buttonsGroupComponent);
		}
		catch (LoadWindowException ex) {
			log.error("initializeComponents() - Error creando botonera. Error : " + ex.getMessage(), ex);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(ex.getMessage());
		}
	}
	
	protected void loadIdentificationTypes() {
        identTypes.clear();
        
        String countryCode = session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode();
        List<CountryIdType> identificationTypes = identTypeService.findByCountryCode(countryCode, null);
        if (identificationTypes.isEmpty()) {
            //Añadimos elemento vacío
            identTypes.add(new CountryIdType());
        } else {
            identTypes.addAll(identificationTypes);
        }

        updateSelectedIdentificationType();
    }

	@SuppressWarnings("unchecked")
    @Override
	public void onSceneOpen() throws InitializeGuiException {
		//si no tenemos el parámetro modo edición no hacemos nada, ya está abierta
		if(!sceneData.containsKey(EDITION_MODE)){
			return;
		}
		
		customerHasDeleted = false;
		
		if(editionMode){
			return;
		}
		
		frCustomerData.clearErrorStyle();
		lbError.setText("");
		
		if(sceneData.containsKey(INDEX_SELECTED_CUSTOMER)){
			indexCustomer = (int) sceneData.remove(INDEX_SELECTED_CUSTOMER);
			customers = (ObservableList<CustomerDto>) sceneData.remove(CUSTOMERS_LIST);
			customer = customers.get(indexCustomer).getCustomer();
		}
		else{
			customers = null;
			customer = null;
		}
		editionMode = (boolean) sceneData.get(EDITION_MODE);
		customerStatus = (int ) sceneData.remove(CUSTOMER_STATUS);

		identTypes = FXCollections.observableArrayList();

		loadIdentificationTypes();
		cbIdentTypeCode.setItems(identTypes);


		try {
			List<TaxTreatment> taxtTreatments = taxTreatmentService.findTaxTreatmentByCountryCode(
					session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode());

			this.taxTreatments = FXCollections.observableArrayList();
			taxTreatments.add(null);
			//Añadimos elemento vacío
			taxTreatments.addAll(taxtTreatments);
			cbTaxTreatment.setItems(taxTreatments);
		}
		catch(Exception ex){
			log.error("onSceneOpen() - Se produjo un error en el tratamiento de los impuestos", ex);
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Error consultando los tratamientos de impuestos de la tienda."));
		}

		if(customer!=null){
			String countryCode = customer.getCountryCode();
			if(countryCode!=null && !countryCode.isEmpty()){
				Country country = getCountry(countryCode);
				if(country!=null){
					customer.setCountryDes(country.getCountryDes());
				}
			}
			else{
				tfCountryCode.setText(AppConfig.getCurrentConfiguration().getCountry());
				this.countryCode = AppConfig.getCurrentConfiguration().getCountry().toUpperCase();
				Country pais = getCountry(AppConfig.getCurrentConfiguration().getCountry());
				tfCountryDes.setText(pais.getCountryDes());
			}
			refreshScreenData();
		}
		else{
			cleanFields();
			tfCountryCode.setText(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode());
			countryCode = tfCountryCode.getText();
			Country country = getCountry(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode());
			if(country != null) {
				tfCountryDes.setText(country.getCountryDes());
			}
			cbIdentTypeCode.getSelectionModel().selectFirst();
		}

		setEditionMode(editionMode);
	}        

	@Override
	public void initializeFocus() {
		if(customerStatus == Status.NEW) {
			tfVatNumber.requestFocus();
		}
		else {
			tfCompanyName.requestFocus();
		}
	}

	public void refreshScreenData(){
		tfCompanyName.setText(customer.getTradeName());
		tfProvince.setText(customer.getProvince());
		tfAddress.setText(customer.getAddress());
		tfLocation.setText(customer.getLocation());
		tfPhone.setText(customer.getPhone1());
		tfPhone2.setText(customer.getPhone2());
		tfVatNumber.setText(customer.getVatNumber());
		tfCountryCode.setText(customer.getCountryCode());
		countryCode = customer.getCountryCode().toUpperCase();
		tfCountryDes.setText(customer.getCountryDes());
		tfCity.setText(customer.getCity());
		tfPostalCode.setText(customer.getPostalCode());

		tfBank.setText(customer.getBank());
		tfAddressBank.setText(customer.getBankAddress());
		tfCityBank.setText(customer.getBankCity());
		tfIBANBank.setText(customer.getIban());
		
		updateSelectedIdentificationType();
		
		cbActive.setSelected(customer.getActive());
		tfRate.setText(customer.getRateCode());
		tfDescription.setText(customer.getCustomerDes());
		tfEmail.setText(customer.getEmail());
		tfFax.setText(customer.getFax());
		TaxTreatment taxTreatment = taxTreatmentService.findById(customer.getTaxTreatmentId());
		TaxTreatment storeTaxTreatment = taxTreatmentService.findById(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getTaxTreatmentId());
		TaxTreatment selectedTaxTreatment = null;
		for(TaxTreatment taxTreat : taxTreatments){
			if(taxTreat!=null){
				if(Objects.equals(taxTreat.getTaxTreatmentId(), taxTreatment.getTaxTreatmentId())){
					selectedTaxTreatment = taxTreat;
					if(!taxTreat.getTaxTreatmentId().equals(storeTaxTreatment.getTaxTreatmentId())) {
						DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}.",taxTreat.getTaxTreatmentDes(),storeTaxTreatment.getTaxTreatmentDes()));
					}
					break;
				}
			}
		}
		
		hideField(true, tfCountryCode);

		cbTaxTreatment.getSelectionModel().select(selectedTaxTreatment);
		taRemarks.setText(customer.getComments());

		if(StringUtils.isNotBlank(customer.getIdentificationTypeCode())){
			updateSelectedIdentificationType();
		}
		
	}
	
	protected void updateSelectedIdentificationType() {
        if (customer != null) {
            updateSelectedIdentificationType(customer.getIdentificationTypeCode());
        } else {
            cbIdentTypeCode.getSelectionModel().clearSelection();
        }
    }
	
	protected void updateSelectedIdentificationType(String selectedIdentType) {
        cbIdentTypeCode.getSelectionModel().clearSelection();

        if (identTypes == null) {
            return;
        }

        CountryIdType identType = null;
        if (selectedIdentType != null) {
            for (CountryIdType identificationType : identTypes) {
                if (identificationType != null && identificationType.getIdentificationTypeCode() != null && identificationType.getIdentificationTypeCode().equals(selectedIdentType)) {
                    identType = identificationType;
                }
            }
        }
        if(identType != null) {
            cbIdentTypeCode.getSelectionModel().select(identType);
        } else {
            cbIdentTypeCode.getSelectionModel().select(0);
        }
    }

	protected void cleanFields(){
		tfCompanyName.setText("");
		tfProvince.setText("");
		tfAddress.setText("");
		tfLocation.setText("");
		tfPhone.setText("");
		tfPhone2.setText("");
		tfVatNumber.setText("");
		tfCountryCode.setText("");
		countryCode = "";
		tfCountryDes.setText("");
		tfCity.setText("");
		tfPostalCode.setText("");
		updateSelectedIdentificationType();
		cbActive.setSelected(true);
		taRemarks.setText("");
		tfDescription.setText("");
		tfEmail.setText("");
		tfFax.setText("");
		tfRate.setText("");
		cbTaxTreatment.getSelectionModel().select(null);
		tfBank.setText("");
		tfAddressBank.setText("");
		tfCityBank.setText("");
		tfIBANBank.setText("");
	}
	
	public void actionAccept(){
		// Si el foco está en el input de código postal lo pasamos a otro sitio para que muestre la ventana de códigos postales si es necesario,
		// y evitar el bloqueo con otros mensajes que puedan salir
		if(tfPostalCode.isFocused()) {
			tfDescription.requestFocus();
		}
		
		Customer customerDataToSave = new Customer();
		
		if(!(customer == null)){
			BeanUtils.copyProperties(customer, customerDataToSave);	
		}
			
		
		if(validateData()){
			if(customerStatus == Status.NEW){
				if(customer == null){
					//cliente = new ClienteBean();
					//El código de cliente no se puede editar, así que el que traiga el cliente se le queda, si no, el cif del cliente
					customerDataToSave.setCustomerCode(tfVatNumber.getText());
				}
			}
			
			customerDataToSave.setCountryCode(tfCountryCode.getText());
			customerDataToSave.setVatNumber(tfVatNumber.getText());
			customerDataToSave.setPostalCode(tfPostalCode.getText());
			customerDataToSave.setAddress(tfAddress.getText());
			customerDataToSave.setCity(tfCity.getText());
			customerDataToSave.setLocation(tfLocation.getText());
			customerDataToSave.setPhone1(tfPhone.getText());
			customerDataToSave.setPhone2(tfPhone2.getText());
			customerDataToSave.setCountryDes(tfCountryDes.getText());
			if (cbIdentTypeCode.getSelectionModel().getSelectedItem() != null) {
				customerDataToSave.setIdentificationTypeCode(cbIdentTypeCode.getSelectionModel().getSelectedItem().getIdentificationTypeCode());
			}
			customerDataToSave.setTradeName(tfCompanyName.getText());
			customerDataToSave.setProvince(tfProvince.getText());
			customerDataToSave.setActive(cbActive.isSelected());
			customerDataToSave.setComments(taRemarks.getText());
			customerDataToSave.setCustomerDes(tfDescription.getText());
			customerDataToSave.setFax(tfFax.getText());
			customerDataToSave.setEmail(tfEmail.getText());
			customerDataToSave.setTaxTreatmentId(cbTaxTreatment.getSelectionModel().getSelectedItem().getTaxTreatmentId());

			customerDataToSave.setBank(tfBank.getText());
			customerDataToSave.setBankAddress(tfAddressBank.getText());
			customerDataToSave.setBankCity(tfCityBank.getText());
			customerDataToSave.setIban(tfIBANBank.getText());
			
			String rateCode = tfRate.getText();
			
			if(StringUtils.isBlank(rateCode)){
				rateCode = null;
			}
			
			customerDataToSave.setRateCode(rateCode);
			
			if(checkClientTaxes(customerDataToSave)) {
				if(customerStatus == Status.NEW){
					try {
						boolean save = true;
						String result = validateDocument();
						if(result != null) {
							save = DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(result);
							if(!save) {
								tfVatNumber.requestFocus();
								return;
							}
						}

						customerService.create(modelMapper.map(customerDataToSave, NewCustomer.class));
					} catch(KeyConstraintViolationException e){
						log.error("Error actualizando el cliente.",e);
						DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Lo sentimos, ya existe un cliente con el  documento {0} en el sistema ", customerDataToSave.getVatNumber()), e);
						return;
					}
				}
				else{
					boolean save = true;
					String result = validateDocument();
					if(result != null) {
						save = DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(result);
						if(!save) {
							tfVatNumber.requestFocus();
							return;
						}
					}

					if(!saveCustomerData(customerDataToSave))
	                    return;
				}

				if(customer == null){
					customer = new Customer();
				}
				BeanUtils.copyProperties(customerDataToSave, customer);
//				TODO: MSB: Borrado temporal
//				for(View vista : getApplication().getMainView().getSubViews()) {
//					if(vista instanceof IdentificacionClienteView) {
//						sceneData.put(CLIENTE_EDITADO, cliente);
//					}
//					else if(vista instanceof BuscarClienteView) {
//						((BuscarClienteController) vista.getController()).accionBuscar();
//					}
//				}	
				
				setEditionMode(false);
				closeSuccess();
			}
		}
	}
	
	protected boolean checkClientTaxes(Customer customer) {
		TaxTreatment taxTreatment = null;
		TaxTreatment storeTaxTreatment = null;
		try {
			taxTreatment = taxTreatmentService.findById(customer.getTaxTreatmentId());
			storeTaxTreatment = taxTreatmentService.findById(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getTaxTreatmentId());
		}catch(Exception ignore) {}
		if(taxTreatment == null || !taxTreatment.getCountryCode().equals(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode())) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No es posible seleccionar este cliente al tener un tratamiento de impuestos no disponible para el país asociado a la tienda actual."));
			return false;
		}else if(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getTaxTreatmentId().equals(taxTreatment.getTaxTreatmentId()) || 
				DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}. Confirme si desea continuar.",taxTreatment.getTaxTreatmentDes(),storeTaxTreatment.getTaxTreatmentDes()))) {
			return true;
		}
		return false;
	}

	protected void setEditionMode(boolean editionMode){
		this.editionMode = editionMode;
		
		tfVatNumber.setEditable(editionMode);
		tfCompanyName.setEditable(editionMode);
		tfProvince.setEditable(editionMode);
		tfAddress.setEditable(editionMode);
		tfLocation.setEditable(editionMode);
		tfPhone.setEditable(editionMode);
		tfPhone2.setEditable(editionMode);
		tfFax.setEditable(editionMode);
		tfEmail.setEditable(editionMode);
		tfDescription.setEditable(editionMode);
		tfCountryCode.setEditable(editionMode);
		tfCountryDes.setEditable(editionMode);
		tfCity.setEditable(editionMode);
		tfPostalCode.setEditable(editionMode);
		cbIdentTypeCode.setDisable(!editionMode);
		cbActive.setDisable(!editionMode);
		taRemarks.setEditable(editionMode);
		cbTaxTreatment.setDisable(!editionMode);
		btSearchCentral.setDisable(!editionMode);
		btSearchCountry.setDisable(!editionMode);

		tfBank.setEditable(editionMode);
		tfAddressBank.setEditable(editionMode);
		tfCityBank.setEditable(editionMode);
		tfIBANBank.setEditable(editionMode);

		buttonsGroupComponent.getButtonByKey("VOLVER").setDisable(editionMode);
		buttonsGroupComponent.getButtonByKey("AÑADIR").setDisable(editionMode);
		buttonsGroupComponent.getButtonByKey("EDITAR").setDisable(editionMode);
		buttonsGroupComponent.getButtonByKey("ELIMINAR").setDisable(editionMode);
		buttonsGroupComponent.getButtonByKey("ACCION_TABLA_PRIMER_REGISTRO").setDisable(editionMode);
		buttonsGroupComponent.getButtonByKey("ACCION_TABLA_ANTERIOR_REGISTRO").setDisable(editionMode);
		buttonsGroupComponent.getButtonByKey("ACCION_TABLA_SIGUIENTE_REGISTRO").setDisable(editionMode);
		buttonsGroupComponent.getButtonByKey("ACCION_TABLA_ULTIMO_REGISTRO").setDisable(editionMode);

		buttonsGroupComponent.getButtonByKey("ACEPTAR").setDisable(!editionMode);
		buttonsGroupComponent.getButtonByKey("CANCELAR").setDisable(!editionMode);
	}

	@FXML
	public void actionSearchCountry(){
		openScene(CountriesController.class, new SceneCallback<Country>() {
			
			@Override
			public void onSuccess(Country country) {
				tfCountryDes.setText(country.getCountryDes());
				tfCountryCode.setText(country.getCountryCode());
				countryCode = country.getCountryCode().toUpperCase();
			}
		});
	}

	protected Country getCountry(String countryCode){
		Country country = null;

		try {
			country = countryService.findById(countryCode);
		}
		catch (NotFoundException ex) {
			log.error("No se encontró el código del cliente.");
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Error en la búsqueda, país no encontrado"));
			tfCountryCode.setText("");
			this.countryCode = "";
			tfCountryDes.setText("");
		}

		return country;
	}

	protected boolean saveCustomerData(Customer customer){
		
		try {
			customerService.update(modelMapper.map(customer, UpdateCustomer.class));			
		} catch (Exception e) {
			log.error("Error actualizando el cliente.",e);
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("No se pudo crear el cliente."));
			return false;
		}
		return true;
	}

	@Override
	public void executeAction(ActionButtonComponent pressedButton) {
		switch (pressedButton.getClave()) {
		case "VOLVER":
			if(customerHasDeleted) {
				closeSuccess();
			}else {
				closeCancel();
			}
			break;
		case "AÑADIR":
			customerStatus = Status.NEW;
			newCustomer();
			break;
		case "EDITAR":
			customerStatus = Status.MODIFIED;
			setEditionMode(true);
			break;
		case "ELIMINAR":
			if(DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se borrará el cliente seleccionado. ¿Está seguro?"))){
				CustomerManagementTasks.executeDeleteTask(customer, customers, getStage(), new Callback<Boolean, Void>() {
					@Override
					public Void call(Boolean result) {
						if(result){
							if(customers.isEmpty()){
								closeSuccess();
							}else{
								customerHasDeleted = true;
								indexCustomer--;
								if(indexCustomer < 0){
									indexCustomer = 0;
								}
								customer = customers.get(indexCustomer).getCustomer();
								refreshScreenData();
							}
						}
						return null;
					}
				});
			}
			break;
		case "ACCION_TABLA_PRIMER_REGISTRO":
			indexCustomer = 0;
			customer = customers.get(indexCustomer).getCustomer();
			refreshScreenData();
			break;
		case "ACCION_TABLA_ANTERIOR_REGISTRO":
			indexCustomer--;
			if(indexCustomer < 0){
				indexCustomer = 0;
			}
			customer = customers.get(indexCustomer).getCustomer();
			refreshScreenData();
			break;
		case "ACCION_TABLA_SIGUIENTE_REGISTRO":
			indexCustomer++;
			if(indexCustomer >= customers.size()){
				indexCustomer = customers.size() - 1;
			}
			customer = customers.get(indexCustomer).getCustomer();
			refreshScreenData();
			break;
		case "ACCION_TABLA_ULTIMO_REGISTRO":
			indexCustomer = customers.size() - 1;
			customer = customers.get(indexCustomer).getCustomer();
			refreshScreenData();
			break;
		case "ACEPTAR":
			actionAccept();
			
			break;
		case "CANCELAR":
			setEditionMode(false);
			if(customers == null){
				closeSuccess();
			}
			else{
				if(customer == null){
					customer = customers.get(indexCustomer).getCustomer();
				}
				customer = customers.get(indexCustomer).getCustomer();
				refreshScreenData();
				customerStatus = Status.UNMODIFIED;
			}
			break;
		}
	}

	public void newCustomer(){
		setEditionMode(true);
		cleanFields();
		customer = null;
	}

	public void loadCentralCustomer(){

		if(validateCentralSearch()){
			String countryCode = null;
			if(cbIdentTypeCode.getSelectionModel().getSelectedItem() != null && StringUtils.isNotBlank(cbIdentTypeCode.getSelectionModel().getSelectedItem().getIdentificationTypeCode())) {
				if(StringUtils.isNotBlank(tfCountryCode.getText())) {
					countryCode = tfCountryCode.getText();
				}
				else {
					countryCode = session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode();
				}
			}

			searchRemoteCustomer(countryCode);
		}
	}

	public void searchRemoteCustomer(String countryCode) {
		String identificationTypeCode = null;
		if (cbIdentTypeCode.getSelectionModel().getSelectedItem() != null) {
			identificationTypeCode = cbIdentTypeCode.getSelectionModel().getSelectedItem().getIdentificationTypeCode();
		}

		QueryRemoteCustomerTask queryRemoteTask = SpringContext.getBean(QueryRemoteCustomerTask.class, tfVatNumber.getText(), identificationTypeCode, countryCode,
		        new RestBackgroundTask.FailedCallback<Customer>(){

			        @Override
			        public void succeeded(Customer result) {
				        try {
					        cleanFields();
					        customer = result;
					        refreshScreenData();
				        }
				        catch (Exception e) {
					        log.error("searchRemoteCustomer() - An error ocurred while loading the customer data: " + e.getMessage(), e);
					        DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Ha habido un error al cargar los datos del cliente"));
				        }
			        }

			        @Override
			        public void failed(Throwable throwable) {
			        }

		        }, getStage());
		queryRemoteTask.start();
	}

	public boolean validateData(){
		log.debug("validateData()");

		boolean valid;

		// Limpiamos los errores que pudiese tener el formulario
		frSearchCentral.clearErrorStyle();
		frCustomerData.clearErrorStyle();
		
		frCustomerData.setAddress(tfAddress.getText());
		frCustomerData.setPostalCode(tfPostalCode.getText());
		frCustomerData.setVatNumber(tfVatNumber.getText());
		frCustomerData.setProvince(tfProvince.getText());
		frCustomerData.setCompanyName(tfCompanyName.getText());
		frCustomerData.setCity(tfCity.getText());
		frCustomerData.setLocation(tfLocation.getText());
		frCustomerData.setCountry(tfCountryCode.getText());
		frCustomerData.setPhone(tfPhone.getText());
		frCustomerData.setPhone2(tfPhone2.getText());
		frCustomerData.setDescription(tfDescription.getText());
		frCustomerData.setFax(tfFax.getText());
		frCustomerData.setEmail(tfEmail.getText());

		frCustomerData.setBank(tfBank.getText());
		frCustomerData.setAddressBank(tfAddressBank.getText());
		frCustomerData.setCityBank(tfCityBank.getText());
		frCustomerData.setIbanBank(tfIBANBank.getText());
		
		frCustomerData.setRemarks(taRemarks.getText());
		
		try {
            countryService.findById(tfCountryCode.getText());
        }
        catch (NotFoundException e) {
        	PathImpl path = PathImpl.createPathFromString("pais");
        	frCustomerData.setFocus(path);
        	frCustomerData.setErrorStyle(path, true);
        	return false;
        }
		
		TaxTreatment taxTreatment = cbTaxTreatment.getSelectionModel().getSelectedItem();
		if(taxTreatment == null){
			frCustomerData.setTaxTreatId("");
		}
		else{
			frCustomerData.setTaxTreatId(taxTreatment.getTaxTreatmentCode());
		}
		
		//Limpiamos el posible error anterior
		lbError.setText("");

		// Validamos el formulario de login
		Set<ConstraintViolation<CustomerManagementForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frCustomerData);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<CustomerManagementForm> next = constraintViolations.iterator().next();
			Path path = next.getPropertyPath();
			if (path.toString().toLowerCase().contains("banco")) {
				tabPane.getSelectionModel().select(tabBank);
			} else {
				tabPane.getSelectionModel().select(tabGeneral);
			}
			frCustomerData.setErrorStyle(path, true);
			frCustomerData.setFocus(path);
			lbError.setText(next.getMessage());
			valid = false;
		}
		else {
			valid = true;
		}

		return valid;
	}
	
	public String validateDocument() {
		CountryIdType identType = cbIdentTypeCode.getSelectionModel().getSelectedItem();
		String result = null;
		
		if(identType != null && StringUtils.isNotBlank(identType.getIdentificationTypeCode()) && StringUtils.isNotBlank(identType.getValidationClass()) && StringUtils.isNotBlank(tfVatNumber.getText())) {
			String vatNumber = tfVatNumber.getText();
			
			if (!documentTypeValidatorBuilder.validate(identType, vatNumber)) {
			   result = I18N.getText("El documento indicado no es válido, ¿desea guardar el cliente de todas formas?");
			}
		}
		return result;
	}
	
	public boolean validateCentralSearch(){
		log.debug("validateCentralSearch()");

		boolean valid;

		// Limpiamos los errores que pudiese tener el formulario
		frSearchCentral.clearErrorStyle();
		frCustomerData.clearErrorStyle();
		
		frSearchCentral.setCustomerCode(tfVatNumber.getText().trim());
		
		//Limpiamos el posible error anterior
		lbError.setText("");

		// Validamos el formulario de login
		Set<ConstraintViolation<CentralCustomerSearchForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frSearchCentral);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<CentralCustomerSearchForm> next = constraintViolations.iterator().next();
			frSearchCentral.setErrorStyle(next.getPropertyPath(), true);
			frSearchCentral.setFocus(next.getPropertyPath());
			lbError.setText(next.getMessage());
			valid = false;
		}
		else {
			valid = true;
		}

		return valid;
	}

	public void hideField(boolean hide, Node element){
		
		Node parent = element.getParent();
		parent.prefHeight(0.0);
	}

	protected void searchPostalCode() {
		if(editionMode) {
	        String postalCodeStr = tfPostalCode.getText();
	    	if(postalCodeStr != null && !postalCodeStr.equals("")) {
                List<PostalCode> postalCodes = postalCodeService.findByPostalCode(postalCodeStr);
                if(postalCodes != null && !postalCodes.isEmpty()) {
                	if(postalCodes.size() == 1) {
                		PostalCode postalCode = postalCodes.get(0);
                		tfLocation.setText(postalCode.getLocation());
                		tfCity.setText(postalCode.getCity());
                		tfProvince.setText(postalCode.getProvince());
                	} else {
                		sceneData.put(LocationSelectionController.PARAM_POSTAL_CODES_LIST, postalCodes);
                		
                		openScene(LocationSelectionController.class, new SceneCallback<PostalCode>() {							
							@Override
							public void onSuccess(PostalCode postalCode) {								
								tfLocation.setText(postalCode.getLocation());
								tfCity.setText(postalCode.getCity());
								tfProvince.setText(postalCode.getProvince());
							}
						});
                		
                	}
                }
	    	}
		}
    }
	
	@FXML
 	public void actionValidateDocument() {
		if (cbIdentTypeCode.getSelectionModel().getSelectedItem() == null) return;
		
		if (documentTypeValidatorBuilder.validate(cbIdentTypeCode.getSelectionModel().getSelectedItem(), tfVatNumber.getText())) {
			DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("Documento valido"));
		} else {
			DialogWindowBuilder.getBuilder(getStage()).simpleInfoDialog(I18N.getText("Documento no valido"));
		}
 	}
	
	@Override
	public boolean canClose() {
	    if(super.canClose()){
	    	setEditionMode(false);
	    	return true;
	    }else{
	    	return false;
	    }
	}

}
