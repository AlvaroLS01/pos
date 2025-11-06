package com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.resource.spi.IllegalStateException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.virtualmoney.client.AccountsApi;
import com.comerzzia.api.virtualmoney.client.AccountsBalancesApi;
import com.comerzzia.api.virtualmoney.client.PinTarjetasWalletApi;
import com.comerzzia.api.virtualmoney.client.model.AccountDTO;
import com.comerzzia.api.virtualmoney.client.model.BalanceEvent;
import com.comerzzia.api.virtualmoney.client.model.BalanceRedemptionCancelRequest;
import com.comerzzia.api.virtualmoney.client.model.BalanceTransactionRequest;
import com.comerzzia.api.virtualmoney.client.model.NewPinRequest;
import com.comerzzia.api.virtualmoney.client.model.OperationParam;
import com.comerzzia.api.virtualmoney.client.model.OperationRequestDTO;
import com.comerzzia.api.virtualmoney.client.model.PinStatusResponse;
import com.comerzzia.api.virtualmoney.client.model.RestrictionCheckResult;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.params.AccountParamDto;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.params.BalanceParamDto;
import com.comerzzia.dinosol.pos.services.ticket.lineas.TarjetaRegaloDto;
import com.comerzzia.dinosol.pos.util.xml.ObjectParseUtil;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.payments.PaymentDto;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.gson.Gson;

@Component
@Scope("prototype")
public class VirtualMoneyManager extends BasicPaymentMethodManager {
	
	private Logger log = Logger.getLogger(VirtualMoneyManager.class);
	
	public static String PARAM_NUMERO_TARJETA = "VirtualMoneyManager.NumeroTarjeta";
	
	public static String PARAM_RESULTADO = "VirtualMoneyManager.Resultado";
	
	@Autowired
	private Sesion sesion;
    
    @Autowired
    private ComerzziaApiManager apiManager;
    
    @Autowired
    private VariablesServices variablesServices;
	
	protected String tipoCuenta;

	@Override
	public boolean pay(BigDecimal amount) throws PaymentException {
		log.debug("pay() - Iniciamos la petición para pagar " + amount);
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		
		String numeroTarjeta = (String) parameters.get(PARAM_NUMERO_TARJETA);
		
		comprobarPagoUnicoTarjeta(numeroTarjeta);
		
		try {
			AccountsBalancesApi accountsBalancesApi = getAccountsBalancesApi();
			
			AccountsApi accountsApi = getAccountsApi();
			
			AccountDTO account = consultarSaldo(numeroTarjeta);
			
			List<String> items = getItems(new BigDecimal(1));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			List<RestrictionCheckResult> restrictions = accountsApi.checkAccountRestrictions(tipoCuenta, numeroTarjeta, format.format(new Date()), items);
			
			if(restrictions != null && !restrictions.isEmpty()) {
				String msgError = "";
				for(RestrictionCheckResult restriction : restrictions) {
					msgError = msgError + " · " + restriction.getError() + " (artículo " +  restriction.getDescription() + ")" + System.lineSeparator();
				}
				throw new PaymentException(I18N.getTexto("La venta no cumple con las siguientes restricciones: ") + System.lineSeparator() + msgError);
			}
			
			BalanceTransactionRequest request = new BalanceTransactionRequest();
			request.setTransactionDate(new Date());
			request.setAmount(amount.abs().negate());
			request.setDocument(ticket.getCabecera().getCodTicket());
			request.setDocumentLine(paymentId);
			request.setUserId(sesion.getSesionUsuario().getUsuario().getIdUsuario());
			request.setStoreId(sesion.getAplicacion().getCodAlmacen());
			request.setTerminalId(sesion.getAplicacion().getCodCaja());
			request.setItems(items);
			
			BalanceEvent balanceEvent = accountsBalancesApi.accountBalanceNewTransaction(account.getId(), account.getPrincipaltBalance().getBalanceId(), request);
			log.debug("pay() - " + balanceEvent);
			Map<String, Object> respuestaDto = ObjectParseUtil.introspect(balanceEvent);

			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
			for (String key : respuestaDto.keySet()) {
				event.addExtendedData(key, respuestaDto.get(key));
			}
			event.addExtendedData("accountId", account.getId());
			event.addExtendedData("accountBalanceId", account.getPrincipaltBalance().getBalanceId());
			event.addExtendedData("numeroTarjeta", numeroTarjeta);
			event.addExtendedData("balanceAmount", account.getPrincipaltBalance().getBalance().subtract(amount));
			event.addExtendedData("fecha", respuestaDto.get("eventDate"));
			event.addExtendedData("numeroOperacion", respuestaDto.get("accountTransactionUid"));
			getEventHandler().paymentOk(event);

			log.debug("pay() - Finalizada la petición de pago.");
			
			return true;
		}
		catch (Exception e) {
			log.error("pay() - Ha habido un error al pagar: " + e.getMessage(), e);

			throw new PaymentException(I18N.getTexto("Ha habido un error al realizar la petición de pago: " + e.getMessage()), e, paymentId, this);
		}
	}

