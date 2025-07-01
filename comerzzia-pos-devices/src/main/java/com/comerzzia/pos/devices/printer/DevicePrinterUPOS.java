

package com.comerzzia.pos.devices.printer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinterAbstractImpl;
import com.comerzzia.pos.devices.drivers.direct.CodesDirect;
import com.comerzzia.pos.devices.drivers.direct.DirectPrinter;
import com.comerzzia.pos.devices.drivers.escpos.EscPosDevicePrinter;
import com.comerzzia.pos.devices.drivers.javapos.JavaPosPrinter;
import com.comerzzia.pos.devices.drivers.javapos.JavaPosUtils;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoDeviceException;
import com.comerzzia.pos.devices.drivers.javapos.facade.exceptions.NoLogicalNameException;
import com.comerzzia.pos.devices.drivers.javapos.facade.jpos.POSPrinterFacade;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinterConst;


public class DevicePrinterUPOS extends DevicePrinterAbstractImpl implements DevicePrinter {
    private static final Logger log = Logger.getLogger(DevicePrinterUPOS.class.getName());
    
    public static final String TAG_LOGICAL_NAME = "logicalName";
    public static final String TAG_TRANSACTION_MODE = "transactionMode";
    public static final String TAG_ASYNC_MODE = "asyncMode";

    protected JavaPosPrinter impresora;
    protected String idJavaPOSPrinter;

    protected CodesDirect codigosImpresora;
	
	protected String charsetName;
	
	protected Exception lastException;
	
	protected Map<String, Integer> barcodeTypes = new HashMap<>();
	
	// default print mode transactional
	protected boolean printInTransactionMode = true;
	protected boolean printInAsyncMode = false;

    public DevicePrinterUPOS(){
    	barcodeTypes.put("UPC-A", POSPrinterConst.PTR_BCS_UPCA);
    	barcodeTypes.put("UPC-E", POSPrinterConst.PTR_BCS_UPCE);
    	barcodeTypes.put("EAN13", POSPrinterConst.PTR_BCS_EAN13); barcodeTypes.put("13", POSPrinterConst.PTR_BCS_EAN13);
    	barcodeTypes.put("EAN8", POSPrinterConst.PTR_BCS_EAN8); barcodeTypes.put("8", POSPrinterConst.PTR_BCS_EAN8);
    	barcodeTypes.put("CODE39", POSPrinterConst.PTR_BCS_Code39);
    	barcodeTypes.put("ITF", POSPrinterConst.PTR_BCS_ITF);
    	barcodeTypes.put("CODABAR", POSPrinterConst.PTR_BCS_Codabar);
    	barcodeTypes.put("CODE93", POSPrinterConst.PTR_BCS_Code93);
    	barcodeTypes.put("CODE128", POSPrinterConst.PTR_BCS_Code128_Parsed); barcodeTypes.put("128", POSPrinterConst.PTR_BCS_Code128_Parsed);
    	barcodeTypes.put("GS1-128", POSPrinterConst.PTR_BCS_GS1DATABAR); 
    	barcodeTypes.put("QR", POSPrinterConst.PTR_BCS_QRCODE);	
    }
    
    @Override
	public void loadPrinterConfiguration(DeviceConfiguration config) {
		idJavaPOSPrinter = config.getModelConfiguration().getConnectionName();		
		
        // Leemos el XML de configuración de la impresora
        XMLDocumentNode configConexion = config.getModelConfiguration().getConnectionConfig();
        try {
        	XMLDocumentNode logicalNameNode = configConexion.getNode(TAG_LOGICAL_NAME, true);
            if (logicalNameNode != null) {
            	idJavaPOSPrinter = logicalNameNode.getValue();
            }
            
            XMLDocumentNode transactionModeNode = configConexion.getNode(TAG_TRANSACTION_MODE, true);
            if (transactionModeNode != null) {
            	printInTransactionMode = transactionModeNode.getValueAsBoolean();
            }
            
            XMLDocumentNode asyncModeNode = configConexion.getNode(TAG_ASYNC_MODE, true);
            if (asyncModeNode != null) {
            	printInAsyncMode = asyncModeNode.getValueAsBoolean();
            }
        }
        catch (XMLDocumentNodeNotFoundException ex) {
            log.error("configurar()- Error obteniendo configuracion de la impresora: " + ex.getMessage(), ex);
        }
	}

