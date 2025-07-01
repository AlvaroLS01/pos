package com.comerzzia.pos.gui.sales.loyalcustomer.remarks;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.gui.sales.loyalcustomer.LoyalCustomerController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

@Component
public class LoyalCustomerRemarksPanelController extends TabController{
	
protected static final Logger log = Logger.getLogger(LoyalCustomerRemarksPanelController.class);
	
	@Autowired
    protected VariableServiceFacade variablesService;
	
	@Autowired
	protected LoyalCustomerController loyalCustomerController;
	
	@FXML
	@Getter
	protected TextArea taRemarks;
	
	@Getter
	@Setter
	protected LyCustomerDetail lyCustomer;
	
	protected boolean editionMode;

	@Override
	public void initializeTab(URL arg0, ResourceBundle arg1) {
		
	}
	
	@Override
	public void selected(){
		LyCustomerDetail newLyCustomer = loyalCustomerController.getLyCustomer();
		if(newLyCustomer != null && (lyCustomer == null || !lyCustomer.getLyCustomerId().equals(newLyCustomer.getLyCustomerId()))){
			lyCustomer = newLyCustomer;
			taRemarks.setText(lyCustomer.getRemarks());
		}
		
		if("CONSULTA".equals(loyalCustomerController.getMode())){
			editFields(false);
		}
		else{
			editFields(true);
		}
	}
	
	public void editFields(boolean editionMode){
		taRemarks.setEditable(editionMode);
	}
	
	public void clearForm(){
		taRemarks.clear();
	}

}
