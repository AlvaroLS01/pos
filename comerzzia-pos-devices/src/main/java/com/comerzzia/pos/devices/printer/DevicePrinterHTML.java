


package com.comerzzia.pos.devices.printer;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.core.io.ClassPathResource;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinterAbstractImpl;
import com.comerzzia.pos.core.gui.MainStageManager;
import com.comerzzia.pos.core.gui.components.documentviewer.DocumentViewerComponent;
import com.comerzzia.pos.core.gui.controllers.StageController;
import com.comerzzia.pos.devices.drivers.direct.DirectPrinter;
import com.comerzzia.pos.devices.drivers.escpos.EscPosDevicePrinter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.application.Platform;
import javafx.stage.Stage;


public class DevicePrinterHTML extends DevicePrinterAbstractImpl implements DevicePrinter{
    
    private static final Logger log = Logger.getLogger(DevicePrinterHTML.class.getName());
    
    private String resultadoImpresion;
    private static DevicePrinterHTML instance;

	private boolean modoPrevisualizacion;

    public DevicePrinterHTML(){
    }
    
    public static DevicePrinterHTML getInstance(){
        if (instance == null){
            instance = new DevicePrinterHTML();
        }
        return instance;
    }

    @Override
    public void initialize() {
        resultadoImpresion = "<html>\n";
        resultadoImpresion += "<meta charset=\"UTF-8\"/>\n";
        resultadoImpresion += "<style>\n";
        resultadoImpresion += "body {\r\n" + 
        		"    font: normal 16px Lucida Console, Courier, monospace;\r\n" +
        		"    line-height: 8px;\r\n" +       
        		"    width: auto;\r\n" +        		
        		"}\n";
        resultadoImpresion += "p.doubleHeight {\r\n" + 
        		"    -webkit-transform: scaleY(2);\r\n" + 
        		"    -moz-transform: scaleY(2);\r\n" + 
        		"}\n";
        
        
        resultadoImpresion += ".center {\r\n" + 
		        "  display: block;\r\n" + 
		        "  margin-left: auto;\r\n" + 
		        "  margin-right: auto;\r\n" + 
		        "  max-width: auto;\r\n" + 
		        "}\n";
        resultadoImpresion += "</style>\n";
        resultadoImpresion += "<body>\n";
    }

    @Override
    public void beginLine(int size, int lineCols) {
        log.trace("empezarLinea()" );
        
        if (size == 0) {
           resultadoImpresion += "<p>\n";
        } else if (size == 1) {
        	resultadoImpresion += "<p class=\"doubleHeight\">\n";        	
        }
    }

    @Override
    public boolean endDocument() {
		log.trace("terminarDocumento()" );
		resultadoImpresion +="</body></html>\n";
    		
		if(!modoPrevisualizacion){
			MainStageManager stageManager = (MainStageManager)CoreContextHolder.getInstance("mainStageManager");
			StageController focusedStageController = stageManager.getFocusedStageController();
			Stage focusedStage = focusedStageController != null? focusedStageController.getStage():stageManager.getStage();
			Platform.runLater(() -> DocumentViewerComponent.createDocumentViewer(resultadoImpresion, focusedStage));
		}
		
    	// Se resetea los parámetros para contemplar los casos en los que se accede desde el cajón o directamente desde la impresión
    	return true;
    }

    @Override
    public void printText(String texto ,Integer size, String align, Integer style, String fontName, int fontSize) {
        log.trace("imprimirTexto()" );
        // Si esta definido size y align debemos de tratar la cadena de salida
        String textoProcesado =null;
        String estilo="";
        
    	if(size != null && align != null) {
    		switch (align) {
                case ALIGN_CENTER: {
                	textoProcesado = DirectPrinter.alignCenter(texto, size);
                	break;
                }
                case ALIGN_RIGHT: {
                	textoProcesado = DirectPrinter.alignRight(texto, size);
                	break;
                }
                default:
                	textoProcesado = DirectPrinter.alignLeft(texto, size);
                	break;
    		}
    	}else{
    		//Alineación normal por comando UPOS
    		if (align != null && !align.isEmpty()) {
    			switch (align) {
        			case ALIGN_CENTER: {
    		            estilo=" text-align: center;";
        				break;
        			}
        			case ALIGN_RIGHT: {
    		            estilo=" text-align: right;";
        				break;
        			}
        			default:
        				break;
    			}
    		}
    	}
    	       
        //APLICAMOS TAMAÑO             
//        if (fontSize == DevicePrinter.SIZE_0) {
//        	estilo+=" font-size:14px;";
//        }
//        else if (fontSize == DevicePrinter.SIZE_1) {
//        	estilo+=" font-size:16px;";
//        }
//        else if (fontSize == DevicePrinter.SIZE_2) {
//        	estilo+=" font-size:18px;";
//        }
//        else if (fontSize == DevicePrinter.SIZE_3) {
//        	estilo+=" font-size:20px;";
//        }
//        else 
        	if (fontSize == EscPosDevicePrinter.SIZE_CONDENSED) {
        	estilo+=" font-size:11px;";
        }
        
        if (textoProcesado == null){ // En caso de que no venga el parametro size o la longitud del texto sea igual a size.
            textoProcesado = texto;
        }
        if (StringUtils.isEmpty(textoProcesado)) {
           textoProcesado = "&nbsp;";	
        } else {
        	textoProcesado = textoProcesado.replace(" ", "&nbsp;");
        }
        
        if (!estilo.isEmpty()) {
           estilo = " style=\"" + estilo + "\"";
        }
        
        if ((style & EscPosDevicePrinter.STYLE_BOLD) != 0) {
     	   textoProcesado = "<b>" + textoProcesado + "</b>";
        }
        
        resultadoImpresion += "<span" + estilo + ">" + textoProcesado + "</span>";
    }
    