	@SuppressWarnings("unchecked")
	protected void comprobarPagoUnicoTarjeta(String numeroTarjeta) throws PaymentException {
		List<PagoTicket> pagos = (List<PagoTicket>) ticket.getPagos();
		
		if(pagos != null && !pagos.isEmpty()) {
			for(PagoTicket pago : pagos) {
				String numeroTarjetPago = (String) pago.getExtendedData("numeroTarjeta");
				if(StringUtils.isNotBlank(numeroTarjetPago) && numeroTarjetPago.equals(numeroTarjeta)) {
					throw new PaymentException(I18N.getTexto("Esta tarjeta ya ha sido usada en esta venta. Borre el pago si desea modificar el importe."));
				}
			}
		}
	}

	@Override
	public boolean returnAmount(BigDecimal amount) throws PaymentException {
		log.debug("returnAmount() - Iniciamos la petición para devolver " + amount);
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		
		String numeroTarjeta = (String) parameters.get(PARAM_NUMERO_TARJETA);
		
		try {
			AccountsBalancesApi accountsBalancesApi = getAccountsBalancesApi();
			
			AccountsApi accountsApi = getAccountsApi();
			
			AccountDTO account = consultarSaldo(numeroTarjeta);
			
			List<String> items = getItems(new BigDecimal(-1));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			List<RestrictionCheckResult> restrictions = accountsApi.checkAccountRestrictions(tipoCuenta, numeroTarjeta, format.format(new Date()), items);
			
			if(restrictions != null && !restrictions.isEmpty()) {
				String msgError = "";
				for(RestrictionCheckResult restriction : restrictions) {
					msgError = msgError + " · " + restriction.getError() + " (artículo " +  restriction.getDescription() + ")" + System.lineSeparator();
				}
				throw new PaymentException(I18N.getTexto("La devolución no cumple con las siguientes restricciones: ") + System.lineSeparator() + msgError);
			}
			
			BalanceTransactionRequest request = new BalanceTransactionRequest();
			request.setTransactionDate(new Date());
			request.setAmount(amount.abs());
			request.setDocument(ticket.getCabecera().getCodTicket());
			request.setDocumentLine(paymentId);
			request.setUserId(sesion.getSesionUsuario().getUsuario().getIdUsuario());
			request.setStoreId(sesion.getAplicacion().getCodAlmacen());
			request.setTerminalId(sesion.getAplicacion().getCodCaja());
			request.setItems(items);
			
			BalanceEvent balanceEvent = accountsBalancesApi.accountBalanceNewTransaction(account.getId(), account.getPrincipaltBalance().getBalanceId(), request);
			log.debug("returnAmount() - " + balanceEvent);
			Map<String, Object> respuestaDto = ObjectParseUtil.introspect(balanceEvent);

			PaymentOkEvent event = new PaymentOkEvent(this, paymentId, amount);
			for (String key : respuestaDto.keySet()) {
				event.addExtendedData(key, respuestaDto.get(key));
			}
			event.addExtendedData("accountId", account.getId());
			event.addExtendedData("accountBalanceId", account.getPrincipaltBalance().getBalanceId());
			event.addExtendedData("numeroTarjeta", numeroTarjeta);
			event.addExtendedData("balanceAmount", account.getPrincipaltBalance().getBalance().subtract(amount));
			getEventHandler().paymentOk(event);

			log.debug("returnAmount() - Finalizada la petición de pago.");
			return true;
		}
		catch (Exception e) {
			log.error("returnAmount() - Ha habido un error al pagar: " + e.getMessage(), e);

			throw new PaymentException(I18N.getTexto("Ha habido un error al realizar la petición de pago: " + e.getMessage()), e, paymentId, this);
		}
	}

