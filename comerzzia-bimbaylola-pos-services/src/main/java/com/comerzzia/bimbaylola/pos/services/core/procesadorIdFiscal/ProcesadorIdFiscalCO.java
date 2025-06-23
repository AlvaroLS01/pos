package com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.ByLConfigContadorBean;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRango;
import com.comerzzia.bimbaylola.pos.persistence.core.config.configcontadores.rangos.ConfigContadorRangoExample;
import com.comerzzia.bimbaylola.pos.persistence.core.contadores.ByLContadorBean;
import com.comerzzia.bimbaylola.pos.services.core.ByLServicioContadores;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.ByLServicioConfigContadoresImpl;
import com.comerzzia.bimbaylola.pos.services.core.config.configContadores.rangos.ServicioConfigContadoresRangos;
import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.bimbaylola.pos.services.edicom.EdicomFile;
import com.comerzzia.core.util.base64.Base64Coder;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentUtils;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.contadores.ContadorNotFoundException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.fiscaldata.FiscalData;
import com.comerzzia.pos.services.fiscaldata.FiscalDataProperty;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.util.i18n.I18N;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import services.EdicomService;

@SuppressWarnings("rawtypes")
@Component
public class ProcesadorIdFiscalCO implements IProcesadorFiscal {

	protected static final Logger log = Logger.getLogger(ProcesadorIdFiscalCO.class);
	
	public static final String NUM_RESOLUCION = "NUM_RESOLUCION";
	public static final String FECHA_RESOLUCION = "FECHA_RESOLUCION";
	public static final String CLAVE_TECNICA = "CLAVE_TECNICA";
	public static final String PREF = "PREF";
	public static final String RANGO_INICIO = "RANGO_INICIO";
	public static final String RANGO_FIN = "RANGO_FIN";
	public static final String RANGO_FECHA_INICIO = "RANGO_FECHA_INICIO";
	public static final String RANGO_FECHA_FIN = "RANGO_FECHA_FIN";
	public static final String CUFE = "CUFE";
	public static final String ORIGEN_CUFE = "ORIGEN_CUFE";
	public static final String QR = "QR";
	public static final String ORIGEN_IDENTIFICADOR_FISCAL = "ORIGEN_IDENTIFICADOR_FISCAL";
	public static final String IDENTIFICADOR_FISCAL_EDICOM = "IDENTIFICADOR_FISCAL_EDICOM";
	public static final String FECHA_ORIGEN_DOCUMENTO_FISCAL = "FECHA_ORIGEN_DOCUMENTO_FISCAL";
	
	@Autowired
	protected Documentos documentos;

	@Autowired
	protected VariablesServices variablesService;

	@Autowired
	private ByLServicioContadores byLServicioContadores;
	
	@Autowired
	private EdicomFile edFile;

	@Override
	public String obtenerIdFiscal(Ticket ticket) throws ProcesadorIdFiscalException {
		log.debug("obtenerIdFiscal() - Obteniendo número de identificación fiscal para COLOMBIA ...");
		String identificadorFiscal = null;

		Map<String, String> parametrosContador = new HashMap<>();
		Map<String, String> condicionesVigencias = new HashMap<>();

		parametrosContador.put("CODEMP", ticket.getEmpresa().getCodEmpresa());
		parametrosContador.put("CODALM", ticket.getTienda().getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODSERIE", ticket.getTienda().getAlmacenBean().getCodAlmacen());
		parametrosContador.put("CODCAJA", ticket.getCodCaja());
		parametrosContador.put("CODTIPODOCUMENTO", ticket.getCabecera().getCodTipoDocumento());
		parametrosContador.put("PERIODO", ((new Fecha()).getAño().toString()));

		// Se añaden vigencias para los rangos
		condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODCAJA, ticket.getCabecera().getCodCaja());
		condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODALM, ticket.getCabecera().getTienda().getCodAlmacen());
		condicionesVigencias.put(ConfigContadorRango.VIGENCIA_CODEMP, ticket.getCabecera().getEmpresa().getCodEmpresa());

