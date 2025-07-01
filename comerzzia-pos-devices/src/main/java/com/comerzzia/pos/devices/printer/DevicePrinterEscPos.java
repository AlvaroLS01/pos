

package com.comerzzia.pos.devices.printer;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;

import org.apache.commons.lang3.StringUtils;

import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinterAbstractImpl;
import com.comerzzia.pos.devices.drivers.direct.CodesDirect;
import com.comerzzia.pos.devices.drivers.direct.DirectPrinter;
import com.comerzzia.pos.devices.drivers.escpos.EscPosDevicePrinter;
import com.comerzzia.pos.devices.drivers.escpos.escpos.ESCPOS;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;


public class DevicePrinterEscPos extends DevicePrinterAbstractImpl implements DevicePrinter {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DevicePrinterEscPos.class.getName());
   
	public static final String TAG_CT_DRIVER_PRINTERNAME = "printerName";
	public static final String DEFAULT_PRINTER_NAME = "DEFECTO";
	
    private String nombreImpresora = null;
    protected Map<String,Object> parametros = new HashMap<>();
    
    protected CodesDirect codigosImpresora;
    protected ByteArrayOutputStream streamOut;
    
    protected Map<String, String> barcodeTypes = new HashMap<>();
    

    public DevicePrinterEscPos() {
    	barcodeTypes.put("UPC-A", "65");
    	barcodeTypes.put("UPC-E", "66");
    	barcodeTypes.put("EAN13", "67"); barcodeTypes.put("13", "67");
    	barcodeTypes.put("EAN8", "68"); barcodeTypes.put("8", "68");
    	barcodeTypes.put("CODE39", "69");
    	barcodeTypes.put("ITF", "70");
    	barcodeTypes.put("CODABAR", "71");
    	barcodeTypes.put("CODE93", "72");
    	barcodeTypes.put("CODE128", "73"); barcodeTypes.put("128", "73");
    	barcodeTypes.put("GS1-128", "74");    	
    }


    @Override
	public void loadPrinterConfiguration(DeviceConfiguration config) {
        // Leemos el XML de configuración de la impresora
        XMLDocumentNode configConexion = config.getModelConfiguration().getConnectionConfig();
        try {
            XMLDocumentNode nombreImpresoraNode = configConexion.getNode(TAG_CT_DRIVER_PRINTERNAME, true);
            if (nombreImpresoraNode!=null){
                nombreImpresora = nombreImpresoraNode.getValue();
            }
        }
        catch (XMLDocumentNodeNotFoundException ex) {
            log.error("configurar()- Error obteniendo valor del nombre e la impresora");
        }
        
        if (nombreImpresora == null){
            nombreImpresora = DEFAULT_PRINTER_NAME;
        } 
	}

	public void establecerComandos() {
        codigosImpresora = new CodesDirect();
        codigosImpresora.configure(getConfiguration().getModelConfiguration().getModelConfiguration());
    }
    
    @Override
    public void initialize() {
        streamOut = new ByteArrayOutputStream();
        establecerComandos();
        ejecutaComando(codigosImpresora.getCodes().get(CodesDirect.ENCODING));
    }

    @Override
    public void close() {

    }

    @Override
    public void beginLine(int size, int lineCols) {
        try {
            //Comando de tamaño de línea 
            ejecutaComando(codigosImpresora.getCodes().get(CodesDirect.SELECT_PRINTER));

            if (size == EscPosDevicePrinter.SIZE_0) {
                ejecutaComando(codigosImpresora.getSize0());
            }
            else if (size == EscPosDevicePrinter.SIZE_1) {
                ejecutaComando(codigosImpresora.getSize1());
            }
            else if (size == EscPosDevicePrinter.SIZE_2) {
                ejecutaComando(codigosImpresora.getSize2());
            }
            else if (size == EscPosDevicePrinter.SIZE_3) {
                ejecutaComando(codigosImpresora.getSize3());
            }
            else if (size == EscPosDevicePrinter.SIZE_CONDENSED) {
                ejecutaComando(codigosImpresora.getSizeCondensed());
            }
            else {
                ejecutaComando(codigosImpresora.getSize0());
            }

            // Margen izquierdo en caracteres. No influye n imágenes o códigos de barra
            for (int i = 0; i <= codigosImpresora.getLeftMarginChars(); i++) {
                streamOut.write(" ".getBytes());
            }
            
        }
        catch (IOException ex) {
            log.debug("imprimirTexto() - error imprimiendo línea", ex);
        }
    }

    @Override
    public void endLine() {
        ejecutaComando(codigosImpresora.getNewLine());
    }

    @Override
    public void beginDocument(Map<String,Object> datos) {
    	parametros = datos;
    }

    @Override
    public boolean endDocument() {
    	if(parametros.get("procesador")!=null && parametros.get("procesador").toString().toUpperCase().equals("JASPER")){
    		log.warn("La impresión de JASPER desde xml no mantiene soporte en la versión actual de comerzzia");
    		return false;
    	}
    	else{
	        try {
	            // imprimimos
	            log.debug("Imprimiendo ...");
                Object charset = parametros.get("charset");
                String charsetName = charset != null? charset.toString() : "Windows-1252";
                log.trace(streamOut.toString(charsetName));
	            DirectPrinter.printDocument(streamOut, nombreImpresora, charsetName);
	            
	            return true;
	        }
	        catch (Exception ex) {
	            log.debug("imprimirTexto() - Error imprimiendo documento: "+ ex.getMessage(), ex);
	            return false;
	        }
	    }
	        
    }

    @Override
    public void printText(String texto, Integer size, String align, Integer style, String fontName, int fontSize) {
        try {
            String textoAlineado = "";

            // ALINEACIÓN
            if (align != null && !align.isEmpty()) {
                switch (align) {
                    case ALIGN_CENTER: {
                    	if (size == null){
                    		log.error("imprimirTexto() - Argumento inválido: Alineamiento center con tamaño nulo. Utilizamos alineación izquierda");    	
                    	}
                    	else{
                    		textoAlineado = DirectPrinter.alignCenter(texto, size);                    		
                    	}
                    	break;
                    }
                    case ALIGN_RIGHT: {
                    	if (size == null){
                    		log.error("imprimirTexto() - Argumento inválido: Alineamiento center con tamaño nulo. Utilizamos alineación izquierda");
                    	}     
                    	else{
                    		textoAlineado = DirectPrinter.alignRight(texto, size);
                    	}
                        break;
                    }
                }
            }
            // En caso es que la alineación no haya sido centrada o derecha pero establezcamos un tamaño del texto
            if (textoAlineado.isEmpty()) {
                if (size != null && size.intValue() > 0) {
                    textoAlineado = DirectPrinter.alignLeft(texto, size);
                }
                else {
                    textoAlineado = texto;
                }
            }

            // APLICAMOS ESTILO
            streamOut.write(ESCPOS.SELECT_PRINTER);

            if ((style & EscPosDevicePrinter.STYLE_BOLD) != 0) {
                ejecutaComando(codigosImpresora.getBoldSet());
            }
            if ((style & EscPosDevicePrinter.STYLE_UNDERLINE) != 0) {
                ejecutaComando(codigosImpresora.getUnderlineSet());
            }

            // APLICAMOS FUENTE
            if (fontName == null || fontName.isEmpty() || fontName.equals("A")) {
                ejecutaComando(codigosImpresora.getFontA());
            }
            else if (fontName.equals("B")) {
                ejecutaComando(codigosImpresora.getFontB());
            }
            else if (fontName.equals("C")) {
                ejecutaComando(codigosImpresora.getFontC());
            }
            else if (fontName.equals("D")) {
                ejecutaComando(codigosImpresora.getFontD());
            }
            else {
                try {
                    Integer idFuente = new Integer(fontName);
                    int idFuenteint = idFuente.intValue();
                    ejecutaComando(codigosImpresora.getFontSelect());
                    streamOut.write((char) idFuenteint);
                }
                catch (Exception e) {
                    log.debug("Fuente configurada de forma errónea en el ticket");
                    ejecutaComando(codigosImpresora.getFontA());
                }
            }

            //APLICAMOS TAMAÑO             
            if (fontSize == EscPosDevicePrinter.SIZE_0) {
                ejecutaComando(codigosImpresora.getSize0());
            }
            else if (fontSize == EscPosDevicePrinter.SIZE_1) {
                ejecutaComando(codigosImpresora.getSize1());
            }
            else if (fontSize == EscPosDevicePrinter.SIZE_2) {
                ejecutaComando(codigosImpresora.getSize2());
            }
            else if (fontSize == EscPosDevicePrinter.SIZE_3) {
                ejecutaComando(codigosImpresora.getSize3());
            }
            else if (fontSize == EscPosDevicePrinter.SIZE_CONDENSED) {
                ejecutaComando(codigosImpresora.getSizeCondensed());
            }
            else {
                ejecutaComando(codigosImpresora.getSize0());
            }

            // ESCRIBIMOS LA LÍNEA
            Object charset = parametros.get("charset");
            String charsetName = charset != null? charset.toString() : "Windows-1252";            
            streamOut.write(textoAlineado.getBytes(charsetName));

            //ELIMINAMOS FUENTE
            ejecutaComando(codigosImpresora.getFontA());

            // ELIMINAMOS TAMAÑO
            ejecutaComando(codigosImpresora.getSize0());

            // ELIMINAMOS ESTILO
            if ((style & EscPosDevicePrinter.STYLE_UNDERLINE) != 0) {
                ejecutaComando(codigosImpresora.getUnderlineReset());
            }
            if ((style & EscPosDevicePrinter.STYLE_BOLD) != 0) {
                ejecutaComando(codigosImpresora.getBoldReset());
            }
        }
        catch (IOException ex) {
            log.debug("imprimirTexto() - error imprimiendo línea");
        }
    }

    @Override
    public void printBarcode(String codigoBarras, String tipo, String alineacion, int tipoLeyendaNumericaCodBar, int height) {
    	if(height == 0) {
    		height = 60;
    	}
    	
        try {
            // Alineación del código de barras
            if (StringUtils.isNotBlank(alineacion)) {
                switch (alineacion) {
                    case ("center"):
                        ejecutaComando(codigosImpresora.getBarCodeAlignCenter());
                        break;
                    case ("left"):
                        ejecutaComando(codigosImpresora.getBarCodeAlignLeft());
                        break;
                    case ("right"):
                        ejecutaComando(codigosImpresora.getBarCodeAlignRight());
                        break;
                    default:
                        ejecutaComando(codigosImpresora.getBarCodeAlignLeft());
                        break;
                }
            }

            if (StringUtils.equals(tipo, "QR")) {
                // Correccion L(48), M(49), Q(50), H(51);       
                
                String qRPrefix =codigosImpresora.getQRPrefix();
                byte[] qrPrefixByte = getBytes(qRPrefix);
                if (qRPrefix!=null){ //Si no se introduce qrPrefix, será porque hemos personalizado el sistema para otro modelo de impresora con preCommand y postCommand
                    // Selección de QR code
                    streamOut.write(encode(qrPrefixByte, new byte[]{0x31, 0x43}, new byte[]{(byte) 4})); // Lo último es un parámetro

                    // Selección de nivel de corrección
                    streamOut.write(encode(qrPrefixByte, new byte[]{0x31, 0x45}, new byte[]{(byte) 50}));

                    // Almacenamiento del QR
                    streamOut.write(encode(qrPrefixByte, new byte[]{0x31, 0x50, 0x30}, codigoBarras.getBytes("Windows-1252")));

                    // Impresión del QR              
                    streamOut.write(encode(qrPrefixByte, new byte[]{0x31, 0x51}, new byte[]{0x30}));
                }
            }
            else {           	
                String posicion = EscPosDevicePrinter.POSITION_NONE; // Sacar a xml

                ejecutaComando(codigosImpresora.getNewLine());

                // Imprimimos la secuencia de selección de código de barras
                ejecutaComando(codigosImpresora.getSelectPrinter());

                ejecutaComando(codigosImpresora.getBarHeight()+ ";" + String.valueOf(height));

                ejecutaComando(codigosImpresora.getBarWidth());

                if (EscPosDevicePrinter.POSITION_NONE.equals(posicion)) {
                    ejecutaComando(codigosImpresora.getBarPositionNone());
                }
                else {
                    ejecutaComando(codigosImpresora.getBarPositionDown());
                }
                ejecutaComando(codigosImpresora.getBarHRFont());

              
                // Configuramos el tipo de numeración que aparecerá en el código de barras
                ejecutaComando(codigosImpresora.getBarCodeNumberCommand()+";"+tipoLeyendaNumericaCodBar);                
                
                // TIPO DE CODIGO DE BARRAS
                String barcodeTypeCode = "73"; // code-128 default
                
                if (barcodeTypes.containsKey(tipo)) {
                	barcodeTypeCode = barcodeTypes.get(tipo);
                }
                
                String barcodePrefix = ""; 
                                
                // code-128 type compatibility
                if (StringUtils.equals(barcodeTypeCode, "73")) {                			
                	if (codigoBarras.indexOf("{") == -1) {
                		barcodePrefix = "{B";
                	}
                }
                                
                String barcodeEncoded = barcodePrefix + codigoBarras;
            	
            	// for compatibility. Delete forced code-128 barcode type from command
            	// <command name="BAR_CODE_LENGHT" value="29;107;73" />
                String barcodeCommand = codigosImpresora.getBarCodeDataLength().replace(";73", "");
                String barcodeType = ";" + barcodeTypeCode;
                              
                ejecutaComando(barcodeCommand + barcodeType);

            	// barcode bytes length
            	streamOut.write((byte) barcodeEncoded.getBytes().length);

                // barcode bytes
                streamOut.write(barcodeEncoded.getBytes());

                // new line
                ejecutaComando(codigosImpresora.getNewLine());
            }

            // Reestablecemos la alineación a la izquierda
            if (!StringUtils.equals(alineacion, "left")) {
               ejecutaComando(codigosImpresora.getBarCodeAlignLeft());
            }
        }
        catch (IOException ex) {
            log.error("imprimirCodigoBarras() - Error imprimiendo código de barras :" + ex.getMessage(), ex);
        }

    }

    @Override
    public void openCashDrawer() {
        ejecutaComando(codigosImpresora.getOpenDrawer());
    }

    @Override
    public void printLogo() {

        ejecutaComando(codigosImpresora.getImageHeader());

    }

    @Override
    public void commandBeginTemplate(String comandoEntradaTexto, int leftMargin) {
        ejecutaComando(comandoEntradaTexto);
        ejecutaComando(codigosImpresora.getLeftMarginCommand());

        // Calculamos nl y nh según el ancho configurado
        int nH = leftMargin / 256;
        int nL = leftMargin % 256;

        ejecutaComando("" + nL + ";" + nH);
    }

    @Override
    public void commandEndTemplate(String comandoSalidaTexto) {
        ejecutaComando(comandoSalidaTexto);
        ejecutaComando(codigosImpresora.getLeftMarginCommand() + "0;0");
    }

    @Override
    public void commandBeginBarcode(String comandoEntradaCodBar) {
        ejecutaComando(comandoEntradaCodBar);
    }

    @Override
    public void commandEndBarcode(String comandoSalidaTexto) {
        ejecutaComando(comandoSalidaTexto);
    }

    @Override
    public void commandBeginLine(String comandoEntradaLinea) {
        ejecutaComando(comandoEntradaLinea);
    }

    @Override
    public void commandEndLine(String comandoEntradaTexto) {
        ejecutaComando(comandoEntradaTexto);
    }

    @Override
    public void commandEndTextElement(String comandoSalidaTexto) {
        ejecutaComando(comandoSalidaTexto);
    }

    @Override
    public void commandEndLineElement(String comandoSalidaTexto) {
        ejecutaComando(comandoSalidaTexto);
    }

    protected void ejecutaComando(String comando) {
        try {
            if (comando != null && !comando.isEmpty()) {
                String[] split = comando.split(";");
                for (String parteComando : split) {
                    Integer parteComandoInt = new Integer(parteComando);
                    streamOut.write((char) parteComandoInt.intValue());
                }
            }
        }
        catch (Exception e) {
            log.error("ejecutaComando()- Error ejecutando comando - " + comando);
        }
    }

    /* Tratamiento de los códigos QR */
    public static byte[] encodeLength(int length) {
        byte highByte = (byte) (length >> 8);
        byte lowByte = (byte) (length & 0xff);
        return new byte[]{lowByte, highByte};
    }

    private static byte[] encode(byte[] prefix, byte[] command, byte[] data) {
        byte[] length = encodeLength(command.length + data.length);
        ByteBuffer bb = ByteBuffer.allocate(2 + prefix.length + command.length + data.length);
        bb.put(prefix);
        bb.put(length);
        bb.put(command);
        bb.put(data);
        return bb.array();
    }
    
    private byte[] getBytes(String encodeBytes){
        String[] split = encodeBytes.split(";");
        byte[] res = new byte[split.length];
        for (int i=0;i<split.length;i++){
            Integer parteComandoInt = new Integer(split[i]);
            res[i] = ((byte) (parteComandoInt.intValue()));
        }
        return res;
    }

    @Override
    public void connect() {
        
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void cutPaper() {
        
        for (int i = 0; i <= codigosImpresora.getBottomMargin(); i++) {
                ejecutaComando(codigosImpresora.getNewLine());
        }
        
        ejecutaComando(codigosImpresora.getCutReceipt());
    }

	@Override
    public Exception getLastException() {
	    return null;
    }
	
	@Override
	public void printJasper(byte[] rawXmlInput, Map<String, Object> params) throws Exception {
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
        printerJob.defaultPage(pageFormat);

        int selectedService = 0;
        AttributeSet attributeSet = new HashPrintServiceAttributeSet(new PrinterName(nombreImpresora, null));


        PrintService[] printService = PrintServiceLookup.lookupPrintServices(null, attributeSet);
        PrintService usePrintService = null;
        
        if(printService.length == 0){
        	usePrintService = PrintServiceLookup.lookupDefaultPrintService();
        }
        else{
        	usePrintService = printService[selectedService];
        }
        
        try {
            printerJob.setPrintService(usePrintService);
        } catch (Exception e) {
        	log.error("imprimirFacturaJasper() - Ha habido un error al imprimir: " + e.getMessage(), e);
        }

        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        printRequestAttributeSet.add(new Copies(1));

        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setPrintServiceAttributeSet(usePrintService.getAttributes());
        configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);
        
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		exporter.setExporterInput(new SimpleExporterInput(new ByteArrayInputStream(rawXmlInput)));
		exporter.setConfiguration(configuration );
		
        exporter.exportReport();
	}
	
	@Override
    public void printLogo(String logoId) {		
		if (StringUtils.isNumeric(logoId)) {
			ejecutaComando(StringUtils.replaceIgnoreCase(codigosImpresora.getPringLogo(), "n", logoId));
		} else {
			log.error("Invalid logo id " + logoId + " for esc/pos printing");
		}
    }

}