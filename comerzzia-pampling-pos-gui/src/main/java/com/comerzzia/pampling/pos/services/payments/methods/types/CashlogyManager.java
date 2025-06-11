package com.comerzzia.pampling.pos.services.payments.methods.types;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.ConfigurationPropertyDto;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.ContadoManager;
import com.comerzzia.tier.librerias.cashlogyclient.CashlogyClient;
import com.comerzzia.tier.librerias.cashlogyclient.model.CancelSaleRequest;
import com.comerzzia.tier.librerias.cashlogyclient.model.CancelSaleResponse;
import com.comerzzia.tier.librerias.cashlogyclient.model.DispenseRequest;
import com.comerzzia.tier.librerias.cashlogyclient.model.DispenseResponse;
import com.comerzzia.tier.librerias.cashlogyclient.model.LoginResponse;
import com.comerzzia.tier.librerias.cashlogyclient.model.SaleRequest;
import com.comerzzia.tier.librerias.cashlogyclient.model.SaleResponse;

@Component
@Scope("prototype")
public class CashlogyManager extends ContadoManager {

	private static final Logger log = Logger.getLogger(CashlogyManager.class);

	private String cashlogyBaseUrl;
	private String cashlogyUser;
	private String cashlogyPassword;
	private String deviceId;
	private boolean cashlogyActivo;

	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);
		Map<String, String> params = configuration.getConfigurationProperties();
		this.cashlogyBaseUrl = params.get("cashlogyBaseUrl");
		this.cashlogyUser = params.get("cashlogyUser");
		this.cashlogyPassword = params.get("cashlogyPassword");
		this.deviceId = params.get("deviceId");
		String activoParam = params.get("cashlogyActivo");
		this.cashlogyActivo = activoParam != null && activoParam.equalsIgnoreCase("S");
		log.debug("Configuración de CashLogy establecida: cashlogyBaseUrl=" + cashlogyBaseUrl + ", cashlogyUser=" + cashlogyUser + ", deviceId=" + deviceId + ", cashlogyActivo=" + cashlogyActivo);
	}

	@Override
	public List<ConfigurationPropertyDto> getConfigurationProperties() {
		List<ConfigurationPropertyDto> properties = new ArrayList<ConfigurationPropertyDto>();
		properties.add(new ConfigurationPropertyDto("cashlogyBaseUrl", "Cashlogy URL"));
		properties.add(new ConfigurationPropertyDto("cashlogyUser", "Cashlogy User"));
		properties.add(new ConfigurationPropertyDto("cashlogyPassword", "Cashlogy Password"));
		properties.add(new ConfigurationPropertyDto("deviceId", "Device ID"));
		properties.add(new ConfigurationPropertyDto("cashlogyActivo", "Cashlogy Activo (S/N)"));
		return properties;
	}

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		if (!cashlogyActivo) {
			log.debug("cashlogyActivo es N, delegando el pago a la implementación estándar.");
			return super.pay(amount);
		}
		log.debug("Iniciando proceso de pago (Cashlogy personalizado) para el monto: " + amount);
		int requiredCents = amount.multiply(new BigDecimal(100)).intValue();
		log.debug("Monto convertido a céntimos: " + requiredCents);

		CashlogyClient client = new CashlogyClient(cashlogyBaseUrl);
		log.debug("Intentando iniciar sesión con el usuario: " + cashlogyUser);
		LoginResponse loginResp = client.login(cashlogyUser, cashlogyPassword);
		if (!"OK".equalsIgnoreCase(loginResp.getResult())) {
			log.error("Error en el inicio de sesión: " + loginResp.getMensajeError());
			throw new PaymentException("Error en login Cashlogy: " + loginResp.getMensajeError());
		}
		String token = loginResp.getToken();
		log.debug("Inicio de sesión exitoso, token obtenido: " + token);

		String operationId = generateOperationId();
		String posId = getPosId();
		log.debug("ID de operación generado: " + operationId + ", ID del punto de venta: " + posId);

		SaleRequest saleReq = new SaleRequest(deviceId, operationId, posId, requiredCents, "EUR");
		log.debug("Enviando solicitud de venta: deviceId=" + deviceId + ", céntimos requeridos=" + requiredCents);

		SaleResponse saleResp = client.sale(saleReq, token);
		if (!"OK".equalsIgnoreCase(saleResp.getResult())) {
			log.error("Error en la venta: " + saleResp.getMensajeError());
			throw new PaymentException("Error en venta Cashlogy: " + saleResp.getMensajeError());
		}
		log.debug("Respuesta de venta recibida: totalDepositado=" + saleResp.getTotalDeposited() + ", totalEntregado=" + saleResp.getTotalDispensed());

		BigDecimal depositedAmount = new BigDecimal(saleResp.getTotalDeposited()).divide(new BigDecimal(100));
		log.debug("Importe depositado (totalDepositado): " + depositedAmount);

		PaymentOkEvent event = new PaymentOkEvent(this, getPaymentId(), depositedAmount);
		log.debug("Evento de pago OK generado para paymentId=" + getPaymentId() + " y monto=" + depositedAmount);
		getEventHandler().paymentOk(event);
		return true;
	}

	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		if (!cashlogyActivo) {
			log.debug("cashlogyActivo es N, delegando la devolución a la implementación estándar.");
			return super.returnAmount(amount);
		}
		log.debug("Iniciando proceso de devolución (Cashlogy personalizado) para el monto: " + amount);
		int returnCents = amount.multiply(new BigDecimal(100)).intValue();
		CashlogyClient client = new CashlogyClient(cashlogyBaseUrl);
		LoginResponse loginResp = client.login(cashlogyUser, cashlogyPassword);
		if (!"OK".equalsIgnoreCase(loginResp.getResult())) {
			throw new PaymentException("Error en login de la devolución del Cashlogy: " + loginResp.getMensajeError());
		}
		String token = loginResp.getToken();
		String operationId = generateOperationId();
		String posId = getPosId();
		log.debug("Proceso de devolución: operationId=" + operationId + ", posId=" + posId + ", céntimos a devolver=" + returnCents);

		DispenseRequest dispenseReq = new DispenseRequest(deviceId, operationId, posId, returnCents, "EUR");
		DispenseResponse dispenseResp = client.dispense(dispenseReq, token);
		if (!"OK".equalsIgnoreCase(dispenseResp.getResult())) {
			throw new PaymentException("Error en dispensación Cashlogy (devolución): " + dispenseResp.getMensajeError());
		}
		int dispensedCents = dispenseResp.getTotalDispensed();
		BigDecimal netAmount = new BigDecimal(dispensedCents).divide(new BigDecimal(100));

		PaymentOkEvent event = new PaymentOkEvent(this, getPaymentId(), netAmount);
		log.debug("Evento de pago OK de devolución generado para paymentId=" + getPaymentId() + " y monto=" + netAmount);
		getEventHandler().paymentOk(event);
		return true;
	}

	@Override
	public boolean cancelPay(PaymentDto paymentDto) throws PaymentException {
		if (!cashlogyActivo) {
			log.debug("cashlogyActivo es N, delegando la cancelación de pago a la implementación estándar.");
			return super.cancelPay(paymentDto);
		}
		log.debug("Iniciando proceso de cancelación de pago (Cashlogy personalizado) para paymentId: " + paymentDto.getPaymentId());
		CashlogyClient client = new CashlogyClient(cashlogyBaseUrl);
		LoginResponse loginResp = client.login(cashlogyUser, cashlogyPassword);
		if (!"OK".equalsIgnoreCase(loginResp.getResult())) {
			throw new PaymentException("Error en login Cashlogy (cancelación de pago): " + loginResp.getMensajeError());
		}
		String token = loginResp.getToken();
		String paymentIdStr = String.valueOf(paymentDto.getPaymentId());
		String posId = getPosId();
		log.debug("Cancelación de pago: deviceId=" + deviceId + ", paymentIdStr=" + paymentIdStr + ", posId=" + posId);
		CancelSaleRequest cancelReq = new CancelSaleRequest(deviceId, paymentIdStr, posId);
		CancelSaleResponse cancelResp = client.cancelSale(cancelReq, token);
		if (!"OK".equalsIgnoreCase(cancelResp.getResult())) {
			throw new PaymentException("Error al cancelar pago Cashlogy: " + cancelResp.getMensajeError());
		}
		PaymentOkEvent event = new PaymentOkEvent(this, paymentDto.getPaymentId(), paymentDto.getAmount());
		event.setCanceled(true);
		log.debug("Evento de cancelación de pago generado para paymentId=" + paymentDto.getPaymentId());
		getEventHandler().paymentOk(event);
		return true;
	}

	@Override
	public boolean cancelReturn(PaymentDto paymentDto) throws PaymentException {
		if (!cashlogyActivo) {
			log.debug("cashlogyActivo es N, delegando la cancelación de devolución a la implementación estándar.");
			return super.cancelReturn(paymentDto);
		}
		log.debug("Iniciando proceso de cancelación de devolución (Cashlogy personalizado) para paymentId: " + paymentDto.getPaymentId());
		PaymentOkEvent event = new PaymentOkEvent(this, paymentDto.getPaymentId(), paymentDto.getAmount());
		event.setCanceled(true);
		getEventHandler().paymentOk(event);
		return true;
	}

	public boolean isCashlogyActivo() {
		return cashlogyActivo;
	}

	@Override
	public boolean recordCashFlowImmediately() {
		return true;
	}

	private String generateOperationId() {
		String opId = String.valueOf(System.currentTimeMillis());
		log.debug("ID de operación generado: " + opId);
		return opId;
	}

	private String getPosId() {
		String pos = "POS001";
		return pos;
	}

	public static Logger getLog() {
		return log;
	}

	public String getCashlogyBaseUrl() {
		return cashlogyBaseUrl;
	}

	public String getCashlogyUser() {
		return cashlogyUser;
	}

	public String getCashlogyPassword() {
		return cashlogyPassword;
	}

	public String getDeviceId() {
		return deviceId;
	}

	protected int getPaymentId() {
		return this.paymentId;
	}
}