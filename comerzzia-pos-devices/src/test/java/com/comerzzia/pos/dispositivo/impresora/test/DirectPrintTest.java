package com.comerzzia.pos.dispositivo.impresora.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class DirectPrintTest {
	

	public static void main(String[] args) throws PrintException, IOException {
	     String NEW_LINE ="13;10";
	     
	     String BAR_CODE_ALIGN_CENTER = "27;97;1"; 
	     String BAR_HEIGHT = "29;104;96";
	     String BAR_WIDTH ="29;119;2";
	     String BAR_HR_FONT = "29;102;1";
	     String BAR_CODE_128_TEXT_DOWN = "29;72;2";
	     String BAR_CODE_128 = "29;107;73"; 
	     
	    //		 Justo después el código de barras forzando el tipo "B" de code_128, como por ejemplo: {B1911199011000304c9d
	   // 		 Espero que con esto podáis probar la impresión directa por ESC/POS.

		String defaultPrinter = PrintServiceLookup.lookupDefaultPrintService().getName();
		System.out.println("Default printer: " + defaultPrinter);
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();

		// prints the famous hello world! plus a form feed
		ByteArrayOutputStream streamSalida = new ByteArrayOutputStream();
		
		streamSalida.write("hello world!".getBytes("UTF8"));
		ejecutaComando(streamSalida, NEW_LINE);
		ejecutaComando(streamSalida, NEW_LINE);
		
		ejecutaComando(streamSalida, BAR_CODE_ALIGN_CENTER + 
				               ";" + BAR_HEIGHT + 
				               ";" + BAR_WIDTH +
				               ";" + BAR_HR_FONT +
				               ";" + BAR_CODE_128_TEXT_DOWN +
				               ";" + BAR_CODE_128);

		String codigoBarras = "{B1911199011000304c9d";
		
		// Imprimir longitud del texto del código de barras
		streamSalida.write((byte) codigoBarras.getBytes().length);

		// Imprimir el código de barras
		streamSalida.write(codigoBarras.getBytes());

		// nueva linea para finalizar
        ejecutaComando(streamSalida, NEW_LINE);
        ejecutaComando(streamSalida, NEW_LINE);
		
		InputStream is = new ByteArrayInputStream(streamSalida.toByteArray()); 

		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(new Copies(1));

		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(is, flavor, null);
		DocPrintJob job = service.createPrintJob();

		PrintJobWatcher pjw = new PrintJobWatcher(job);
		job.print(doc, pras);
		pjw.waitForDone();
		
		is.close();
		streamSalida.close();
	}
	
    static private void ejecutaComando(ByteArrayOutputStream stream, String comando) {
        try {
            if (comando != null && !comando.isEmpty()) {
                String[] split = comando.split(";");
                for (String parteComando : split) {
                    Integer parteComandoInt = new Integer(parteComando);
                    stream.write((char) parteComandoInt.intValue());
                }
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
            throw e;
        }
    }
}

class PrintJobWatcher {
	boolean done = false;

	PrintJobWatcher(DocPrintJob job) {
		job.addPrintJobListener(new PrintJobAdapter() {
			public void printJobCanceled(PrintJobEvent pje) {
				allDone();
			}

			public void printJobCompleted(PrintJobEvent pje) {
				allDone();
			}

			public void printJobFailed(PrintJobEvent pje) {
				allDone();
			}

			public void printJobNoMoreEvents(PrintJobEvent pje) {
				allDone();
			}

			void allDone() {
				synchronized (PrintJobWatcher.this) {
					done = true;
					System.out.println("Printing done ...");
					PrintJobWatcher.this.notify();
				}
			}
		});
	}

	public synchronized void waitForDone() {
		try {
			while (!done) {
				wait();
			}
		} catch (InterruptedException e) {
		}
	}
}