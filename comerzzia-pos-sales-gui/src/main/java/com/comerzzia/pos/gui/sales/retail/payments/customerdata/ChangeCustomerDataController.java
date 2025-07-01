


package com.comerzzia.pos.gui.sales.retail.payments.customerdata;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.Country;
import com.comerzzia.core.facade.model.CountryIdType;
import com.comerzzia.core.facade.service.country.CountryServiceFacade;
import com.comerzzia.core.facade.service.countryidentificationtype.CountryIdentificationTypeFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketCustomer;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.model.store.StoreDefaultCustomer;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.basket.exception.BasketUpdateException;
import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.countryidtypevalidator.CountryDocTypeValidatorBuilder;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.country.CountriesController;
import com.comerzzia.pos.gui.sales.customer.CentralCustomerSearchForm;
import com.comerzzia.pos.gui.sales.retail.customer.CustomerSearchController;
import com.comerzzia.pos.gui.sales.retail.invoice.InvoiceDataForm;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

@Controller
@CzzScene
public class ChangeCustomerDataController extends SceneController{

    protected final Logger log = Logger.getLogger(getClass());
    
    @FXML
    protected TextField tfCustomerCode, tfCustomerDes, tfBusinessName, tfAddress, tfCity, tfProvince, tfLocation, 
    tfPostalCode, tfVatNumber, tfPhone, tfCountryCode, tfCountryDes;
    @FXML
    protected TextField tfBank, tfAddressBank, tfCityBank, tfIBANBank; 
    
    @FXML
    protected TabPane tabPane;
    @FXML
    protected Tab tabBank;
    @FXML
    protected Tab tabGeneral;
    
    @FXML
    protected Label lbTitle, lbError;
    
    @FXML
    protected Label lbRequiredDocument, lbRequiredCustomer, lbRequiredName, lbRequiredAddress, lbRequiredPhone, lbRequiredCountry;
    
    @FXML
    protected ComboBox<CountryIdType> cbIdentType;
    
    protected Customer ticketCustomer;
    
    protected BasketManager<?, ?> basketManager;
    
    protected ObservableList<CountryIdType> identTypes;

    protected InvoiceDataForm form;
    protected CentralCustomerSearchForm frSearchCentral;
    
    @FXML
    protected Label lbBusinessName;
    
    @FXML
    protected VBox customerCodePane, vbBusinessName;
    
    @FXML
    protected Button btSearch, btCentralSearch;
    
    @Autowired
    protected Session session;
    
    @Autowired
    protected CountryIdentificationTypeFacade identTypesService;
    
    @Autowired
    protected CountryServiceFacade countriesService;
    
    @Autowired
    protected ModelMapper modelMapper;
    
    @Autowired
	protected CountryDocTypeValidatorBuilder documentTypeValidatorBuilder;
    
    protected boolean isGenericCustomer;
    
    protected String countryCode;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	form = SpringContext.getBean(InvoiceDataForm.class);
    	lbBusinessName.setText(I18N.getText("Nombre"));
    	form.setFormField("customerDes", tfCustomerDes);
    	form.setFormField("businessName", tfBusinessName);
    	form.setFormField("address", tfAddress);
    	form.setFormField("city", tfCity);
    	form.setFormField("province",tfProvince);
        form.setFormField("location",tfLocation);
        form.setFormField("postalCode",tfPostalCode);
    	form.setFormField("vatNumber", tfVatNumber);
    	form.setFormField("phone", tfPhone);
    	form.setFormField("country", tfCountryCode);
    	
