


package com.comerzzia.pos.core.devices.device.printer;

import java.util.Map;

import org.springframework.util.MimeTypeUtils;

import com.comerzzia.pos.core.devices.device.Device;

public interface DevicePrinter extends Device{
	
	public static String MIME_TYPE_JASPERPRINT = "application/jasperprint";
	public static String MIME_TYPE_PDF = "application/pdf";
	public static String MIME_TYPE_XML = MimeTypeUtils.TEXT_XML_VALUE;
	public static String MIME_TYPE_HTML = MimeTypeUtils.TEXT_HTML_VALUE;
    
    public static String ALIGN_LEFT = "left";
    public static String ALIGN_CENTER = "center";
    public static String ALIGN_RIGHT = "right";

    public void initialize();

    public void close();
    
    
    /**
     * Comienza una nueva línea
     */
    public void beginLine(int size, int lineCols);
    
    /**
     * Termina una línea
     */
    public void endLine();

    /**
     * Comienza la impresión de un documento
     * @param defaultLineCols 
     * @param impresoraDocumento 
     */
    public void beginDocument(Map<String,Object> parametros);

    /**
     * Termina la impresión de un documento
     */
    public boolean endDocument();
    
    /**
     * Imprime un texto
     * @param texto 
     * @param size 
     * @param align 
     */
    public void printText(String texto,Integer size, String align, Integer style, String fontName, int fontSize);    
 
    
    public void printBarcode(String codigoBarras, String tipo, String alineacion, int tipoAlineacionTexto, int height);
    
    /**
     * Abre el cajón
     */
    public void openCashDrawer();
    
    /**
     * Corta el papel
     */
    public void cutPaper();
    
    /**
     * Imprime la imagen de cabecera en el ticket
     */
    public void printLogo();
    
    public void printLogo(String logoId);
    
    default public void printLogo(String logoId, Integer width, String alignment) {
    	printLogo(logoId);
    }
    
    
    public boolean isDocumentTypeAvailable(String docTypeCode);
    
    public String getMimeType();

    /*Comandos de entrada y salida*/
    
    public void commandBeginTemplate(String comandoEntradaTexto, int leftMargin);

    public void commandEndTemplate(String comandoSalidaTexto);

    public void commandBeginBarcode(String comandoEntradaCodBar);

    public void commandEndBarcode(String comandoSalidaTexto);

    public void commandBeginLine(String comandoEntradaLinea);

    public void commandEndLine(String comandoEntradaTexto);

    public void commandEndTextElement(String comandoSalidaTexto);

    public void commandEndLineElement(String comandoSalidaTexto);
    
    public Exception getLastException();
    
    public void printJasper(byte[] rawXmlInput, Map<String, Object> params) throws Exception;
    
    public void printTest() throws Exception;
    
    default long getTimeoutMillis() {
    	return 0;
    }
}
