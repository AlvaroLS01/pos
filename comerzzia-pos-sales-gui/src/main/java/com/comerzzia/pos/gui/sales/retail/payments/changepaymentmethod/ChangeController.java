

package com.comerzzia.pos.gui.sales.retail.payments.changepaymentmethod;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.service.payments.PaymentsManagerServiceFacade;
import com.comerzzia.omnichannel.facade.service.store.StorePosPaymentMethodServiceFacade;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.paymentmethod.ActionButtonPaymentMethodComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j;

@Controller
@CzzScene
@Log4j
public class ChangeController extends SceneController implements Initializable, ButtonsGroupController {
    //Clave del parametro que recibe la ventana
    public static final String PARAM_INPUT_PAYMENT_METHOD_CODE_CHANGE = "paymentMethodCodeChange";
    public static final String PARAM_INPUT_PAYMENT_METHOD_DES_CHANGE = "paymentMethodDesChange";

    public static final String KEY_HIDE_BUTTONS = "hideButtons";
    public static final String KEY_PAYMENT_MANAGER = "paymentManager";

    // Medio de pago seleccionado en esta ventana (Variable de salida)
    protected String selectedPaymentMethodCode;

    //Elementos de pantalla
    @FXML
    protected Button btAccept;

    @FXML
    protected AnchorPane paymentMethodsPane;
    
    @FXML
    protected AnchorPane buttonsPane;
    
    @FXML
    protected Label lbPaymentMethod;

    // botonera de medios de pago
    protected ButtonsGroupComponent paymentMethodsButtonsGroup;
    
    protected PaymentsManagerServiceFacade paymentsManager;
    
	@Autowired
	protected StorePosPaymentMethodServiceFacade paymentMethodsService;
	
	@Autowired
	protected Session session;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() {
        log.debug("initializeComponents()");
        
        selectedPaymentMethodCode = null;
    }
    
    @Override
    public void onSceneOpen() {
//    	this.paymentsManager = (PaymentsManager) sceneData.get(KEY_PAYMENT_MANAGER);
    	
    	showPaymentMethods();
    	
        if (this.sceneData.containsKey(PARAM_INPUT_PAYMENT_METHOD_CODE_CHANGE)) {
            selectedPaymentMethodCode = (String) this.sceneData.get(PARAM_INPUT_PAYMENT_METHOD_CODE_CHANGE);
            String selectedPaymentMethodDes = (String) this.sceneData.get(PARAM_INPUT_PAYMENT_METHOD_DES_CHANGE);
            lbPaymentMethod.setText(selectedPaymentMethodDes);
        } else {
	    	selectedPaymentMethodCode = null;
	    	lbPaymentMethod.setText("");
        }
    }

	protected void showPaymentMethods() {
		List<ButtonConfigurationBean> listaAccionesMP = new LinkedList<ButtonConfigurationBean>();
        log.debug("showPaymentMethods() - Creando acciones para botonera de pago contado");
//        for (PaymentMethodDetail pay :paymentMethodsService.getStoreVisibleInSalesPaymentsMethods(session.getApplicationSession().getCodAlmacen())) {
//        	if(paymentsManager.isExchangePaymentMethodAvailable(pag.getCodMedioPago())) {
//	            ConfiguracionBotonMedioPagoBean cfg = new ConfiguracionBotonMedioPagoBean(null, pag.getDesMedioPago(), null, "ACCION_SELECIONAR_MEDIO_PAGO", "", pag);
//	            listaAccionesMP.add(cfg);
//        	}
//        }

        try {
            paymentMethodsButtonsGroup = new ButtonsGroupComponent(4, 4, this, listaAccionesMP, paymentMethodsPane.getPrefWidth() , paymentMethodsPane.getPrefHeight(), ActionButtonPaymentMethodComponent.class.getName());
            paymentMethodsButtonsGroup.setFocusablesButtons(true);
            paymentMethodsPane.getChildren().add(paymentMethodsButtonsGroup);
        }
        catch (LoadWindowException ex) {
            log.error("showPaymentMethods() - Error cargando pantalla de medio de pago de devolución: " + ex.getMessage(), ex);
        }
	}

    @Override
    public void initializeFocus() {
    	btAccept.requestFocus();
    }

    @FXML
    public void actionAccept(ActionEvent event) {
        log.debug("actionAccept()");
        if(selectedPaymentMethodCode != null) {
	        closeSuccess(selectedPaymentMethodCode);
        }
        else {
        	DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe seleccionar un medio de pago para el cambio."));
        }
    }

    @Override
    public void executeAction(ActionButtonComponent pressedButton) {
        switch (pressedButton.getClave()) {
            case "ACCION_SELECIONAR_MEDIO_PAGO":
                ActionButtonPaymentMethodComponent button = (ActionButtonPaymentMethodComponent) pressedButton;
                selectedPaymentMethodCode = button.getPaymentMethod().getPaymentMethodCode();
                lbPaymentMethod.setText(button.getPaymentMethod().getPaymentMethodDes());
                break;
            default:
                break;
        }
    }
}
