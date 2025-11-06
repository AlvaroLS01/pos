package com.comerzzia.dinosol.pos.gui.tarjetas.saldo;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comarch.clm.partner.dto.ExtendedBalanceInquiryResponse;
import com.comarch.clm.partner.dto.TenderRedemptionGroupDataResponse;
import com.comarch.clm.partner.exception.BpConfiguracionException;
import com.comarch.clm.partner.exception.BpRespuestaException;
import com.comarch.clm.partner.exception.BpSoapException;
import com.comerzzia.api.virtualmoney.client.model.AccountDTO;
import com.comerzzia.dinosol.librerias.gtt.client.GttException;
import com.comerzzia.dinosol.librerias.gtt.client.GttIOException;
import com.comerzzia.dinosol.librerias.gtt.client.GttServerException;
import com.comerzzia.dinosol.librerias.gtt.client.dto.SaldoResponseDTO;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.payments.methods.prefijos.PrefijosTarjetasService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.bp.BPManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.GttManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.excepciones.GttConfiguracionException;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.VirtualMoneyManager;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class DinoSaldoTarjetasController extends WindowController {

	private Logger log = Logger.getLogger(DinoSaldoTarjetasController.class);

	public static final String PARAM_TIPO_TARJETA = "param_tipo_tarjeta";

	public static String TIPO_TARJETA_BP = I18N.getTexto("Tarjeta DinoBP");

	public static String TIPO_TARJETA_GTT = I18N.getTexto("Tarjeta GTT");

	@FXML
	private Label lbSaldoTarjeta, lbEstado;

	@FXML
	private TextField tfNumeroTarjeta;
	
	@FXML
	private Button btCerrar;

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
    
    @Autowired
    private PrefijosTarjetasService prefijosTarjetasService;

	private GttManager gttManager;

	private BPManager bpManager;
	
	private VirtualMoneyManager virtualMoneyManager;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarServicioBp();
		inicializarServicioGtt();
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfNumeroTarjeta.setUserData(keyboardDataDto);
		
		addSeleccionarTodoEnFoco(tfNumeroTarjeta);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		lbSaldoTarjeta.setText("");
		lbEstado.setText("");
		tfNumeroTarjeta.clear();
	}

	@Override
	public void initializeFocus() {
		tfNumeroTarjeta.requestFocus();
	}

	private void inicializarServicioBp() {
		PaymentMethodManager manager = null;
		/* Recorremos los medios de pago con configuración */
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			log.debug("inicializarServicioBp() - Comprobando la configuración de los medios de pago");
			try {
				/* Ignoramos los medios de pago no configurados */
				if (!configuration.getConfigurationProperties().isEmpty()) {
					/* Sacamos la configuración del medio de pago, comprobando el manager */
					if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getManager() != null) {
						if (configuration.getManager() instanceof BPManager) {
							manager = configuration.getManager();
							log.debug("inicializarServicioBp() - Configuración cargada : " + manager.getPaymentCode());
						}
					}
				}
			}
			catch (Exception e) {
				String mensajeError = "Error al cargar la configuración del medio de pago BP";
				log.error("inicializarServicioBp() - " + mensajeError + " " + configuration.getPaymentCode() + " : " + e.getMessage());
			}
		}

		/* En caso de no tener el manager configurado, deberemos devolver un error */
		if (manager != null) {
			bpManager = (BPManager) manager;
			try {
				bpManager.initialize();
				log.debug("inicializarServicioBp() - Creando ticket para poder realizar la carga del salgo");
				TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
				ticketManager.nuevoTicket();
				bpManager.setTicketData(ticketManager.getTicket(), null);
			}
			catch (Exception e) {
				String mensajeError = "Error al inicializar la pantalla";
				log.error("inicializarServicioBp() - : " + mensajeError + " " + e.getMessage(), e);
			}
		}
		else {
			String mensajeError = "No se ha encontrado configuración para BP";
			log.error("inicializarServicioBp() - " + mensajeError);
		}
	}

	private void inicializarServicioGtt() {
		PaymentMethodManager manager = null;
		/* Recorremos los medios de pago con configuración */
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			try {
				/* Sacamos la configuración del medio de pago, comprobando el manager */
				if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getManager() != null && configuration.getManager() instanceof GttManager) {
					manager = configuration.getManager();
					log.debug("inicializarServicioGtt() - Configuración cargada : " + manager.getPaymentCode());
					break;
				}
			}
			catch (Exception e) {
				log.error("inicializarServicioGtt() - No se ha podido cargar la configuración de un manager: " + e.getMessage());
			}
		}

		/* En caso de no tener el manager configurado, deberemos devolver un error */
		if (manager != null) {
			gttManager = (GttManager) manager;
			try {
				gttManager.initialize();
				log.debug("inicializarServicioGtt() - Creando ticket para poder realizar la carga del salgo");
				TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
				ticketManager.nuevoTicket();
				gttManager.setTicketData(ticketManager.getTicket(), null);
			}
			catch (Exception e) {
				log.error("inicializarServicioGtt() - : Error al inicializar la pantalla " + e.getMessage(), e);
			}
		}
		else {
			log.error("inicializarServicioGtt() - No se ha encontrado configuración para GTT");
		}
	}
	
	private void inicializarServicioVirtualMoney(String numeroTarjeta) {
		String medioPago = prefijosTarjetasService.getMedioPagoPrefijo(numeroTarjeta);
		
		PaymentMethodManager manager = null;
		/* Recorremos los medios de pago con configuración */
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			try {
				/* Sacamos la configuración del medio de pago, comprobando el manager */
				if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getPaymentCode().equals(medioPago)) {
					manager = configuration.getManager();
					log.debug("inicializarServicioGtt() - Configuración cargada : " + manager.getPaymentCode());
					break;
				}
			}
			catch (Exception e) {
				log.error("inicializarServicioVirtualMoney() - No se ha podido cargar la configuración de un manager: " + e.getMessage());
			}
		}

		/* En caso de no tener el manager configurado, deberemos devolver un error */
		if (manager != null) {
			virtualMoneyManager = (VirtualMoneyManager) manager;
			try {
				virtualMoneyManager.initialize();
				log.debug("inicializarServicioVirtualMoney() - Creando ticket para poder realizar la carga del salgo");
				TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
				ticketManager.nuevoTicket();
				virtualMoneyManager.setTicketData(ticketManager.getTicket(), null);
			}
			catch (Exception e) {
				log.error("inicializarServicioVirtualMoney() - : Error al inicializar la pantalla " + e.getMessage(), e);
			}
		}
		else {
			log.error("inicializarServicioVirtualMoney() - No se ha encontrado configuración para GTT");
		}
	}

	
	public void consultarSaldoIntro(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			consultarSaldo();
		}
	
	}

	public void consultarSaldo() {
		if (StringUtils.isBlank(tfNumeroTarjeta.getText())) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe introducir un número de tarjeta."), getStage());
			btCerrar.requestFocus();
			tfNumeroTarjeta.requestFocus();
			return;
		}
	
		log.debug("consultarSaldo() - Se va a consultar el saldo de la tarjeta: " + tfNumeroTarjeta.getText());
		new ConsultarSaldoTask().start();
	}

	private ConsultaSaldoDto consultarSaldoTarjetaGtt() {
		if (gttManager == null) {
			log.error("consultarSaldoTarjetaGtt() - No se ha conseguido obtener el manager de GTT de la configuracion de medios de pago");
			return null;
		}
		
		SaldoResponseDTO saldoResponse = null;
		try {
			String numeroTarjeta = tfNumeroTarjeta.getText();
			if(numeroTarjeta.length() == 12) {
				numeroTarjeta = "0" + numeroTarjeta;
			}
			
			saldoResponse = gttManager.getSaldo(numeroTarjeta);
		} catch (GttException e) {
			log.error("consultarSaldoTarjetaGtt() - : " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
		} catch (GttIOException e) {
			String mensaje = I18N.getTexto("No se ha podido establecer la comunicación con el servidor de GTT.");
			log.error("consultarSaldoTarjetaGtt() - : " + mensaje , e);
			VentanaDialogoComponent.crearVentanaError(getStage(), mensaje, e);
		} catch (GttServerException e) {
			String mensaje = I18N.getTexto("El servidor de GTT ha devuelto un error al realizar la petición.");
			log.error("consultarSaldoTarjetaGtt() - : " + mensaje , e);
			VentanaDialogoComponent.crearVentanaError(getStage(), mensaje, e);
		} catch (GttConfiguracionException e) {
			log.error("consultarSaldoTarjetaGtt() - : " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
		}
		
		if (saldoResponse != null) {
			return new ConsultaSaldoDto(saldoResponse.getSaldo(), null);
		}

		return null;
	}

	private ConsultaSaldoDto consultarSaldoTarjetaVirtualMoney() {
		
		try {
			String numeroTarjeta = tfNumeroTarjeta.getText();
			if(numeroTarjeta.length() == 12) {
				numeroTarjeta = "0" + numeroTarjeta;
			}
			
			inicializarServicioVirtualMoney(numeroTarjeta);
			
			AccountDTO respuesta = virtualMoneyManager.consultarSaldo(numeroTarjeta);
			
			BigDecimal saldo = respuesta.getPrincipaltBalance().getBalance();
			String estado = "";
			
			if(respuesta.getState() != null) {
				String state = respuesta.getState().getStateDescription();
				if(StringUtils.isNotBlank(state)) {
					estado = state;
				}
				
				String substate = respuesta.getState().getSubStateDescription();
				if(StringUtils.isNotBlank(substate) && !state.equals(substate)) {
					estado = estado + " / " + substate;
				}
				
				if(respuesta.getStartDate() != null && respuesta.getEndDate() != null) {
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					estado = estado + " (" + format.format(respuesta.getStartDate()) + " - " + format.format(respuesta.getEndDate()) + ")";
				}
			}
			
			return new ConsultaSaldoDto(saldo, estado);
		}
		catch (Exception e) {
			log.error("virtualMoneyManager() - : " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
			return null;
		}
	}

	private ConsultaSaldoDto consultarSaldoTarjetaBP() throws BpSoapException, BpRespuestaException, BpConfiguracionException {
		String numeroTarjeta = tfNumeroTarjeta.getText();
		if(numeroTarjeta.length() == 12) {
			numeroTarjeta = "0" + numeroTarjeta;
		}
		
		ExtendedBalanceInquiryResponse saldoResponse = bpManager.getSaldo(numeroTarjeta);
		
		if ("0".equals(saldoResponse.getErrorCode())) {
			if(saldoResponse.getTenderRedemptionGroupDataList() != null) {
				for (TenderRedemptionGroupDataResponse virtual : saldoResponse.getTenderRedemptionGroupDataList()) {
					if (BPManager.VIRTUAL_MONEY_CODE.equals(virtual.getTenderRedemptionGroupCode())) {
						return new ConsultaSaldoDto(new BigDecimal(virtual.getVirtualMoneyBalance()), null);
					}
				}
			}
		}
		
		return new ConsultaSaldoDto(BigDecimal.ZERO, null);
	}

	private class ConsultarSaldoTask extends BackgroundTask<ConsultaSaldoDto> {

		@Override
		protected ConsultaSaldoDto call() throws Exception {
			ConsultaSaldoDto dto = null;
			
			String numeroTarjeta = tfNumeroTarjeta.getText();	
			if(numeroTarjeta.length() == 12) {
				numeroTarjeta = "0" + numeroTarjeta;
			}
			
			if (prefijosTarjetasService.isTarjetaBp(numeroTarjeta)) {
				dto = consultarSaldoTarjetaBP();
			}
			else if (prefijosTarjetasService.isTarjetaGtt(numeroTarjeta)) {
				dto = consultarSaldoTarjetaGtt();
			}
			else if (prefijosTarjetasService.isTarjetaVirtualMoney(numeroTarjeta)) {
				dto = consultarSaldoTarjetaVirtualMoney();
			}			
			else{
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El prefijo de la tarjeta introducida no se corresponde con ningún medio de pago asociado"), getStage());
			}
			return dto;
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			ConsultaSaldoDto dto = getValue();
			
			BigDecimal saldo = dto.getSaldo();
			if(saldo != null) {
				lbSaldoTarjeta.setText(FormatUtil.getInstance().formateaImporte(saldo) + I18N.getTexto(" €"));
			}
			else{
				lbSaldoTarjeta.setText("");
			}
			
			String estado = dto.getEstado();
			if(StringUtils.isNotBlank(estado)) {
				lbEstado.setText(estado);
			}
			else {
				lbEstado.setText("");
			}
			
			tfNumeroTarjeta.requestFocus();
			tfNumeroTarjeta.selectAll();
		}

		@Override
		protected void failed() {
			super.failed();

			Throwable exception = getException();
			String mensaje = I18N.getTexto("Ha habido un error consultando el saldo de la tarjeta: ") + System.lineSeparator() + System.lineSeparator() + exception.getMessage();
			VentanaDialogoComponent.crearVentanaError(getStage(), mensaje, exception);
			lbSaldoTarjeta.requestFocus();
			initializeFocus();

		}

	}

}
