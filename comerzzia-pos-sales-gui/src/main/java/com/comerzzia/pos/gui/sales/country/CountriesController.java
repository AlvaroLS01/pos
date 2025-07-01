package com.comerzzia.pos.gui.sales.country;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.facade.model.Country;
import com.comerzzia.core.facade.service.country.CountryServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.helper.HelperSceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.util.config.SpringContext;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

@Controller
@CzzScene
@Slf4j
public class CountriesController extends HelperSceneController<CountryRow>{
    
    @FXML
    protected TextField tfCountryDes, tfCountryCode;
    
    protected CountryForm frSearchCountry;
    
    
    @Autowired
    protected CountryServiceFacade countryService;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	super.initialize(url, rb);

    	frSearchCountry = SpringContext.getBean(CountryForm.class);
        frSearchCountry.setFormField("countryCode", tfCountryCode);
        frSearchCountry.setFormField("countryDes", tfCountryDes);
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	super.onSceneOpen();
        tfCountryCode.setText("");
        tfCountryDes.setText("");
    }

    @Override
    public void initializeFocus() {
        tfCountryCode.requestFocus();
    }
    
    public void actionSearch(){
        
        frSearchCountry.clearErrorStyle();
        
        frSearchCountry.setCountryCode(tfCountryCode.getText());
        frSearchCountry.setCountryDes(tfCountryDes.getText());
        
        Set<ConstraintViolation<CountryForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frSearchCountry);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<CountryForm> next = constraintViolations.iterator().next();
            frSearchCountry.setErrorStyle(next.getPropertyPath(), true);
            frSearchCountry.setFocus(next.getPropertyPath());
        }
        else{
            try {
            	helperRowList.clear();
            	
            	helperRowList.addAll(buildHelpersRows(sceneData));
                
            }
            catch (NotFoundException ex) {
                log.debug("No se encontraron resultados en la búsqueda de países con los parámetros codPais="+tfCountryCode.getText()+", y descPais="+tfCountryDes.getText());
            }
        }
    }
    
    public void actionSearchCountryIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            actionSearch();
        }
    }
    
	@Override
	public void setTitle() {
	}

	@Override
	public List<CountryRow> buildHelpersRows(Map<String, Object> params) {
		List<Country> result;
		List<CountryRow> countries = new ArrayList<>();
		
    	if (StringUtils.isBlank(tfCountryCode.getText()) && StringUtils.isBlank(tfCountryDes.getText())) {
    		result = countryService.findAll();
    	}
    	else {
    		result = countryService.findPage(tfCountryCode.getText().toUpperCase(), tfCountryDes.getText());
    	}
        
        for(Country country: result){
            countries.add(new CountryRow(country));
        }
        
        return countries;
	};
}
