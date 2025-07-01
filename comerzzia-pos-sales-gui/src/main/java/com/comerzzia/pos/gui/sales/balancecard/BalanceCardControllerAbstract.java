package com.comerzzia.pos.gui.sales.balancecard;


import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketBalanceCard;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.balancecard.BalanceCardService;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
@CzzScene
public abstract class BalanceCardControllerAbstract extends SceneController {
	@Autowired
	protected Session session;
    @Autowired
	protected VariableServiceFacade variableService;
    @Autowired
    protected BalanceCardService balanceCardService;
    
    @FXML
    protected TextField tfBalanceCardNumber, tfAvailableBalance;    
    @FXML
    protected Label lbTitle;
    @FXML
    protected Button btAccept;
    @FXML
	protected NumericTextField tfAmount;
    @FXML
	protected NumericKeypad numericKeypad;
    
    protected BalanceCardNumberForm frBalanceCardNumber;
    
    protected BasketBalanceCard balanceCard;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        frBalanceCardNumber = SpringContext.getBean(BalanceCardNumberForm.class);
        frBalanceCardNumber.setFormField("balanceCardNumber", tfBalanceCardNumber);
    }    

    @Override
    public void initializeComponents() {
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {        
    	tfBalanceCardNumber.setText("");
        balanceCard = null;
        
        showCardData();
    }

	protected void showCardData() {
		boolean showData = balanceCard != null;
		tfAvailableBalance.clear();
		
		if(showData) {
			BigDecimal balance = balanceCard.getBalance();
			tfAvailableBalance.setText(FormatUtils.getInstance().formatNumber(balance != null? balance : BigDecimal.ZERO, 2));
		}
	}

    @Override
    public void initializeFocus() {
    	tfBalanceCardNumber.requestFocus();
    }
    
    @FXML
    public void actionEnter(KeyEvent e){
		if (e.getCode() == KeyCode.ENTER) {
			searchGiftcard();
		}
    }
    
    public void searchGiftcard() {
    	frBalanceCardNumber.setBalanceCardNumber(tfBalanceCardNumber.getText());
    	balanceCard = null;
        
    	frBalanceCardNumber.clearErrorStyle();
        // Validate form
        Set<ConstraintViolation<BalanceCardNumberForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frBalanceCardNumber);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<BalanceCardNumberForm> next = constraintViolations.iterator().next();
            frBalanceCardNumber.setErrorStyle(next.getPropertyPath(), true);
            frBalanceCardNumber.setFocus(next.getPropertyPath());         
			DialogWindowBuilder.getBuilder(this.getStage()).simpleErrorDialog(next.getMessage());
        }
        else{
        	String cardNumber = tfBalanceCardNumber.getText();
        	
        	if(StringUtils.isNotBlank(cardNumber)) {
        		SpringContext.getBean(QueryBalanceCardByCardNumberTask.class, cardNumber, new RestBackgroundTask.FailedCallback<BasketBalanceCard>(){
        			
        			@Override
        			public void succeeded(BasketBalanceCard result) {
        				balanceCard = result;
        				showCardData();
        			}
        			
        			@Override
        			public void failed(Throwable throwable) {
        				tfBalanceCardNumber.requestFocus();
        				tfBalanceCardNumber.selectAll();
        			}
        		}, getStage()).start();;
		        
	        }
        }
    }
    
    @FXML
    public void actionAccept(){
    	if(balanceCard == null && !tfBalanceCardNumber.getText().isEmpty()) {
    		searchGiftcard();
    	}else if(balanceCard == null) {
    		closeCancel();
    	}else {
    		closeSuccess(balanceCard);
    	}
    }
    
}
