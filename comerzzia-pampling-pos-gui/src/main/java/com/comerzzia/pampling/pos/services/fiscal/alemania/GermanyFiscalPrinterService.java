package com.comerzzia.pampling.pos.services.fiscal.alemania;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentUtils;
import com.comerzzia.pampling.pos.services.fiscal.alemania.epos.EposCompress;
import com.comerzzia.pampling.pos.services.fiscal.alemania.epos.EposInput;
import com.comerzzia.pampling.pos.services.fiscal.alemania.epos.EposOutput;
import com.comerzzia.pampling.pos.services.fiscal.alemania.epos.EposStorage;
import com.comerzzia.pampling.pos.services.fiscal.alemania.epos.EposTransaction;
import com.comerzzia.pampling.pos.services.fiscal.alemania.exception.EpsonTseException;
import com.comerzzia.pampling.pos.services.ticket.cabecera.PamplingCabeceraTicket;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.format.FormatUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
public class GermanyFiscalPrinterService {

	public static final String OPERACION_START_TRANSACTION = "StartTransaction";
	public static final String OPERACION_FINISH_TRANSACTION = "FinishTransaction";
	public static final String OPERACION_SETUP = "SetUp";
	public static final String OPERACION_REGISTER_CLIENT = "RegisterClient";
	public static final String OPERACION_GET_CHALLENGE = "GetChallenge";
	public static final String OPERACION_AUTHENTICATE_USERFORADMIN = "AuthenticateUserForAdmin";
	public static final String OPERACION_LOGOUT_FORTIMEADMIN = "LogOutForTimeAdmin";
	public static final String OPERACION_LOGOUT_FORADMIN = "LogOutForAdmin";
	public static final String OPERACION_AUTHENTICATE_USERFORTIMEADMIN = "AuthenticateUserForTimeAdmin";
	public static final String OPERACION_UPDATETIME_FORFIRST = "UpdateTimeForFirst";
	public static final String OPERACION_UPDATETIME = "UpdateTime";
	public static final String OPERACION_GET_EXPORTDATA = "GetExportData";
	public static final String OPERACION_ARCHIVE_EXPORT = "ArchiveExport";
	public static final String OPERACION_FINALIZE_EXPORT = "FinalizeExport";
	public static final String OPERACION_CANCEL_EXPORT = "CancelExport";
	public static final String OPERACION_GET_STORAGEINFO = "GetStorageInfo";	
	public static final String OPERACION_RUN_TSE_SELFTEST = "RunTSESelfTest";

	public static final String PROCESSTYPE_KASSENBELEG_V1 = "Kassenbeleg-V1";
	public static final String PROCESSTYPE_SONSTINGERVORGANG = "SonstigerVorgang";

	public static final String TIPO_TRANSACCION_BELEG = "Beleg";
	public static final String TIPO_TRANSACCION_TAGESSTART = "Tagesstart";
	public static final String TIPO_TRANSACCION_TAGESENDE = "Tagesende";
	public static final String TIPO_TRANSACCION_EINZAHLUNG = "Einzahlung";
	public static final String TIPO_TRANSACCION_ENTNAHME = "Entnahme";

	public static final String NOMBRE_CONEXION_TSE = "TSE";
	
	public static final String EXECUTION_OK = "EXECUTION_OK";
	public static final String ERROR_OTHER_ERROR_TIMEADMIN_NOT_AUTHORIZED = "OTHER_ERROR_TIMEADMIN_NOT_AUTHORIZED";
	public static final String TSE1_ERROR_WRONG_STATE_NEEDS_SELF_TEST = "TSE1_ERROR_WRONG_STATE_NEEDS_SELF_TEST";
	
	public static final String PARAMETRO_SALIDA_CONFIGURACION = "salida_configuracion";
	public static final String EPSON_IP = "IP";
		
	protected Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private Sesion sesion;

	public static Socket socket;
	public static final int BUFFER_LENGTH = 500000;
	
