package com.comerzzia.bimbaylola.pos.services.countries.portugal;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.base64.Base64Coder;
import com.comerzzia.pos.persistence.core.impuestos.tratamientos.TratamientoImpuestoBean;
import com.comerzzia.pos.services.core.impuestos.tratamientos.TratamientoImpuestoService;
import com.comerzzia.pos.services.core.sesion.SesionAplicacion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.fiscaldata.FiscalData;
import com.comerzzia.pos.services.fiscaldata.FiscalDataException;
import com.comerzzia.pos.services.fiscaldata.FiscalDataService;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Service("ByLFiscalDataServicePT")
public class ByLFiscalDataServicePT implements FiscalDataService {

	protected static final Logger log = Logger.getLogger(ByLFiscalDataServicePT.class);

	private static final String SEPARATOR = "*";
	private static final String DEFAULT_DOCUMENT_STATE = "N";
	private static final String DEFAULT_CIF = "999999990";
	private static final String DATE_FORMAT_PATTERN = "yyyyMMdd";
	private static final String DEFAULT_ATCUD = "0";
	private static final String DEFAULT_IVA = "0";
	private static final String DEFAULT_SOFTWATE_CERTIFICATE = "2175";
	private static final String PORTUGAL = "PT";
	private static final String PORTUGAL_AZORES = "AC";
	private static final String PORTUGAL_MADEIRA = "MA";
	protected static final String PROPERTY_QR = "QR";
	protected static final String PROPERTY_ATCUD = "ATCUD";
	protected static final String EXTENSION_RANGE_ID = "RANGE_ID";

	private static final String CODIMP_NORMAL = "1";
	private static final String CODIMP_REDUCED = "2";
	private static final String CODIMP_SUPER_REDUCED = "3";
	private static final String CODIMP_EXEMPT = "4";

	@Autowired
	TratamientoImpuestoService taxTreatmentService;

	@Autowired
	SesionAplicacion applicationSession;

	@Autowired
	protected VariablesServices variablesService;

	protected Date startValidationDate;

