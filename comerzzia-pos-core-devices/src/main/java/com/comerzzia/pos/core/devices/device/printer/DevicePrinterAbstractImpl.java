package com.comerzzia.pos.core.devices.device.printer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

import lombok.extern.log4j.Log4j;

@Log4j
public abstract class DevicePrinterAbstractImpl extends DeviceAbstractImpl implements DevicePrinter{

	protected List<String> documentTypes = new ArrayList<String>();
	protected String mimeType;
	protected long timeoutMillis = 60000l;
	
	protected boolean allowPrintTest;
	
	@Override
	protected void loadConfiguration(DeviceConfiguration config) throws DeviceException {
		XMLDocumentNode configuration = config.getModelConfiguration().getConnectionConfig();
		try {
			mimeType = configuration.getAttributeValue("mime-type", false);
		} catch (XMLDocumentNodeNotFoundException e) {
			mimeType = "";
		}
		List<XMLDocumentNode> documentTypesParentNode = configuration.getChildren("documenttypes");
		if(!documentTypesParentNode.isEmpty()) {
			List<XMLDocumentNode> documentTypesNode = documentTypesParentNode.get(0).getChildren();
			for(XMLDocumentNode documentTypeNode:documentTypesNode) {
				documentTypes.add(documentTypeNode.getValue());
			}
		}
		try {
			XMLDocumentNode timeoutNode = configuration.getNode("timeoutMillis", true);
			if (timeoutNode != null) {
				timeoutMillis = Long.parseLong(timeoutNode.getValue());
			}
		} catch (XMLDocumentNodeNotFoundException ignore) {
			// ignore error
		}
		try {
			XMLDocumentNode allowPrintTestNode = configuration.getNode("allowPrintTest", true);
			if (allowPrintTestNode != null) {
				allowPrintTest = allowPrintTestNode.getValueAsBoolean();
			}
		} catch (XMLDocumentNodeNotFoundException ignore) {
			// ignore error
		}
		
		loadPrinterConfiguration(config);		
	}
	
	public boolean isDocumentTypeAvailable(String docTypeCode) {
		return documentTypes.contains(docTypeCode);
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	protected abstract void loadPrinterConfiguration(DeviceConfiguration config);
	
	@Override
	public void printJasper(byte[] rawXmlInput, Map<String, Object> params) throws Exception {
		log.error("printJasper() - Print manager does not support jasper print jobs.");
	}
	
	public void printTest() throws Exception {
		if (!allowPrintTest) return;
		
		log.debug("Realizando prueba de impresión");
		
		Integer size = 0;
		int cols = 42;
		String align = null;
		Integer style = 0;
		Integer fontSize = 0;
		String fontName = null;
		Map<String,Object> datos = new HashMap<>();
		datos.put("line-cols", cols+"");
		
		beginDocument(datos);
		printLogo();
		beginLine(size, cols);
		printText("WELCOME", size, align, style, fontName, fontSize);
		endLine();
		beginLine(size, cols);
		printText("BIENVENIDO/A", size, align, style, fontName, fontSize);
		endLine();
		beginLine(size, cols);
		printText("BENVENUTO/A", size, align, style, fontName, fontSize);
		endLine();
		beginLine(size, cols);
		printText("BIENVENUE", size, align, style, fontName, fontSize);
		endLine();
		beginLine(size, cols);
		printText("BEM-VINDO", size, align, style, fontName, fontSize);
		endLine();
		beginLine(size, cols);
		printText("ONGI ETORRI", size, align, style, fontName, fontSize);
		endLine();
		beginLine(size, cols);
		printText("BENVINGUT", size, align, style, fontName, fontSize);
		endLine();
		endDocument();
	}

	@Override
	public long getTimeoutMillis() {
		return timeoutMillis;
	}
}