	public static String socketID;

	private String generaPeticion(EposTransaction peticionTransaccion) {
		Gson gson = new Gson();
		String jsonRequest;
		String peticion;

		jsonRequest = gson.toJson(peticionTransaccion);
		peticion="<device_data>"
					+ "<device_id>local_TSE</device_id>"
					+ "<data>"
						+ "<type>operate</type>"
						+ "<timeout>20000</timeout>"
						+ "<requestdata>"+ jsonRequest + "</requestdata> "
					+ "</data>"
				+ "</device_data>\0";
		
		return peticion;
	}

	public String openDevice() {
		// Abrir conexion con el TSE
		String peticion = "<open_device>"
				+ "<device_id>local_TSE</device_id>"
					+ "<data>"
						+ "<type>type_storage</type>"
					+ "</data>"
				+ "</open_device> \0";
		
		return peticion;
	}
	
	public String closeDevice() {
		// Cerrar conexion con el TSE
		String peticion = "<close_device> "
				+ "<device_id>local_TSE</device_id> "
			+ "</close_device>\0";
		
		return peticion;
	}

	public String setUp() {
		EposInput input = new EposInput();
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		input.setPuk("123456");
		input.setAdminPin("12345");
		input.setTimeAdminPin("54321");

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_SETUP, input, eposCompress);

