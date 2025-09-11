package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.nif;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.util.config.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class PagoNifPtController extends WindowController {

	public static final String PAGO_COD_PAIS_PT = "PT";

	public static final String PAGO_NIF = "NIF";
	public static final String PAGO_NIF_DOC_DEFAULT = "999999990";
	public static final String CANCELADO = "CANCELADO";

	@Autowired
	private TiposIdentService tiposIdentService;

	@FXML
	protected TextField tfNif;

	@FXML
	protected Button btAceptar;

	@FXML
	protected Button btCancelar;

	@FXML
	protected Label lbTitulo, lbMensajeError;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		lbTitulo.setText(I18N.getTexto("INSERTAR NIF DEL CLIENTE"));

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registraEventoTeclado(tecla -> {
			if (tecla.getCode().equals(KeyCode.ENTER)) {
				accionAceptar();
				tecla.consume();
			}
		}, KeyEvent.KEY_RELEASED);
		registraEventoTeclado(tecla -> {
			if (tecla.getCode().equals(KeyCode.ESCAPE)) {
				accionAceptar();
			}
		}, KeyEvent.KEY_RELEASED);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {

		if (datos.containsKey(PAGO_NIF)) {
			tfNif.setText((String) datos.get(PAGO_NIF));
			datos.remove(PAGO_NIF);
		}
		else {
			tfNif.setText("");
		}

		lbMensajeError.setText("");

	}

	@Override
	public void initializeFocus() {
		tfNif.requestFocus();
	}

	@FXML
	void accionAceptar() {

		String nif = tfNif.getText();
		try {
			List<TiposIdentBean> tiposDocumentos = tiposIdentService.consultarTiposIdent(null, true, PAGO_COD_PAIS_PT);
			TiposIdentBean tipoDocumento = tiposDocumentos.stream().filter(t -> t.getCodTipoIden().equalsIgnoreCase("NIF")).findFirst().orElse(null);
			if (tipoDocumento != null && tipoDocumento.getClaseValidacion() != null) {
				IValidadorDocumentoIdentificacion validarDocumento = (IValidadorDocumentoIdentificacion) Class.forName(tipoDocumento.getClaseValidacion()).newInstance();
				if (!validarDocumento.validarDocumentoIdentificacion(nif)) {
					lbMensajeError.setText(I18N.getTexto("El documento introducido no es válido. Por favor, introduzca un documento válido."));
					return;
				}
			}
		}
		catch (Exception ignored) {
		}

		if (StringUtils.isNotBlank(nif)) {
			getDatos().put(PAGO_NIF, nif);
		}

		getStage().close();

	}

	@FXML
	public void cancelar() {
		getDatos().put(CANCELADO, Boolean.TRUE);
		getStage().close();
	}
}
