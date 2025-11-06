package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.tarjetas;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.payments.methods.prefijos.PrefijosTarjetasService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class TarjetasPagoController extends WindowController {
	
	private Logger log = Logger.getLogger(TarjetasPagoController.class);
	
	public static final String PARAM_COD_MEDIO_PAGO = "paramMedioPago";

	public static final String PARAM_NUM_TARJETA = "paramNumTarjeta";
	
	@FXML
	private TextField tfNumeroTarjeta;
	
	@Autowired
	private PrefijosTarjetasService prefijosTarjetasService;

	@Override
    public void initialize(URL url, ResourceBundle rb) {
    }

	@Override
    public void initializeComponents() throws InitializeGuiException {		
		setShowKeyboard(false);
    }

	@Override
    public void initializeForm() throws InitializeGuiException {
		tfNumeroTarjeta.clear();
		tfNumeroTarjeta.getStyleClass().remove("error-formulario");
    }

	@Override
    public void initializeFocus() {
		tfNumeroTarjeta.requestFocus();
    }
	
	public void buscarTarjeta() {
		log.debug("buscarTarjeta() - Iniciando proceso de buscar tarjeta.");
		
		tfNumeroTarjeta.getStyleClass().remove("error-formulario");
		
		String numeroTarjeta = tfNumeroTarjeta.getText();
		
		if(Dispositivos.getInstance().getFidelizacion().isPrefijoTarjeta(numeroTarjeta)) {
			log.debug("buscarTarjeta() - La tarjeta es de Fidelización,debe introducirse en la pantalla de pagos.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha escaneado su QR de la App cliente Hiperdino. Para su registro, deberá volver a la pantalla de ventas. Recuerde que para volver a la pantalla de ventas, debe eliminar las formas de pago"), getStage());
			tfNumeroTarjeta.getStyleClass().add("error-formulario");
			return;
		}
		
		if(StringUtils.isBlank(numeroTarjeta)) {
			log.debug("buscarTarjeta() - No se ha escrito nada en el campo.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe introducir un número de tarjeta."), getStage());
			tfNumeroTarjeta.getStyleClass().add("error-formulario");
			return;
		}
		
		if(numeroTarjeta.length() == 12) {
			numeroTarjeta = "0" + numeroTarjeta;
		}
		
		String codMedioPago = prefijosTarjetasService.getMedioPagoPrefijo(numeroTarjeta);
		
		if(StringUtils.isBlank(codMedioPago)) {
			log.debug("buscarTarjeta() - Iniciando proceso de buscar tarjeta.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El prefijo de la tarjeta introducida no se corresponde con ningún medio de pago asociado."), getStage());
			tfNumeroTarjeta.getStyleClass().add("error-formulario");
			tfNumeroTarjeta.requestFocus();
			tfNumeroTarjeta.selectAll();
			return;
		}

		getDatos().put(PARAM_COD_MEDIO_PAGO, codMedioPago);
		getDatos().put(PARAM_NUM_TARJETA, numeroTarjeta);
		getStage().close();
	}
	
	public void buscarTarjetaIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			buscarTarjeta();
        }
	}

}
