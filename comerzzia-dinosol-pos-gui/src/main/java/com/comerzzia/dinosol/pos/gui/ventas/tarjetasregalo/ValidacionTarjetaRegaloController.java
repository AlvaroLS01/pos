package com.comerzzia.dinosol.pos.gui.ventas.tarjetasregalo;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.virtualmoney.client.model.AccountDTO;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.VirtualMoneyManager;
import com.comerzzia.dinosol.pos.services.tarjetasregalo.TipoTarjetaRegaloDto;
import com.comerzzia.dinosol.pos.services.ticket.lineas.TarjetaRegaloDto;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

@Component
public class ValidacionTarjetaRegaloController extends WindowController {

	private Logger log = Logger.getLogger(ValidacionTarjetaRegaloController.class);

	public static final String PARAM_CANTIDAD= "ValidacionTarjetaRegaloController.Cantidad";

	public static final String PARAM_TIPO_TARJETA = "ValidacionTarjetaRegaloController.TipoTarjeta";

	public static final String PARAM_TARJETA_REGALO = "ValidacionTarjetaRegaloController.TarjetaRegalo";

	@FXML
	private TextField tfNumeroTarjeta;

	@FXML
	private HBox hbImporte;

	@FXML
	private TextFieldImporte tfImporte;

	@FXML
	private Button btAceptar;

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	private VirtualMoneyManager virtualMoneyManager;

	private TipoTarjetaRegaloDto tipoTarjetaActual;
	
	private BigDecimal cantidad;
	
	private AccountDTO accountDto;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarVirtualMoneyManager();
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();

		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		keyboardDataDto.setMostrar(false);
		tfNumeroTarjeta.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfNumeroTarjeta);
		addSeleccionarTodoEnFoco(tfImporte);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		limpiarPantalla();

		tipoTarjetaActual = (TipoTarjetaRegaloDto) getDatos().get(PARAM_TIPO_TARJETA);
		
		cantidad = (BigDecimal) getDatos().get(PARAM_CANTIDAD);
		
		accountDto = null;
	}

	private void limpiarPantalla() {
		tfNumeroTarjeta.clear();
		tfImporte.clear();
		hbImporte.setDisable(true);
		btAceptar.setDisable(true);
	}

	@Override
	public void initializeFocus() {
		tfNumeroTarjeta.requestFocus();
	}

	public void validarTarjeta() {
		String numeroTarjeta = tfNumeroTarjeta.getText();

		if (StringUtils.isBlank(numeroTarjeta)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe introducir un número de tarjeta."), getStage());
			tfImporte.requestFocus();
			tfNumeroTarjeta.requestFocus();
		}

		new ConsultarEstadoTarjeta(numeroTarjeta).start();
	}

	public void aceptar() {
		String numeroTarjeta = tfNumeroTarjeta.getText();
		if (StringUtils.isBlank(numeroTarjeta)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe introducir un número de tarjeta."), getStage());
			tfNumeroTarjeta.requestFocus();
		}

		String importeTexto = tfImporte.getText();
		if (StringUtils.isBlank(numeroTarjeta)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe introducir un importe."), getStage());
			tfImporte.requestFocus();
		}

		BigDecimal importe = BigDecimal.ONE;
		if(BigDecimalUtil.isMayorACero(cantidad)) {
			importe = FormatUtil.getInstance().desformateaImporte(importeTexto);
			BigDecimal importeMaximo = tipoTarjetaActual.getImporteMaximo();
			BigDecimal importeMinimo = tipoTarjetaActual.getImporteMinimo();
			
			if(BigDecimalUtil.isMayor(importe, importeMaximo)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe máximo para esta tarjeta son {0} €", FormatUtil.getInstance().formateaImporte(importeMaximo)), getStage());
				tfImporte.requestFocus();
				return;
			}
			
			if(BigDecimalUtil.isMenor(importe, importeMinimo)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe mínimo para esta tarjeta son {0} €", FormatUtil.getInstance().formateaImporte(importeMinimo)), getStage());
				tfImporte.requestFocus();
				return;
			}
		}
		
		TarjetaRegaloDto tarjeta = new TarjetaRegaloDto();
		tarjeta.setNumeroTarjeta(numeroTarjeta);
		tarjeta.setSaldo(importe);
		if(tipoTarjetaActual.getDiasVigencia() > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, tipoTarjetaActual.getDiasVigencia());
			tarjeta.setFechaCaducidad(calendar.getTime());
		}
		
		tarjeta.setIdTarjeta(accountDto.getId());
		
		getDatos().put(PARAM_TARJETA_REGALO, tarjeta);
		getStage().close();
	}

	private void inicializarVirtualMoneyManager() {
		PaymentMethodManager manager = null;
		/* Recorremos los medios de pago con configuración */
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			/* Sacamos la configuración del medio de pago, comprobando el manager */
			if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getManager() != null && configuration.getManager() instanceof VirtualMoneyManager) {
				manager = configuration.getManager();
				log.debug("inicializarVirtualMoneyManager() - Configuración cargada : " + manager.getPaymentCode());
				break;
			}
		}

		/* En caso de no tener el manager configurado, deberemos devolver un error */
		if (manager != null) {
			virtualMoneyManager = (VirtualMoneyManager) manager;
			try {
				virtualMoneyManager.initialize();
				log.debug("inicializarVirtualMoneyManager() - Creando ticket para poder realizar la carga del salgo");
				TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
				ticketManager.nuevoTicket();
				virtualMoneyManager.setTicketData(ticketManager.getTicket(), null);
			}
			catch (Exception e) {
				log.error("inicializarVirtualMoneyManager() - : Error al inicializar la pantalla " + e.getMessage(), e);
			}
		}
		else {
			log.error("inicializarVirtualMoneyManager() - No se ha encontrado configuración para Virtual Money");
		}
	}

	public void validarTarjetaIntro(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			validarTarjeta();
		}
	}

	private class ConsultarEstadoTarjeta extends BackgroundTask<AccountDTO> {
		
		private String numeroTarjeta;

		public ConsultarEstadoTarjeta(String numeroTarjeta) {
			this.numeroTarjeta = numeroTarjeta;
		}

		@Override
		protected AccountDTO call() throws Exception {
			return virtualMoneyManager.consultarSaldo(numeroTarjeta);
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			accountDto = getValue();
			if (accountDto.getStateActionId().equals(50002L) && accountDto.getStateId().equals(30)) {
				hbImporte.setDisable(false);
				btAceptar.setDisable(false);
				
				if(BigDecimalUtil.isMenorACero(cantidad)) {
					aceptar();
				}
				else {
					tfImporte.requestFocus();
				}
			}
			else {
				String mensaje = I18N.getTexto("El estado actual de la tarjeta no permite su activación. Contacte con un responsable.");
				mensaje = mensaje + System.lineSeparator() + System.lineSeparator() + I18N.getTexto("Estado: ") + accountDto.getStateId();
				VentanaDialogoComponent.crearVentanaError(mensaje, getStage());
			}
		}

		@Override
		protected void failed() {
			super.failed();

			Throwable exception = getException();
			log.error("validarTarjeta() - Ha habido un error al validar la tarjeta: " + exception.getMessage(), exception);

			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido realizar la validación de la tarjeta regalo: " + exception.getMessage()), exception);
		};
	}

}