    @Override
    public void initialize() {
    	log.debug("inicializar()");
        impresora = JavaPosPrinter.getImpresora();

        try {
        	codigosImpresora = new CodesDirect();
        	
            codigosImpresora.configure(getConfiguration().getModelConfiguration().getModelConfiguration());
        	
        	impresora.inicializar(idJavaPOSPrinter);
        }
        catch (JposException ex) {
            log.error("inicializar() - Error inicializando impresora jpos: " + ex.getMessage(), ex);
        }
        catch (NoLogicalNameException ex) {
            log.error("inicializar() - No se encontró la configuración de impresora con nombre " + idJavaPOSPrinter, ex);
        }
        catch (NoDeviceException ex) {
            log.error("inicializar() - No se encontró la impresora con nombre " + idJavaPOSPrinter, ex);
        }
        catch (Throwable e) {
        	log.error("inicializar() - Ha habido un error al inicializar el dispositivo: " + e.getMessage(), e);
        }
    }

    @Override
    public void beginLine(int size, int lineCols) {
       try {
    	   if (impresora.getDevice().getRecLineChars() != lineCols) {
    		   log.trace("empezarLinea() - lineCols: " + lineCols);
    		   impresora.setRecLineChars(lineCols);
    	   }
		} catch (JposException e) {
			log.error("empezarLinea() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			lastException = e;
		}
    }

    @Override
    public boolean endDocument() {
        log.trace("terminarDocumento()");
        try {
			impresora.transactionEnd();
			
			if(lastException != null) {
				return false;
			}
			
			return true;
		} catch (JposException e) {
			log.error("terminarDocumento() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			return false;
		}
    }

    @Override
    public void printText(String texto, Integer size, String align, Integer style, String fontName, int fontSize) {
    	if (log.isTraceEnabled()) {
    		log.trace("imprimirTexto() - texto: " + texto + ", size: " + size + ", align: " + align + ", style: " + style + ", fontName: " + fontName);
    	}
        try {
        	List<byte[]> constants = new LinkedList<byte[]>();
        	
        	//Si tenemos size y align, alineamos con espacios
        	if(size != null && align != null) {
        		switch (align) {
	                case ALIGN_CENTER: {
	                	texto = DirectPrinter.alignCenter(texto, size);
	                	break;
	                }
	                case ALIGN_RIGHT: {
	                	texto = DirectPrinter.alignRight(texto, size);
	                	break;
	                }
	                default:
	                	texto = DirectPrinter.alignLeft(texto, size);
	                	break;
        		}
        	}else{
        		//Alineación normal por comando UPOS
        		if (align != null && !align.isEmpty()) {
        			switch (align) {
	        			case ALIGN_CENTER: {
	        				constants.add(POSPrinterFacade.CENTERED_SEQUENCE);
	        				break;
	        			}
	        			case ALIGN_RIGHT: {
	        				constants.add(POSPrinterFacade.RIGHT_JUSTIFY_SEQUENCE);
	        				break;
	        			}
	        			default:
	        				constants.add(POSPrinterFacade.LEFT_JUSTIFY_SEQUENCE);
	        				break;
        			}
        		}
        	}
        	
            if ((style & EscPosDevicePrinter.STYLE_BOLD) != 0) {
            	constants.add(POSPrinterFacade.BOLD_SEQUENCE);
            }
            if ((style & EscPosDevicePrinter.STYLE_UNDERLINE) != 0) {
                constants.add(POSPrinterFacade.UNDERLINE_SEQUENCE);
            }
            
            if (fontSize == 1) {
            	constants.add(POSPrinterFacade.DOUBLE_HEIGHT_SEQUENCE);
            } else if(fontSize == 2) {
            	constants.add(POSPrinterFacade.DOUBLE_WIDTH_SEQUENCE);
            } else if(fontSize == 3) {
            	constants.add(POSPrinterFacade.DOUBLE_HEIGHT_DOUBLE_WIDTH_SEQUENCE);
            }

            impresora.printBuffer(texto, constants);
        }
        catch (JposException ex) {
            log.error("Error imprimiendo texto : " + texto, ex);
            lastException = ex;
        }
    }

    @Override
    public void endLine() {
    	try {
    		log.trace("terminarLinea()");
    		impresora.printBuffer("\n", null);
			impresora.endLineBuffer();
		} catch (JposException e) {
			log.error("terminarLinea() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			lastException = e;
		}
    }

    @Override
    public void beginDocument(Map<String,Object> parametros) {
		try {
			lastException = null;
			
			log.trace("empezarDocumento() - line-cols: " + parametros.get("line-cols"));
    		impresora.setRecLineChars(Integer.valueOf((String) parametros.get("line-cols")));
			
			Object charset = parametros.get("charset");
            charsetName = charset != null? charset.toString() : "Windows-1252";
            
            log.trace("printInTransactionMode = " + printInTransactionMode);
            log.trace("printInAsyncMode = " + printInAsyncMode);
           	impresora.transactionStart(printInTransactionMode, printInAsyncMode);
		} catch (Exception e) {
			log.error("empezarDocumento() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			lastException = e;
		}
    }

    @Override
    public void close() {
    }

    @Override
    public void printBarcode(String codigoBarras, String tipo, String alineacion, int tipoLeyendaNumericaCodBar, int height) {
    	log.trace("imprimirCodigoBarras() - codigoBarras: " + codigoBarras + ", tipo: " + tipo + ", alineacion: " + alineacion + ", tipoLeyendaNumericaCodBar: " + tipoLeyendaNumericaCodBar + ", height: " + height);
    	    	
        try {
        	if(height == 0) {
        		height = 60;
        	}
        	
        	int JPOSAlign = POSPrinterConst.PTR_BC_CENTER;
        	
            // Alineación del código de barras
            if (alineacion != null && !alineacion.isEmpty()) {
                switch (alineacion) {
                    case ("left"):
                    	JPOSAlign = POSPrinterConst.PTR_BC_LEFT;
                        break;
                    case ("right"):
                    	JPOSAlign = POSPrinterConst.PTR_BC_RIGHT;                    	
                        break;
                    default:
                    	JPOSAlign = POSPrinterConst.PTR_BC_CENTER;
                        break;
                }
            }        	
            
            // Texto del codigo de barras, por defecto, desactivado
            int JPOSTextPosition = POSPrinterConst.PTR_BC_TEXT_NONE;
            
            if (tipoLeyendaNumericaCodBar != 0) {
                switch (tipoLeyendaNumericaCodBar) {
                    case (1):
                    	JPOSTextPosition = POSPrinterConst.PTR_BC_TEXT_ABOVE;                    	
                        break;
                    case (2):
                    	JPOSTextPosition = POSPrinterConst.PTR_BC_TEXT_BELOW;                    	                	
                        break;
                    default:
                    	JPOSTextPosition = POSPrinterConst.PTR_BC_TEXT_NONE;
                        break;
                }
            }             
            
            // default barcode type
            int barcodeType = POSPrinterConst.PTR_BCS_Code128_Parsed;
            
            if (barcodeTypes.containsKey(tipo)) {
            	barcodeType = barcodeTypes.get(tipo);
            }        	
            
            Integer width = 100;
			if (barcodeType == POSPrinterConst.PTR_BCS_QRCODE) {
				width = height;
			}

       		impresora.printBarCode(barcodeType, height, width, JPOSAlign, JPOSTextPosition, codigoBarras, POSPrinterConst.PTR_S_RECEIPT);
        }
        catch (JposException ex) {
            log.error("Error imprimiendo Código de Barras : " + codigoBarras, ex);
            lastException = ex;
        }
    }
    
    @Override
    public void openCashDrawer() {
    	Devices.openCashDrawer();
    }

    @Override
    public void printLogo() {
    	printLogo("logo.bmp");
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
    public void connect() throws DeviceException {
    	log.debug("connect()");
        initialize();
        
        if (!isReady()) {
        	throw new DeviceException(getAvailabilityErrorsAsAstring());
        }        
    }

    @Override
    public void disconnect() {
        try {
        	log.debug("disconnect() - Desconectando impresora");
            impresora.finishDevice();
        }
        catch (Exception ex) {
            log.error("Error desconectando impresora: " + ex.getMessage() , ex);
        }        
    }

    @Override
    public void cutPaper() {
    	log.trace("cortarPapel()");
        try {
            for (int i = 0; i <= codigosImpresora.getBottomMargin(); i++) {
        		impresora.printNormal("\n");
            }
            
            impresora.cutPaper();
        }
        catch (JposException e) {
        	log.error("cortarPapel() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
        	lastException = e;
        }
    }
    
    @Override
    public boolean isReady() {
    	boolean ready = false;
    	try {
    		ready = getAvailabilityErrors().isEmpty();
    	} catch(Exception ignore) {  		
    	}
    	
    	return ready;
    }
    
    @Override
    public List<String> getAvailabilityErrors() {
    	List<String> errors = JavaPosUtils.getAvailabilityErrors(impresora.getDevice());
    	        	
    	// estado de corriente
    	try {
	    	if ((impresora.getDevice().getCapPowerReporting() != JposConst.JPOS_PR_NONE &&
	    		 impresora.getDevice().getPowerState() != JposConst.JPOS_PS_UNKNOWN) 
	    		&&
		    	(impresora.getDevice().getPowerState() != JposConst.JPOS_PS_ONLINE)) {
	    		log.error("getAvailabilityErrors() - Dispositivo apagado: " + impresora.getDevice().getPowerState());
	    		errors.add(I18N.getText("Dispositivo apagado."));
    		}
    	}
    	catch(Exception ignore) {
    		log.error("getAvailabilityErrors() - impresora.getDevice().getState(): " + ignore.getMessage());
    		errors.add(I18N.getText("Error obteniendo estado de corriente del dispositivo"));
    	}
    	
    	// estado de la impresora
    	try {
    		int status = impresora.getDevice().getState();
    		
    		if (status != JposConst.JPOS_S_IDLE) {
	    		log.error("getAvailabilityErrors() - impresora.getDevice().getState() != JposConst.JPOS_S_IDLE");
	    		
	    		switch (status) {
	    		case JposConst.JPOS_S_CLOSED:
	    			errors.add(I18N.getText("La impresora no se ha inicializado correctamente."));
	    			break;
	    		case JposConst.JPOS_S_BUSY:
	    			errors.add(I18N.getText("La impresora está ocupada."));
	    			break;
	    		case JposConst.JPOS_S_ERROR:
	    			errors.add(I18N.getText("La impresora está en un estado erróneo."));
	    			break;
	    		default:
	    			errors.add(I18N.getText("El estado de la impresora es desconocido."));
	    			break;
	    		}
    		}
    	}
    	catch(Exception ignore) {
    		log.error("getAvailabilityErrors() - impresora.getDevice().getState(): " + ignore.getMessage());
    		errors.add(I18N.getText("Error obteniendo estado del dispositivo"));
    	}
    	
    	// estado de salud (solo en modo sincrono)    	
    	try {
    		if (!impresora.getDevice().getAsyncMode()) {
    			impresora.getDevice().checkHealth(JposConst.JPOS_CH_INTERNAL);
    		}
    	}
    	catch(JposException ignore) {
    		String checkHealthText = "Desconocido";
    		try {
    			checkHealthText = impresora.getDevice().getCheckHealthText();
        	}
        	catch (JposException ignore1) {
        		// no se pudo obtener el error
        	}
    		log.error("getAvailabilityErrors() - impresora.getDevice().checkHealth(JposConst.JPOS_CH_INTERNAL): " + checkHealthText, ignore);
    		errors.add(I18N.getText("La impresora no ha pasado el checkeo de salud."));
    	}    	
    	
    	// sin papel
    	try {
	    	if(impresora.getDevice().getRecEmpty()) {
	    		log.error("getAvailabilityErrors() - impresora.getDevice().getRecEmpty()");
	    		errors.add(I18N.getText("No queda papel en la impresora."));
	    	}
    	}
    	catch(Exception ignore) {
    		log.error("getAvailabilityErrors() - impresora.getDevice().getRecEmpty(): " + ignore.getMessage());
    	}
    	
    	// tapa abierta
    	try {
	    	if(impresora.getDevice().getCoverOpen()) {
	    		log.error("getAvailabilityErrors() - impresora.getDevice().getCoverOpen()");
	    		errors.add(I18N.getText("La tapa de la impresora está abierta."));
	    	}
    	}
    	catch(Exception ignore) {
    		log.error("getAvailabilityErrors() - impresora.getDevice().getCoverOpen(): " + ignore.getMessage());
    	}
    	
        return errors;
    }
    
    public Exception getLastException() {
    	return lastException;
    }

	@Override
	public void printLogo(String logoId) {
		printLogo(logoId, 0, "");
    }
	
	@Override
	public void printLogo(String logoId, Integer width, String alignment) {
		if (StringUtils.isBlank(logoId)) {
			logoId = "logo.bmp";
		}
				
		if (!StringUtils.contains(logoId, ".")) {
		   logoId = logoId + ".bmp";	
		}
		
		int alignmentValue = POSPrinterConst.PTR_BM_CENTER;
		
		if (StringUtils.equalsIgnoreCase("LEFT", alignment)) {
			alignmentValue = POSPrinterConst.PTR_BM_LEFT;			
		} else if (StringUtils.equalsIgnoreCase("RIGHT", alignment)) {
			alignmentValue = POSPrinterConst.PTR_BM_RIGHT;		
		}
		
		if (width == null || width == 0) {
		   width = POSPrinterConst.PTR_BM_ASIS; // One pixel per printer dot
		}
		
    	try {
			impresora.printImage(null, logoId, width, alignmentValue);
		} catch (JposException e) {
			log.error("imprimirLogo() - " + logoId + ": "+ e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			lastException = e;
		}
	}
    
}
