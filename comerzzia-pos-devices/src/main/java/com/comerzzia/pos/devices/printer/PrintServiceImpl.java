package com.comerzzia.pos.devices.printer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import com.comerzzia.core.commons.exception.BadRequestException;
import com.comerzzia.omnichannel.facade.model.documents.PrintDocumentRequest;
import com.comerzzia.omnichannel.facade.service.documents.TicketDocumentServiceFacade;
import com.comerzzia.pos.core.devices.Devices;
import com.comerzzia.pos.core.devices.device.DeviceException;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinter;
import com.comerzzia.pos.devices.printer.parser.PrintParser;
import com.comerzzia.pos.devices.printer.parser.PrintParserException;

import lombok.extern.log4j.Log4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

@Service
@Log4j
public class PrintServiceImpl implements PrintService {

	@Value("${com.comerzzia.resources.reports:classpath:/reports/}")
	protected String reportsPath;

	@Autowired
	protected TicketDocumentServiceFacade documentService;
	
	@Autowired
	protected ResourceLoader resourceLoader;
	
	@Override
	public PrintDocumentRequest getDefaultPrintSettings(String docTypeCode) {		
		PrintDocumentRequest printSettings = new PrintDocumentRequest();

		DevicePrinter printer = getPrinter(docTypeCode);
		
		if (StringUtils.isNotBlank(printer.getMimeType())) {
			printSettings.setMimeType(printer.getMimeType());
		}
		
		printSettings.setCustomParams(new HashMap<>());
		
		return printSettings;
	}
	
	protected ByteArrayOutputStream getRawXmlPrintDocument(String documentUid, String docTypeCode, PrintDocumentRequest request) {
		ByteArrayOutputStream rawXmlOutput = new ByteArrayOutputStream();
		
		DevicePrinter printer = getPrinter(docTypeCode);
		request.setMimeType(StringUtils.isNotBlank(printer.getMimeType())?printer.getMimeType():null);

		documentService.printDocument(documentUid, request, rawXmlOutput);

		return rawXmlOutput;		
	}
	

	@Override
	public void printDocument(String documentUid, String docTypeCode, PrintDocumentRequest request) throws DeviceException {				
		try (ByteArrayOutputStream rawXmlOutput = getRawXmlPrintDocument(documentUid, docTypeCode, request)) {
			print(request, rawXmlOutput.toByteArray(), docTypeCode);
	    } catch (IOException e) {
	    	throw new DeviceException(e);
		}
	}

	@Override
	public String printScreen(String documentUid, PrintDocumentRequest request) throws DeviceException {
		log.debug("printScreen() - Previsualizando por pantalla: ");
		
		try (ByteArrayOutputStream htmlOutput = new ByteArrayOutputStream()) {
			request.setMimeType(MimeTypeUtils.TEXT_HTML_VALUE);
			request.setScreenOutput(Boolean.TRUE);

			documentService.printDocument(documentUid, request, htmlOutput);
			
			return htmlOutput.toString();
	    } catch (Exception e) {
	        log.error("printScreen() - Error imprimiendo documento: " + e.getMessage(), e);
	        throw new DeviceException(e);
	    }
	}
	
