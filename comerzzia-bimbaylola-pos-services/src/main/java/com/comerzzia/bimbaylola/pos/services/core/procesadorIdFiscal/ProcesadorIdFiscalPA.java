package com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.fiscaldata.FiscalData;
import com.comerzzia.pos.services.fiscaldata.FiscalDataProperty;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.format.FormatUtil;
import com.google.gson.Gson;
import com.webpossa.efactura.efClientIntegration.model.dto.WebPosDTO;
import com.webpossa.efactura.efClientIntegration.model.dto.panama.AddInfo;
import com.webpossa.efactura.efClientIntegration.model.dto.panama.FiscalDoc;
import com.webpossa.efactura.efClientIntegration.model.dto.panama.Item;
import com.webpossa.efactura.efClientIntegration.model.dto.panama.Payment;
import com.webpossa.efactura.efClientIntegration.model.dto.panama.peticion.RequestPA;
import com.webpossa.efactura.efClientIntegration.model.dto.panama.respuesta.DgiErrMsg;
import com.webpossa.efactura.efClientIntegration.model.dto.panama.respuesta.ResponsePA;
import com.webpossa.efactura.efClientIntegration.services.client.EFClientService;

@SuppressWarnings("rawtypes")
@Component
public class ProcesadorIdFiscalPA implements IProcesadorFiscal {

	protected static final Logger log = Logger.getLogger(ProcesadorIdFiscalPA.class);

	// METODOS DE PAGO
	private static final String EFECTIVO = "0000";
	private static final String TARJETA_REGALO_ABONO = "1001";
	private static final String CAMBIO = "1000";

	// SECCIONES
	private static final String TEXTIL_PLANA = "PL";
	private static final String TEXTIL_PUNTO = "PT";
	private static final String TEXTIL = "R";
	private static final String ZAPATO = "Z";

	// CARPETAS WEBPOS
	public static final String PA_WEBPOS_DIR_INPUT_FICHEROS = "Input/";
	public static final String PA_WEBPOS_DIR_ERROR_FICHEROS = "Error/";
	public static final String PA_WEBPOS_DIR_RESULTS_FICHEROS = "Results/";
	
	// CLIENTE DEFAULT
	public static final String PA_DEFAULT_NUMERO_INDENTIFICACION = "9999999999999";
	
	public static final String COD_DOCUMENTO_PASAPORTE = "PAS";
	public static final String COD_DOCUMENTO_CEDULA = "CED";
	public static final String COD_DOCUMENTO_RUC = "RUC";
	public static final String COD_DOCUMENTO_CONSUMIDOR_FINAL = "CF";

	@Autowired
	protected Documentos documentos;

	@Autowired
	protected VariablesServices variablesService;

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected ServicioContadores servicioContadores;

	@Override
	public String obtenerIdFiscal(Ticket ticket) throws ProcesadorIdFiscalException {
		return null;
	}

	@Override
	public byte[] procesarDocumentoFiscal(Ticket ticket) throws Exception {
		log.debug("procesarDocumentoFiscal() - Inicio del envio del documento fiscal");
		byte[] respuesta = null;
		String webPosIsProcessed = "N";
		try {
			checkService();
		}
		catch (Exception e) {
			throw new ProcesarDocumentoFiscalPAException(e.getMessage() + " Revise que el servicio de EFClient está arrancado.");
		}

		try {
			ResponsePA respuestaWebPos = llamadaWebPos(ticket);
			if (respuestaWebPos != null) {
				webPosIsProcessed = respuestaWebPos.getAccepted() && respuestaWebPos.getReceived() ? "S" : "N";
			}

			FiscalData fiscalData = new FiscalData();
			fiscalData.addProperty(ByLVariablesServices.FICHERO_FISCAL_PROCESADO, webPosIsProcessed);
			ticket.getCabecera().setFiscalData(fiscalData);
		}
		catch (Exception e) {
			log.error("procesarDocumentoFiscal() - " + e.getMessage(), e);
			if (e instanceof SocketTimeoutException) {
				ticket.getCabecera().getFiscalData().addProperty(ByLVariablesServices.FICHERO_FISCAL_PROCESADO, "N");
				throw new SocketTimeoutException("Ha ocurrido un error al realizar el envio del documento: " + e.getMessage());
			}
			else {
				limpiaPropertiesFiscalData(ticket);
				throw new ProcesadorIdFiscalException(e.getMessage(), e);
			}
		}

		return respuesta;
	}

