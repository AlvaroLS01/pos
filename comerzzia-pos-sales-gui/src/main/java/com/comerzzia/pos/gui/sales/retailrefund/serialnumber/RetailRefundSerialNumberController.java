package com.comerzzia.pos.gui.sales.retailrefund.serialnumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.gui.sales.serialnumber.SerialNumberControllerAbstract;

@Controller
@CzzScene
public class RetailRefundSerialNumberController extends SerialNumberControllerAbstract {
	
	public static final String PARAM_LINE_ORIG_DOCUMENT_SERIAL_CODES = "PARAM_LINE_ORIG_DOCUMENT_SERIAL_CODES";
	
	protected Set<String> originalDocumentSerialNumbers;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		super.onSceneOpen();
		originalDocumentSerialNumbers = (Set<String>) sceneData.get(PARAM_LINE_ORIG_DOCUMENT_SERIAL_CODES);
	}
	
	@Override
	protected boolean validateSerialNumber(String serialNumber) {
		if(!super.validateSerialNumber(serialNumber)) {
			return false;
		}
		
		if(originalDocumentSerialNumbers != null && !originalDocumentSerialNumbers.contains(serialNumber)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog("El número debe formar parte de la linea original.");
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void loadWebViewPrincipal() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("requiredQuantity", requiredSerialCodes);
		params.put("lineDescription", lineDescription);
		params.put("serialNumbers", serialNumbers);
		params.put("originalDocumentSerialNumbers", originalDocumentSerialNumbers);
		
		loadWebView(getWebViewPath(), params, wvPrincipal);
	}
	
	@Override
	protected String getWebViewPath() {
		return "sales/refund/serialnumber/serialnumber";
	}

}
