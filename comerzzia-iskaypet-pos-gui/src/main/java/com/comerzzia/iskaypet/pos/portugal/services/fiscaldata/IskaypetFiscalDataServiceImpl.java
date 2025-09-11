package com.comerzzia.iskaypet.pos.portugal.services.fiscaldata;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.base64.Base64Coder;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.pos.persistence.core.impuestos.tratamientos.TratamientoImpuestoBean;
import com.comerzzia.pos.portugal.services.fiscaldata.FiscalDataServiceImpl;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Service("IskaypetFiscalDataServicePT")
public class IskaypetFiscalDataServiceImpl extends FiscalDataServiceImpl {

	@Override
	protected String generateQRBase64Data(ITicket ticket, String atcud) {
		String a = "A:" + ticket.getCabecera().getEmpresa().getCif();
		String b = "B:";
		if (ticket.getCabecera() instanceof IskaypetCabeceraTicket && StringUtils.isNotBlank(((IskaypetCabeceraTicket) ticket.getCabecera()).getDocumentoCliente())) {
			b = b + ((IskaypetCabeceraTicket) ticket.getCabecera()).getDocumentoCliente();
		}
		else if (ticket.getCliente().getDatosFactura() != null) {
			b = b + ticket.getCliente().getDatosFactura().getCif();
		}
		else {
			b = b + "999999990";
		}

		String c = "C:";
		if (ticket.getCliente().getDatosFactura() != null) {
			c = c + ticket.getCliente().getDatosFactura().getPais();
		}
		else {
			c = c + ticket.getCliente().getCodpais();
		}

		String d = "D:" + ticket.getCabecera().getCodTipoDocumento();
		String e = "E:N";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String f = "F:" + dateFormat.format(new Date());
		String g = "G:" + ticket.getCabecera().getCodTicket();
		String h = "H:" + (StringUtils.isNotBlank(atcud) ? atcud : "0");
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
		Iterator var27 = ticket.getCabecera().getSubtotalesIva().iterator();

		String o;
		while (var27.hasNext()) {
			Object subtotal = var27.next();
			SubtotalIvaTicket subtotalIva = (SubtotalIvaTicket) subtotal;
			switch (subtotalIva.getCodImpuesto()) {
				case "1":
					normalBase = normalBase.add(subtotalIva.getBase());
					normalTaxes = normalTaxes.add(subtotalIva.getImpuestos());
					break;
				case "2":
					reducedBase = reducedBase.add(subtotalIva.getBase());
					reducedTaxes = reducedTaxes.add(subtotalIva.getImpuestos());
					break;
				case "3":
					superReducedBase = superReducedBase.add(subtotalIva.getBase());
					superReducedTaxes = superReducedTaxes.add(subtotalIva.getImpuestos());
					break;
				case "4":
					exemptBase = exemptBase.add(subtotalIva.getBase());
			}
		}

		Long taxTreatmentId = ticket.getCliente().getIdTratImpuestos();
		TratamientoImpuestoBean taxTreatment = this.taxTreatmentService.consultarTratamientoImpuesto(this.applicationSession.getUidActividad(), taxTreatmentId);
		if ("AC".equals(taxTreatment.getRegionImpuestos())) {
			tax1 = "J1:AC";
			tax2 = "J2:" + exemptBase.toString();
			tax3 = "J3:" + superReducedBase.toString();
			tax4 = "J4:" + superReducedTaxes.toString();
			tax5 = "J5:" + reducedBase.toString();
			tax6 = "J6:" + reducedTaxes.toString();
			tax7 = "J7:" + normalBase.toString();
			tax8 = "J8:" + normalTaxes.toString();
		}
		else if ("MA".equals(taxTreatment.getRegionImpuestos())) {
			tax1 = "K1:MA";
			tax2 = "K2:" + exemptBase.toString();
			tax3 = "K3:" + superReducedBase.toString();
			tax4 = "K4:" + superReducedTaxes.toString();
			tax5 = "K5:" + reducedBase.toString();
			tax6 = "K6:" + reducedTaxes.toString();
			tax7 = "K7:" + normalBase.toString();
			tax8 = "K8:" + normalTaxes.toString();
		}
		else {
			tax1 = "I1:0";
			if (!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getImpuestos())) {
				tax1 = "I1:PT";
				tax2 = "I2:" + exemptBase.toString();
				tax3 = "I3:" + superReducedBase.toString();
				tax4 = "I4:" + superReducedTaxes.toString();
				tax5 = "I5:" + reducedBase.toString();
				tax6 = "I6:" + reducedTaxes.toString();
				tax7 = "I7:" + normalBase.toString();
				tax8 = "I8:" + normalTaxes.toString();
			}
		}

		String n = "N:" + ticket.getCabecera().getTotales().getImpuestos().setScale(2).toString();
		o = "O:" + ticket.getCabecera().getTotales().getTotal().setScale(2).toString();
		BigDecimal surchargeFee = BigDecimal.ZERO.setScale(2);

		SubtotalIvaTicket subtotalIva;
		for (Iterator var32 = ticket.getCabecera().getSubtotalesIva().iterator(); var32.hasNext(); surchargeFee = surchargeFee.add(subtotalIva.getCuotaRecargo())) {
			Object subtotal = var32.next();
			subtotalIva = (SubtotalIvaTicket) subtotal;
		}

		String p = "P:" + surchargeFee.toString();
		String firma = ticket.getCabecera().getFirma().getFirma();
		String q = "Q:" + "" + firma.charAt(0) + firma.charAt(10) + firma.charAt(20) + firma.charAt(30);
		String r = "R:2175";
		String data = a + "*" + b + "*" + c + "*" + d + "*" + e + "*" + f + "*" + g + "*" + h + "*" + tax1 + "*" + (StringUtils.isNotBlank(tax2) ? tax2 + "*" : "")
		        + (StringUtils.isNotBlank(tax3) ? tax3 + "*" : "") + (StringUtils.isNotBlank(tax4) ? tax4 + "*" : "") + (StringUtils.isNotBlank(tax5) ? tax5 + "*" : "")
		        + (StringUtils.isNotBlank(tax6) ? tax6 + "*" : "") + (StringUtils.isNotBlank(tax7) ? tax7 + "*" : "") + (StringUtils.isNotBlank(tax8) ? tax8 + "*" : "") + n + "*" + o + "*" + p + "*"
		        + q + "*" + r;
		log.debug("getFiscalData() - La cadena generada para el QR es " + data);
		Base64Coder base64Coder = new Base64Coder("UTF-8");
		String base64Data = null;

		try {
			base64Data = base64Coder.encodeBase64(data);
			return base64Data;
		}
		catch (UnsupportedEncodingException var40) {
			throw new RuntimeException(var40);
		}
	}

}
