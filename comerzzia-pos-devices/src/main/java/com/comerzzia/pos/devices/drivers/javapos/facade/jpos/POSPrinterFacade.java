
package com.comerzzia.pos.devices.drivers.javapos.facade.jpos;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoDeviceException;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoLogicalNameException;
import com.comerzzia.pos.devices.drivers.javapos.facade.listener.PosPrinterEventListener;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;

public class POSPrinterFacade extends CommonDeviceCatFacade {

    private static final Logger log = Logger.getLogger(POSPrinterFacade.class.getName());
	/**
	 * Secuencia de escape que imprime una linea recta
	 */
	public static final byte RECT_LINE_SEQUENCE[] = new byte[] {0x1b, '|', '*','1','4','d','L','p','0',',','3','0','0','d','1','w','1','s','2','c','1'};
	/**
	 * Secuencia de escape que hace que los caracteres se impriman al doble de ancho;
	 */
	public static final byte DOUBLE_WIDTH_SEQUENCE[] = new byte[] {0x1b, '|', '2', 'C' };
	/**
	 * Secuencia de escape que hace que los caracteres se impriman al doble de alto;
	 */
	public static final byte DOUBLE_HEIGHT_SEQUENCE[] = new byte[] {0x1b, '|', '3', 'C' };
	/**
	 * Secuencia de escape que hace que los caracteres se impriman al doble de ancho y de alto;
	 */
	public static final byte DOUBLE_HEIGHT_DOUBLE_WIDTH_SEQUENCE[] = new byte[] {0x1b, '|', '4', 'C' };
	/**
	 * Secuencia de escape que hace que los caracteres se impriman en negrita;
	 */
	public static final byte BOLD_SEQUENCE[] = new byte[] {0x1b, '|', 'b', 'C' };
	/**
	 * Secuencia de escape que hace que los caracteres se impriman subrayados;
	 */
	public static final byte UNDERLINE_SEQUENCE[] = new byte[] {0x1b, '|', 'u', 'C' };
	/**
	 * Secuencia de escape que hace que los caracteres se impriman en cursiva;
	 */
	public static final byte ITALIC_SEQUENCE[] = new byte[] {0x1b, '|', 'i', 'C' };
	/**
	 * Secuencia de escape que hace que la linea se imprima centrada;
	 */
	public static final byte CENTERED_SEQUENCE[] = new byte[] {0x1b, '|', 'c', 'A' };
	/**
	 * Secuencia de escape que hace que la linea se imprima alineada a la derecha;
	 */
	public static final byte RIGHT_JUSTIFY_SEQUENCE[] = new byte[] {0x1b, '|', 'r', 'A' };
	/**
	 * Secuencia de escape que hace que la linea se imprima alineada a la izquierda;
	 */
	public static final byte LEFT_JUSTIFY_SEQUENCE[] = new byte[] {0x1b, '|', 'l', 'A' };

	/**
	 * Instancia del observador al que se le notificaran los eventos que ocurran. Para ver un ejemplo de la estructura de estos observadores, explore la clase ExamplePosPrinterEventListener en el paquete idinfor.jpos.helpers
	 */
	private PosPrinterEventListener eventListener;
	private StringBuilder lineBuffer = new StringBuilder();
	private boolean transactionPrint = true;
	
	/**
	 * Constructor: Instancia un nuevo POSPrinter con nombre logico pasado por parametro
	 * @param logicalName Nombre logico del dispositivo. Se configuro de tal manera en el archivo jpos.xml
	 */
	public POSPrinterFacade(String logicalName){
		super();
		this.setDevice(new POSPrinter());
		this.setLogicalName(logicalName);
	}
	
	/**
	 * Constructor: Reutiliza una instancia POSPrinter pasada como parametro con nombre logico de dispositivo pasado por parametro
	 * @param posPrinter La instancia de tipo POSPrinter a reutilizar
	 * @param logicalName Nombre logico del dispositivo. Se configuro de tal manera en el archivo jpos.xml
	 */
	public POSPrinterFacade(POSPrinter posPrinter, String logicalName){
		super();
		this.setDevice(posPrinter);
		this.setLogicalName(logicalName);
	}
	
