
package com.comerzzia.pos.devices.printer.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.util.i18n.I18N;

import lombok.extern.log4j.Log4j;

/**
 * Imprime documentos en función de una plantilla recibida
 *
 */
@SuppressWarnings("deprecation")
@Log4j
public class PrintParser extends DefaultHandler {
	private static final String ATTRIBUTE_PRINT_LINE = "print";
	
    protected SAXParser parseador = null;
    
    protected boolean imprimirConImpresoraDeterminada = false; //Si es true, imprimirá con la impresora seleccionada y no hará caso a las etiquetas de impresora de la plantilla

    protected DevicePrinter printerFacade;
    protected DevicePrinter impresora1;
    protected DevicePrinter impresora2;
    protected int defaultLineCols = 40;
    protected int currentLineCols = 40;

    protected StringBuffer texto = null;

    //PLANTILLA
    protected int leftMargin;
    protected String bottomMargin;

    //ATRIBUTOS DE LINEA
    protected Integer lineSize; //Tamaño linea
    protected String comandoEntradaLinea;
    protected String comandoSalidaLinea;

    //ATRIBUTOS DE TEXTO    
    protected String align; // alineación del texto. Para poder alinear el texto, deberá estar definido size para la linea
    protected Integer size; // longitud del texto;    
    protected Integer style; //Estilo del texto
    protected String comandoEntradaTexto;
    protected String comandoSalidaTexto;
    protected String fontName;
    protected Integer fontSize;

    //ATRIBUTOS DE CODIGODE BARRAS
    protected String type; //Tipo del código de barras.
    protected String comandoEntradaCodBar;
    protected String comandoSalidaCodBar;
    protected String alignCodBar; //alineamiento del código de barras
    protected int tipoLeyendaNumericaCodBar; //posición de números en código de barras
    protected int height; //altura
    
    protected String logoId;    

    protected Map<String,Object> parameters;
    
    protected Boolean principalPrintLine = true;
    protected Boolean sectionPrintLine;
    protected Boolean elementPrintLine;

    /**
     * Crea una nueva instancia de PlantillaParser
     */
    public PrintParser() {
        impresora1 = Devices.getInstance().getPrinter1();
        impresora2 = Devices.getInstance().getPrinter2();
        impresora1.initialize();
        impresora2.initialize();
    }

