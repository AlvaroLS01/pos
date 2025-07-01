package com.comerzzia.pos.devices.linedisplay.secondaryscreen.scale;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.comerzzia.omnichannel.facade.model.basket.items.BasketItem;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.hashcontrolled.HashControlledDevice;
import com.comerzzia.pos.core.devices.device.scale.DeviceScale;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.SecondaryScreenSceneController;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecondaryScreenScaleControllerAbstract extends SecondaryScreenSceneController {
	
	protected BasketItem basketItem;
	protected Integer scaleStatus;
	protected String certNumber;
	
	@FXML
	protected WebView wvPrincipal;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		setShowKeyboard(false);
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		DeviceScale scale = Devices.getInstance().getScale();
		this.certNumber = scale.getCertNumber();
		
		updateScaleStatus(null, null);
	}

	@Override
	protected void initializeFocus() {
	}
	
	public void updateScaleStatus(Integer status, BasketItem basketItem) {
		 log.debug("updateScaleStatus() - Loading screendata...");
		 this.basketItem = basketItem;
		 scaleStatus = status;
		 
		 loadWebViewPrincipal();
	}
	
	protected void loadWebViewPrincipal() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("basketItem", basketItem);
		params.put("status", scaleStatus);
		if(Devices.getInstance().getScale() instanceof HashControlledDevice) {
			HashControlledDevice scale = (HashControlledDevice) Devices.getInstance().getScale();
			String libraryHash = scale.getLibraryHash();
			
			params.put("hashLibrary", libraryHash);
			params.put("certNumber", certNumber);
		}
		
		loadWebView("secondary_screen/sales/scale/scaledata", params, wvPrincipal);
	}

}
