


package com.comerzzia.pos.gui.sales.retail.payments.changedue;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.cashdrawer.DeviceCashDrawerDummy;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j;

@Controller
@CzzScene
@Log4j
public class ChangeDueScreenController extends SceneController {

    public static final String PARAM_INPUT_CHANGE = "CHANGE";
    public static final String PARAM_INPUT_TOTAL = "TOTAL";
    public static final String PARAM_INPUT_DELIVERED = "DELIVERED";
    public static final String PARAM_INPUT_PAYMENT_METHOD_CHANGE = "PAYMENT_METHOD_CHANGE";
    public static final String PARAM_INPUT_TOTAL_QUANTITY = "TOTAL_QUANTITY";
    public static final String PARAM_BASKET_TRANSACTION = "BASKET_TRANSACTION";
    
    @FXML
    protected Button btAccept;
    
    @FXML
    protected WebView wvMain;
    
    protected TextField tfTotal, tfChange, tfDelivered, tfPaymentMethodChange, tfTotalQuantity;
    
    protected Timeline cashDrawerCloseTimeLine;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void initializeComponents() {
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	activateCashDrawerCloseHandler();
    	
    	loadWebViewPrincipal((BasketPromotable<?>) sceneData.get(PARAM_BASKET_TRANSACTION));
    	
    }
    
    protected void loadWebViewPrincipal(BasketPromotable<?> basketTransaction) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("basketTransaction", basketTransaction);
		
		loadWebView("sales/changedue/changedue", params, wvMain);
	}

    @Override
    public void initializeFocus() {
    	btAccept.requestFocus();
    }
    
    protected void addEscapePressedEvent() {
		getScene().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					event.consume();
					
					cancelAction();
				}
			}
		});
	}

    
    public void cancelAction() {
    	deactivateCashDrawerCloseHandler();
    	
        closeSuccess();
    }
    
    protected void activateCashDrawerCloseHandler() {
		if ((Devices.getInstance().getCashDrawer() instanceof DeviceCashDrawerDummy))
			return;
		
		if (!Devices.getInstance().getCashDrawer().isReady()) {
			log.info("Cash drawer is not ready. Automatic scene close is disabled");
			return;
		}

		if (cashDrawerCloseTimeLine == null) {
			cashDrawerCloseTimeLine = new Timeline(new KeyFrame(Duration.millis(100), new CashDrawerCloseHandler()));
			cashDrawerCloseTimeLine.setCycleCount(Timeline.INDEFINITE);
		}

		log.debug("Timeline activation for cash drawer close detection");
		cashDrawerCloseTimeLine.play();
	}

	protected void deactivateCashDrawerCloseHandler() {
		if (cashDrawerCloseTimeLine != null) {
			cashDrawerCloseTimeLine.stop();
		}
	}

	protected class CashDrawerCloseHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (!Devices.getInstance().getCashDrawer().isOpened() && getStage().isFocused() && btAccept.isFocused()) {
				log.debug("Cash drawer close detected. Closing scene");
				cashDrawerCloseTimeLine.stop();
				cancelAction();
			}
			event.consume();
		}
	}
	
	@Override
	public boolean canCloseCancel() {
		return false;
	}

}
