package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.valedevolucionmanual;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class ValeDevolucionManualController extends Controller {
	public static final String IMPORTE_DEV_MANUAL = "ImporteManual";
	public static final String NUM_TARJETA_DEV_MANUAL = "numTarjeta";
	public static final String CANCELADO = "CANCELADO";

	@FXML
	protected TextField tfTarjeta;
	@FXML
	protected TextFieldImporte tfImporte;

	@FXML
	protected Button btAceptar, btCancelar;

	@FXML
	protected Label lbTitulo, lbError;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registraEventoTeclado(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent tecla){
				if(tecla.getCode().equals(KeyCode.ENTER)){
					accionAceptar();
					tecla.consume();
				}
			}
		}, KeyEvent.KEY_RELEASED);
		registraEventoTeclado(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent tecla){
				if(tecla.getCode().equals(KeyCode.ESCAPE)){
					accionCancelar();
				}
			}
		}, KeyEvent.KEY_RELEASED);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		lbError.setText("");
		tfImporte.setText("");
		tfTarjeta.setText((String) getDatos().get(NUM_TARJETA_DEV_MANUAL));
	}

	@Override
	public void initializeFocus() {
		tfImporte.requestFocus();
	}

	@FXML
	void accionAceptar() {
		if(StringUtils.isBlank(tfImporte.getText())) {
			lbError.setText(I18N.getTexto("El importe no es válido"));
			return;
		}
		BigDecimal importe = FormatUtil.getInstance().desformateaImporte(tfImporte.getText());
		importe = BigDecimalUtil.redondear(importe);
		if(BigDecimalUtil.isMenorACero(importe)) {
			lbError.setText(I18N.getTexto("El importe no puede ser menor a 0"));
			return;
		}
		getDatos().put(IMPORTE_DEV_MANUAL, importe);
		getStage().close();

	}

	@FXML
	void accionCancelar() {
		if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Va a cancelar la introducción manual del vale, \n ¿Está seguro?"), getStage())) {
			getDatos().put(CANCELADO, true);
			getStage().close();
		}
	}
}
