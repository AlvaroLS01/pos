package com.comerzzia.pos.gui.sales.retail.invoice;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketInvoiceData;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.omnichannel.facade.service.basket.BasketManager;
import com.comerzzia.omnichannel.facade.service.sale.customer.CustomerServiceFacade;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.sales.basket.BasketItemizationControllerAbstract;
import com.comerzzia.pos.gui.sales.customer.CentralCustomerSearchForm;
import com.comerzzia.pos.gui.sales.retail.payments.customerdata.ChangeCustomerDataController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Controller
public class InvoiceController extends ChangeCustomerDataController {
	
	
	@Autowired
	protected CustomerServiceFacade customersService;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
	
	@Autowired
	protected ModelMapper modelMapper;

    //  - cliente seleccionado
    protected BasketInvoiceData invoiceData;
    //FacturacionBean facturacion;  <- Objeto que se corresponda con lo contenido en la pantalla
    //  - botonera de acciones de tabla
    protected ButtonsGroupComponent buttonsGroupComponent;

    protected InvoiceDataForm frInvoiceData;
    
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
        
        lbTitle.setText(I18N.getText("Datos del cliente para la factura"));
        
        frInvoiceData = SpringContext.getBean(InvoiceDataForm.class);
        frInvoiceData.setFormField("vatNumber", tfVatNumber);
        frInvoiceData.setFormField("postalCode", tfPostalCode);
        frInvoiceData.setFormField("phone", tfPhone);
        frInvoiceData.setFormField("province", tfProvince);
        frInvoiceData.setFormField("city", tfCity);
        frInvoiceData.setFormField("address", tfAddress);
        frInvoiceData.setFormField("businessName", tfBusinessName);
        frInvoiceData.setFormField("vatNumber", tfVatNumber);
        frInvoiceData.setFormField("country", tfCountryCode);
        frInvoiceData.setFormField("location", tfLocation);
        
