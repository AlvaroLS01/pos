
package com.comerzzia.pos.gui.sales.layaway.items.datosCliente;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.facade.model.Country;
import com.comerzzia.core.facade.model.CountryIdType;
import com.comerzzia.core.facade.service.countryidentificationtype.CountryIdentificationTypeFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.gui.sales.layaway.ApartadosManager;
import com.comerzzia.pos.gui.sales.retail.payments.customerdata.ChangeCustomerDataController;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;

import javafx.application.Platform;
import javafx.collections.FXCollections;

@Component
public class CambiarDatosClienteApartadoController extends ChangeCustomerDataController{

	public static final String PARAMETRO_APARTADOS_MANAGER = "APARTADO_MANAGER";
	
	@Autowired
	protected Session sesion;
	
	protected ApartadosManager apartadosManager;
	
	@Autowired
	protected CountryIdentificationTypeFacade tiposIdentService;
	
	protected FormularioDatosClienteApartadoBean formClienteApartados;
	
	public void initializeComponents() throws InitializeGuiException{
		super.initializeComponents();
		
		formClienteApartados = SpringContext.getBean(FormularioDatosClienteApartadoBean.class);
		
		formClienteApartados.setFormField("desCliente", tfCustomerDes);
		formClienteApartados.setFormField("domicilio", tfAddress);
		formClienteApartados.setFormField("poblacion", tfCity);
		formClienteApartados.setFormField("provincia", tfProvince);
		formClienteApartados.setFormField("localidad", tfLocation);
		formClienteApartados.setFormField("codigoPostal", tfPostalCode);
		formClienteApartados.setFormField("cif", tfVatNumber);
		formClienteApartados.setFormField("telefono", tfPhone);
		formClienteApartados.setFormField("codPais", tfCountryCode);
		
		customerCodePane.setManaged(true);
		customerCodePane.setVisible(true);
		tfCustomerCode.setEditable(false);
		vbBusinessName.setManaged(true);
		vbBusinessName.setVisible(true);
	}
	
	@Override
    public void onSceneOpen() throws InitializeGuiException {
		
		btSearch.setVisible(false);
        
        apartadosManager = (ApartadosManager)sceneData.remove(PARAMETRO_APARTADOS_MANAGER);
        identTypes = FXCollections.observableArrayList();        
        setTitle();
        
        //Limpiamos el posible error anterior
        formClienteApartados.clearErrorStyle();
        lbError.setText("");
        
        ticketCustomer = apartadosManager.getCliente();
        
        List<CountryIdType> tiposIdentificacion = tiposIdentService.findByCountryCode(sesion.getApplicationSession().getStorePosBusinessData().getDefaultCustomer().getCountryCode(), null);
        
        //Añadimos elemento vacío
        identTypes.add(new CountryIdType());
                    
        for(CountryIdType tipoIdent: tiposIdentificacion){
            identTypes.add(tipoIdent);
        }
        cbIdentType.setItems(identTypes);

        String codPais = ticketCustomer.getCountryCode();
        if(codPais!=null && !codPais.isEmpty()){
        	Country paisCliente = getCountry(codPais);
        	if(paisCliente!=null){
        		ticketCustomer.setCountryDes(paisCliente.getCountryDes());
        	}
        }
        else{
        	tfCountryCode.setText(AppConfig.getCurrentConfiguration().getCountry());
        	Country pais = getCountry(AppConfig.getCurrentConfiguration().getCountry());
        	tfCountryDes.setText(pais.getCountryDes());
        }
        
        refreshScreenData();
	}    

	public void refreshScreenData(){

		tfCustomerCode.setText(ticketCustomer.getCustomerCode());
		tfCustomerDes.setText(ticketCustomer.getCustomerDes());
		tfBusinessName.setText(ticketCustomer.getTradeName());
		tfProvince.setText(ticketCustomer.getProvince());
		tfAddress.setText(ticketCustomer.getAddress());
		tfLocation.setText(ticketCustomer.getLocation());
		tfPhone.setText(ticketCustomer.getPhone1());
		tfVatNumber.setText(ticketCustomer.getVatNumber());
		tfCountryCode.setText(ticketCustomer.getCountryCode());
		tfCountryDes.setText(ticketCustomer.getCountryDes());
		tfCity.setText(ticketCustomer.getCity());
		tfPostalCode.setText(ticketCustomer.getPostalCode());
		CountryIdType tipoIdentSeleccionado = null;
		for(CountryIdType tipoIdent : identTypes) {
			if(tipoIdent != null && tipoIdent.getIdentificationTypeCode() != null && tipoIdent.getIdentificationTypeCode().equals(ticketCustomer.getIdentificationTypeCode())) {
				tipoIdentSeleccionado = tipoIdent;
			}
		}
		cbIdentType.getSelectionModel().select(tipoIdentSeleccionado);

		if(StringUtils.isNotBlank(ticketCustomer.getIdentificationTypeCode())){
			tipoIdentSeleccionado = null;
    		for(CountryIdType tipoIdent : identTypes) {
    			if(tipoIdent != null && tipoIdent.getIdentificationTypeCode() != null && tipoIdent.getIdentificationTypeCode().equals(ticketCustomer.getIdentificationTypeCode())) {
    				tipoIdentSeleccionado = tipoIdent;
    			}
    		}
    		cbIdentType.getSelectionModel().select(tipoIdentSeleccionado);
		}
	}

	public void actionAccept(){

		if(validarFormularioDatosFactura()){
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
			ticketCustomer.setTradeName(tfBusinessName.getText());
			ticketCustomer.setProvince(tfProvince.getText());
			ticketCustomer.setCustomerDes(tfCustomerDes.getText());

			apartadosManager.guardarDatosCliente(ticketCustomer);

			closeSuccess();
		}
	}
	
	private boolean validarFormularioDatosFactura() {
    	log.debug("validarFormularioCambiarDatosClienteApartadoController()");

        boolean valido = true;

        // Limpiamos los errores que pudiese tener el formulario
        formClienteApartados.clearErrorStyle();
        
        formClienteApartados.setDomicilio(tfAddress.getText());
        formClienteApartados.setDesCliente(tfCustomerDes.getText());
        formClienteApartados.setCodPais(tfCountryCode.getText());
        formClienteApartados.setCif(tfVatNumber.getText());    
        formClienteApartados.setPoblacion(tfCity.getText());
        formClienteApartados.setProvincia(tfProvince.getText());
        formClienteApartados.setLocalidad(tfLocation.getText());
        formClienteApartados.setCodigoPostal(tfPostalCode.getText());
        formClienteApartados.setTelefono(tfPhone.getText());
        
        //Limpiamos el posible error anterior
        lbError.setText("");
        
        final Set<ConstraintViolation<FormularioDatosClienteApartadoBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(formClienteApartados);
        Iterator<ConstraintViolation<FormularioDatosClienteApartadoBean>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            final ConstraintViolation<FormularioDatosClienteApartadoBean> next = iterator.next();
            formClienteApartados.setErrorStyle(next.getPropertyPath(), true);
            
            if(valido){ //Ponemos el foco en el primero que da error
            	lbError.setText(next.getMessage());
            	Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
    					formClienteApartados.setFocus(next.getPropertyPath());
    				}
    			});
            }
            valido = false;
        }
        
        return valido;
    }

}
