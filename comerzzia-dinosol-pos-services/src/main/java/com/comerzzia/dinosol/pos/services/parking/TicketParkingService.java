package com.comerzzia.dinosol.pos.services.parking;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
public class TicketParkingService {
	
	private Logger log = Logger.getLogger(TicketParkingService.class);
	
	public static String ID_VARIABLE_CODIGO_DESCUENTO = "X_PARKING_QR.COD_DESCUENTO";
	public static String ID_VARIABLE_IMPORTE_DESCUENTO = "X_PARKING_QR.IMPORTE_DESCUENTO";
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private VariablesServices variablesServices;
	
	public void generarQrParking(TicketVentaAbono ticket) {
		BigDecimal importeDescuento = variablesServices.getVariableAsBigDecimal(ID_VARIABLE_IMPORTE_DESCUENTO);
		BigDecimal totalAPagar = ticket.getTotales().getTotalAPagar();
		
		if(BigDecimalUtil.isMayorOrIgual(totalAPagar, importeDescuento)) {
			String codTicket = ticket.getCabecera().getCodTicket();
			codTicket = codTicket.substring(codTicket.length() - 4, codTicket.length());
			String codCaja = sesion.getAplicacion().getCodCaja();
			
			String qr = generarCodigoQrDescuento(codCaja + codTicket);
			
			((DinoCabeceraTicket) ticket.getCabecera()).setCodigoParking(qr);
			((DinoCabeceraTicket) ticket.getCabecera()).setCodigoParkingFormatoQr(true);
		}
		else {
			log.debug("generarQrParking() - No se generará QR de parking debido a que no se ha superado el importe establecido.");
			log.debug("generarQrParking() - Importe de la compra: " + totalAPagar + ". Importe mínimo: " + importeDescuento);
		}
	}

	public String generarCodigoQrDescuento(String codTicket) {
		String codCaja = sesion.getAplicacion().getCodCaja();
		
		String emisor = "00" + sesion.getAplicacion().getCodAlmacen();
		String caja = StringUtils.leftPad(codCaja, 3, '0');
		String fecha = new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		String numSerie = codTicket;
		
		String codDescuento = variablesServices.getVariableAsString(ID_VARIABLE_CODIGO_DESCUENTO);

		String qr = emisor + caja + fecha + numSerie + codDescuento;
		log.debug("generarQrParking() - QR de parking generado: " + qr);
		return qr;
	}

}
