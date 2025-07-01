package com.comerzzia.pos.devices.printer;


import java.io.ByteArrayInputStream;
import java.util.Map;

import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import net.sf.jasperreports.view.JasperViewer;


public class DevicePrinterScreen extends DevicePrinterHTML implements DevicePrinter{      
    private static DevicePrinterScreen instance;

    public DevicePrinterScreen(){
    }
    
    public static DevicePrinterScreen getInstance(){
        if (instance == null){
            instance = new DevicePrinterScreen();
        }
        return instance;
    }
    
    @Override
    public void printJasper(byte[] rawXmlInput, Map<String, Object> params) throws Exception {
    	final JasperViewer view = new JasperViewer(new ByteArrayInputStream(rawXmlInput), false, false);
    	String titulo = I18N.getText("Impresión de informe");
    	if(params.containsKey("titulo")) {
    		titulo = (String) params.get("titulo");
    	}else if( params.containsKey("title")) {
    		titulo = (String) params.get("title");
    	}
    	view.setTitle(titulo);
    	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			view.setVisible(true);
    		}
    	});
    }
}
