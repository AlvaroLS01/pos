package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.descuentogeneral;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class DescuentoGeneralController extends WindowController {

	private static Logger log = Logger.getLogger(DescuentoGeneralController.class);
	public static final String PARAMETRO_DESCUENTO = "DESCUENTO_GENERAL";

	@FXML
	protected TextField tfDescuento;

	protected TicketManager ticketManager;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// Listener para que el descuento sea un numero entero entre 0 y 100
		tfDescuento.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				tfDescuento.setText(newValue.replaceAll("[^\\d]", ""));
			}
			else {
				int enteredValue = Integer.parseInt(newValue);
				if (enteredValue < 0 || enteredValue > 100) {
					tfDescuento.setText(oldValue);
				}
			}
		});
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
		tfDescuento.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfDescuento.setText("");
	}

	public void aceptarIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			aceptar();
		}
	}

	public void aceptar() {
		try {
			if (StringUtils.isNotBlank(tfDescuento.getText())) {
				BigDecimal dto = new BigDecimal(tfDescuento.getText());

				// Si el descuento es menor o igual a 0 y mayor o igual que 100, mostramos el aviso y no hacemos nada
				if (!(BigDecimalUtil.isMayorACero(dto) && BigDecimalUtil.isMenor(dto, BigDecimalUtil.CIEN))) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El valor del descuento tiene que estar entre 1 y 99"), getStage());
					return;
				}

				getDatos().put(PARAMETRO_DESCUENTO, new BigDecimal(tfDescuento.getText()));
				getStage().close();
			}
		}
		catch (Exception e) {
			log.error("aceptar() - Excepción controlada : " + e.getCause());
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("El valor del descuento tiene que estar entre 0 y 100, y no tener decimales."), e);
		}
	}

}
