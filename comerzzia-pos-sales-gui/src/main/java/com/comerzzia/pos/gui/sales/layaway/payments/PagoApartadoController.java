package com.comerzzia.pos.gui.sales.layaway.payments;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.service.basket.retail.RetailBasketManager;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.numerickeypad.NumericKeypad;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.layaway.items.DetalleApartadosController;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
@CzzScene
public class PagoApartadoController extends SceneController{

	public static final String PARAMETRO_IMPORTE_PAGO = "IMPORTE_PAGO";
	
	@FXML
	protected NumericKeypad tecladoNumerico;
	
	@FXML
	protected NumericTextField tfImporte;
	
	protected RetailBasketManager ticketManager;
	
	protected BigDecimal importeMaximoPago;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		initNumericKeypad(tecladoNumerico);
        //Se registra el evento para salir de la pantalla pulsando la tecla escape.
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		tfImporte.clear();
		importeMaximoPago = (BigDecimal)sceneData.get(DetalleApartadosController.PARAMETRO_IMPORTE_MAXIMO_PAGO);
		tfImporte.setText(FormatUtils.getInstance().formatAmount(importeMaximoPago));
	}

	@Override
	public void initializeFocus() {
		tfImporte.requestFocus();
	}
	
	public void accionAceptarIntro(KeyEvent e){
		if(e.getCode() == KeyCode.ENTER){
			accionAceptar();
		}
	}

	public void accionAceptar(){
		String importe = tfImporte.getText();
		BigDecimal importePago = FormatUtils.getInstance().parseAmount(importe);
		
		if(importePago != null){
			if(BigDecimalUtil.isGreaterThanZero(importePago)) {
				if(importeMaximoPago.compareTo(importePago)>=0){
					sceneData.put(PARAMETRO_IMPORTE_PAGO, importePago);
					closeSuccess();
				}
				else{
					initializeFocus();
					DialogWindowComponent.openWarnWindow(I18N.getText("El importe no debe superar el total del apartado."), getStage());
				}
			}
			else {
				DialogWindowComponent.openWarnWindow(I18N.getText("El importe debe ser mayor que cero."), getStage());
			}
		}
		else{
			initializeFocus();
			DialogWindowComponent.openWarnWindow(I18N.getText("Debe introducir un número válido."), getStage());
		}
	}

}
