
package com.comerzzia.pos.devices.printer;

import java.util.Map;

import org.apache.log4j.Logger;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinterAbstractImpl;
import com.comerzzia.pos.devices.drivers.escpos.EscPosDevicePrinter;
import com.comerzzia.pos.devices.drivers.escpos.EscPosDeviceTicket;
import com.comerzzia.pos.devices.drivers.escpos.EscPosTicketPrinterException;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosCodesEpson;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosDevicePrinterESCPOS;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosPrinterWritterUsb;
import com.comerzzia.pos.devices.drivers.escpos.escpos.EscPosUnicodeTranslatorInt;

public class DevicePrinterSerialPort extends DevicePrinterAbstractImpl implements DevicePrinter {

    private static final Logger log = Logger.getLogger(DevicePrinterSerialPort.class.getName());
    private EscPosDevicePrinterESCPOS impresora;
    private static DevicePrinter instance;

    public DevicePrinterSerialPort(){
    }
    
    public static DevicePrinter getInstance(){
        if (instance == null){
            instance = new DevicePrinterSerialPort();
        }
        return instance;
    }
    
    @Override
	public void loadPrinterConfiguration(DeviceConfiguration config) {
		// TODO: leer parámetros de configuración de puerto y ponerlos en atributos. Por defecto se crea el printer con valores a fuego.
		// Habrá que distinguir también si se trata de USB o puerto serie de alguna manera
	}

    @Override
    public void initialize() {
        try {
            if (impresora == null) {
                impresora = new EscPosDevicePrinterESCPOS(new EscPosPrinterWritterUsb("04B8-0E03-null-null-01,1"), new EscPosCodesEpson(), new EscPosUnicodeTranslatorInt());
            }
        }
        catch (EscPosTicketPrinterException e) {
            log.error("inicializar() - Fallo inicializando impresora: "+e.getMessage());
        }
    }

    @Override
    public void beginLine(int size, int lineCols) {
        impresora.beginLine(size);
    }

    @Override
    public boolean endDocument() {
        impresora.endReceipt();
        return true;
    }

    @Override
    public void printText(String texto, Integer size, String align, Integer style, String fontName, int fontSize) {
        String textoAImprimir;
        if (align == null || size == null) {
            impresora.printText(EscPosDevicePrinter.SIZE_0, EscPosDeviceTicket.alignLeft(texto, texto.length()));
        }
        else {
            if (align.equals("right")) {
                textoAImprimir = EscPosDeviceTicket.alignRight(texto, size);
                impresora.printText(EscPosDevicePrinter.SIZE_0, textoAImprimir);
            }
            if (align.equals("center")) {
                textoAImprimir = EscPosDeviceTicket.alignCenter(texto, size);
                impresora.printText(EscPosDevicePrinter.SIZE_0, textoAImprimir);
            }
            if (align.equals("left")) {
                textoAImprimir = EscPosDeviceTicket.alignLeft(texto, size);
                impresora.printText(EscPosDevicePrinter.SIZE_0, textoAImprimir);
            }
        }
    }

    @Override
    public void endLine() {
        impresora.endLine();
    }

    @Override
    public void beginDocument(Map<String,Object> parametros) {
        impresora.beginReceipt();
    }

    @Override
    public void close() {
        impresora.internalClose();
    }

    @Override
    public void printBarcode(String codigoBarras, String tipo, String alineacion, int tipoLeyendaNumericaCodBar, int height) {
    	if(tipo!=null && tipo.equals(EscPosDevicePrinter.BARCODE_CODE128)){
    		impresora.printBarCode(EscPosDevicePrinter.BARCODE_CODE128, EscPosDevicePrinter.POSITION_NONE, codigoBarras);
    	}
    	else{
    		impresora.printBarCode(EscPosDevicePrinter.BARCODE_EAN13, EscPosDevicePrinter.POSITION_NONE, codigoBarras);
    	}
    }
    
    @Override
    public void openCashDrawer() {        
        impresora.openDrawer();
    }

    @Override
    public void printLogo() {
        // No implementado
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
    public void connect() {
        initialize();
    }

    @Override
    public void disconnect() {

        impresora.internalClose();
        impresora = null;
    }

    @Override
    public void cutPaper() {
        impresora.cutPaper();
    }

	@Override
    public Exception getLastException() {
	    return null;
    }

	@Override
	public void printLogo(String logoId) {
	}
}