	/**
	 * Constructor: Reutiliza una instancia POSPrinter pasada como parametro con nombre logico de dispositivo pasado por parametro
	 * @param posPrinter La instancia de tipo POSPrinter a reutilizar
	 * @param logicalName Nombre logico del dispositivo. Se configuro de tal manera en el archivo jpos.xml
	 * @param eventListener Instancia del observador al que se le notificaran los eventos que ocurran. Para ver un ejemplo de la estructura de estos observadores, explore la clase ExamplePosPrinterEventListener en el paquete idinfor.jpos.helpers
	 */
	public POSPrinterFacade(POSPrinter posPrinter, String logicalName, PosPrinterEventListener eventListener){
		super();
		this.setDevice(posPrinter);
		this.setLogicalName(logicalName);
		setPosPrinterEventListener(eventListener);
	}

	/* (non-Javadoc)
	 * @see idinfor.jpos.CommonDeviceCatFacade#getDevice()
	 */
	@Override
	public POSPrinter getDevice(){
		return (POSPrinter) super.getDevice();
	}
	
	/**
	 * Devuelve el observador al que se le notifican los eventos
	 * @return Instancia del observador al que se le notificaran los eventos que ocurran. Para ver un ejemplo de la estructura de estos observadores, explore la clase ExamplePosPrinterEventListener en el paquete idinfor.jpos.helpers
	 */
	public PosPrinterEventListener getPosPrinterEventListener() {
		return eventListener;
	}

	/**
	 * Inserta el observador al que se le notifican los eventos
	 * @param eventListener Instancia del observador al que se le notificaran los eventos que ocurran. Para ver un ejemplo de la estructura de estos observadores, explore la clase ExamplePosPrinterEventListener en el paquete idinfor.jpos.helpers
	 */
	public void setPosPrinterEventListener(PosPrinterEventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	/* (non-Javadoc)
	 * @see idinfor.jpos.CommonDeviceCatFacade#prepareDevice()
	 */
	@Override
	public void prepareDevice() throws JposException, NoLogicalNameException, NoDeviceException{		
		POSPrinter posPrinter = getDevice();
		
		log.debug("prepareDevice() - open");
		this.open(getLogicalName());
		log.debug("prepareDevice() - claim");
		this.claim(0);
		
		if (posPrinter.getCapPowerReporting() != JposConst.JPOS_PR_NONE) {
			log.debug("prepareDevice() - power notify enabled");
			posPrinter.setPowerNotify(JposConst.JPOS_PN_ENABLED);
		}
		log.debug("prepareDevice() - enable");
		this.enable();		
		
		if(eventListener != null) {
			posPrinter.addDirectIOListener(eventListener);
			posPrinter.addErrorListener(eventListener);
			posPrinter.addOutputCompleteListener(eventListener);
			posPrinter.addStatusUpdateListener(eventListener);
		}
	}
	
	public void setRecLineChars(int recLineChars) throws jpos.JposException {
		getDevice().setRecLineChars(recLineChars);
	}
	
	public void transactionStart(boolean transactionPrint, boolean printInAsyncMode) throws jpos.JposException {
		this.transactionPrint = transactionPrint;

		log.trace("transactionStart() - Limpiando salida");			
		getDevice().clearOutput();
		
		if (transactionPrint) {
			log.trace("transactionStart() - Comenzando transacción");
			getDevice().transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);
		}

		if (getDevice().getAsyncMode() != printInAsyncMode) {
			log.trace("transactionStart() - Cambiando async mode a " + printInAsyncMode);
			getDevice().setAsyncMode(printInAsyncMode);
		}
		log.trace("transactionStart() - Modo asíncrono: " + getDevice().getAsyncMode());
	}
	
	public void transactionStart() throws jpos.JposException {
		transactionStart(true, false);
	}
	