        frSearchCentral = SpringContext.getBean(CentralCustomerSearchForm.class);
        frSearchCentral.setFormField("customerCode", tfVatNumber);
    }
    
    @Override
    public void onSceneOpen() {
    	clearFields();
    	
    	log.debug("onSceneOpen()");
    	basketManager = (BasketManager<?, ?>) sceneData.get(BasketItemizationControllerAbstract.BASKET_KEY);
        invoiceData = basketManager.getBasketTransaction().getHeader().getInvoiceData();
        
        frInvoiceData.clearErrorStyle();
        
        ticketCustomer = new Customer();

        if (invoiceData !=null){
        	modelMapper.map(invoiceData, ticketCustomer);
        }
        else{
        	modelMapper.map(basketManager.getBasketTransaction().getHeader().getCustomer(), ticketCustomer);
        }
        
        isGenericCustomer = ticketCustomer.getCustomerCode() != null && ticketCustomer.getCustomerCode().equals(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCustomerCode()) && ticketCustomer.getCustomerDes().equals(session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCustomerDes());

		if (StringUtils.isNotBlank(ticketCustomer.getCountryCode())){
			updateCustomerCountry(ticketCustomer.getCountryCode());
		}
		else {
			updateCustomerCountry(session.getApplicationSession().getStorePosBusinessData().getCountry().getCountryCode());
		}

		this.identTypes = FXCollections.observableArrayList();
		loadIdentificationTypes();
		cbIdentType.setItems(identTypes);

        refreshScreenData();
    }

    /**
     * Gestiona el evento de presionar la tecla intro para establecer el cliente de la factura
     *
     * @param event
     */
    @FXML
    public void actionAcceptIntro(KeyEvent event) {
    	log.debug("actionAcceptIntro()");

        if (event.getCode() == KeyCode.ENTER) {
            setInvoiceCustomer(event);
        }
    }

    /**
     * Acción aceptar
     */
    @FXML
    public void actionAccept(ActionEvent event) {
        log.debug("actionAccept() - Acción aceptar");

        setInvoiceCustomer(event);
    }

    protected void setInvoiceCustomer(Event event) {
    	log.debug("setInvoiceCustomer()");
    	
        frInvoiceData.setVatNumber(frInvoiceData.trimTextField(tfVatNumber));
        frInvoiceData.setAddress(frInvoiceData.trimTextField(tfAddress));
        frInvoiceData.setProvince(frInvoiceData.trimTextField(tfProvince));
        frInvoiceData.setLocation(frInvoiceData.trimTextField(tfLocation));
        frInvoiceData.setCity(frInvoiceData.trimTextField(tfCity));
        frInvoiceData.setBusinessName(frInvoiceData.trimTextField(tfBusinessName));
        frInvoiceData.setPostalCode(frInvoiceData.trimTextField(tfPostalCode));
        frInvoiceData.setPhone(frInvoiceData.trimTextField(tfPhone));
        frInvoiceData.setCountry(frInvoiceData.trimTextField(tfCountryCode));
        frInvoiceData.setBank(frInvoiceData.trimTextField(tfBank));
        frInvoiceData.setAddressBank(frInvoiceData.trimTextField(tfAddressBank));
        frInvoiceData.setCityBank(frInvoiceData.trimTextField(tfCityBank));
        frInvoiceData.setIbanBank(frInvoiceData.trimTextField(tfIBANBank));

        if (validateInvoiceDataForm()) {
            
            BasketInvoiceData invoiceData = new BasketInvoiceData();
            invoiceData.setVatNumber(tfVatNumber.getText());
            invoiceData.setPostalCode(tfPostalCode.getText());
            invoiceData.setAddress(tfAddress.getText());
            invoiceData.setProvince(tfProvince.getText());
            invoiceData.setPhone1(tfPhone.getText());
            invoiceData.setCustomerDes(tfBusinessName.getText());
            invoiceData.setCity(tfCity.getText());
            invoiceData.setLocation(tfLocation.getText());
            invoiceData.setCountryCode(tfCountryCode.getText());
            invoiceData.setBank(tfBank.getText());
            invoiceData.setBankAddress(tfAddressBank.getText());
            invoiceData.setBankCity(tfCityBank.getText());
            invoiceData.setIban(tfIBANBank.getText());

            if(cbIdentType.getSelectionModel().getSelectedItem() != null){
            	invoiceData.setIdentificationTypeCode(cbIdentType.getSelectionModel().getSelectedItem().getIdentificationTypeCode());
            }
            
            basketManager.updateInvoiceData(invoiceData);
            closeSuccess();
        }
    }

    /**
     * Valida los campos editables
     *
     * @return
     */
    protected boolean validateInvoiceDataForm() {
    	log.debug("validateInvoiceDataForm()");

    	String result = validateDocument();
		if(result != null) {
			boolean save = DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(result);
			if(!save) {
				tfVatNumber.requestFocus();
				return false;
			}
		}

        boolean valid = true;

        // Limpiamos los errores que pudiese tener el formulario
        frInvoiceData.clearErrorStyle();
        //Limpiamos el posible error anterior
        lbError.setText("");

        final Set<ConstraintViolation<InvoiceDataForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frInvoiceData);
        Iterator<ConstraintViolation<InvoiceDataForm>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            final ConstraintViolation<InvoiceDataForm> next = iterator.next();
            frInvoiceData.setErrorStyle(next.getPropertyPath(), true);
            if(valid){ //Ponemos el foco en el primero que da error
            	lbError.setText(next.getMessage());
            	Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
    					frInvoiceData.setFocus(next.getPropertyPath());
    				}
    			});
            }
            valid = false;
        }

        return valid;
    }
    
    @Override
    public void loadCentralCustomer() {
		if (validateCentralSearchForm()) {
			String countryCode = null;
			String identTypeCode = null;
			if (cbIdentType.getSelectionModel().getSelectedItem() != null && StringUtils.isNotBlank(cbIdentType.getSelectionModel().getSelectedItem().getIdentificationTypeCode())) {
				identTypeCode = cbIdentType.getSelectionModel().getSelectedItem().getIdentificationTypeCode();
				if (StringUtils.isNotBlank(tfCountryCode.getText())) {
					countryCode = tfCountryCode.getText();
				}
				else {
					countryCode = session.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode();
				}
			}

			try {
				List<Customer> customers = customersService.findByTaxIdentification(identTypeCode, tfVatNumber.getText());
				if (customers != null && !customers.isEmpty()) {
					fillInvoiceData(customers.get(0));
				}
				else {
					searchRemoteCustomer(countryCode);
				}
			}
			catch (Exception e) {
				log.error("loadCentralCustomer() - Ha habido un error al buscar en la base de datos el cliente para facturar: " + e.getMessage(), e);
				searchRemoteCustomer(countryCode);
			}
		}
    }
    
	protected boolean validateCentralSearchForm() {
		log.debug("validateCentralSearchForm()");

		boolean valid;

		// Limpiamos los errores que pudiese tener el formulario
		frInvoiceData.clearErrorStyle();
		frSearchCentral.clearErrorStyle();

		frSearchCentral.setCustomerCode(tfVatNumber.getText().trim());

		// Limpiamos el posible error anterior
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

	public void searchRemoteCustomer(String countryCode) {
		String identificationTypeCode = null;
		if (cbIdentType.getSelectionModel().getSelectedItem() != null) {
			identificationTypeCode = cbIdentType.getSelectionModel().getSelectedItem().getIdentificationTypeCode();
		}

		QueryRemoteCustomerTask queryRemoteTask = SpringContext.getBean(QueryRemoteCustomerTask.class, tfVatNumber.getText(), identificationTypeCode, countryCode,
		        new RestBackgroundTask.FailedCallback<Customer>(){

			        @Override
			        public void succeeded(Customer result) {
				        try {
					        clearFields();
					        fillInvoiceData(result);
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

	protected void fillInvoiceData(Customer customer) {
		tfBusinessName.setText(customer.getCustomerDes());
		tfAddress.setText(customer.getAddress());
		tfCity.setText(customer.getCity());
		tfProvince.setText(customer.getProvince());
		tfLocation.setText(customer.getLocation());
		tfPostalCode.setText(customer.getPostalCode());
		tfPhone.setText(customer.getPhone1());
		tfVatNumber.setText(customer.getVatNumber());
				
		updateCustomerCountry(customer.getCountryCode());

		updateSelectedIdentificationType(customer.getIdentificationTypeCode());
	}
    
}