	@Override
	public boolean cancelPay(PaymentDto payment) throws PaymentException {
		log.debug("cancelPay() - Iniciamos la petición para cancelar el pago" + payment.getPaymentId());
		
		PaymentInitEvent initEvent = new PaymentInitEvent(this);
		getEventHandler().paymentInitProcess(initEvent);
		
		try {
			AccountsBalancesApi accountsBalancesApi = getAccountsBalancesApi();
			
			Long accountId = (Long) payment.getExtendedData().get("accountId");
			Long accountBalanceId = (Long) payment.getExtendedData().get("accountBalanceId");
			String transactionUid = (String) payment.getExtendedData().get("accountTransactionUid");
			String document = (String) payment.getExtendedData().get("document");
			BigDecimal amount = payment.getAmount().abs().negate();
			
			BalanceRedemptionCancelRequest request = new BalanceRedemptionCancelRequest();
			request.setAccountTransactionUid(transactionUid);
			request.setAmount(amount);
			request.setDocument(document);
			request.setCancelUserId(sesion.getSesionUsuario().getUsuario().getIdUsuario());
			
			BalanceEvent response = accountsBalancesApi.accountBalanceTransactionCancel(accountId, accountBalanceId, request);
			log.debug("cancelPay() - " + response);

			PaymentOkEvent event = new PaymentOkEvent(this, payment.getPaymentId(), payment.getAmount());
			event.setCanceled(true);
			getEventHandler().paymentOk(event);

			log.debug("cancelPay() - Finalizada la petición de pago.");
			return true;
		}
		catch (Exception e) {
			log.error("cancelPay() - Ha habido un error al pagar: " + e.getMessage(), e);

			throw new PaymentException(I18N.getTexto("Ha habido un error al realizar la petición de pago: " + e.getMessage()), e, paymentId, this);
		}
	}

	@Override
	public boolean cancelReturn(PaymentDto payment) throws PaymentException {
		return false;
	}
	
	public AccountDTO consultarSaldo(String numeroTarjeta) throws Exception {
		log.debug("consultarSaldo() - Consultando saldo de la tarjeta " + numeroTarjeta);
		
		AccountsApi accountsApi = getAccountsApi();
		AccountDTO account = accountsApi.getAccountByCode(tipoCuenta, numeroTarjeta);
		return account;
	}

	protected AccountsApi getAccountsApi() throws EmpresaException, Exception {
		DatosSesionBean datosSesion = new DatosSesionBean();
		datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
		datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
		datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
		AccountsApi accountsApi = apiManager.getClient(datosSesion, "AccountsApi");
		return accountsApi;
	}
	
	protected AccountsBalancesApi getAccountsBalancesApi() throws EmpresaException, Exception {
		DatosSesionBean datosSesion = new DatosSesionBean();
		datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
		datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
		datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
		AccountsBalancesApi accountsApi = apiManager.getClient(datosSesion, "AccountsBalancesApi");
		return accountsApi;
	}
	
	protected PinTarjetasWalletApi getPinTarjetasWalletApi() throws EmpresaException, Exception {
		DatosSesionBean datosSesion = new DatosSesionBean();
		datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
		datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
		datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
		PinTarjetasWalletApi pinTarjetasWalletApi = apiManager.getClient(datosSesion, "PinTarjetasWalletApi");
		return pinTarjetasWalletApi;
	}
	
