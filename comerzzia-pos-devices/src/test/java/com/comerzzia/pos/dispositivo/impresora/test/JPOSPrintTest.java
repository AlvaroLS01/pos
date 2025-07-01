package com.comerzzia.pos.dispositivo.impresora.test;

import java.io.IOException;
import java.util.Arrays;

//StarReceiptTest.java
//This file contains sample code illustrating how to use the POSPrinter class to
//control your Star printer.  The basic printing and status querying mechanisms
//are used here.  For more advanced usage of the POSPrinter class, see the 
//JavaPOS specification.

//usage instructions - Windows
//1. add current directory to the CLASSPATH = set CLASSPATH=%CLASSPATH%;.
//2. compile from command line - javac StarReceiptTest.java
//3. execute from command line - java StarReceiptTest

//usage instructions - Linux
//1. compile from command line - javac -classpath jpos191-controls.jar:jcl.jar StarReceiptTest.java
//2. execute from command line - java -classpath jpos191-controls.jar:jcl.jar:xercesimpl.jar:xml-apis.jar:commandemulator.jar:stario.jar:starjavapos.jar:. StarReceiptTest

//NOTE: CHANGE THE PRINTER NAME IN THE printer.open STATEMENT BELOW TO MATCH YOUR CONFIGURED DEVICE NAME

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.util.JposPropertiesConst;

public class JPOSPrintTest
{
    private static String EURO     = ((char) 0x80) + "";
		
	 public static void mainxx(String[] args) throws IOException {
		//String codificacion = "ISO-8859-1";
		String codificacion = "Windows-1252";
		
		//String cadena = new String("Cadena áéí con € euros codificada".getBytes(Charset.forName("Windows-1252")));
		String cadena = new String("€");
		System.out.println("Cadena: " + Arrays.toString(cadena.getBytes()));
		System.out.println("Cadena utf-8: " + Arrays.toString(cadena.getBytes("utf-8")));
		System.out.println("Cadena: " + cadena);
		
		
		String conversion = new String(cadena.getBytes(codificacion), codificacion);
		System.out.println("Conversion a " + codificacion + ": " + Arrays.toString(conversion.getBytes(codificacion)));
		System.out.println("Conversion: " + conversion);
		
		//byte[] cp1252 = cadena.getBytes("Windows-1252");
		
		//System.out.println("Conversion a 1252: " + Arrays.toString(cp1252));
		//System.out.println("Conversion: " + new String(cp1252, "Windows-1252"));
		System.out.println("------------------");
		System.out.println("------------------");
		
		System.out.println("Simbolo: " + Arrays.toString(EURO.getBytes()));
		System.out.println("Simbolo: " + EURO);
		
 
	 }
	 