		return generaPeticion(eposTransaction);
	}

	public String registerClient(String clientID) {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();
		input.setClientId(clientID);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_REGISTER_CLIENT, input, eposCompress);

		return generaPeticion(eposTransaction);
	}

	public String getChallenge(String userId) {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();
		input.setUserId(userId);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_GET_CHALLENGE, input, eposCompress);

		return generaPeticion(eposTransaction);
	}

	public String authenticateUserForAdmin(String hash) {
		EposInput input = new EposInput();
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		input.setUserId("Administrator");
		input.setPin("12345");
		input.setHash(hash);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_AUTHENTICATE_USERFORADMIN, input, eposCompress);
		return generaPeticion(eposTransaction);
	}

	public String authenticateUserForTimeAdmin(String hash) {
		String clientID = sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja();
		EposInput input = new EposInput();
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		input.setClientId(clientID);
		input.setPin("54321");
		input.setHash(hash);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_AUTHENTICATE_USERFORTIMEADMIN, input, eposCompress);

		return generaPeticion(eposTransaction);
	}

	public String logOutForTimeAdmin() {
		String clientID = sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja();
		EposInput input = new EposInput();
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		input.setClientId(clientID);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_LOGOUT_FORTIMEADMIN, input, eposCompress);
		return generaPeticion(eposTransaction);
	}
	
	public String logOutForAdmin() {
		EposInput input = new EposInput();
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_LOGOUT_FORADMIN, input, eposCompress);
		return generaPeticion(eposTransaction);
	}

	public String updateTimeForFirst(String userID) {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getDefault());

		EposInput input = new EposInput();

		input.setUserId(userID);
		input.setNewDateTime(sdf.format(new Date()));
		input.setUseTimeSync(false);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_UPDATETIME_FORFIRST, input, eposCompress);
		return generaPeticion(eposTransaction);
	}
	
	public String updateTime(String userID) {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getDefault());

		EposInput input = new EposInput();

		input.setUserId(userID);
		input.setNewDateTime(sdf.format(new Date()));
		input.setUseTimeSync(false);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_UPDATETIME, input, eposCompress);
		return generaPeticion(eposTransaction);
	}
	
	public String getExportData() {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();
		
		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_GET_EXPORTDATA, input, eposCompress);
		return generaPeticion(eposTransaction);
	}
	
	public String archiveExport() {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_ARCHIVE_EXPORT, input, eposCompress);
		return generaPeticion(eposTransaction);
	}

	public String finalizeExport() {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();
		input.setDeleteData(false);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_FINALIZE_EXPORT, input, eposCompress);
		return generaPeticion(eposTransaction);
	}

	public String cancelExport() {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();
		input.setDeleteData(false);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_CANCEL_EXPORT, input, eposCompress);
		return generaPeticion(eposTransaction);
	}

	public String getGetStorageInfo() {
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_GET_STORAGEINFO, input, eposCompress);
		return generaPeticion(eposTransaction);
	}

	public String startTransaction(String processType, String tipoTransaccion, BigDecimal ventaBruta, BigDecimal pagoEfectivo, BigDecimal pagoNoEfectivo, Boolean isAccionCaja) {
		String clientID = sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja();
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		String processData;
		if (!isAccionCaja) {
			processData = generaProcessData(processType, tipoTransaccion, ventaBruta, pagoEfectivo, pagoNoEfectivo);
		}
		else {
			processData = generaProcessDataCajas(processType, tipoTransaccion, ventaBruta);
		}
		
		log.debug("startTransaction() - ProcessData [" + processData + "]");

		String processDataEncode64 = Base64.encodeBase64String(processData.getBytes());
		EposInput input = new EposInput();

		input.setClientId(clientID);
		input.setProcessData(processDataEncode64);
		input.setProcessType(processType);
		input.setAdditionalData("");

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_START_TRANSACTION, input, eposCompress);

		return generaPeticion(eposTransaction);
	}

	public String finishTransaction(String processType, String tipoTransaccion, BigDecimal ventaBruta, BigDecimal pagoEfectivo, BigDecimal pagoNoEfectivo, Integer transactionNumber, Boolean isAccionCaja) {
		String clientID = sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja();
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		String processData;
		if (!isAccionCaja) {
			processData = generaProcessData(processType, tipoTransaccion, ventaBruta, pagoEfectivo, pagoNoEfectivo);
		}
		else {
			processData = generaProcessDataCajas(processType, tipoTransaccion, ventaBruta);
		}
		String processDataEncode64 = Base64.encodeBase64String(processData.getBytes());
		EposInput input = new EposInput();

		input.setClientId(clientID);
		input.setProcessData(processDataEncode64);
		input.setProcessType(processType);
		input.setAdditionalData("");
		input.setTransactionNumber(transactionNumber);

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_FINISH_TRANSACTION, input, eposCompress);

		return generaPeticion(eposTransaction);
	}
	
	public String generaProcessData(String processType, String tipoTransaccion, BigDecimal ventaBruta, BigDecimal pagoEfectivo, BigDecimal pagoNoEfectivo) {
		String processData = "";
		String ingresosFiscalesBrutos = String.valueOf(ventaBruta) + "_0.00_0.00_0.00";

		String pagos = "";
		if (pagoEfectivo != null) {
			pagos = String.valueOf(pagoEfectivo) + ":Bar";
		}
		if (pagoNoEfectivo != null) {
			if (pagoEfectivo != null) {
				pagos = pagos + "_";
			}
			pagos = pagos + String.valueOf(pagoNoEfectivo) + ":Unbar";
		}

		processData = tipoTransaccion + "^" + ingresosFiscalesBrutos + "^" + pagos;

		return processData;
	}
	
	public String generaProcessDataCajas(String processType, String tipoTransaccion, BigDecimal importe) {
		String processData = "";
		processData = tipoTransaccion + "^" + String.valueOf(importe);

		return processData;
	}


	public String lecturaSocket() throws IOException {
		String respuesta = null;
		DataInputStream in;

		try {
			in = new DataInputStream(socket.getInputStream());

			byte[] messageByte = new byte[BUFFER_LENGTH];
			boolean end = false;
			StringBuilder dataString = new StringBuilder(BUFFER_LENGTH);
			int totalBytesRead = 0;
			int totalBytesResponse = 0;
			while (!end) {
				int currentBytesRead = in.read(messageByte);
				if (currentBytesRead != -1) {
					totalBytesRead = currentBytesRead + totalBytesRead;
					if (totalBytesRead <= BUFFER_LENGTH) {
						dataString.append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
					}
					else {
						dataString.append(new String(messageByte, 0, BUFFER_LENGTH - totalBytesRead + currentBytesRead, StandardCharsets.UTF_8));
					}

					if (dataString.length() >= BUFFER_LENGTH || (totalBytesRead >= totalBytesResponse && totalBytesResponse > 0)) {
						end = true;
					}

					if (!dataString.toString().trim().isEmpty()) {
						end = true;
					}
				}
				else {
					end = true;
				}

				respuesta = dataString.toString().replace("&quot;", "\"").trim();
				log.debug("lecturaSocket() - " + respuesta);
			}
		}
		catch (IOException e) {
			log.warn("lecturaSocket() - Error al recuperar información del socket -" + e.getMessage());
			throw new IOException(e.getMessage(), e);
		}

		return respuesta;
	}

	public void enviarPeticion(String peticion) throws IOException {
		DataOutputStream salida;

		salida = new DataOutputStream(socket.getOutputStream());
		salida.write(peticion.getBytes());

		log.debug("enviarPeticion() - " + peticion);

	}

	public String conecta(String ip) throws Exception {
		String socketID = null;
		if (socket == null || socket.isClosed()) {
			socket = new Socket(ip, 8009);
//			socket.setSoTimeout(20*1000);
			String respuesta = lecturaSocket();

			Document xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(respuesta.getBytes()));
			Element root = xml.getDocumentElement();
			Element data = XMLDocumentUtils.getElement(root, "data", false);
			socketID = XMLDocumentUtils.getTagValueAsString(data, "client_id", false);
		}

		return socketID;
	}

	public void desconecta() throws IOException {
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}
	
	public void desconectaTSE() throws IOException {
		if (socket != null && !socket.isClosed()) {
			enviarPeticion(logOutForTimeAdmin());
			lecturaSocket();

			enviarPeticion(closeDevice());
			lecturaSocket();

			desconecta();
		}
	}

	@SuppressWarnings("deprecation")
	public HashMap<String, String> tratamientoRespuesta(String respuesta, List<String> listaCampos) throws SAXException, IOException, XMLDocumentException {
		Document xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(respuesta.getBytes()));

		HashMap<String, String> mapaCampos = new HashMap<String, String>();
		Element root = xml.getDocumentElement();
		Element nodoData = XMLDocumentUtils.getElement(root, "data", false);
		String resultData = XMLDocumentUtils.getTagValueAsString(nodoData, "resultdata", false);
		JsonElement jsonRoot = new JsonParser().parse(resultData);

		for (String campo : listaCampos) {
			String campoValue = jsonRoot.getAsJsonObject().get("output").getAsJsonObject().get(campo).getAsString();
			mapaCampos.put(campo, campoValue);
		}

		return mapaCampos;
	}
	
	@SuppressWarnings("deprecation")
	public HashMap<String, String> tratamientoRespuestaGetStorageInfo(String respuesta, List<String> listaCampos) throws SAXException, IOException, XMLDocumentException {
		Document xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(respuesta.getBytes()));

		HashMap<String, String> mapaCampos = new HashMap<String, String>();
		Element root = xml.getDocumentElement();
		Element nodoData = XMLDocumentUtils.getElement(root, "data", false);
		String resultData = XMLDocumentUtils.getTagValueAsString(nodoData, "resultdata", false);
		JsonElement jsonRoot = new JsonParser().parse(resultData);

		for (String campo : listaCampos) {
			String campoValue = jsonRoot.getAsJsonObject().get("output").getAsJsonObject().get("tseInformation").getAsJsonObject().get(campo).getAsString();
			mapaCampos.put(campo, campoValue);
		}

		return mapaCampos;
	}
	
	@SuppressWarnings("deprecation")
	public String tratamientoRespuestaResult(String respuesta) throws SAXException, IOException, XMLDocumentException {
		Document xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(respuesta.getBytes()));

		Element root = xml.getDocumentElement();
		Element nodoData = XMLDocumentUtils.getElement(root, "data", false);
		String resultData = XMLDocumentUtils.getTagValueAsString(nodoData, "resultdata", false);
		JsonElement jsonRoot = new JsonParser().parse(resultData);

		return jsonRoot.getAsJsonObject().get("result").getAsString();
	}


	public void procesoInicialTSE(String ip) throws Exception {
		conecta(ip);
		
		enviarPeticion(openDevice());
		String respuestaOpenDevice = lecturaSocket();

		Document xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(respuestaOpenDevice.getBytes()));
		Element root = xml.getDocumentElement();
		String code = XMLDocumentUtils.getTagValueAsString(root, "code", false);

		if (code.equals("OK")) {
			procesoLoginTimeAdmin();

			enviarPeticion(updateTimeForFirst(sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja()));
			lecturaSocket();
		}
	}
	
	public String reconecta(String oldSocket, String newSocket, String dataId) {
		// Abrir conexion con el TSE
		String peticion = "<reconnect>"
							+ "<data>"
								+ "<old_client_id>" + oldSocket + "</old_client_id>"
								+ "<new_client_id>" + newSocket + "</new_client_id>"
								+ "<received_id>" + dataId + "</received_id>"
							+ "</data>"
						+ "</reconnect> \0";		
		return peticion;
	}

	public String generaQR(String respuestaGetStorageInfo, String processType, String processData, String numTransaccion, String contadorFirma, String startTime, String finishTime, String firma)
	        throws IOException, SAXException, XMLDocumentException {
		String qr = "";

		String qrCodeVersion = "V0";
		String idClient = sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja();
		String signatureAlgorithm = "";
		String formatoTimeLog = "unixTime";
		String tsePublicKey = "";

		List<String> listaCampos = new ArrayList<String>();
		listaCampos.add("signatureAlgorithm");
		listaCampos.add("tsePublicKey");
		HashMap<String, String> mapaCampos = tratamientoRespuestaGetStorageInfo(respuestaGetStorageInfo, listaCampos);
		signatureAlgorithm = mapaCampos.get("signatureAlgorithm");
		tsePublicKey = mapaCampos.get("tsePublicKey");

		qr = qrCodeVersion + ";" + idClient + ";" + processType + ";" + processData + ";" + numTransaccion + ";" + contadorFirma + ";" + startTime + ";" + finishTime + ";" + signatureAlgorithm + ";"
		        + formatoTimeLog + ";" + firma + ";" + tsePublicKey;

		return qr;
	}
	
	public void procesoLoginTimeAdmin() throws SAXException, IOException, XMLDocumentException, NoSuchAlgorithmException {
		String getChallenge = getChallenge(sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja());
		enviarPeticion(getChallenge);
		String respuestaGetChallenge = lecturaSocket();
		String challenge = "";

		List<String> listaCampos = new ArrayList<String>();
		listaCampos.add("challenge");
		HashMap<String, String> mapaCampos = tratamientoRespuesta(respuestaGetChallenge, listaCampos);

		challenge = mapaCampos.get("challenge") + "EPSONKEY";
		MessageDigest digest;
		String cadena = "";
		// try {
		digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(challenge.getBytes(StandardCharsets.UTF_8));
		cadena = Base64.encodeBase64String(hash);

		String peticionAuthenticateAdmin = authenticateUserForTimeAdmin(cadena);
		enviarPeticion(peticionAuthenticateAdmin);
		lecturaSocket();
	}
	
	public void procesoLoginAdmin() throws SAXException, IOException, XMLDocumentException, NoSuchAlgorithmException {
		String getChallenge = getChallenge("Administrator");
		enviarPeticion(getChallenge);
		String respuestaGetChallenge = lecturaSocket();
		String challenge = "";

		List<String> listaCampos = new ArrayList<String>();
		listaCampos.add("challenge");
		HashMap<String, String> mapaCampos = tratamientoRespuesta(respuestaGetChallenge, listaCampos);

		challenge = mapaCampos.get("challenge") + "EPSONKEY";
		MessageDigest digest;
		String cadena = "";

		digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(challenge.getBytes(StandardCharsets.UTF_8));
		cadena = Base64.encodeBase64String(hash);

		String peticionAuthenticateAdmin = authenticateUserForAdmin(cadena);
		enviarPeticion(peticionAuthenticateAdmin);
		lecturaSocket();
	}

	public String setUpInicialCompleto(String ip) throws EpsonTseException {
		log.debug("setUpInicialCompleto() - Inicio del SetUp");
		String code = null;
		try {
			conecta(ip);

			enviarPeticion(openDevice());
			String respuestaOpenDevice = lecturaSocket();

			Document xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(respuestaOpenDevice.getBytes()));
			Element root = xml.getDocumentElement();
			code = XMLDocumentUtils.getTagValueAsString(root, "code", false);

			if (StringUtils.isNotBlank(code) && code.equals("OK")) {
				enviarPeticion(setUp());
				lecturaSocket();

				String getChallenge = getChallenge("Administrator");
				enviarPeticion(getChallenge);
				String respuestaGetChallenge = lecturaSocket();
				String challenge = "";

				List<String> listaCampos = new ArrayList<String>();
				listaCampos.add("challenge");
				HashMap<String, String> mapaCampos = tratamientoRespuesta(respuestaGetChallenge, listaCampos);

				challenge = mapaCampos.get("challenge") + "EPSONKEY";
				MessageDigest digest;
				String cadena = "";

				digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(challenge.getBytes(StandardCharsets.UTF_8));
				cadena = Base64.encodeBase64String(hash);

				String peticionAuthenticateAdmin = authenticateUserForAdmin(cadena);
				enviarPeticion(peticionAuthenticateAdmin);
				lecturaSocket();

				enviarPeticion(updateTimeForFirst("Administrator"));
				lecturaSocket();

				enviarPeticion(registerClient(sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja()));
				lecturaSocket();

				enviarPeticion(logOutForAdmin());
				lecturaSocket();
				enviarPeticion(closeDevice());
				lecturaSocket();
			}

			desconecta();
		}
		catch (Exception e) {
			throw new EpsonTseException(e.getMessage(), e);
		}

		return code;
	}

	public Boolean accionExportData() throws EpsonTseException, IOException {
		Boolean exportCorrecto = false;
		try {
			procesoLoginAdmin();

			String archiveExport = archiveExport();
			enviarPeticion(archiveExport);
			lecturaSocket();

			Boolean exportCompleto = false;
			String exportDataBase64 = "";

			List<String> listaCampos = new ArrayList<String>();
			listaCampos.add("exportData");
			listaCampos.add("exportStatus");
			while (!exportCompleto) {
				String respuestaExportData = "";
				String getExportData = getExportData();
				enviarPeticion(getExportData);

				Boolean lecturaPeticion = false;
				while (!lecturaPeticion) {
					String cadenaExport = lecturaSocket();
					respuestaExportData += cadenaExport;
					lecturaPeticion = cadenaExport.contains("EXPORT_COMPLETE") || cadenaExport.contains("EXPORT_INCOMPLETE");
				}

				exportCompleto = respuestaExportData.contains("EXPORT_COMPLETE");

				HashMap<String, String> mapaCampos = tratamientoRespuesta(respuestaExportData, listaCampos);
				String exportData = mapaCampos.get("exportData");
				exportDataBase64 += exportData;

			}

			String exportDataTotal = new String(Base64.decodeBase64(exportDataBase64));
			String nombreFichero = "ExportData" + FormatUtil.getInstance().formateaFecha(new Date()).replace(".", "");
			FileUtils.writeByteArrayToFile(new File(".\\" + nombreFichero + ".tar"), exportDataTotal.getBytes());

			exportCorrecto = true;
		}
		catch (Exception e) {
			throw new EpsonTseException(e.getMessage(), e);
		}
		finally {
			String finalizeExport = finalizeExport();
			enviarPeticion(finalizeExport);
			lecturaSocket();

			enviarPeticion(logOutForAdmin());
			lecturaSocket();
		}

		return exportCorrecto;
	}

	public Boolean socketConectado() {
		return socket != null && !socket.isClosed();
	}
	
	public String autoTest() throws Exception {
		log.debug("autoTest() - Se realiza auto test");
		EposStorage eposStorage = new EposStorage("TSE", "TSE1");
		EposCompress eposCompress = new EposCompress(false, "");

		EposInput input = new EposInput();

		EposTransaction eposTransaction = new EposTransaction(eposStorage, OPERACION_RUN_TSE_SELFTEST, input, eposCompress);
		String peticion = generaPeticion(eposTransaction);
		log.debug("autoTest() - Petición generada " + peticion);

		enviarPeticion(peticion);
		lecturaSocket();
		
		enviarPeticion(updateTime(sesion.getAplicacion().getCodAlmacen() + sesion.getAplicacion().getCodCaja()));
		lecturaSocket();

		return peticion;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean tseStartTransaction(TicketManager ticketManager) {
		try {
			List<PagoTicket> pagos = ((TicketVenta) ticketManager.getTicket()).getPagos();
			BigDecimal pagosEfectivo = null;
			BigDecimal pagosNoEfectivo = null;
			for (PagoTicket pagoTicket : pagos) {
				if (pagoTicket.getCodMedioPago().equals("0000")) {
					pagosEfectivo = new BigDecimal(0);
					pagosEfectivo = pagosEfectivo.add(pagoTicket.getImporte());
				}
				else {
					pagosNoEfectivo = new BigDecimal(0);
					pagosNoEfectivo.add(pagoTicket.getImporte());
				}
			}

			String peticionStartTransaction = startTransaction(PROCESSTYPE_KASSENBELEG_V1, TIPO_TRANSACCION_BELEG,
			        ticketManager.getTicket().getTotales().getTotal(), pagosEfectivo, pagosNoEfectivo, false);
			enviarPeticion(peticionStartTransaction);
			String respuestaStartTransaction = lecturaSocket();

			List<String> listaCampos = new ArrayList<>();
			listaCampos.add("logTime");
			listaCampos.add("transactionNumber");
			listaCampos.add("serialNumber");
			listaCampos.add("signature");
			listaCampos.add("signatureCounter");
			HashMap<String, String> mapaCampos = tratamientoRespuesta(respuestaStartTransaction, listaCampos);

			String result = tratamientoRespuestaResult(respuestaStartTransaction);
			if (result.equals(EXECUTION_OK)) {
				String logTimeStart = mapaCampos.get("logTime");
				String transactionNumber = mapaCampos.get("transactionNumber");
				String serialNumber = mapaCampos.get("serialNumber");
				String signature = mapaCampos.get("signature");
				String signatureCounter = mapaCampos.get("signatureCounter");

				EposOutput eposOutput = new EposOutput();
				eposOutput.setLogTimeStart(logTimeStart);
				eposOutput.setTransactionNumber(transactionNumber);
				eposOutput.setSerialNumber(serialNumber);
				eposOutput.setSignature(signature);
				eposOutput.setSignatureCounter(signatureCounter);

				((PamplingCabeceraTicket) ticketManager.getTicket().getCabecera()).setTse(eposOutput);
			}
			else {
				return false;
			}

			return true;
		}
		catch (Exception e) {
			log.warn("TSE() - Error al realizar el proceso de TSE -" + e.getMessage());
			return false;
		}
	}
}