	 public void transactionEnd() throws jpos.JposException {
		 if (this.transactionPrint) {
			 // finalizar transaccion de impresion
			 getDevice().transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);
		 }
    }
	
	 public void printBuffer(String text, List<byte[]> constants) throws JposException {
		 if (constants != null && constants.size() == 0 && StringUtils.isNotBlank(text)) {
			 lineBuffer.append("\u001b|N"); //Estilo normal
		 }
		 if (constants != null) {
			 for(int i = 0; i < constants.size(); i++) {
				 lineBuffer.append(new String(constants.get(i)));
			 }
		 }
		 lineBuffer.append(text);
	 }
	 
	public void endLineBuffer() throws JposException {
		printNormal(lineBuffer.toString());		
		lineBuffer.setLength(0);
	}
	 
	/**
	 * Imprime en modo asincrono un texto pasado por parametro en la estacion pasada por parametro
	 * @param station Constante que indica el tipo de estacion. Para mas informacion sobre que son las estaciones, consulte la guia que se ofrece con esta libreria. Los valores son: POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_S_SLIP 
	 * @param text String a imprimir String a imprimir
	 * @throws JposException Excepcion generada por la librería JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printNormal(int station, String text) throws JposException{
		getDevice().printNormal(station, text);
	}
	
	/**
	 * Imprime en modo sincrono un texto pasado por parametro en la estacion pasada por parametro
	 * @param station Constante que indica el tipo de estacion. Para mas informacion sobre que son las estaciones, consulte la guia que se ofrece con esta libreria. Los valores son: POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_S_SLIP 
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printInmediate(int station, String text) throws JposException{
		getDevice().printImmediate(station, text);
	}
	
	/**
	 * Imprime en modo asincrono un texto pasado por parametro en la estacion Receipt
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printNormal(String text) throws JposException{
		this.printNormal(POSPrinterConst.PTR_S_RECEIPT, text);
	}
	
	/**
	 * Imprime en modo sincrono un texto pasado por parametro en la estacion Receipt
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printInmediate(String text) throws JposException{
		this.printInmediate(POSPrinterConst.PTR_S_RECEIPT, text);
	}
	
	/**
	 * Corta el papel de modo perpendicular al mismo
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void cutPaper() throws JposException{
		getDevice().cutPaper(90);
	}
	
	/**
	 * Imprime un cddigo de barras de la simbologia, altura, anchura, posicion del texto y alineacion pasada por parametro. El texto alfanumerico del codigo de barras debe casar con la simbologia que se utilice. Por ejemplo, si utilizamos EAN-13, debemos asegurarnos que la cadena pasada por parametro tenga una longitud de 13 caracteres.
	 * @param symbology La simbologia del codigo de barras. Estas simbologias pueden encontrarse en la clase POSPrinterConst. Los valores posibles son aquellos que empiezan por POSPrinterConst.PTR_BCS_XXXXX
	 * @param height La altura del codigo de barras
	 * @param width La anchura del codigo de barras
	 * @param alignment Alineacion del codigo de barras. Los valores posibles son POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_RIGHT, POSPrinterConst.PTR_BC_CENTER
	 * @param textPos Posicion de la cadena alfanumerica del codigo de barras en relacion al codigo de barras. Los valores posibles son: POSPrinterConst.PTR_BC_TEXT_NONE, POSPrinterConst.PTR_BC_TEXT_ABOVE, POSPrinterConst.PTR_BC_TEXT_BELOW
	 * @param barcode La cadena alfanumerica que representa la informacion del codigo de barras
	 * @param station Constante que indica el tipo de estacion. Para mas informacion sobre que son las estaciones, consulte la guia que se ofrece con esta libreria. Los valores son: POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_S_SLIP. Si el parametro pasado es null, el valor por defecto es Receipt
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printBarCode(int symbology, int height, int width, int alignment, int textPos, String barcode, Integer station) throws JposException{
		if(station==null){
			station=POSPrinterConst.PTR_S_RECEIPT;
		}
        getDevice().printBarCode(station, barcode, symbology, height, width, alignment, textPos);
	}
	
	/**
	 * Imprime un cddigo de barras de la simbologia pasada por parametro con una anchura de 100, altura de 40, centrado, con la cadena alfanumerica debajo del codigo de barras y en la estacion Receipt. El texto alfanumerico del codigo de barras debe casar con la simbologia que se utilice. Por ejemplo, si utilizamos EAN-13, debemos asegurarnos que la cadena pasada por parametro tenga una longitud de 13 caracteres.
	 * @param barcode La cadena alfanumerica que representa la informacion del codigo de barras
	 * @param symbology La simbologia del codigo de barras. Estas simbologias pueden encontrarse en la clase POSPrinterConst. Los valores posibles son aquellos que empiezan por POSPrinterConst.PTR_BCS_XXXXX
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printBarCode(String barcode, int symbology) throws JposException{
		this.printBarCode(symbology, 100, 100, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_NONE, barcode, POSPrinterConst.PTR_S_RECEIPT);
	}
	
	/**
	 * @param station Constante que indica el tipo de estacion. Para mas informacion sobre que son las estaciones, consulte la guia que se ofrece con esta libreria. Los valores son: POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_S_SLIP 
	 * @param fileName Ruta hasta el archivo de la imagen
	 * @param width Ancho de la imagen
	 * @param alignment Alineacion
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printImage(Integer station, String fileName, int width, int alignment) throws JposException{
		if(station==null){
			station=POSPrinterConst.PTR_S_RECEIPT;
		}
        getDevice().printBitmap(station, fileName, width, alignment);
	}
	
	/**
	 * @param fileName
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printImage(String fileName) throws JposException{
		getDevice().printBitmap(POSPrinterConst.PTR_S_RECEIPT, fileName, 180, POSPrinterConst.PTR_BM_CENTER);
	}
	
	/**
	 * @param station Constante que indica el tipo de estacion. Para mas informacion sobre que son las estaciones, consulte la guia que se ofrece con esta libreria. Los valores son: POSPrinterConst.PTR_S_JOURNAL, POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_S_SLIP
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printLineBreak(int station) throws JposException{
		this.printNormal(station, "\n");
	}
	
	/**
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printLineBreak() throws JposException{
		this.printNormal("\n");
	}
	
	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printCentered(String text) throws JposException{
		String textoCentrado=new String(CENTERED_SEQUENCE)+text;
		printLine(textoCentrado);
	}
	
	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printRightAlign(String text) throws JposException{
		String textoAlineadoDerecha=new String(RIGHT_JUSTIFY_SEQUENCE)+text;
		printLine(textoAlineadoDerecha);
	}
	
	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printLeftAlign(String text) throws JposException{
		String textoAlineadoIzqda=new String(LEFT_JUSTIFY_SEQUENCE)+text;
		printLine(textoAlineadoIzqda);
	}
	
	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printItalic(String text) throws JposException{
		String textoItalic=new String(ITALIC_SEQUENCE)+text;
		printLine(textoItalic);
	}
	
	
	/**
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printMarginBottom() throws JposException {
		String text = "\n\n";
		this.printNormal(text);
	}
	
	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printLine(String text) throws JposException {
		printNormal(text+"\n");
	}

	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printDoubleHeightDoubleWidth(String text) throws JposException {
		String doubleHeightDoubleWidthText=new String(DOUBLE_HEIGHT_DOUBLE_WIDTH_SEQUENCE)+text;
		printLine(doubleHeightDoubleWidthText);
	}

	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printDoubleHeight(String text) throws JposException {
		String doubleHeightText=new String(DOUBLE_HEIGHT_SEQUENCE)+text;
		printLine(doubleHeightText);
	}

	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printDoubleWidth(String text) throws JposException {
		String doubleWidthText = new String(DOUBLE_WIDTH_SEQUENCE)+text;
		printLine(doubleWidthText);
	}

	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printBold(String text) throws JposException {
		String boldText = new String(BOLD_SEQUENCE)+text;
		printLine(boldText);
	}

	/**
	 * @param text String a imprimir
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printUnderlined(String text) throws JposException {
		String underlinedText = new String(UNDERLINE_SEQUENCE)+text;
		printLine(underlinedText);
	}

	/**
	 * @throws JposException Excepcion generada por la libreria JavaPOS. Contiene un codigo que identifica la razon de la misma. Estos codigos pueden encontrarse en la clase JposConst.class dentro de jposversion.jar, paquete jpos.
	 */
	public void printASCIITable() throws JposException{
		for (int i = 256; i< 356; i++){
			printNormal(" "+i+": "+Character.toString((char) i));
		}
	}
	
}
