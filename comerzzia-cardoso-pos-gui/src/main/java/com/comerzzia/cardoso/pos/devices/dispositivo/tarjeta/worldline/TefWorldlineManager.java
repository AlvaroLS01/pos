package com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.worldline;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.services.pagos.worldline.WorldlineService;
import com.comerzzia.pos.dispositivo.tarjeta.TefBasicManager;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.util.i18n.I18N;

@Component
@Scope("prototype")
public class TefWorldlineManager extends TefBasicManager {

	private static final Logger log = Logger.getLogger(TefWorldlineManager.class);

	private final String ID_TERMINAL = "idTerminal";
	private final String ID_COMERCIO = "codComercio";
	private final String TIMEOUT_CANCEL_PAY = "timeoutCancelPay";

	@Autowired
	protected WorldlineService wordlineService;

	private String idTerminal;
	private String codComercio;
	private int timeoutCancelPay;
	private int contadorPagos = 0;

	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		List<ConfigurationPropertyDto> properties = new ArrayList<>();
		properties.add(new ConfigurationPropertyDto(ID_TERMINAL, I18N.getTexto("ID del terminal")));
		properties.add(new ConfigurationPropertyDto(ID_COMERCIO, I18N.getTexto("ID del comercio")));
		properties.add(new ConfigurationPropertyDto(TIMEOUT_CANCEL_PAY, I18N.getTexto("Timeout para las cancelaciones (segundos)")));
		return properties;
	}

	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);
		for (String parametro : configuration.getConfigurationProperties().keySet()) {
			if (parametro.equals(ID_TERMINAL)) {
				idTerminal = configuration.getConfigurationProperty(parametro);
			}
			if (parametro.equals(ID_COMERCIO)) {
				codComercio = configuration.getConfigurationProperty(parametro);
			}
			if (parametro.equals(TIMEOUT_CANCEL_PAY) && StringUtils.isNotBlank(configuration.getConfigurationProperty(parametro))) {
				timeoutCancelPay = Integer.parseInt(configuration.getConfigurationProperty(parametro));
			}

		}
	}

	@Override
	public void conect() {

	}

	@Override
	public void disconect() {

	}

	@Override
	public void cancel() {
		wordlineService.closeAuxiliarScreen();
	}

	@Override
	protected void doPaymentRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) throws Exception {
		log.debug("doPaymentRequest() - Iniciando petición para realizar pago en TPV...");
		wordlineService.pay((TicketVentaAbono) ticket, idTerminal, codComercio, contadorPagos, amount, response, timeoutCancelPay);
		contadorPagos++;
	}

	@Override
	protected void doReturnRequest(BigDecimal amount, DatosRespuestaPagoTarjeta response) throws Exception {
		log.debug("doReturnRequest() - Iniciando petición para realizar devolución en TPV...");
		wordlineService.refund((TicketVentaAbono) ticket, idTerminal, codComercio, contadorPagos, amount, response);
		contadorPagos++;
	}

	@Override
	protected void doPaymentCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) throws Exception {

		log.debug("doPaymentCancelRequest() - Iniciando petición para realizar cancelación de pago en TPV...");
		wordlineService.cancelPay(payment, idTerminal, false);
	}

	@Override
	protected void doReturnCancelRequest(PaymentDto payment, DatosRespuestaPagoTarjeta response) throws Exception {
		log.debug("doReturnCancelRequest() - Iniciando petición para realizar cancelación de pago en TPV...");
		wordlineService.cancelRefund((TicketVentaAbono) ticket, payment, idTerminal, codComercio, response);
	}

	public void doAutomaticCancel(String uidticket, boolean esProcesoAutomatico) throws Exception {
		wordlineService.automaticCancel(uidticket, idTerminal, timeoutCancelPay, esProcesoAutomatico);
	}

	public String getIdTerminal() {
		return idTerminal;
	}

}
