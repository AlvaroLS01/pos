package com.comerzzia.pos.gui.sales.retail.payments.shippingdata;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.model.CountryIdType;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketShippingData;
import com.comerzzia.omnichannel.facade.model.sale.Customer;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.sales.retail.payments.customerdata.ChangeCustomerDataController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;

@Controller
public class ShippingDataController extends ChangeCustomerDataController {

	protected ShippingDataForm shippingDataForm;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		shippingDataForm = SpringContext.getBean(ShippingDataForm.class);
		shippingDataForm.setFormField("name", tfBusinessName);
		shippingDataForm.setFormField("address", tfAddress);
		shippingDataForm.setFormField("city", tfCity);
		shippingDataForm.setFormField("province", tfProvince);
		shippingDataForm.setFormField("location", tfLocation);
		shippingDataForm.setFormField("postalCode", tfPostalCode);
		shippingDataForm.setFormField("vaNumber", tfVatNumber); 
		shippingDataForm.setFormField("phone", tfPhone);
	}
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
	    super.onSceneOpen();
	    shippingDataForm.clearErrorStyle();
	    lbError.setText("");
//	    isClienteGenerico = clienteTicket.getCodCliente().equals(sesion.getAplicacion().getTienda().getCliente().getCodCliente()) && clienteTicket.getDesCliente().equals(sesion.getAplicacion().getTienda().getCliente().getDesCliente());
	}

	public void refreshScreenData() {
		log.debug("refreshScreenData()");
		
		lbRequiredDocument.setVisible(false);
		lbRequiredDocument.setManaged(false);
		lbRequiredCustomer.setVisible(false);
		lbRequiredCustomer.setManaged(false);
		lbRequiredName.setVisible(true);
		lbRequiredName.setManaged(true);
		lbRequiredAddress.setVisible(true);
		lbRequiredAddress.setManaged(true);
		lbRequiredPhone.setVisible(true);
		lbRequiredPhone.setManaged(true);
		lbRequiredCountry.setVisible(false);
		lbRequiredCountry.setManaged(false);
		
		tfPostalCode.setText(ticketCustomer.getPostalCode());
		cbIdentType.getSelectionModel().select(0);
		tfProvince.setText(ticketCustomer.getProvince());
    	tfCountryCode.setText(ticketCustomer.getCountryCode());
    	countryCode = ticketCustomer.getCountryCode() != null ? ticketCustomer.getCountryCode().toUpperCase() : "";
    	tfCountryDes.setText(ticketCustomer.getCountryDes());

		if (!isGenericCustomer) {
			tfBusinessName.setText(ticketCustomer.getCustomerDes());
			tfProvince.setText(ticketCustomer.getProvince());
			tfAddress.setText(ticketCustomer.getAddress());
			tfLocation.setText(ticketCustomer.getLocation());
			tfPhone.setText(ticketCustomer.getPhone1());
			tfVatNumber.setText(ticketCustomer.getVatNumber());
			tfCountryCode.setText(ticketCustomer.getCountryCode());
			countryCode = ticketCustomer.getCountryCode() != null ? ticketCustomer.getCountryCode().toUpperCase() : "";
			tfCountryDes.setText(ticketCustomer.getCountryDes());
			tfCity.setText(ticketCustomer.getCity());
			tfPostalCode.setText(ticketCustomer.getPostalCode());
			CountryIdType selectedIdentType = null;
			for (CountryIdType identType : identTypes) {
				if (identType != null && identType.getIdentificationTypeCode() != null && identType.getIdentificationTypeCode().equals(ticketCustomer.getIdentificationTypeCode())) {
					selectedIdentType = identType;
				}
			}
			if(selectedIdentType != null) {
				cbIdentType.getSelectionModel().select(selectedIdentType);
			}
			if(StringUtils.isNotBlank(ticketCustomer.getIdentificationTypeCode())){
				selectedIdentType = null;
	    		for(CountryIdType identType : identTypes) {
	    			if(identType != null && identType.getIdentificationTypeCode() != null && identType.getIdentificationTypeCode().equals(ticketCustomer.getIdentificationTypeCode())) {
	    				selectedIdentType = identType;
	    			}
	    		}
	    		cbIdentType.getSelectionModel().select(selectedIdentType);
	        }
		}
		
		BasketLoyalCustomer loyalCustomer = basketManager.getBasketTransaction().getHeader().getLoyalCustomer();
		if(loyalCustomer != null && isGenericCustomer){
			tfBusinessName.setText(loyalCustomer.getName()+" "+loyalCustomer.getLastName());
			tfBusinessName.setEditable(false);
			tfProvince.setText(loyalCustomer.getProvince());
			tfAddress.setText(loyalCustomer.getAddress());
			tfLocation.setText(loyalCustomer.getLocation());
			tfCountryCode.setText(loyalCustomer.getCountryCode());
			countryCode = loyalCustomer.getCountryCode() != null ? loyalCustomer.getCountryCode().toUpperCase() : "";
			tfCountryDes.setText(loyalCustomer.getCountryDes());
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
		}else{
			tfBusinessName.setEditable(true);
		}
	}

	@Override
	protected void getCustomer() {
		log.debug("getCustomer()");
		BasketShippingData customer = basketManager.getBasketTransaction().getHeader().getShippingData();
		ticketCustomer = customer!=null?modelMapper.map(customer, Customer.class):null;
		if (ticketCustomer == null) {
			clearFields();

			ticketCustomer = new Customer();
			BeanUtils.copyProperties(basketManager.getBasketTransaction().getHeader().getCustomer(), ticketCustomer);
		}
	}

	@Override
	public void actionAccept() {
		log.debug("actionAccept()");
		if (validateForm()) {
			BasketShippingData shippingData = new BasketShippingData();
			shippingData.setCountryCode(tfCountryCode.getText());
			shippingData.setVatNumber(tfVatNumber.getText());
			shippingData.setPostalCode(tfPostalCode.getText());
			shippingData.setAddress(tfAddress.getText());
			shippingData.setCity(tfCity.getText());
			shippingData.setLocation(tfLocation.getText());
	        shippingData.setPhone1(tfPhone.getText());
	        if(cbIdentType.getSelectionModel().getSelectedItem() != null){
	        	shippingData.setIdentificationTypeCode(cbIdentType.getSelectionModel().getSelectedItem().getIdentificationTypeCode());
	        }
	        shippingData.setTradeName(tfBusinessName.getText());
	        shippingData.setCustomerDes(tfBusinessName.getText());
	        shippingData.setProvince(tfProvince.getText());
	        
			basketManager.updateShippingData(shippingData);
	        
	        closeSuccess();
		}
	}

	@Override
	protected boolean validateForm() {
		log.debug("validateForm()");

		shippingDataForm.setName(form.trimTextField(tfBusinessName));
		shippingDataForm.setAddress(form.trimTextField(tfAddress));
		shippingDataForm.setCity(form.trimTextField(tfCity));
		shippingDataForm.setProvince(form.trimTextField(tfProvince));
		shippingDataForm.setLocation(form.trimTextField(tfLocation));
		shippingDataForm.setPostalCode(form.trimTextField(tfPostalCode));
		shippingDataForm.setPhone(form.trimTextField(tfPhone));
		shippingDataForm.setnumDocIdent(form.trimTextField(tfVatNumber)); 

		boolean valid = true;

		// Limpiamos los errores que pudiese tener el formulario
		shippingDataForm.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbError.setText("");
		
		//Limpiamos el posible error anterior       
        final Set<ConstraintViolation<ShippingDataForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(shippingDataForm);
        Iterator<ConstraintViolation<ShippingDataForm>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            final ConstraintViolation<ShippingDataForm> next = iterator.next();
            shippingDataForm.setErrorStyle(next.getPropertyPath(), true);
            
            if(valid){ //Ponemos el foco en el primero que da error
            	lbError.setText(next.getMessage());
            	Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
    					shippingDataForm.setFocus(next.getPropertyPath());
    				}
    			});
            }
            valid = false;
        }

		return valid;
	}

	protected void setTitle() {
		log.debug("setTitle()");
		lbTitle.setText(I18N.getText("Datos del Envío"));
	}
}