    	form.setFormField("bank", tfBank);
    	form.setFormField("addressBank", tfAddressBank);
    	form.setFormField("cityBank", tfCityBank);
    	form.setFormField("ibanBank", tfIBANBank);
    }

    @Override
    public void initializeComponents() throws InitializeGuiException { 	
    	customerCodePane.setVisible(false);
    	customerCodePane.setManaged(false);

        tfCountryCode.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
            	String newCountryCode = tfCountryCode.getText().toUpperCase();

                if(oldValue && !StringUtils.equals(newCountryCode, countryCode)) {
           			updateCustomerCountry(newCountryCode);
           			loadIdentificationTypes();
                }
            }
        });

    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	form.clearErrorStyle();
    	lbError.setText("");
    	
        basketManager = (RetailBasketManager) sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
        setTitle();

        getCustomer();

        StoreDefaultCustomer defaultCustomer = session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer();
        
        isGenericCustomer = StringUtils.equals(ticketCustomer.getCustomerCode(), defaultCustomer.getCustomerCode()) 
        		            && 
        		            StringUtils.equals(ticketCustomer.getCustomerDes(), defaultCustomer.getCustomerDes());

		if (StringUtils.isNotBlank(ticketCustomer.getCountryCode())){
			updateCustomerCountry(ticketCustomer.getCountryCode());
		}
		else {
			updateCustomerCountry(session.getApplicationSession().getStorePosBusinessData().getCountry().getCountryCode());
		}


        identTypes = FXCollections.observableArrayList();
        loadIdentificationTypes();
        cbIdentType.setItems(identTypes);

        // Escondemos el botón de búsqueda en la central ya que solo se usará en la pantalla de datos de factura
        btCentralSearch.setVisible(false);
        
        refreshScreenData();
    }

    protected void loadIdentificationTypes() {
        this.identTypes.clear();
        if(StringUtils.isBlank(countryCode)) {
        	countryCode = session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode();
        }
        List<CountryIdType> identTypes = identTypesService.findByCountryCode(countryCode, null);
        if (identTypes.isEmpty()) {
            //Añadimos elemento vacío
        	this.identTypes.add(new CountryIdType());
        } else {
        	this.identTypes.addAll(identTypes);
        }

        updateSelectedIdentificationType();
    }

    @Override
    public void initializeFocus() {
        tfBusinessName.requestFocus();
    }
    
    public void refreshScreenData(){
    	clearFields();
    	
    	lbRequiredDocument.setVisible(true);
    	lbRequiredDocument.setManaged(true);
    	lbRequiredCustomer.setVisible(false);
    	lbRequiredCustomer.setManaged(false);
    	lbRequiredName.setVisible(true);
    	lbRequiredName.setManaged(true);
    	lbRequiredAddress.setVisible(true);
    	lbRequiredAddress.setManaged(true);
    	lbRequiredPhone.setVisible(false);
    	lbRequiredPhone.setManaged(false);
    	lbRequiredCountry.setVisible(true);
    	lbRequiredCountry.setManaged(true);
    	
    	tfProvince.setText(ticketCustomer.getProvince());
    	tfLocation.setText(ticketCustomer.getLocation());
    	tfPostalCode.setText(ticketCustomer.getPostalCode());
    	
    	updateCustomerCountry(ticketCustomer.getCountryCode());
    	
    	if(!isGenericCustomer){
    	
	    	tfCity.setText(ticketCustomer.getCity());
			tfCustomerCode.setText(ticketCustomer.getCustomerCode());
			tfCustomerDes.setText(ticketCustomer.getCustomerDes());
			tfBusinessName.setText(ticketCustomer.getCustomerDes());// (clienteTicket.getNombreComercial());
			tfAddress.setText(ticketCustomer.getAddress());
			tfPhone.setText(ticketCustomer.getPhone1());
			tfVatNumber.setText(ticketCustomer.getVatNumber());
            updateSelectedIdentificationType();
			
			tfBank.setText(ticketCustomer.getBank());
			tfAddressBank.setText(ticketCustomer.getBankAddress());
			tfCityBank.setText(ticketCustomer.getBankCity());
			tfIBANBank.setText(ticketCustomer.getIban());
    	}
    	BasketLoyalCustomer loyalCustomer = basketManager.getBasketTransaction().getHeader().getLoyalCustomer();
		if(loyalCustomer != null && isGenericCustomer){
			tfBusinessName.setText(loyalCustomer.getName()+" "+loyalCustomer.getLastName());
			tfBusinessName.setEditable(false);
			tfProvince.setText(loyalCustomer.getProvince());
			tfAddress.setText(loyalCustomer.getAddress());
			tfLocation.setText(loyalCustomer.getLocation());
			
			updateCustomerCountry(loyalCustomer.getCountryCode());
			
			tfCity.setText(loyalCustomer.getCity());
			tfPostalCode.setText(loyalCustomer.getPostalCode());
			CountryIdType selectedIdentType = null;
			for (CountryIdType identType : identTypes) {
				if (identType != null && identType.getIdentificationTypeCode() != null && identType.getIdentificationTypeCode().equals(loyalCustomer.getIdentificationTypeCode())) {
					selectedIdentType = identType;
				}
			}
			if(selectedIdentType != null) {
				cbIdentType.getSelectionModel().select(selectedIdentType);
			}
			if(loyalCustomer.getIdentificationTypeCode()!=null && !loyalCustomer.getIdentificationTypeCode().isEmpty()){
				selectedIdentType = null;
	    		for(CountryIdType identType : identTypes) {
	    			if(identType != null && identType.getIdentificationTypeCode() != null && identType.getIdentificationTypeCode().equals(loyalCustomer.getIdentificationTypeCode())) {
	    				selectedIdentType = identType;
	    			}
	    		}
	    		cbIdentType.getSelectionModel().select(selectedIdentType);
	        }
			tfVatNumber.setText(loyalCustomer.getVatNumber());
		}
    }

    protected void updateSelectedIdentificationType() {
        if (ticketCustomer != null) {
            updateSelectedIdentificationType(ticketCustomer.getIdentificationTypeCode());
        } else {
            cbIdentType.getSelectionModel().clearSelection();
        }
    }

    protected void updateSelectedIdentificationType(String selectedIdentType) {
        cbIdentType.getSelectionModel().clearSelection();

        if (identTypes == null) {
            return;
        }

        CountryIdType identType = null;
        if (selectedIdentType != null) {
            for (CountryIdType countryIdentType : identTypes) {
                if (countryIdentType != null && countryIdentType.getIdentificationTypeCode() != null && countryIdentType.getIdentificationTypeCode().equals(selectedIdentType)) {
                    identType = countryIdentType;
                }
            }
        }
        if(identType != null) {
            cbIdentType.getSelectionModel().select(identType);
        } else {
            cbIdentType.getSelectionModel().select(0);
        }
    }

    protected void clearFields(){      
        tfBusinessName.setText("");
        tfBusinessName.setEditable(true);
        tfProvince.setText("");
        tfAddress.setText("");
        tfLocation.setText("");
        tfPhone.setText("");
        tfVatNumber.setText("");
        tfCountryCode.setText("");
        countryCode = "";
        tfCountryDes.setText("");
        tfCity.setText("");
        tfPostalCode.setText("");
        updateSelectedIdentificationType();
        
        tfBank.setText("");
		tfAddressBank.setText("");
		tfCityBank.setText("");
		tfIBANBank.setText("");
    }
        
    public void actionAccept(){
    	if(validateForm()) {
	        ticketCustomer.setCountryCode(tfCountryCode.getText());
	        ticketCustomer.setVatNumber(tfVatNumber.getText());
	        ticketCustomer.setPostalCode(tfPostalCode.getText());
	        ticketCustomer.setAddress(tfAddress.getText());
	        ticketCustomer.setCity(tfCity.getText());
	        ticketCustomer.setLocation(tfLocation.getText());
	        ticketCustomer.setPhone1(tfPhone.getText());
	        ticketCustomer.setCountryDes(tfCountryDes.getText());
	        if(cbIdentType.getSelectionModel().getSelectedItem() != null){
	           ticketCustomer.setIdentificationTypeCode(cbIdentType.getSelectionModel().getSelectedItem().getIdentificationTypeCode());
	        }
	        
	        ticketCustomer.setCustomerDes(tfBusinessName.getText());// .setNombreComercial(tfRazonSocial.getText());
	        ticketCustomer.setProvince(tfProvince.getText());
	        
	        ticketCustomer.setBank(tfBank.getText());
	        ticketCustomer.setBankAddress(tfAddressBank.getText());
	        ticketCustomer.setBankCity(tfCityBank.getText());
	        ticketCustomer.setIban(tfIBANBank.getText());
	        
	        try {
				saveCustomerData(ticketCustomer);
			} catch (BasketUpdateException e) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(e.getMessage());
				return;
			}
	        
	        closeSuccess();
    	}
    }
    
    
    @FXML
    public void accionBuscarPais(){
    	openScene(CountriesController.class, new SceneCallback<Country>() {
			
			@Override
			public void onSuccess(Country pais) {
				updateCustomerCountry(pais);
			}
		});
        
    }
    
    @FXML
    public void accionBuscarCliente(){
        try {
            log.trace("accionBuscarCliente()");
            
            openScene(CustomerSearchController.class, new SceneCallback<Customer>() {
				@Override
				public void onSuccess(Customer callbackData) {
					setClienteConsultado(callbackData);
				}
			});
        }
        catch (Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
            DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(ex);
        }
    }
    
    protected void setClienteConsultado(Customer clienteSeleccionado) {
        this.ticketCustomer = clienteSeleccionado;
        clearFields();
        // Establecemos los campos en pantalla:
        if (clienteSeleccionado.getCustomerDes() != null) {
            tfBusinessName.setText(clienteSeleccionado.getCustomerDes());
        }
        else {
            tfBusinessName.setText("");
        }
        if (clienteSeleccionado.getVatNumber() != null) {
            tfVatNumber.setText(clienteSeleccionado.getVatNumber());
        }
        else {
            tfVatNumber.setText("");
        }
        if (clienteSeleccionado.getPostalCode() != null) {
            tfPostalCode.setText(clienteSeleccionado.getPostalCode());
        }
        else {
            tfPostalCode.setText("");
        }
        if (clienteSeleccionado.getAddress() != null) {
            tfAddress.setText(clienteSeleccionado.getAddress());
        }
        else {
            tfAddress.setText("");
        }
        if (clienteSeleccionado.getCity() != null) {
            tfCity.setText(clienteSeleccionado.getCity());
        }
        else {
            tfCity.setText("");
        }
        if(clienteSeleccionado.getLocation() != null) {
            tfLocation.setText(clienteSeleccionado.getLocation());
        }
        else{
            tfLocation.setText("");
        }
        if(clienteSeleccionado.getProvince() != null) {
            tfProvince.setText(clienteSeleccionado.getProvince());
        }
        else{
            tfProvince.setText("");
        }
        if(clienteSeleccionado.getPhone1() != null) {
            tfPhone.setText(clienteSeleccionado.getPhone1());
        }
        else{
            tfPhone.setText("");
        }
        
        
		if (StringUtils.isNotBlank(clienteSeleccionado.getCountryCode())){
			updateCustomerCountry(clienteSeleccionado.getCountryCode());
		}
		else {
			updateCustomerCountry(session.getApplicationSession().getStorePosBusinessData().getCountry().getCountryCode());
		}

        if(clienteSeleccionado.getBank()!= null) {
        	tfBank.setText(clienteSeleccionado.getBank());
        }
        else{
        	tfBank.setText("");
        }
        if(clienteSeleccionado.getBankAddress()!= null) {
        	tfAddressBank.setText(clienteSeleccionado.getBankAddress());
        }
        else{
        	tfAddressBank.setText("");
        }
        if(clienteSeleccionado.getBankCity()!= null) {
        	tfCityBank.setText(clienteSeleccionado.getBankCity());
        }
        else{
        	tfCityBank.setText("");
        }
        if(clienteSeleccionado.getIban() != null) {
            tfIBANBank.setText(clienteSeleccionado.getIban());
        }
        else{
        	tfIBANBank.setText("");
        }
    }
    
    protected Country getCountry(String codPais){
    	if (codPais == null) return null;
    	
        Country pais = null;
        
        try {
            pais = countriesService.findById(codPais);
        }
        catch (NotFoundException ex) {
            log.error("No se encontró el código del cliente.");
            DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Error en la búsqueda, país no encontrado"));
            tfCountryCode.setText("");
            this.countryCode = "";
            tfCountryDes.setText("");
        }

        return pais;
    }
    
    protected void getCustomer(){    	
    	if (basketManager.getBasketTransaction().getHeader().getCustomer() != null) {
    	   ticketCustomer = modelMapper.map(basketManager.getBasketTransaction().getHeader().getCustomer(), Customer.class);
    	}
    	if(ticketCustomer == null){
    		ticketCustomer = new Customer();
    	}
    }
    
    protected void saveCustomerData(Customer customer) throws BasketUpdateException{
    	BasketCustomer newCustomer = modelMapper.map(customer, BasketCustomer.class);
    	
    	
		basketManager.updateCustomer(newCustomer);
    }
    
    protected void setTitle(){
    	lbTitle.setText(I18N.getText("Datos del Cliente"));
    }
    
    protected boolean validateForm() {
    	log.debug("validateForm()");
    	
    	String result = validateDocument();
		if(result != null) {
			boolean save = DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(result);
			if(!save) {
				tfVatNumber.requestFocus();
				return false;
			}
		}

    	form.setBusinessName(form.trimTextField(tfBusinessName));
    	form.setAddress(form.trimTextField(tfAddress));
    	form.setCity(form.trimTextField(tfCity));
    	form.setProvince(form.trimTextField(tfProvince));
        form.setLocation(form.trimTextField(tfLocation));
        form.setPostalCode(form.trimTextField(tfPostalCode));
    	form.setVatNumber(form.trimTextField(tfVatNumber));
    	form.setPhone(form.trimTextField(tfPhone));
    	form.setCountry(form.trimTextField(tfCountryCode));
    	
    	form.setBank(form.trimTextField(tfBank));
    	form.setAddressBank(form.trimTextField(tfAddressBank));
    	form.setCityBank(form.trimTextField(tfCityBank));
    	form.setIbanBank(form.trimTextField(tfIBANBank));
    	
        boolean valid = true;

        // Limpiamos los errores que pudiese tener el formulario
        form.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        final Set<ConstraintViolation<InvoiceDataForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(form);
        Iterator<ConstraintViolation<InvoiceDataForm>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            final ConstraintViolation<InvoiceDataForm> next = iterator.next();
            Path path = next.getPropertyPath();
            form.setErrorStyle(path, true);
            if(valid){ //Ponemos el foco en el primero que da error
            	if (path.toString().toLowerCase().contains("banco")) {
    				tabPane.getSelectionModel().select(tabBank);
    			} else {
                    tabPane.getSelectionModel().select(tabGeneral);
                }
            	lbError.setText(next.getMessage());
            	Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
    					form.setFocus(next.getPropertyPath());
    				}
    			});
            }
            valid = false;
        }

        return valid;
    }
    
    public void loadCentralCustomer() {}

	public String validateDocument() {
		CountryIdType identType = cbIdentType.getSelectionModel().getSelectedItem();
		String result = null;
		if(identType != null && StringUtils.isNotBlank(identType.getIdentificationTypeCode()) && StringUtils.isNotBlank(identType.getValidationClass()) && StringUtils.isNotBlank(tfVatNumber.getText())) {
			String vatNumber = tfVatNumber.getText();
			
			if (!documentTypeValidatorBuilder.validate(identType, vatNumber)) {
			   result = I18N.getText("El documento indicado no es válido, ¿desea guardar el cliente de todas formas?");
			}
		}
		return result;
	}
	
	protected void updateCustomerCountry(String countryCode) {
        Country country;
		
		if (StringUtils.isNotBlank(countryCode)){
			country = getCountry(countryCode);
		}
		else {
			country = session.getApplicationSession().getStorePosBusinessData().getCountry();
		}
		
		updateCustomerCountry(country);
	}
	
	protected void updateCustomerCountry(Country country) {
		if (country != null) {
			this.countryCode = country.getCountryCode();
			tfCountryCode.setText(country.getCountryCode());		
			tfCountryDes.setText(country.getCountryDes());
		} else {
			this.countryCode = null;
			
			tfCountryCode.setText("");
			tfCountryDes.setText("");
		}
	}

    
}