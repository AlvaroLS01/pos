package com.comerzzia.pos.devices.linedisplay.secondaryscreen.tender;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.SecondaryScreenSceneController;
import com.comerzzia.pos.devices.linedisplay.DeviceLineDisplaySecondaryScreen;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

public class SecondaryScreenTenderControllerAbstract extends SecondaryScreenSceneController implements Initializable {

	protected Logger log = Logger.getLogger(getClass());
 	
 	@FXML
 	protected WebView wvPrincipal;
 	
 	protected BasketPromotable<?> documento;
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	public void refrescarDatosPantalla() {
        log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");
        documento = DeviceLineDisplaySecondaryScreen.getBasket();
		
		loadWebViewPrincipal();		
    }

	protected void loadWebViewPrincipal() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("basketTransaction", documento);
		loadWebView("secondary_screen/sales/payments/secondarypayments", params, wvPrincipal);
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		setShowKeyboard(false);
		documento = DeviceLineDisplaySecondaryScreen.getBasket();
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {		
		refrescarDatosPantalla();
	}

	@Override
	public void initializeFocus() {

	}
	
}
