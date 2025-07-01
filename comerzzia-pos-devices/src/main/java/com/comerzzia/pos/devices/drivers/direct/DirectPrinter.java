

package com.comerzzia.pos.devices.drivers.direct;

import static com.comerzzia.pos.devices.drivers.escpos.EscPosDeviceTicket.getWhiteString;

import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;


public class DirectPrinter {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DirectPrinter.class.getName());
    
    private static PrintService[] printServices;
    
    static{
    	printServices = PrinterJob.lookupPrintServices();
    }

    /**
     * Imprime el array de bits
     *
     * @param streamOut
     * @throws PrintException
     */
    public static void printDocument(ByteArrayOutputStream streamOut, String nombreImpresora, String charset) throws PrintException, FileNotFoundException {

        log.debug("printDocument()");
        log.debug("file.encoding=" + System.getProperty("file.encoding"));
        log.debug("Default Charset=" + Charset.defaultCharset());


        String cadenaLeida = "";
        try {
            //IMprimimos la cadena leida
            cadenaLeida = streamOut.toString(charset);
            log.trace("CADENA LEIDA:");
            log.trace(cadenaLeida);
        }
        catch (UnsupportedEncodingException ex) {
            log.error("imprimirDocumento() - Encoding no válido");
        }

        InputStream streamIn = null;
        streamIn = new ByteArrayInputStream(streamOut.toByteArray());

        DocFlavor myFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;

        // Creamos el documento
        Doc myDoc = new SimpleDoc(streamIn, myFormat, null);

        // Preparamos los atributos
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new Copies(1));
        //aset.add(MediaSize.ISO.A4); // Útil para impresión de informes
        aset.add(Sides.ONE_SIDED);
        // Localizamos la impresora por defecto
         
        PrintService service = null;
        if (nombreImpresora.equals("DEFECTO")){
             service = PrintServiceLookup.lookupDefaultPrintService();
        }
        else{           
            for (PrintService printService : printServices) {
                if (printService.getName().equals(nombreImpresora)){
                    service = printService;
                }                
            }
        }
        if (service == null){
        	log.error("imprimirDocumento() - No se ha encontrado la impresora con nombre + "+nombreImpresora);
        	throw new PrintException();
        }
        
        //Lista las opciones de impresión disponibles
        DocFlavor[] flavors = service.getSupportedDocFlavors();
        for (DocFlavor flav : flavors) {
            log.trace(flav.toString());
        }
        
        // Creamos el trabajo de impresión
        DocPrintJob job = service.createPrintJob();
        job.print(myDoc, aset);
    }

    /* FUNCIONES AUXILIARES */
    public static String alignLeft(String sLine, int iSize) {
        if (sLine.length() > iSize) {
            return sLine.substring(0, iSize);
        }
        else {
            return sLine + getWhiteString(iSize - sLine.length());
        }
    }

    public static String alignRight(String sLine, int iSize) {
        if (sLine.length() > iSize) {
            return sLine.substring(sLine.length() - iSize);
        }
        else {
            return getWhiteString(iSize - sLine.length()) + sLine;
        }
    }

    public static String alignCenter(String sLine, int iSize) {
        if (sLine.length() > iSize) {
            return alignRight(sLine.substring(0, (sLine.length() + iSize) / 2), iSize);
        }
        else {
            return alignRight(sLine + getWhiteString((iSize - sLine.length()) / 2), iSize);
        }
    }

    public static String alignCenter(String sLine) {
        return alignCenter(sLine, 42);
    }

}
