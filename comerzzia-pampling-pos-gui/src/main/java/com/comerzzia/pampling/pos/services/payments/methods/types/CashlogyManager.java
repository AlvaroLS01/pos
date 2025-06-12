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
import com.comerzzia.tier.librerias.cashlogyclient.model.StatusSaleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.ResourceAccessException;

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
	        log.debug("cashlogyActivo = N, delegando a ContadoManager.pay()");
	        return super.pay(amount);
	    }

	    // 1) Login
	    log.debug("→ PAY inicio: urlBase=" + cashlogyBaseUrl + " user=" + cashlogyUser + " deviceId=" + deviceId);
	    CashlogyClient client = new CashlogyClient(cashlogyBaseUrl);
	    LoginResponse loginResp = client.login(cashlogyUser, cashlogyPassword);
	    log.debug("← loginResp.result=" + loginResp.getResult() + " token=" + loginResp.getToken());
	    if (!"OK".equalsIgnoreCase(loginResp.getResult())) {
	        throw new PaymentException("Error en login Cashlogy: " + loginResp.getMensajeError());
	    }
	    String token = loginResp.getToken();

	    // 2) Preparar petición de venta
	    int cents = amount.multiply(new BigDecimal(100)).intValue();
	    String operationId = generateOperationId();
	    String posId       = getPosId();
	    SaleRequest saleReq = new SaleRequest(deviceId, operationId, posId, cents, "EUR");

	    // 3) Serializar y loguear JSON completo de petición
	    String saleJson = "{}";
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        saleJson = mapper.writeValueAsString(saleReq);
	    } catch (JsonProcessingException e) {
	        log.warn("No se pudo serializar SaleRequest a JSON para debug", e);
	    }
            String saleUrl = cashlogyBaseUrl + "/api/MiddlewareCommand/Sale";
            log.debug("→ POST a " + saleUrl + " con payload: " + saleJson);

            // 4) Enviar venta con gestión de timeout
            SaleResponse saleResp;
            try {
                saleResp = client.sale(saleReq, token);
            } catch (ResourceAccessException timeoutEx) {
                log.error("Timeout en llamada Sale, intentando cancelSale", timeoutEx);
                CancelSaleRequest cancelReq = new CancelSaleRequest(deviceId, operationId, posId);
                try {
                    client.cancelSale(cancelReq, token);
                } catch (Exception cancelEx) {
                    log.warn("Error al llamar a cancelSale tras el timeout", cancelEx);
                }
                throw new PaymentException("Timeout de comunicacion con Cashlogy", timeoutEx);
            }
            log.debug("← saleResp.result=" + saleResp.getResult()
                + " deposited=" + saleResp.getTotalDeposited()
                + " dispensed=" + saleResp.getTotalDispensed());

	    // 5) Si está en progreso, hacer polling hasta OK o error
	    if ("TRANSACTION_IN_PROGRESS".equalsIgnoreCase(saleResp.getResult())) {
	        log.debug("Venta en progreso: esperando completación...");
	        long deadline = System.currentTimeMillis() + 10_000; // timeout 10s
	        while (System.currentTimeMillis() < deadline) {
	            try {
	                Thread.sleep(500);
	            } catch (InterruptedException ie) {
	                Thread.currentThread().interrupt();
	                break;
	            }
	            StatusSaleResponse st = client.statusSale(token);
	            log.debug("← statusSale.result=" + st.getResult());
	            if ("OK".equalsIgnoreCase(st.getResult())) {
	                saleResp.setTotalDeposited(st.getTotalDeposited());
	                saleResp.setTotalDispensed(st.getTotalDispensed());
	                break;
	            }
	            if (!"TRANSACTION_IN_PROGRESS".equalsIgnoreCase(st.getResult())) {
	                throw new PaymentException("Error en venta Cashlogy: " + st.getMensajeError());
	            }
	        }
	    }

	    // 6) Comprobar resultado final
	    if (!"OK".equalsIgnoreCase(saleResp.getResult())) {
	        throw new PaymentException("Error en venta Cashlogy: " + saleResp.getMensajeError());
	    }

	    // 7) Disparar evento OK
	    BigDecimal deposited = new BigDecimal(saleResp.getTotalDeposited()).divide(new BigDecimal(100));
	    log.debug("Venta completada: deposited=" + deposited);
	    getEventHandler().paymentOk(new PaymentOkEvent(this, getPaymentId(), deposited));
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