


package com.comerzzia.pos.core.devices.device.printer;

import java.util.Map;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;


public class DevicePrinterDummy extends DevicePrinterAbstractImpl implements DevicePrinter {

    @Override
    public void connect() {
       
    }

    @Override
    public void disconnect() {
      
    }
    
    @Override
    public void initialize() {
        
    }

    @Override
    public void close() {
        
    }

    @Override
    public void beginLine(int size, int lineCols) {
        
    }

    @Override
    public void endLine() {
        
    }

    @Override
    public void beginDocument(Map<String,Object> parametros) {
        
    }

    @Override
    public boolean endDocument() {
        return true;
    }

    @Override
    public void printText(String texto, Integer size, String align, Integer style, String fontName, int fontSize) {
        
    }

    @Override
    public void printBarcode(String codigoBarras, String tipo, String alineacion, int tipoAlineacionTexto, int height) {
        
    }

    @Override
    public void openCashDrawer() {
        
    }

    @Override
    public void printLogo() {
        
    }

    @Override
    public void commandBeginTemplate(String comandoEntradaTexto, int leftMargin) {
        
    }

    @Override
    public void commandEndTemplate(String comandoSalidaTexto) {
        
    }

    @Override
    public void commandBeginBarcode(String comandoEntradaCodBar) {
        
    }

    @Override
    public void commandEndBarcode(String comandoSalidaTexto) {
        
    }

    @Override
    public void commandBeginLine(String comandoEntradaLinea) {
        
    }

    @Override
    public void commandEndLine(String comandoEntradaTexto) {
        
    }

    @Override
    public void commandEndTextElement(String comandoSalidaTexto) {
        
    }

    @Override
    public void commandEndLineElement(String comandoSalidaTexto) {
        
    }

    @Override
    public void cutPaper() {
        
    }

	@Override
    public Exception getLastException() {
	    return null;
    }

	@Override
	protected void loadPrinterConfiguration(DeviceConfiguration config) {
	}

	@Override
	public void printLogo(String logoId) {
		
	}

}