	private ResponsePA llamadaWebPos(Ticket ticket) throws Exception {
		// El ClientFE es un agente que se instala en cada equipo, que devuelve PDF con CUFE y QR y se encarga de
		// comunicar a la Hacienda.
		// Para más info: BYL-250

		log.debug("llamadaWebPos() - Comenzando conexión con el ClientFE");
		String rutaCarpeta = variablesService.getVariableAsString(ByLVariablesServices.PA_WEBPOS_RUTA_CARPETA);
		if (!rutaCarpeta.endsWith("/") && !rutaCarpeta.endsWith("\\")) {
			rutaCarpeta = rutaCarpeta.concat("\\");
		}
		String directorioInput = rutaCarpeta + PA_WEBPOS_DIR_INPUT_FICHEROS;
//		String directorioError = rutaCarpeta + PA_WEBPOS_DIR_ERROR_FICHEROS;
		String directorioResults = rutaCarpeta + PA_WEBPOS_DIR_RESULTS_FICHEROS;

		WebPosDTO webPosDTO = rellenarJsonFacturaElectronica(ticket);
		String webPosJson = new Gson().toJson(webPosDTO.getRequestPanama());

		log.debug("llamadaWebPos() - JSON que se va a enviar a EFClient: \n" + webPosJson);

		Map<String, String> parametrosContador = new HashMap<>();
		parametrosContador.put("CODEMP", sesion.getAplicacion().getEmpresa().getCodEmpresa());
		parametrosContador.put("CODALM", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODSERIE", sesion.getAplicacion().getTienda().getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODCAJA", sesion.getAplicacion().getCodCaja());
		parametrosContador.put("CODTIPODOCUMENTO", ticket.getCabecera().getCodTipoDocumento());
		parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

		TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ticket.getCabecera().getCodTipoDocumento());
		ContadorBean ticketContador = null;
		if (documento != null) {
			ticketContador = servicioContadores.obtenerContador(documento.getIdContador(), parametrosContador, ticket.getUidActividad());
		}

		writeFileToInputFolder(directorioInput, webPosDTO, webPosJson, ticketContador);

//		if(errorFileExists(directorioError, webPosDTO)) {
//			throw new ProcesadorIdFiscalException("Se ha producido un error durante el procesamiento del ticket por el agente ClientFE");
//		}
		
		String jsonResult = resultFileExists(directorioResults);