	public ByLFiscalDataServicePT() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2023);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		startValidationDate = cal.getTime();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public FiscalData getFiscalData(ITicket ticket) throws FiscalDataException {

		log.debug("getFiscalData() - Obteniendo datos fiscales para Portugal del ticket " + ticket.getCabecera().getCodTicket());

		FiscalData fiscalData = new FiscalData();

		String atcud = generateATCUD(ticket);
		fiscalData.addProperty(PROPERTY_QR, generateQRBase64Data(ticket, atcud));
		fiscalData.addProperty(PROPERTY_ATCUD, atcud);

		return fiscalData;
	}

	@SuppressWarnings("rawtypes")
	private String generateQRBase64Data(ITicket ticket, String atcud) {
		String a = "A:" + ticket.getCabecera().getEmpresa().getCif();

		String b = "B:";
		if (ticket.getCliente().getDatosFactura() != null) {
			b += ticket.getCliente().getDatosFactura().getCif();
		}
		else if (ticket.getCliente().getCif() != null && ticket.getCliente().getCif() != ticket.getTienda().getCliente().getCif()) {
			b += ticket.getCliente().getCif();
		}
		else {
			b += DEFAULT_CIF;
		}

		String c = "C:";
		if (ticket.getCliente().getDatosFactura() != null) {
			c += ticket.getCliente().getDatosFactura().getPais();
		}
		else {
			c += ticket.getCliente().getCodpais();
		}

		String d = "D:" + ticket.getCabecera().getCodTipoDocumento();

		String e = "E:" + DEFAULT_DOCUMENT_STATE;

		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		String f = "F:" + dateFormat.format(new Date());

		String g = "G:" + ticket.getCabecera().getCodTicket();

		String h = "H:" + DEFAULT_ATCUD;

		String tax1 = "";
		String tax2 = "";
		String tax3 = "";
		String tax4 = "";
		String tax5 = "";
		String tax6 = "";
		String tax7 = "";
		String tax8 = "";

		BigDecimal normalBase = BigDecimal.ZERO.setScale(2);
		BigDecimal normalTaxes = BigDecimal.ZERO.setScale(2);
		BigDecimal reducedBase = BigDecimal.ZERO.setScale(2);
		BigDecimal reducedTaxes = BigDecimal.ZERO.setScale(2);
		BigDecimal superReducedBase = BigDecimal.ZERO.setScale(2);
		BigDecimal superReducedTaxes = BigDecimal.ZERO.setScale(2);
		BigDecimal exemptBase = BigDecimal.ZERO.setScale(2);

		for (Object subtotal : ticket.getCabecera().getSubtotalesIva()) {
			SubtotalIvaTicket subtotalIva = (SubtotalIvaTicket) subtotal;
			switch (subtotalIva.getCodImpuesto()) {
				case CODIMP_NORMAL:
					normalBase = normalBase.add(subtotalIva.getBase());
					normalTaxes = normalTaxes.add(subtotalIva.getImpuestos());
					break;
				case CODIMP_REDUCED:
					reducedBase = reducedBase.add(subtotalIva.getBase());
					reducedTaxes = reducedTaxes.add(subtotalIva.getImpuestos());
					break;
				case CODIMP_SUPER_REDUCED:
					superReducedBase = superReducedBase.add(subtotalIva.getBase());
					superReducedTaxes = superReducedTaxes.add(subtotalIva.getImpuestos());
					break;
				case CODIMP_EXEMPT:
					exemptBase = exemptBase.add(subtotalIva.getBase());
					break;
			}
		}

		Long taxTreatmentId = ticket.getCliente().getIdTratImpuestos();

		TratamientoImpuestoBean taxTreatment = taxTreatmentService.consultarTratamientoImpuesto(applicationSession.getUidActividad(), taxTreatmentId);

		if (PORTUGAL_AZORES.equals(taxTreatment.getRegionImpuestos())) {
			tax1 = "J1:" + PORTUGAL_AZORES;
			tax2 = "J2:" + exemptBase.toString();
			tax3 = "J3:" + superReducedBase.toString();
			tax4 = "J4:" + superReducedTaxes.toString();
			tax5 = "J5:" + reducedBase.toString();
			tax6 = "J6:" + reducedTaxes.toString();
			tax7 = "J7:" + normalBase.toString();
			tax8 = "J8:" + normalTaxes.toString();
		}
		else if (PORTUGAL_MADEIRA.equals(taxTreatment.getRegionImpuestos())) {
			tax1 = "K1:" + PORTUGAL_MADEIRA;
			tax2 = "K2:" + exemptBase.toString();
			tax3 = "K3:" + superReducedBase.toString();
			tax4 = "K4:" + superReducedTaxes.toString();
			tax5 = "K5:" + reducedBase.toString();
			tax6 = "K6:" + reducedTaxes.toString();
			tax7 = "K7:" + normalBase.toString();
			tax8 = "K8:" + normalTaxes.toString();
		}
		else {
			tax1 = "I1:" + DEFAULT_IVA;
			if (!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getImpuestos())) {
				tax1 = "I1:" + PORTUGAL;
				tax2 = "I2:" + exemptBase.toString();
				tax3 = "I3:" + superReducedBase.toString();
				tax4 = "I4:" + superReducedTaxes.toString();
				tax5 = "I5:" + reducedBase.toString();
				tax6 = "I6:" + reducedTaxes.toString();
				tax7 = "I7:" + normalBase.toString();
				tax8 = "I8:" + normalTaxes.toString();
			}

		}

		String n = "N:" + ticket.getCabecera().getTotales().getImpuestos().toString();
		String o = "O:" + ticket.getCabecera().getTotales().getTotal().toString();
		BigDecimal surchargeFee = BigDecimal.ZERO;
		for (Object subtotal : ticket.getCabecera().getSubtotalesIva()) {
			SubtotalIvaTicket subtotalIva = (SubtotalIvaTicket) subtotal;
			surchargeFee = surchargeFee.add(subtotalIva.getCuotaRecargo());
		}
		String p = "P:" + surchargeFee.toString();

		String firma = ticket.getCabecera().getFirma().getFirma();
		String q = "Q:" + new StringBuilder().append(firma.charAt(0)).append(firma.charAt(10)).append(firma.charAt(20)).append(firma.charAt(30)).toString();
		String r = "R:" + DEFAULT_SOFTWATE_CERTIFICATE;

		String data = a + SEPARATOR + b + SEPARATOR + c + SEPARATOR + d + SEPARATOR + e + SEPARATOR + f + SEPARATOR + g + SEPARATOR + h + SEPARATOR + tax1 + SEPARATOR
		        + (StringUtils.isNotBlank(tax2) ? tax2 + SEPARATOR : "") + (StringUtils.isNotBlank(tax3) ? tax3 + SEPARATOR : "") + (StringUtils.isNotBlank(tax4) ? tax4 + SEPARATOR : "")
		        + (StringUtils.isNotBlank(tax5) ? tax5 + SEPARATOR : "") + (StringUtils.isNotBlank(tax6) ? tax6 + SEPARATOR : "") + (StringUtils.isNotBlank(tax7) ? tax7 + SEPARATOR : "")
		        + (StringUtils.isNotBlank(tax8) ? tax8 + SEPARATOR : "") + n + SEPARATOR + o + SEPARATOR + p + SEPARATOR + q + SEPARATOR + r;

		log.debug("getFiscalData() - La cadena generada para el QR es " + data);

		Base64Coder base64Coder = new Base64Coder(Base64Coder.UTF8);

		String base64Data = null;
		try {
			base64Data = base64Coder.encodeBase64(data);
		}
		catch (UnsupportedEncodingException e1) {
			throw new RuntimeException(e1);
		}

		return base64Data;
	}

	@SuppressWarnings("rawtypes")
	protected String generateATCUD(ITicket ticket) throws FiscalDataException {
		String atcud = DEFAULT_ATCUD;
		String validationCode = (String) ((TicketVentaAbono) ticket).getExtension(EXTENSION_RANGE_ID);
		if (StringUtils.isBlank(validationCode) && new Date().after(startValidationDate)) {
			String msg = "No se dispone de un código de validación para generar un ATCUD y finalizar la venta";
			log.error(msg);
			throw new FiscalDataException(I18N.getTexto(msg));
		}

		if (StringUtils.isNotBlank(validationCode)) {
			String separator = variablesService.getVariableAsString(VariablesServices.CONTADORES_CARACTER_SEPARADOR);
			String seqNumber = ticket.getCabecera().getCodTicket().replace(ticket.getCabecera().getSerieTicket() + separator, "");
			atcud = validationCode + "-" + seqNumber;
		}

		return atcud;
	}

}
