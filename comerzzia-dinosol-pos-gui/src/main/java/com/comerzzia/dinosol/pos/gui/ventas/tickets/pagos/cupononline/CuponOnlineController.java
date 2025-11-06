package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.cupononline;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.librerias.gtt.client.dto.SaldoEstadoResponseDTO;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.payments.methods.prefijos.PrefijosTarjetasService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.GttManager;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class CuponOnlineController extends WindowController {

	private Logger log = Logger.getLogger(CuponOnlineController.class);

	public static final String PARAM_GTT_MANAGER = "CuponOnlineController.GttManager";
	public static final String PARAM_TICKET_MANAGER = "CuponOnlineController.TicketManager";
	public static final String PARAM_COD_MEDIO_PAGO = "CuponOnlineController.paramMedioPago";
	public static final String PARAM_NUM_TARJETA = "CuponOnlineController.paramNumTarjeta";

	private static String PREFIJO_CUPON = "2700";

	@FXML
	private TextField tfNumeroTarjeta;

	@Autowired
	private PrefijosTarjetasService prefijosTarjetasService;

	private GttManager gttManager;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfNumeroTarjeta.setUserData(keyboardDataDto);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfNumeroTarjeta.clear();
		tfNumeroTarjeta.getStyleClass().remove("error-formulario");

		gttManager = (GttManager) getDatos().get(PARAM_GTT_MANAGER);
	}

	@Override
	public void initializeFocus() {
		tfNumeroTarjeta.requestFocus();
	}

	public void buscarTarjeta() {
		log.debug("buscarTarjeta() - Iniciando proceso de buscar tarjeta.");

		tfNumeroTarjeta.getStyleClass().remove("error-formulario");

		String numeroTarjeta = tfNumeroTarjeta.getText();

		if (StringUtils.isBlank(numeroTarjeta)) {
			log.debug("buscarTarjeta() - No se ha escrito nada en el campo.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe introducir un número de tarjeta."), getStage());
			tfNumeroTarjeta.getStyleClass().add("error-formulario");
			return;
		}

		numeroTarjeta = PREFIJO_CUPON + StringUtils.leftPad(numeroTarjeta, 9, '0');

		String codMedioPago = prefijosTarjetasService.getMedioPagoPrefijo(numeroTarjeta);

		if (StringUtils.isBlank(codMedioPago)) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El prefijo del cupón introducido no se corresponde con ningún medio de pago asociado."), getStage());
			tfNumeroTarjeta.getStyleClass().add("error-formulario");
			tfNumeroTarjeta.requestFocus();
			tfNumeroTarjeta.selectAll();
			return;
		}

		if (gttManager != null) {
			realizarPeticion(codMedioPago, numeroTarjeta);
		}
		else {
			log.error("buscarTarjeta() - No se ha encontrado el manager de GTT");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha encontrado la configuración necesaria para los cupones online. Contacte con el administrador."), getStage());
		}

		 
	}

	protected void realizarPeticion(String codMedioPago, String cupon) {
		new BackgroundTask<SaldoEstadoResponseDTO>(){
			@Override
			protected SaldoEstadoResponseDTO call() throws Exception {

				return gttManager.getSaldoAndEstado(cupon);
			}
			
			protected void succeeded() {
				super.succeeded();
				
				SaldoEstadoResponseDTO saldo = getValue();
				
				BigDecimal importeLineasNoRestringidas = gttManager.getImporteLineasNoRestringidas(saldo.getRestriccion());
				
				log.debug("realizarPeticion() - Importe de las líneas no afectadas por la restricción: " + importeLineasNoRestringidas);
				log.debug("realizarPeticion() - Importe del cupón: " + saldo.getSaldo());
				
				if(BigDecimalUtil.isMayor(importeLineasNoRestringidas, saldo.getSaldo())) {
					getDatos().put(PARAM_COD_MEDIO_PAGO, codMedioPago);
					getDatos().put(PARAM_NUM_TARJETA, cupon);
					getStage().close();
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede aplicar el talón porque existen restricciones que no permiten utilizar esta forma de pago."), getStage());
				}
			};
			
			protected void failed() {
				super.failed();
				
				Throwable e = getException();
				log.error("failed() - Ha habido un error al recuperar el estado del cupón: " + e.getMessage(), e);
				
				VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
			};
		}.start();
	}

	public void buscarTarjetaIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			buscarTarjeta();
		}
	}

}