    @Override
    public void endLine() {
    	resultadoImpresion += "</p>\n";
    }     

    @Override
    public void beginDocument(Map<String,Object> datos) {
    }

    public String getPrevisualizacion() {
        return resultadoImpresion;
    }
    
    @Override
    public void close() {
    }

    @Override
    public void printBarcode(String codigoBarras, String tipo, String alineacion, int tipoLeyendaNumericaCodBar, int height) {
    	final int dpi = 200;
    	
    	if ("QR".equals(tipo)) {
    		 QRCodeWriter barcodeWriter = new QRCodeWriter();
    		    try {
					BitMatrix bitMatrix = 
					  barcodeWriter.encode(codigoBarras, BarcodeFormat.QR_CODE, 200, 200);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
					
					String imgString = Base64.encodeBase64String(out.toByteArray());
		    	    
		    	    resultadoImpresion += "<img\r\n" + 
		    	    		"draggable=\"false\"\r\n" +
		    	    		"src=\"data:image/png;base64," + imgString+ "\"\r\n" + 
		    	    		"alt=\"" + codigoBarras + "\"\r\n" + 
		    	    		"class=\"center\"\r\n/>";	
		    	    
				} catch (Exception e) {
					e.printStackTrace();
				}
    	} else if ("13".equals(tipo)) {
    		EAN13Bean eanBean = new EAN13Bean();
    		
	    	try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	    	    //Set up the canvas provider for monochrome PNG output 
	    	    BitmapCanvasProvider canvas = new BitmapCanvasProvider(
	    	            out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
	
	    	    //Generate the barcode
	    	    eanBean.generateBarcode(canvas, codigoBarras);
	
	    	    //Signal end of generation
	    	    canvas.finish();
	    	    
	    	    String imgString = Base64.encodeBase64String(out.toByteArray());
	    	    
	    	    resultadoImpresion += "<img\r\n" + 
	    	    		"draggable=\"false\"\r\n" +
	    	    		"src=\"data:image/x-png;base64," + imgString+ "\"\r\n" + 
	    	    		"alt=\"" + codigoBarras + "\"\r\n" + 
	    	    		"class=\"center\"\r\n/>";
	    	    
	    	} catch (IOException e) {
				e.printStackTrace();
	    	}    		
    	} else {    		
	    	Code128Bean eanBean = new Code128Bean();
	    	eanBean.setCodeset(Code128Constants.CODESET_B);
	    	
	    	try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	    	    //Set up the canvas provider for monochrome PNG output 
	    	    BitmapCanvasProvider canvas = new BitmapCanvasProvider(
	    	            out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
	
	    	    //Generate the barcode
	    	    eanBean.generateBarcode(canvas, codigoBarras);
	
	    	    //Signal end of generation
	    	    canvas.finish();
	    	    
	    	    String imgString = Base64.encodeBase64String(out.toByteArray());
	    	    
	    	    resultadoImpresion += "<img\r\n" + 
	    	    		"draggable=\"false\"\r\n" +
	    	    		"src=\"data:image/png;base64," + imgString+ "\"\r\n" + 
	    	    		"alt=\"" + codigoBarras + "\"\r\n" + 
	    	    		"class=\"center\"\r\n/>";
	    	    
	    	} catch (IOException e) {
				e.printStackTrace();
	    	}
    	}
    }

    @Override
    public void openCashDrawer() {
    }

    @Override
    public void printLogo() {
    	printLogoFile("logo.png");
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
    	getInstance().initialize();
    }

    @Override
    public void disconnect() {
    	getInstance().close();
    }

    @Override
    public void cutPaper() {
        resultadoImpresion +="<hr/>\n";
    }

	public void setPrevisualizacion(boolean previsualizar) {
		this.modoPrevisualizacion = previsualizar;
	}

	@Override
    public Exception getLastException() {
	    return null;
    }

	@Override
	protected void loadPrinterConfiguration(DeviceConfiguration config) {
	}

	    
    protected void printLogoFile(String fileName) {
    	// find logo.png in working directory
    	File file = new File(fileName);
    	
    	// logo.png not in working directory
    	if (!file.exists()) {
    		// find logo.png in classpath
    		ClassPathResource resource = new ClassPathResource("/reports/doctemplates/" + fileName);
    		
    		if (!resource.exists()) {
    			// no logo
    			return;
    		}
    		
    		try {
				file = resource.getFile();
			} catch (IOException e) {
			}
    	}
    	
    	try (FileInputStream input = new FileInputStream(file)) {
			byte fileContent[] = new byte[(int)file.length()];
            
            // Reads up to certain bytes of data from this input stream into an array of bytes.
			input.read(fileContent);
			
    	    String imgString = Base64.encodeBase64String(fileContent);
    	    
    	    resultadoImpresion += "<img\r\n" + 
    	    		"draggable=\"false\"\r\n" +
    	    		"src=\"data:image/png;base64," + imgString+ "\"\r\n" + 
    	    		"class=\"center\"\r\n/>";
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void printLogo(String logoId) {
		// check if logoId includes extension
		if (logoId.contains(".")) {
			printLogoFile(logoId); 
		} else {
			printLogoFile(logoId + ".png"); // default extension in png
		}
	}
}