		try {
			TipoDocumentoBean documentoActivo = documentos.getDocumento(ticket.getCabecera().getTipoDocumento());

			// Tratamos los rangos en caso de que los tenga
			ByLConfigContadorBean confContador = (ByLConfigContadorBean) ByLServicioConfigContadoresImpl.get().consultar(ByLVariablesServices.ID_FISCAL_CO + "_" + documentoActivo.getCodtipodocumento());
			if (!confContador.isRangosCargados()) {
				ConfigContadorRangoExample example = new ConfigContadorRangoExample();
				example.or().andIdContadorEqualTo(confContador.getIdContador());
				example.setOrderByClause(ConfigContadorRangoExample.ORDER_BY_RANGO_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FIN + ", "
				        + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_INICIO + ", " + ConfigContadorRangoExample.ORDER_BY_RANGO_FECHA_FIN);
				List<ConfigContadorRango> rangos = ServicioConfigContadoresRangos.get().consultar(example);

				if (rangos == null || rangos.isEmpty()) {
					throw new ProcesadorIdFiscalException("No existen rangos para el identificador fiscal.");
				}

				confContador.setRangos(rangos);
				confContador.setRangosCargados(true);
			}

			ByLContadorBean ticketContador;
			ticketContador = byLServicioContadores.consultarContadorActivo(confContador, parametrosContador, condicionesVigencias, ticket.getUidActividad(), true);

			if (ticketContador == null || ticketContador.getError() != null) {
				throw new ContadorNotFoundException(I18N.getTexto("No se ha encontrado contador con rangos disponible"));
			}

			identificadorFiscal = String.valueOf(ticketContador.getValor());

			if (StringUtils.isNotBlank(ticketContador.getConfigContadorRango().getRangoCoPref())) {
				FiscalData fiscalData = new FiscalData();
//				if (ticket.getCabecera().getFiscalData() != null) {
//					fiscalData = ticket.getCabecera().getFiscalData();
//				}

				recuperarPropertiesVentaOrigen(ticket, fiscalData);
				
				if (StringUtils.isNotBlank(ticketContador.getConfigContadorRango().getRangoCoNumResolucion())) {
					fiscalData.addProperty(NUM_RESOLUCION, ticketContador.getConfigContadorRango().getRangoCoNumResolucion());
				}
				if (ticketContador.getConfigContadorRango().getRangoCoFechaResolucion() != null) {
					fiscalData.addProperty(FECHA_RESOLUCION, ticketContador.getConfigContadorRango().getRangoCoFechaResolucionAsLocale());
				}
				if (StringUtils.isNotBlank(ticketContador.getConfigContadorRango().getRangoCoClaveTecnica())) {
					fiscalData.addProperty(CLAVE_TECNICA, ticketContador.getConfigContadorRango().getRangoCoClaveTecnica());
				}
				if (StringUtils.isNotBlank(ticketContador.getConfigContadorRango().getRangoCoPref())) {
					fiscalData.addProperty(PREF, ticketContador.getConfigContadorRango().getRangoCoPref());
					fiscalData.addProperty(IDENTIFICADOR_FISCAL_EDICOM, ticketContador.getConfigContadorRango().getRangoCoPref() + ticketContador.getValorFormateado());
				}

				fiscalData.addProperty(RANGO_INICIO, String.valueOf(ticketContador.getConfigContadorRango().getRangoInicio()));
				fiscalData.addProperty(RANGO_FIN, String.valueOf(ticketContador.getConfigContadorRango().getRangoFin()));
				fiscalData.addProperty(RANGO_FECHA_INICIO, ticketContador.getConfigContadorRango().getRangoFechaInicioAsLocale());
				fiscalData.addProperty(RANGO_FECHA_FIN, ticketContador.getConfigContadorRango().getRangoFechaFinAsLocale());

				ticket.getCabecera().setFiscalData(fiscalData);
			}
		}
		catch (Exception e1) {
			String msg = "Se ha producido un error procesando el identificador fiscal del ticket con uid " + ticket.getUidTicket() + " : " + e1.getMessage();
			log.error("obtenerIdFiscal() - " + msg, e1);
			throw new ProcesadorIdFiscalException(e1.getMessage());
		}

