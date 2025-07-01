package com.comerzzia.pos.devices.linedisplay.secondaryscreen.sale;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.BasketPromotable;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.SecondaryScreenSceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.linedisplay.DeviceLineDisplaySecondaryScreen;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

public abstract class SecondaryScreenSaleControllerAbstract extends SecondaryScreenSceneController implements Initializable {
	
	@Autowired
	protected Session sesion;
	
	@Autowired
	protected VariableServiceFacade variablesServices;

	protected Logger log = Logger.getLogger(getClass());
	
	@FXML
	protected WebView wvPrincipal;
		
	protected String combination1Code, combination2Code;
	protected BasketPromotable<?> basket;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	public void refrescarDatosPantalla() {
        log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");
        basket = DeviceLineDisplaySecondaryScreen.getBasket();
		
		loadWebViewPrincipal();
	}

	protected void loadWebViewPrincipal() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("basket", basket);
		params.put("combination1Code", combination1Code);
		params.put("combination2Code", combination2Code);
		
		loadWebView("secondary_screen/sales/basket/secondarybasket", params, wvPrincipal);
	}



	@Override
	public void initializeComponents() throws InitializeGuiException {
		setShowKeyboard(false);
		basket = DeviceLineDisplaySecondaryScreen.getBasket();
		
		if (sesion.getApplicationSession().isDesglose1Activo()) {
			combination1Code = I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE1_TITULO));
		}

		if (sesion.getApplicationSession().isDesglose2Activo()) {
			combination2Code = I18N.getText(variablesServices.getVariableAsString(VariableServiceFacade.ARTICULO_DESGLOSE2_TITULO));
		}
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {		
		refrescarDatosPantalla();
	}

	@Override
	public void initializeFocus() {

	}

}