		return jsonToResponsePA(webPosDTO, jsonResult);
	}

	public WebPosDTO rellenarJsonFacturaElectronica(Ticket ticket) {
		log.debug("rellenarJsonFacturaElectronica() - Transformando ticket a objeto WEBPOS para enviarlo a la API");
		WebPosDTO webPosDTO = new WebPosDTO();

		webPosDTO.setRequestPanama(new RequestPA());

		FiscalDoc fiscalDoc = new FiscalDoc();
		fiscalDoc.setCompanyLicCod(variablesService.getVariableAsString(ByLVariablesServices.PA_WEBPOS_COMPANYLICCOD));

		String branchCod = FormatUtil.getInstance()
		        .completarCerosIzquierda(ticket.getTienda().getCodAlmacen().substring(ticket.getTienda().getCodAlmacen().length() - 2, ticket.getTienda().getCodAlmacen().length()), 4);
		fiscalDoc.setBranchCod(branchCod);

		String PosCod = FormatUtil.getInstance().completarCerosIzquierda(ticket.getCodCaja().substring(ticket.getCodCaja().length() - 2, ticket.getCodCaja().length()), 3);
		fiscalDoc.setPosCod(PosCod);

		fiscalDoc.setDocType("NC".equals(ticket.getCabecera().getCodTipoDocumento()) ? "C" : "F");
		/* Modificamos las / por - a petición de EFClient */
		fiscalDoc.setDocNumber(ticket.getCabecera().getCodTicket().replace("/", "-"));

		fiscalDoc.setDocDate(ticket.getFecha());

		setWebPosCustomer(ticket, fiscalDoc);

		setWebPosInvoiceData(ticket, fiscalDoc);

		setWebPosAdditionalInfo(ticket, fiscalDoc);

		setWebPosItems(ticket, fiscalDoc);

		setWebPosPayments(ticket, fiscalDoc);

		webPosDTO.getRequestPanama().setFiscalDoc(fiscalDoc);
		
		log.debug("rellenarJsonFacturaElectronica() - Ticket transformado a objeto WEBPOS. Preparado para enviar");
		
		return webPosDTO;
	}

	protected void setWebPosCustomer(Ticket ticket, FiscalDoc fiscalDoc) {
		log.debug("setWebPosCustomer() - Rellenando datos del cliente...");
		ClienteBean cliente = ticket.getCabecera().getCliente();
		ClienteBean clienteGenerico = sesion.getAplicacion().getTienda().getCliente();
		if (cliente.getTipoIdentificacion() != null &&(!cliente.getTipoIdentificacion().equals(clienteGenerico.getTipoIdentificacion()) && !cliente.getCif().equals(clienteGenerico.getCif()))
		        && !PA_DEFAULT_NUMERO_INDENTIFICACION.equals(cliente.getCif())) {
			fiscalDoc.setCustomerName(cliente.getDesCliente());
			fiscalDoc.setCustomerRUC(cliente.getCif());

			String cif = cliente.getCif();
			String tipoIdentificacion = cliente.getTipoIdentificacion();
			if (cif != null && StringUtils.isNotBlank(tipoIdentificacion)) {
				if (tipoIdentificacion.equals(COD_DOCUMENTO_PASAPORTE)) {
					fiscalDoc.setCustomerType("06"); // Si es pasaporte
				}
				else if (tipoIdentificacion.equals(COD_DOCUMENTO_RUC)) {
					fiscalDoc.setCustomerType("04"); // Si es RUC
				}
				else if(tipoIdentificacion.equals(COD_DOCUMENTO_CEDULA)){
					fiscalDoc.setCustomerType("05"); // Si es cédula
				}
				else if(tipoIdentificacion.equals(COD_DOCUMENTO_CONSUMIDOR_FINAL)) {
					fiscalDoc.setCustomerType("07"); // Si es consumidor final
				}
			}
			fiscalDoc.setCustomerAddress(cliente.getDomicilio());
			fiscalDoc.setEmail(cliente.getEmail());
		}
		else {
			fiscalDoc.setCustomerName("CONSUMIDOR FINAL");
			fiscalDoc.setCustomerRUC("");
			fiscalDoc.setCustomerType("07");
			fiscalDoc.setCustomerAddress("");
			fiscalDoc.setEmail("");

			// fiscalDoc.setCustomerName("CONTRIBUYENTE");
			// fiscalDoc.setCustomerRUC("1790095389001");
			// fiscalDoc.setCustomerType("04");
			// fiscalDoc.setCustomerAddress("C/ Tier1, Vega 7");
			// fiscalDoc.setEmail("egp@tier1.es");
		}
	}

	protected void setWebPosInvoiceData(Ticket ticket, FiscalDoc fiscalDoc) {
		if ("C".equals(fiscalDoc.getDocType())) {
			if (ticket.getCabecera().getDatosDocOrigen() != null) {
				fiscalDoc.setInvoiceNumber(ticket.getCabecera().getDatosDocOrigen().getCodTicket().replace("/", "-"));
				fiscalDoc.setInvoiceNumberRefDate(ticket.getCabecera().getDatosDocOrigen().getFecha());
			}
			else {
				fiscalDoc.setInvoiceNumber("");
				if (ticket.getCabecera() instanceof ByLCabeceraTicket) {
					fiscalDoc.setInvoiceNumberRefDate(((ByLCabeceraTicket) ticket.getCabecera()).getFechaDocumentoOrigen());
				}
			}
		}
	}

	protected void setWebPosAdditionalInfo(Ticket ticket, FiscalDoc fiscalDoc) {
		List<AddInfo> addInfo = new ArrayList<AddInfo>();
		AddInfo pedido = new AddInfo(1, "Pedido: " + ticket.getCabecera().getCodTicket());
		addInfo.add(pedido);
		AddInfo vendedor = new AddInfo(2, "Vendedor: " + ticket.getCajero().getUsuario());
		addInfo.add(vendedor);
		AddInfo gracias = new AddInfo(3, "Gracias por su visita - Thanks for your visit");
		addInfo.add(gracias);
		AddInfo plazo = new AddInfo(4, "Conserve su ticket para cualquier cambio o devolución. Plazo máximo 30 días naturales desde la fecha de compra.");
		addInfo.add(plazo);
		AddInfo condiciones = new AddInfo(5, "Condiciones válidas para el territorio nacional excepto grandes almacenes y tiendas Outlet.");
		addInfo.add(condiciones);
		AddInfo cambio = new AddInfo(6, "Para el cambio o devolución de artículos es imprescindible que estos no hayan sido usados o deteriorados, y se conserve intacta la etiqueta original.");
		addInfo.add(cambio);
		AddInfo rechazo = new AddInfo(7,
		        "No se admitirán cambios ni devoluciones de artículos de baño sin la protección higiénica, pendientes o accesorios sin el embalaje original, prendas modificadas a petición del cliente, ni de artículos comprados en otro país.");
		addInfo.add(rechazo);
		AddInfo importe = new AddInfo(8,
		        "El importe de su compra será reembolsado en la misma forma de pago siempre con el ticket original. En el caso de ticket regalo, el reembolso se efectuará en una tarjeta abono.");
		addInfo.add(importe);
		AddInfo garantia = new AddInfo(9, "Esta garantía es adicional y no afecta a los derechos legales del consumidor.");
		addInfo.add(garantia);
		AddInfo termino = new AddInfo(10, "El término de garantía legal es de 3 meses a partir de la fecha de compra para todos los artículos de la tienda.");
		addInfo.add(termino);

		fiscalDoc.setAddInfo(addInfo);
	}

	@SuppressWarnings("unchecked")
	protected void setWebPosItems(Ticket ticket, FiscalDoc fiscalDoc) {
		log.debug("getWebPosItems() - Insertando datos de los artículos de la venta...");
		List<Item> items = new ArrayList<Item>();
		List<LineaTicket> lineasTicket = ticket.getLineas();
		Integer contadorLineas = 1;
		for (LineaTicket lineaTicket : lineasTicket) {
			Item item = new Item();
			item.setId(contadorLineas);
			item.setQty(lineaTicket.getCantidad());
			item.setPrice(lineaTicket.getImporteConDto());
			item.setCode(lineaTicket.getCodArticulo() + "-" + lineaTicket.getDesglose1() + "-" + lineaTicket.getDesglose2());
			item.setDesc(lineaTicket.getDesArticulo());
			item.setTax(1); // Tax: 1 (7% ITBMS)
			item.setComments("");
			item.setDperc("0%");
			item.setDamt(0);
			item.setBcode(lineaTicket.getCodigoBarras());
			item.setAuxcode(null);
			
			String codSeccion = lineaTicket.getArticulo().getCodseccion();
			if(StringUtils.isNotBlank(codSeccion)) {
				item.setCat1Code(seccionCmzToWebPos(codSeccion));
			}
			
			item.setIscType(null);
			item.setIscPerc(null);
			item.setIscValue(null);
			item.setOtiS911(null);
			item.setOtiPn(null);
			item.setOtiIs(null);
			item.setMfDate(null);
			item.setExpDate(null);
			item.setLot(null);
			item.setLotQty(null);
			item.setVhModOp(null);
			item.setVhModOpDesc(null);
			item.setVhVin(null);
			item.setVhColorCod(null);
			item.setVhFuel(null);
			item.setVhFuelDesc(null);
			item.setVhMotorNbr(null);
			item.setVhType(null);
			item.setVhUse(null);
			item.setVhModYear(null);
			item.setVhMFYear(null);
			items.add(item);
			contadorLineas++;
		}
		fiscalDoc.setItems(items);
	}

	/**
	 * Método para obtener la sección webpos que corresponde a la del artículo de Comerzzia.
	 * 
	 * @param seccionCmz
	 * @return seccionWebPos
	 */
	private String seccionCmzToWebPos(String seccionCmz) {
		log.debug("getPaymentType() - Obteniendo la seccion (" + seccionCmz + ") de acuerdo al código CPBS aportado por WEBPOS");
		switch (seccionCmz) {
			case TEXTIL_PLANA:
			case TEXTIL_PUNTO:
			case TEXTIL:
				return "5310";
			case ZAPATO:
				return "5311";
			default:
				return "5312";
		}
	}

	@SuppressWarnings("unchecked")
	protected void setWebPosPayments(Ticket ticket, FiscalDoc fiscalDoc) {
		log.debug("getWebPosPayments() - Rellenando pagos...");
		List<Payment> payments = new ArrayList<Payment>();
		List<PagoTicket> pagos = ((TicketVenta) ticket).getPagos();
		Integer contadorIdPago = 1;
		for (PagoTicket pago : pagos) {
			Payment payment = new Payment();
			payment.setId(contadorIdPago);
			payment.setType(pagoCmzToWebPos(pago.getCodMedioPago()));
			payment.setAmt(pago.getImporte());
			payment.setDesc1(pago.getDesMedioPago());
			payments.add(payment);
			contadorIdPago++;
		}
		fiscalDoc.setPayments(payments);
	}

	/**
	 * Método para obtener el paymentType (Tabla proporcionada por el cliente) del medio de pago utilizado.
	 * 
	 * @param codMedioPago
	 * @return paymentType
	 */
	private String pagoCmzToWebPos(String medioPagoCmz) {
		log.debug("getPaymentType() - Obteniendo el tipo de pago (" + medioPagoCmz + ") de acuerdo a la Tabla de Formas de Pagos");
		switch (medioPagoCmz) {
			case EFECTIVO:
				return "01";
			case TARJETA_REGALO_ABONO:
				return "06";
			case CAMBIO:
				return "08";
			default:
				return "02";
		}
	}

	protected void writeFileToInputFolder(String directorioInput, WebPosDTO webPosDTO, String webPosJson, ContadorBean ticketContador) throws InterruptedException {
		String prefixFileName = webPosDTO.getRequestPanama().getFiscalDoc().getBranchCod() + webPosDTO.getRequestPanama().getFiscalDoc().getPosCod();
		String fileName = prefixFileName + FormatUtil.getInstance().completarCerosIzquierda(ticketContador.getValor(), 10) + ".json";
		String absolutePath = directorioInput + fileName;

		writeJsonToInputFolder(absolutePath, webPosJson);

		sleepingTime();

		fileHasBeenReadFromInputFolder(absolutePath);
	}

	protected void writeJsonToInputFolder(String absolutePath, String webPosJson) {
		try {
			FileWriter fileWriter = new FileWriter(absolutePath);
			fileWriter.write(webPosJson);
			fileWriter.close();
			log.debug("Fichero '" + absolutePath + "' creado correctamente.");
		}
		catch (IOException e) {
			log.error("llamadaWebPos() - Error durante la creación del fichero JSON en el ClientFE : " + e.getMessage(), e);
		}
	}

	protected void sleepingTime() throws InterruptedException {
		String sleepingTimeString = variablesService.getVariableAsString(ByLVariablesServices.PA_WEBPOS_TIEMPO_DE_ESPERA_EN_SEGUNDOS);
		Integer sleepingTime = 5000;
		try {
			sleepingTime = Integer.valueOf(sleepingTimeString) * 1000;
			log.debug("sleepingTime() - Esperando  " + sleepingTime + " a que reponda el ClientFE");
		}
		catch (NumberFormatException e) {
			log.warn("sleepingTime() - La variable PA.WEBPOS_TIEMPO_DE_ESPERA_EN_SEGUNDOS no está definida correctamente, se utilizará el valor por defecto (5 segundos).");
		}
		Thread.sleep(sleepingTime); // Le damos N segundos de espera para que le dé tiempo al ClientFE de procesar el
		                            // fichero
	}

	protected void fileHasBeenReadFromInputFolder(String absolutePath) throws InterruptedException {
		Boolean fileIsRead = Boolean.FALSE;
		while (!fileIsRead) {
			try {
				FileReader fileReader = new FileReader(absolutePath);
				fileReader.close();
				log.debug("fileIsReadFromInputFolder() - El fichero '" + absolutePath + "' aún no ha sido leído por el ClientFE");
				sleepingTime();
			}
			catch (IOException e) {
				if (e instanceof FileNotFoundException) {
					log.debug("fileIsReadFromInputFolder() - El fichero '" + absolutePath + "' ha sido leído por el ClientFE");
					fileIsRead = Boolean.TRUE;
				}
				else {
					log.error("fileIsReadFromInputFolder() - Error durante la lectura del fichero JSON por el ClientFE : " + e.getMessage(), e);
				}
			}
		}
	}

	protected Boolean errorFileExists(String directorioError, WebPosDTO webPosDTO) throws ProcesadorIdFiscalException, IOException {
		Path directorio = Paths.get(directorioError);

		File[] archivos = directorio.toFile().listFiles();
		if (archivos != null && archivos.length > 0) {
			Arrays.sort(archivos, new Comparator<File>(){

				@Override
				public int compare(File f1, File f2) {
					return Long.compare(f2.lastModified(), f1.lastModified());
				}
			});
			File lastFile = archivos[0];

			log.debug("errorFileExists() - Último archivo erróneo : " + lastFile.getAbsolutePath());

			if (lastFile.exists() && lastFile.isFile()) {
				String fileContent = new String(Files.readAllBytes(Paths.get(lastFile.getAbsolutePath())));
				RequestPA errorFile = new Gson().getAdapter(RequestPA.class).fromJson(fileContent);
				
				String docNumberOriginalFile = webPosDTO.getRequestPanama().getFiscalDoc().getDocNumber();
				String docNumberErrorFile = errorFile.getFiscalDoc().getDocNumber();
				
				return docNumberOriginalFile.equals(docNumberErrorFile);
			}
			else {
				log.debug("errorFileExists() - El fichero WEBPOS no se encuentra en el directorio /Error");
			}
		}
		return false;
	}
	
	protected String resultFileExists(String directorioResults) throws ProcesadorIdFiscalException, IOException {
		Path directorio = Paths.get(directorioResults);

		File[] archivos = directorio.toFile().listFiles();
		if (archivos != null && archivos.length > 0) {
			// Ordenar los archivos por fecha de última modificación de forma descendente
			Arrays.sort(archivos, new Comparator<File>(){

				@Override
				public int compare(File f1, File f2) {
					return Long.compare(f2.lastModified(), f1.lastModified());
				}
			});
			File lastFile = archivos[0]; // Fichero más reciente

			log.debug("resultFileExists() - Último archivo creado: " + lastFile.getAbsolutePath());

			if (lastFile.exists() && lastFile.isFile()) {
				return new String(Files.readAllBytes(Paths.get(lastFile.getAbsolutePath())));
			}
			else {
				String msgError = "Ha habido un error de conexión durante el envío del fichero WEBPOS al agente ClientFE.";
				log.error("resultFileExists() - El fichero WEBPOS no se no ha procesado correctamente en el ClientFE y no se encuentra en el directorio /Results");
				throw new ProcesadorIdFiscalException(msgError);
			}
		}
		else {
			throw new ProcesadorIdFiscalException("No se encontraron archivos en el directorio: " + directorioResults);
		}
	}

	protected ResponsePA jsonToResponsePA(WebPosDTO webPosDTO, String jsonResult) throws IOException, ProcesadorIdFiscalException {
		ResponsePA responsePA = new Gson().getAdapter(ResponsePA.class).fromJson(jsonResult);
		String docNumber = webPosDTO.getRequestPanama().getFiscalDoc().getDocNumber();
		String sysytemRef = responsePA.getSystemRef();
		// Para asegurarnos de que es nuestro fichero, comprobamos el atributo 'systmeRef'.
		if (!docNumber.equals(sysytemRef)) {
			throw new ProcesadorIdFiscalException(
			        "Error durante el procesamiento del fichero WEBPOS por el agente ClientFE: No se ha procesado correctamente el fichero con docNumber(" + docNumber + ")");
		}

		String dgiErrMsgStrig = responsePA.getDgiErrMsg();
		// Si el atributo 'dgiErrMsg' viene relleno, se lo mostramos al usuario por pantalla.
		if (StringUtils.isNotBlank(dgiErrMsgStrig)) {
			DgiErrMsg dgiErrMsg = new Gson().getAdapter(DgiErrMsg.class).fromJson(dgiErrMsgStrig.substring(1, dgiErrMsgStrig.length() - 1));
			throw new ProcesadorIdFiscalException("Error durante el procesamiento del fichero WEBPOS por el agente ClientFE :\n " + dgiErrMsg.getDCodRes() + " (" + dgiErrMsg.getDMsgRes() + ")");
		}
		return responsePA;
	}

	private void limpiaPropertiesFiscalData(Ticket ticket) {
		log.debug("limpiaPropertiesFiscalData() - Se limpian ciertas properties de fiscal data en caso de error");

		if (ticket.getCabecera().getFiscalData() != null) {
			FiscalDataProperty propertyFicheroFiscal = ticket.getCabecera().getFiscalData().getProperty(ByLVariablesServices.PA_FICHERO_FISCAL_CODIFICADO);
			FiscalDataProperty propertyProcesado = ticket.getCabecera().getFiscalData().getProperty(ByLVariablesServices.FICHERO_FISCAL_PROCESADO);

			if (propertyFicheroFiscal != null) {
				ticket.getCabecera().getFiscalData().getProperties().remove(propertyFicheroFiscal);
			}
			if (propertyProcesado != null) {
				ticket.getCabecera().getFiscalData().getProperties().remove(propertyProcesado);
			}
		}
	}

	private void checkService() throws Exception {
		log.debug("checkService() - Se realiza proceso de check al servicio de EFClient.");
		EFClientService.checkService();
		log.debug("checkService() - Servicio de EFClient responde correctamente. Se puede continuar con la operación.");
	}
}
