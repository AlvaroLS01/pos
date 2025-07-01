package com.comerzzia.pos.devices.printer;

import java.io.File;
import java.util.HashMap;

import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.pos.core.devices.device.DeviceException;

public interface PrintService {	
	public static final String LIST = "LISTA-JASPER";
	public static final String TEMPLATES_JASPER_PATH = File.separator + "POS" + File.separator;
	public static final String TEMPLATES_PATH = "doctemplates" + File.separator;
	public static final String TEMPLATE_JASPER_EXTENSION = ".jasper";

	String printScreen(String documentUid, PrintDocumentRequest request) throws DeviceException;
	
	void printJasper(String template, HashMap<String, Object> params) throws DeviceException;

	void printDocument(String documentUid, String docTypeCode, PrintDocumentRequest request) throws DeviceException;

	PrintDocumentRequest getDefaultPrintSettings(String docTypeCode);

	void print(PrintDocumentRequest printRequest, byte[] rawXmlInput, String docTypeCode) throws DeviceException;
		
}
