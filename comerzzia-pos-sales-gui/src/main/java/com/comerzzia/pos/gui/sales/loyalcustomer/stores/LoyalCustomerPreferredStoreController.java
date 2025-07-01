package com.comerzzia.pos.gui.sales.loyalcustomer.stores;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
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
@SuppressWarnings("unchecked")
public class LoyalCustomerPreferredStoreController extends HelperSceneController<LoyalCustomerPreferredStoreRow>{
	
	public static final String PARAM_STORES = "stores";
    
    @FXML
    protected TextField tfStoreDes, tfStoreCode;
    
    protected LoyalCustomerPreferredStoreFormValidationBean frSearchStore;
    
    private List<LoyalCustomerPreferredStoreRow> originalStores;
    
	@Override
    public void initialize(URL url, ResourceBundle rb) {
    	super.initialize(url, rb);
    	
    	frSearchStore = SpringContext.getBean(LoyalCustomerPreferredStoreFormValidationBean.class);
        frSearchStore.setFormField("storeCode", tfStoreCode);
        frSearchStore.setFormField("storeDes", tfStoreDes);
        
    }

	@Override
    public void onSceneOpen() throws InitializeGuiException {
    	super.onSceneOpen();
    	
    	originalStores = (List<LoyalCustomerPreferredStoreRow>) sceneData.get(PARAM_STORES);
        tfStoreCode.setText("");
        tfStoreDes.setText("");
        actionSearch();
    }

    @Override
    public void initializeFocus() {
        tfStoreCode.requestFocus();
    }
    
    public void actionSearch(){
        
        frSearchStore.clearErrorStyle();
        
        frSearchStore.setStoreCode(tfStoreCode.getText());
        frSearchStore.setStoreDes(tfStoreDes.getText());
        
        Set<ConstraintViolation<LoyalCustomerPreferredStoreFormValidationBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frSearchStore);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<LoyalCustomerPreferredStoreFormValidationBean> next = constraintViolations.iterator().next();
            frSearchStore.setErrorStyle(next.getPropertyPath(), true);
            frSearchStore.setFocus(next.getPropertyPath());
        }
        else{
            List<LoyalCustomerPreferredStoreRow> result = originalStores;
            
            helperRowList.clear();
            if(result != null && !result.isEmpty()){
	            for(LoyalCustomerPreferredStoreRow store: result){
	            	if(store.getStoreCode().toUpperCase().contains(frSearchStore.getStoreCode().toUpperCase()) && store.getStoreDes().toUpperCase().contains(frSearchStore.getStoreDes().toUpperCase()))
	            		helperRowList.add(store);
	            }
            }else{
            	log.debug("No se han encontrado resultados");
            }
           
        }
    }
    
    public void actionSearchStoreIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            actionSearch();
        }
    }
        
    public void actionAccept(){
        
        if(tbHelperRow.getSelectionModel().getSelectedItem()!=null){
            closeSuccess(tbHelperRow.getSelectionModel().getSelectedItem());
        }
        else{
        	DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog("Debe seleccionar antes la tienda correspondiente.");
        }
    }

	@Override
	public void setTitle() {
	}

	
	@Override
	public List<LoyalCustomerPreferredStoreRow> buildHelpersRows(Map<String, Object> params) {
		return(List<LoyalCustomerPreferredStoreRow>) params.get(PARAM_STORES);
	}
    

}