	@Override
	public void setConfiguration(PaymentMethodConfiguration configuration) {
		super.setConfiguration(configuration);
		tipoCuenta = configuration.getConfigurationProperty("accountTypeId");
	}

	@SuppressWarnings("unchecked")
	protected List<String> getItems(BigDecimal sign) {
		List<String> items = new ArrayList<String>();
		for(LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			String codArticulo = linea.getCodArticulo();
			if(!items.contains(codArticulo) && hayArticuloTicket(codArticulo, sign)) {
				items.add(codArticulo);
			}
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	private boolean hayArticuloTicket(String codArticulo, BigDecimal sign) {
		BigDecimal cantidadArticulo = BigDecimal.ZERO;
		for(LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
			if(linea.getCodArticulo().equals(codArticulo)) {
				cantidadArticulo = cantidadArticulo.add(linea.getCantidad());
			}
		}
		return BigDecimalUtil.isMayorACero(cantidadArticulo.multiply(sign));
	}

	public AccountDTO activate(TarjetaRegaloDto tarjetaRegaloDto) throws Exception {
		AccountsApi accountsApi = getAccountsApi();
		
		OperationRequestDTO request = new OperationRequestDTO();
		request.setOperationCode("ACTIVAR");
		OperationParam paramsItem = new OperationParam();
		paramsItem.setParamName("NewAccount");
		paramsItem.setClassName("com.comerzzia.api.virtualmoney.service.proxies.accounts.dto.NewAccount");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		AccountParamDto accountParamDto = new AccountParamDto();
		accountParamDto.setStartDate(sdf.format(new Date()));		
		Date fechaCaducidad = tarjetaRegaloDto.getFechaCaducidad();
		if(fechaCaducidad == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(2050, 01, 01);
			fechaCaducidad = calendar.getTime();
		}
		accountParamDto.setEndDate(sdf.format(fechaCaducidad));
		
		List<BalanceParamDto> balances = new ArrayList<BalanceParamDto>();
		BalanceParamDto balance = new BalanceParamDto("MONEY", tarjetaRegaloDto.getSaldo());
		balances.add(balance);
		accountParamDto.setInitialBalances(balances);
		
		Gson gson = new Gson();
		String json = gson.toJson(accountParamDto);
		
		paramsItem.setObject(json.getBytes());
		
		request.addParamsItem(paramsItem);
		
		AccountDTO accountOperation = accountsApi.accountOperation(tarjetaRegaloDto.getIdTarjeta(), request);
		
		log.debug("activate() - Result: " + accountOperation);
		
		if(accountOperation.getStateId().equals(10)) {
			throw new IllegalStateException(I18N.getTexto("No se ha podido activar la tarjeta. Contacte con el administrador"));
		}
		
		return accountOperation;
	}
	
	public PinStatusResponse solicitarPinTarjetaRegalo(TarjetaRegaloDto tarjetaRegaloDto) throws EmpresaException, Exception {
		PinTarjetasWalletApi api = getPinTarjetasWalletApi();
		
		NewPinRequest request = new NewPinRequest();
		request.setOrigenSolicitud("POS-" + sesion.getAplicacion().getCodAlmacen() + "-" + sesion.getAplicacion().getCodCaja());
		
		Integer diasCaducidad = variablesServices.getVariableAsInteger("X_TR.DIAS_CADUCIDAD_PIN");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, diasCaducidad);
		Date fechaCaducidad = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fechaCaducidadFormateada = sdf.format(fechaCaducidad);
		request.setFechaCaducidad(fechaCaducidadFormateada);
		
		PinStatusResponse response = api.solicitarPin(tipoCuenta, tarjetaRegaloDto.getNumeroTarjeta(), request);
		
		log.debug("solicitarPinTarjetaRegalo() - Respuesta de solicitud de PIN: " + response);
		
		return response;
	}
	
	public String getTipoCuenta() {
		return tipoCuenta;
	}

}