		return identificadorFiscal;
	}

	@Override
	public byte[] procesarDocumentoFiscal(Ticket ticket) throws Exception {
		log.debug("procesarDocumentoFiscal() - Inicio del envio del documento fiscal");
		byte[] respuesta = null;

		try {
			respuesta = envioMensajeEdicom(ticket);

			if (respuesta != null) {
				ticket.getCabecera().getFiscalData().addProperty(ByLVariablesServices.FICHERO_FISCAL_PROCESADO, "S");

				tratarRespuestaEdicom(respuesta, ticket);
			}
		}
		catch (Exception e) {
			log.error("procesarDocumentoFiscal() - " + e.getMessage(), e);
			if (e instanceof SocketTimeoutException) {
				ticket.getCabecera().getFiscalData().addProperty(ByLVariablesServices.FICHERO_FISCAL_PROCESADO, "N");
				throw new SocketTimeoutException("procesarDocumentoFiscal() - Ha ocurrido un error al realizar el envio del documento: " + e.getMessage());
			}
			else {
				limpiaPropertiesFiscalData(ticket);
				throw new ProcesarDocumentoFiscalException("procesarDocumentoFiscal() - " + e.getMessage(), e);
			}
		}

		return respuesta;
	}

	private byte[] envioMensajeEdicom(Ticket ticket) throws Exception {
		log.debug("envioMensajeEdicom() - Inicio del proceso de envio a EDICOM");
		byte[] respuesta = null;
		
		File archivoComprido;

		archivoComprido = edFile.devuelveArchivoComprimidoEdicom(ticket);

		Base64Coder base64Coder = new Base64Coder(Base64Coder.UTF8);
		String ficheroCodificado = new String(base64Coder.encodeBase64(FileUtils.readFileToByteArray(archivoComprido)));

		archivoComprido.delete();

		ticket.getCabecera().getFiscalData().addProperty(ByLVariablesServices.FICHERO_FISCAL_CODIFICADO, ficheroCodificado);

		String user = variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_USER);
		String password = variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_PASSWORD);
		String domain = variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_DOMAIN);
		String group = variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_GROUP);
		Integer publishType = variablesService.getVariableAsInteger(ByLVariablesServices.CO_EDICOM_PUBLISHTYPE);
		String process = variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_PROCESS);
		Integer returnDataType = variablesService.getVariableAsInteger(ByLVariablesServices.CO_EDICOM_RETURNDATATYPE);
		String returnDataProcess = variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_RETURNDATAPROCESS);

		log.debug("envioMensajeEdicom() - Inicio de la llamada SOAP a EDICOM");
		URL url = new URL(variablesService.getVariableAsString(ByLVariablesServices.CO_EDICOM_RESTURL));
		respuesta = EdicomService.getPublishDocument(user, password, domain, group, publishType, ficheroCodificado.getBytes(), process, false, true, true, returnDataType, returnDataProcess, null, url);
		log.debug("envioMensajeEdicom() - Fin de la llamada SOAP a EDICOM");

		return respuesta;
	}

	private void tratarRespuestaEdicom(byte[] respuesta, Ticket ticket) throws IOException, SAXException, XMLDocumentException {
		log.debug("tratarRespuestaEdicom()");
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(respuesta));
		// ZipEntry entry = null;
		ByteArrayOutputStream baos = null;
		while (zipStream.getNextEntry() != null) {

			// String entryName = entry.getName();

			baos = new ByteArrayOutputStream();

			byte[] byteBuff = new byte[4096];
			int bytesRead = 0;
			while ((bytesRead = zipStream.read(byteBuff)) != -1) {
				baos.write(byteBuff, 0, bytesRead);
			}

			log.debug("XML RESPUESTA EDICOM \n" + new String(baos.toByteArray(), "UTF-8") + "\n");
			
			Document xml = XMLDocumentUtils.createDocumentBuilder().parse(new ByteArrayInputStream(baos.toByteArray()));
			Element root = xml.getDocumentElement();
			String UUIDCufe = XMLDocumentUtils.getTagValueAsString(root, "cbc:UUID", true);
			String QRCode = XMLDocumentUtils.getTagValueAsString(root, "sts:QRCode", true);

			ticket.getCabecera().getFiscalData().addProperty(CUFE, UUIDCufe);
			ticket.getCabecera().getFiscalData().addProperty(QR, QRCode);

			baos.close();
			zipStream.closeEntry();
		}
		zipStream.close();
	}

	private void limpiaPropertiesFiscalData(Ticket ticket) {
		log.debug("limpiaPropertiesFiscalData() - Se limpian ciertas properties de fiscal data en caso de error");

		FiscalDataProperty propertyFicheroFiscal = ticket.getCabecera().getFiscalData().getProperty(ByLVariablesServices.FICHERO_FISCAL_CODIFICADO);
		FiscalDataProperty propertyProcesado = ticket.getCabecera().getFiscalData().getProperty(ByLVariablesServices.FICHERO_FISCAL_PROCESADO);

		ticket.getCabecera().getFiscalData().getProperties().remove(propertyFicheroFiscal);
		ticket.getCabecera().getFiscalData().getProperties().remove(propertyProcesado);
	}
	
	private void recuperarPropertiesVentaOrigen(Ticket ticket, FiscalData fiscalData) {
		if (ticket.isEsDevolucion() && ticket.getCabecera().getFiscalData() != null) {
			log.debug("recuperarPropertiesVentaOrigen() - Recuperamos las properties fiscales del documento origen en el caso de devolucion");

			FiscalDataProperty origenCufe = ticket.getCabecera().getFiscalData().getProperty(ORIGEN_CUFE);
			FiscalDataProperty origenIdFiscal = ticket.getCabecera().getFiscalData().getProperty(ORIGEN_IDENTIFICADOR_FISCAL);
			FiscalDataProperty origenFechaOrigen = ticket.getCabecera().getFiscalData().getProperty(FECHA_ORIGEN_DOCUMENTO_FISCAL);

			if (origenCufe != null) {
				fiscalData.addProperty(ORIGEN_CUFE, origenCufe.getValue());
			}

			if (origenIdFiscal != null) {
				fiscalData.addProperty(ORIGEN_IDENTIFICADOR_FISCAL, origenIdFiscal.getValue());
			}

			if (origenFechaOrigen != null) {
				fiscalData.addProperty(FECHA_ORIGEN_DOCUMENTO_FISCAL, origenFechaOrigen.getValue());
			}
		}
	}
}