	 public static void main(String[] args)
	 {   
     /*
      	If you want to place the jpos.xml file elsewhere on your local file system then uncomment the
         following line and specify the full path to jpos.xml.
      
         If you want to place the jpos.xml file on a webserver for access over the internet then uncomment
         the second System.setProperty line below and specify the full URL to jpos.xml.
     */
     //System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "C:\\vistas\\PRODUCTO\\Comerzzia\\branches\\v4.8\\SolucionTienda\\jpos\\comerzzia-pos-devices\\src\\test\\resources\\jpos.xml");
     System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");
     //System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_URL_PROP_NAME, "file://vistas//PRODUCTO//Comerzzia//branches//v4.8//SolucionTienda//jpos//comerzzia-pos-devices//src//test//resources//jpos.xml");
     //System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_URL_PROP_NAME, "file://jpos.xml");
     
     // constants defined for convience sake (could be inlined)
     String ESC    = ((char) 0x1b) + "";
     String LF     = ((char) 0x0a) + "";
     //String TIPO     = ((char) 0x7B) + "";
     String SPACES = "                                                                      ";
     
     // instantiate a new jpos.POSPrinter object
     POSPrinter printer = new POSPrinter();
     
     try
     {
         // open the printer object according to the entry names defined in jpos.xml
    	 System.out.println("Open...");
         printer.open("Printer7199");

         // claim exclsive usage of the printer object
         System.out.println("claim...");
         printer.claim(1);
         
         // enable the device for input and output
         System.out.println("Enable...");
         printer.setDeviceEnabled(true);
         
         // set map mode to metric - all dimensions specified in 1/100mm units
         System.out.println("Map mode...");
         printer.setMapMode(POSPrinterConst.PTR_MM_METRIC);  // unit = 1/100 mm - i.e. 1 cm = 10 mm = 10 * 100 units
         
         do
         {
             // poll for printer status
             //   a javax.swing based application would be best to both poll for status
             //   AND register for asynchronous StatusUpdateEvent notification
             //   see the JavaPOS specification for details on this
             
             // check if the cover is open
             if (printer.getCoverOpen() == true)
             {
                 System.out.println("printer.getCoverOpen() == true");
                 
                 // cover open so do not attempt printing
                 break;
             }
             
             // check if the printer is out of paper
             if (printer.getRecEmpty() == true)
             {
                 System.out.println("printer.getRecEmpty() == true");
                 
                 // the printer is out of paper so do not attempt printing
                 break;
             }
             
             // being a transaction
             //   transaction mode causes all output to be buffered
             //   once transaction mode is terminated, the buffered data is
             //   outputted to the printer in one shot - increased reliability
             printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);
             
             if (printer.getCapRecBitmap() == true)
             {
                 // print an image file
                 try
                 {
                     printer.printBitmap(POSPrinterConst.PTR_S_RECEIPT, "logo.png", POSPrinterConst.PTR_BM_ASIS, POSPrinterConst.PTR_BM_CENTER);
                 }
                 catch (JposException e)
                 {
                     if (e.getErrorCode () != JposConst.JPOS_E_NOEXIST)
                     {
                         // error other than file not exist - propogate it
                         throw e;
                     }
                     
                     // image file not found - ignore this error & proceed
                 }
             }
             
             // call printNormal repeatedly to generate out receipt
             //   the following JavaPOS-POSPrinter control code sequences are used here
             //   ESC + "|cA"          -> center alignment
             //   ESC + "|4C"          -> double high double wide character printing
             //   ESC + "|bC"          -> bold character printing
             //   ESC + "|uC"          -> underline character printing
             //   ESC + "|rA"          -> right alignment
             
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Star Grocer"     + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|bC" +               "Shizuoka, Japan" + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|bC" +               "054-555-5555"    + LF);

             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|uC" + "Qnty Unit Tx Description" + SPACES.substring(0, printer.getRecLineChars() - "Qnty Unit Tx Description".length()) + LF);
             
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               "   1  830    Soba Noodles"        + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               "   1  180    Daikon Radish"       + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               "   1  350    Shouyu Soy Sauce"    + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               "   1   80    Negi Green Onions"   + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               "   1  100    Wasabi Horse Radish" + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               "   2  200 Tx Hashi Chop Sticks"   + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               "   2  200    Prueba " + ((char) 0x80) + "€ áéíóú----"   + LF);             
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);
             
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               ESC + "|rA" +               "Subtotal:  2160" + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               ESC + "|rA" +               "Tax:         24" + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               ESC + "|rA" + ESC + "|bC" + "Total:     2184" + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               ESC + "|rA" +               "Tender:    2200" + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,               ESC + "|rA" + ESC + "|bC" + "Change:      16" + LF);
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);
                  
             String cadena = ((char) 0x80) + "€ áéíóú----"   + LF;
             
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, cadena);
             
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "€" + LF);
             /*
             try {
				String cadena = new String("€"); 
				//String simbolo = new String("€");
				//String conversion = new String(simbolo.getBytes(Charset.forName("Windows-1252")));
				
				String textoCodificado = new String(cadena.getBytes("Windows-1252"), "Windows-1252");
				String textoCodificado2 = new String(cadena.getBytes(), "utf-8");
				
				System.out.println("Conversion a 1252: " + Arrays.toString(textoCodificado2.getBytes())); //"Windows-1252")));
				System.out.println("Conversion bytes: " + Arrays.toString(textoCodificado2.getBytes()));
				
				//System.out.println(Arrays.toString(conversion.getBytes()));
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "->"  + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textoCodificado);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "<-"  + LF);
				
				cadena = new String(EURO.getBytes(), "Windows-1252");
				
				System.out.println("Sinbolo bytes " + Arrays.toString(EURO.getBytes()));
				
	            printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
             
             if (printer.getCapRecBarCode() == true)
             {
            	 /*
                 // print a Code 3 of 9 barcode with the data "123456789012" encoded
                 //   the 10 * 100, 60 * 100 parameters below specify the barcode's height and width
                 //   in the metric map mode (1cm tall, 6cm wide)
            	 System.out.println("Codigo de barras codificacion A 0-9");
                 //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "191108901100000179", POSPrinterConst.PTR_BCS_Code128, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{B!$Aa1234567890", POSPrinterConst.PTR_BCS_Code128_Parsed, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{A12345678901234", POSPrinterConst.PTR_BCS_Code128_Parsed, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 
                 //System.out.println("Codigo de barras localizador codificacion B 0-9-A-z");
                 //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{B1911199011000304c9d", POSPrinterConst.PTR_BCS_Code128_Parsed, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 
                 System.out.println("Codigo de barras codificacion C 0-9");
                 printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C8NZ", POSPrinterConst.PTR_BCS_Code128, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 
                 //System.out.println("Codigo de barras AFRONTA codificacion C 0-9");
                 //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C1911089011000001791234567890", POSPrinterConst.PTR_BCS_Code128_Parsed, 800, 3000, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 
                 //System.out.println("Codigo de barras alfanumerico codificacion A 0-9-A-Z");
                 //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{A191108901100000179AB", POSPrinterConst.PTR_BCS_Code128_Parsed, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 
            	 //System.out.println("Codigo de barras cupon");
                 //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "9500000000807100151234567890", POSPrinterConst.PTR_BCS_Code128, 10 * 100, 80 * 100, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);             
                  */
            	 System.out.println("Codigo de barras codificacion C 0-9");
                 printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C8NZ8NZ8NZ8NZ8NZ", POSPrinterConst.PTR_BCS_Code128, 10 * 100, 60 * 100, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
            	 
            	 /*
            	// print a Code 3 of 9 barcode with the data "123456789012" encoded
                 //   the 10 * 100, 60 * 100 parameters below specify the barcode's height and width
                 //   in the metric map mode (1cm tall, 6cm wide)
            	 System.out.println("Codigo de barras normal");
                 //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "191108901100000179", POSPrinterConst.PTR_BCS_Code128_Parsed, 800, 3000, POSPrinterConst.PTR_BC_LEFT, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C191108901100000179", POSPrinterConst.PTR_BCS_Code128_Parsed, 800, 3000, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                 
            	 System.out.println("Codigo de barras cupon");
                //no funciona 
                //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C95000000008071001521910232210200000000", POSPrinterConst.PTR_BCS_Code128_Parsed, 800, 3000, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                //si funciona
                printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "i|1906170180", POSPrinterConst.PTR_BCS_Code128, 800, 3000, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C190617{Bt018012", POSPrinterConst.PTR_BCS_Code128, 800, 3000, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                //printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C190617{ATT0180", POSPrinterConst.PTR_BCS_Code128, 800, 3000, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "{C1911089011000001791234567890", POSPrinterConst.PTR_BCS_Code128_Parsed, 800, 3000, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
                */
             }
             
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Thank you" + LF);

             // the ESC + "|100fP" control code causes the printer to execute a paper cut
             //   after feeding to the cutter position 
             printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|100fP");

             // terminate the transaction causing all of the above buffered data to be sent to the printer
             printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);             
             
             // exit our printing loop
         } while (false);
     }
     catch(JposException e)
     {
         // display any errors that come up
         e.printStackTrace();
     }
     finally
     {
         // close the printer object
         try
         {
             printer.close();
         }
         catch (Exception e) {}
     }
     
     System.out.println("JPOSPrintTest finished.");
     System.exit(0);
 }
}