    /**
     * Imprime un documento
     *
     * @param plantilla procesada por la aplicación
     * @throws com.comerzzia.pos.devices.printer.parser.PrintParserException
     */
    public void print(byte[] rawXmlInput, DevicePrinter printSelect) throws PrintParserException {
    	if (rawXmlInput == null || rawXmlInput.length == 0) {
    		throw new PrintParserException(I18N.getText("Salida de impresión vacía"));
    	}
    	String plantilla;
		try {
			plantilla = new String(rawXmlInput, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new PrintParserException(e);
		}
    	
        this.printerFacade = printSelect;
        if(printerFacade != null){
        	imprimirConImpresoraDeterminada = true;
        }
        print(new StringReader(plantilla.replace("&", "&amp;").replaceAll("(?<=[^!])[-][-](?=[^>])", "&#45;&#45;"))); //Reemplazamos & por &amp; porque el parseador necesita htmlentities y contemplamos que contenga 2 o mas guiones y los reemplazamos por su codigo ASCII.
        imprimirConImpresoraDeterminada = false;
    }
    
    /**
     * Imprime un documento
     *
     * @param in
     */
    protected void print(Reader in) throws PrintParserException {
        try {
            if (parseador == null) {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                parseador = spf.newSAXParser();
            }
            log.debug("Begin parsing....");
            parseador.parse(new InputSource(in), this);
            log.debug("End parsing....");

        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("print() - Error imprimiendo documento : "+e.getMessage(), e);
            throw new PrintParserException(e);
        }
    }

    /**
     * Procesa el inicio de los tags
     *
     * @param uri
     * @param localName
     * @param tag
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String tag, Attributes attributes) throws SAXException {
    	if (attributes.getValue(ATTRIBUTE_PRINT_LINE) != null) {
    		elementPrintLine = StringUtils.equalsIgnoreCase(attributes.getValue(ATTRIBUTE_PRINT_LINE), "true");
    	} else {
    		if (elementPrintLine == null) {
    			if (sectionPrintLine != null) {
    			    elementPrintLine = sectionPrintLine;	
    			} else {
    			elementPrintLine = principalPrintLine;
    			}
    		}
    	}
        switch (tag) {
	        case "plantilla":
	        case "template":
	        	log.trace("startElement() - case: plantilla");
	            String leftMarginString = attributes.getValue("margin-left");
	            if (leftMarginString != null && !leftMarginString.isEmpty()) {
	                leftMargin = new Integer(leftMarginString);
	            }
	            else {
	                leftMargin = 0;
	            }
	            
	        	if (attributes.getValue(ATTRIBUTE_PRINT_LINE) != null) {
	        		principalPrintLine = elementPrintLine;
	        	}
	            break;
	        case "documento":
	        case "document":
	        	log.trace("startElement() - case: documento");
	        	String impresoraDocumento = attributes.getValue("salida");
	
	        	//En caso de ser null es porque estamos usando la salida por impresora, en otro caso debe ser por pantalla
	        	if(!imprimirConImpresoraDeterminada){
	        		if(impresoraDocumento != null && impresoraDocumento.toUpperCase().equals("IMPRESORA2")){
	            		printerFacade = impresora2;
		            }
		        	else{
		        		printerFacade = impresora1;
		        	}
	        	}                
	            
	            Map<String,Object> datos = new HashMap<String, Object>();
	            datos.putAll(parameters);
	
	            for(int i=0; i< attributes.getLength(); i++){
	            	String valor = attributes.getValue(i);
	            	String nombre = attributes.getQName(i);
	            	
	            	if(valor!=null && !valor.isEmpty() && nombre!=null && !nombre.isEmpty()){
	            		datos.put(nombre, valor);
	            	}
	            }
	            
	            // Establecer el numero de columnas por defecto si llega a nivel de documento
	            if(datos.containsKey("line-cols")) {
	            	defaultLineCols = Integer.parseInt(datos.get("line-cols").toString()); 
	            }
	            
	            datos.put("line-cols", defaultLineCols + "");
	            datos.put("charset", attributes.getValue("charset"));
	
	        	if (attributes.getValue(ATTRIBUTE_PRINT_LINE) != null) {
	        		principalPrintLine = elementPrintLine;
	        	}
	        	
	            printerFacade.beginDocument(datos);
	            break;        
	        case "section":
	        	log.trace("startElement() - case: section");
	            
	        	if (attributes.getValue(ATTRIBUTE_PRINT_LINE) != null) {
	        		sectionPrintLine = elementPrintLine;
	        	}

	            break;
            case "imagen":
            case "image":	            
	        case "logo":
	        	this.logoId = attributes.getValue("id");

	        	String logoWidth = attributes.getValue("width");
                if (StringUtils.isNumeric(logoWidth)) {
                    this.size = Integer.parseInt(logoWidth);
                }
                
                String logoAlign = attributes.getValue("align");
                if (StringUtils.isNotBlank(logoAlign)) {
                    this.align = logoAlign;
                } else {
                	this.align = null;
                }
	        	
	            break;                
            case "codbar":
            case "barcode":
            	log.trace("startElement() - case: codbar");
                texto = new StringBuffer();
                type = attributes.getValue("type");
                String attSizeCodbar = attributes.getValue("size");
                if (attSizeCodbar != null && !attSizeCodbar.isEmpty()) {
                    this.size = Integer.parseInt(attSizeCodbar);
                }
                String numberPositionStr = attributes.getValue("tipo-leyenda");
                if (numberPositionStr != null && !numberPositionStr.isEmpty()) {
                	this.tipoLeyendaNumericaCodBar = Integer.parseInt(numberPositionStr);
                }
                else {
                	this.tipoLeyendaNumericaCodBar = 0;
                }
                String height = attributes.getValue("altura");
                if (height != null && !height.isEmpty()) {
                    this.height = Integer.parseInt(height);
                }
                else {
                    this.height = 0;
                }

                alignCodBar = attributes.getValue("align");
                comandoEntradaCodBar = attributes.getValue("pre-command");
                comandoSalidaCodBar = attributes.getValue("post-command");

                if (elementPrintLine) {
                	printerFacade.commandBeginBarcode(comandoEntradaCodBar);
                }

                break;
            case "linea":
            case "line":
            	log.trace("startElement() - case: linea");
                String attSizeLinea = attributes.getValue("line-size");
                if (attSizeLinea != null && !attSizeLinea.isEmpty()) {
                    this.lineSize = Integer.parseInt(attSizeLinea);
                }
                else {
                    this.lineSize = 0;
                }
                currentLineCols = defaultLineCols;
                String valLineCols = attributes.getValue("line-cols");
                if(valLineCols != null && !valLineCols.isEmpty()) {
                	try {
						currentLineCols = Integer.parseInt(valLineCols);
					} catch (NumberFormatException e) {
					}
                }
                
                if (elementPrintLine) {
	                comandoEntradaLinea = attributes.getValue("pre-command");
	                comandoSalidaLinea = attributes.getValue("post-command");
	                printerFacade.commandBeginLine(comandoEntradaLinea);
	                printerFacade.beginLine(lineSize, currentLineCols);
                }
                break;
            case "texto":
            case "text":
            	log.trace("startElement() - case: texto");
                texto = new StringBuffer();
                this.size = null;
                this.align = null;

                String attSize = attributes.getValue("size");
                if (attSize != null && !attSize.isEmpty()) {
                    this.size = Integer.parseInt(attSize);
                }
                String attAlign = attributes.getValue("align");
                if (attAlign != null && !attAlign.isEmpty()) {
                    this.align = attAlign;
                }
                String attStyle = attributes.getValue("style");
                if (attStyle != null && !attStyle.isEmpty()) {
                    this.style = Integer.parseInt(attStyle);
                }
                else {
                    style = 0;
                }

                // leemos tamaño
                String fontSizeString = attributes.getValue("fontsize");
                if (fontSizeString != null && !fontSizeString.isEmpty()) {
                    fontSize = Integer.parseInt(fontSizeString);
                }
                else {
                    fontSize = lineSize;
                }

                // leemos fuente
                fontName = attributes.getValue("fontname");

                if (elementPrintLine) {
	                comandoEntradaTexto = attributes.getValue("pre-command");
	                comandoSalidaTexto = attributes.getValue("post-command");
	                printerFacade.commandEndLine(comandoEntradaTexto);
                }

                break;
            case "corte":
            case "cut":
            	log.trace("startElement() - case: corte");
                // Ejecutamos al cerrar etiqueta
                break;
            case "apertura-cajon":
            case "open-cashdrawer":
            	log.trace("startElement() - case: apertura-cajon");
                // Ejecutamos al cerrar etiqueta
                break;

        }
    }

    /**
     * Procesa el final de los tags
     *
     * @param uri
     * @param localName
     * @param tag
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String tag) throws SAXException {

        switch (tag) {
	        case "plantilla":
	        case "template":
	        	log.trace("endElement() - case: plantilla");
	        	//Si la impresora seleccionada es nula, es que la plantilla está vacía
	        	if(printerFacade != null) {
	        		printerFacade.commandEndTemplate(comandoSalidaTexto);
	        	}                
	            break;
	        case "documento":
	        case "document":
	        	log.trace("endElement() - case: documento");
	        	if(!printerFacade.endDocument()) {
	            	String textoError = I18N.getText("La impresión no se ha podido completar correctamente.") + System.lineSeparator();
	            	if (printerFacade.getLastException() != null) {                		
	            		throw new SAXException(textoError, printerFacade.getLastException());
	            	}
	            	else {
	            		throw new SAXException(textoError);
	            	}
	            }
	            log.trace("endDocument() - XML de documento se ha parseado por completo y enviado a la impresora.");
	            break;
	        case "section":
	        	sectionPrintLine = null;
	        	break;
            case "codbar":
            case "barcode":
            	log.trace("endElement() - case: codbar");
            	if (elementPrintLine) {
	                printerFacade.printBarcode(texto.toString(), type, alignCodBar, tipoLeyendaNumericaCodBar, height);
	                printerFacade.commandEndBarcode(comandoSalidaTexto);
            	}
                texto = null;
                type = null;
                size = null;
                elementPrintLine = null;
                break;
            case "texto":
            case "text":
            	log.trace("endElement() - case: texto");
            	if (elementPrintLine) {
                   printerFacade.printText(StringEscapeUtils.unescapeXml(texto.toString()), size, align, style, fontName, fontSize);
                   printerFacade.commandEndTextElement(comandoSalidaTexto);
            	}
                texto = null;
                size = null;
                align = null;
                break;
            case "linea":
            case "line":
            	log.trace("endElement() - case: linea");
            	if (Boolean.TRUE.equals(elementPrintLine)) {
	                // Imprimimos salto de línea
	                printerFacade.endLine();
	                printerFacade.commandEndLineElement(comandoSalidaTexto);
            	}
            	elementPrintLine = null;
                break;
            case "imagen":
            case "image":
            case "logo":
            	log.trace("endElement() - case: logo");
            	
            	if (elementPrintLine) {
	            	if (StringUtils.isBlank(this.logoId)) {
	            		printerFacade.printLogo();
	            	} else {
	            		printerFacade.printLogo(this.logoId, this.size, this.align);	
	            	}
            	}
            	                	       
	        	this.logoId = null;
                this.size = null;
                this.align = null;
	        	
	        	elementPrintLine = null;
	        	
                break;                
            case "corte":
            case "cut":
            	log.trace("endElement() - case: corte");
            	if (elementPrintLine) {
            		printerFacade.cutPaper();
            	}
            	elementPrintLine = null;
                break;
            case "apertura-cajon":
            case "open-cashdrawer":
            	log.trace("endElement() - case: apertura-cajon");
            	if (elementPrintLine) {
            		openCashDrawer();
            	}
            	elementPrintLine = null;
                break;
        }                
    }

    /**
     * Procesa los caracteres interiores de los tags de la plantilla
     *
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (texto != null) {
            texto.append(ch, start, length);
        }
    }

    public void openCashDrawer() {        
    	 printerFacade.openCashDrawer();
    }

    public void closePrinters() {
    	log.trace("closePrinters()");
        if (printerFacade != null) {
            printerFacade.close();
        }
        if (impresora2 != null) {
            impresora2.close();
        }
    }

    
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

}