	@Override
	public void print(PrintDocumentRequest printRequest, byte[] rawXmlInput, String docTypeCode) throws DeviceException {
		try {
	        DevicePrinter printer = getPrinter(docTypeCode);
	
	        internalPrint(printRequest, rawXmlInput, printer);
	    } catch (PrintParserException e) {
	        log.error("print() - Error parsing template: " + e.getMessage(), e);
	        throw new DeviceException(e);
	    }
	}

	
	@Override
	public void printJasper(String template, HashMap<String, Object> params) throws DeviceException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()){
			getJasperPrint(output, template, params);
			
			DevicePrinter printer = getJasperPrinter();
			
			printJasper(params, output.toByteArray(), printer);
		}catch (Exception e) {
	        log.error("printJasper() - Error imprimiendo documento: " + e.getMessage(), e);
	        throw new DeviceException(e);
	    }
	}

	protected void internalPrint(PrintDocumentRequest request, byte[] rawXmlInput, DevicePrinter printer)
			throws PrintParserException, DeviceException {
		if(DevicePrinter.MIME_TYPE_JASPERPRINT.equals(printer.getMimeType())) {
			printJasper(request.getCustomParams(), rawXmlInput, printer);
		}else {
			printParser(request, rawXmlInput, printer);
		}
	}
	
	protected void printJasper(Map<String, Object> params, byte[] rawXmlInput, DevicePrinter printer)
			throws DeviceException {
		try {
			printer.printJasper(rawXmlInput, params);
		} catch (Exception e) {
			throw new DeviceException(e);
		}
	}

	protected void printParser(PrintDocumentRequest request, byte[] rawXmlInput, DevicePrinter printer)
			throws PrintParserException, DeviceException {
		PrintParser parser = new PrintParser();
		try {
			parser.setParameters(request.getCustomParams());
			
			if(printer.isReady()) {
				parser.print(rawXmlInput, printer);
			}
			else {				
				throw new DeviceException(printer.getAvailabilityErrorsAsAstring());
			}
		} finally {
       		parser.closePrinters();
		}
	}
	
	protected DevicePrinter getPrinter(String docTypeCode) {
		DevicePrinter printer = Devices.getInstance().getPrinter1();
		DevicePrinter printer2 = Devices.getInstance().getPrinter2();
		if(StringUtils.isNotBlank(docTypeCode) && !printer.isDocumentTypeAvailable(docTypeCode) && printer2.isDocumentTypeAvailable(docTypeCode)) {
			printer = printer2;
		}
		return printer;
	}
	
	protected DevicePrinter getJasperPrinter() throws DeviceException{
		DevicePrinter printer = Devices.getInstance().getPrinter1();
		if(!DevicePrinter.MIME_TYPE_JASPERPRINT.equals(printer.getMimeType())) {
			printer = Devices.getInstance().getPrinter2();
		}
		
		if(!DevicePrinter.MIME_TYPE_JASPERPRINT.equals(printer.getMimeType())) {
			throw new DeviceException("getJasperPrinter() - A device printer configured with mime type application/jasperprint could not be found.");
		}
		
		return printer;
	}
	
	protected void getJasperPrint(OutputStream outputStream, String template, HashMap<String, Object> params) throws DeviceException {
        JasperPrint jasperPrint = getJasperPrint(template, params);

        try {
			JRSaver.saveObject(jasperPrint, outputStream);
		} catch (JRException e) {
			throw new DeviceException("Error generating JasperPrint: " + e.getMessage(), e);
		}
	}

	protected JasperPrint getJasperPrint(String template, HashMap<String, Object> params) throws DeviceException{
		log.debug("getJasperPrint() - Generando impresión con parametros: " + params.toString());
		try {
			Resource templateResource = getTemplateResource(reportsPath + TEMPLATES_JASPER_PATH, template, TEMPLATE_JASPER_EXTENSION);
			addConfigParams(params, templateResource);
			
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(templateResource.getInputStream());

			JasperPrint jasperPrint = null;
			List<?> lista = (List<?>) params.get(LIST);
			if (lista != null) {
				JRDataSource dataSource = new JRBeanCollectionDataSource(lista);
				jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
			}
			else {
				jasperPrint = JasperFillManager.fillReport(jasperReport, params);
			}
			return jasperPrint;
		}
		catch (Exception e) {
			throw new DeviceException(e);
		}

	}

	protected Resource getTemplateResource(String templatesPath, String template, String templateExtension) {
		String localeId = LocaleContextHolder.getLocale().toString();
		String templatePath = templatesPath + template;

		// by localeId
		Resource resource = resourceLoader.getResource(templatePath + "_" + localeId.toLowerCase() + templateExtension);

		// by country
		if (!resource.exists()) {
			resource = resourceLoader.getResource(templatePath + "_" + StringUtils.left(localeId.toLowerCase(), 2) + templateExtension);
		}

		// by name
		if (!resource.exists()) {
			resource = resourceLoader.getResource(templatePath + templateExtension);
		}

		if (resource == null) {
			throw new BadRequestException("Print template not found: " + template + " in path " + reportsPath + templateExtension);
		}

		return resource;
	}

	protected void addConfigParams(HashMap<String, Object> params, Resource templateResource) {
		ResourceBundle rb = ResourceBundle.getBundle("i18n.czz-pos");
		params.put("REPORT_RESOURCE_BUNDLE", rb);

		try {
			String subReportsDir = templateResource.getURL().toString();
			subReportsDir = StringUtils.left(subReportsDir, subReportsDir.lastIndexOf("/") + 1);
			params.put("SUBREPORT_DIR", subReportsDir);
		}
		catch (IOException | StringIndexOutOfBoundsException e) {
			log.error("Error getting SUBREPORT_DIR path: " + e.getMessage(), e);
		}
	}
}
