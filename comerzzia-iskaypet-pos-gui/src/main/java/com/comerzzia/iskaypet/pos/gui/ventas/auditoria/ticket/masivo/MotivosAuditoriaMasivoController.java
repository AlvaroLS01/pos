package com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.masivo;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.comerzzia.pos.core.gui.POSApplication;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class MotivosAuditoriaMasivoController extends WindowController {

	public static final String TITULO_AUDITORIA = "TITULO_AUDITORIA";
	public static final String ACCION_SELECIONADA = "ACCION_SELECIONADA";

	public static final String ACCION_MOTIVOS_TOTAL = "TOTAL";

	public static final String ACCION_MOTIVOS_UNIDAD = "UNIDAD";
	public static final String CANCELADO = "CANCELADO";

	@FXML
	protected Button btTotal, btCancelar, btUnidad;

	@FXML
	protected Label lbTitulo;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		String titulo = (String) getDatos().getOrDefault(TITULO_AUDITORIA, "");
		lbTitulo.setText(titulo);
	}

	@Override
	public void initializeFocus() {

	}

	@FXML
	void accionTotal() {
		getDatos().put(ACCION_SELECIONADA, ACCION_MOTIVOS_TOTAL);
		getStage().close();
	}

	@FXML
	void accionUnidad() {
		getDatos().put(ACCION_SELECIONADA, ACCION_MOTIVOS_UNIDAD);
		getStage().close();
	}

	@FXML
	public void cancelar() {
		getDatos().put(CANCELADO, Boolean.TRUE);
		getStage().close();
	}

	public static String getAccionSeleccionada(BigDecimal cantidadTotalAnadir, String titulo, POSApplication application, Stage stage) {
		String accionSeleccionada = MotivosAuditoriaMasivoController.ACCION_MOTIVOS_UNIDAD;
		if (cantidadTotalAnadir.intValue() > 1) {
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(TITULO_AUDITORIA, titulo);
			application.getMainView().showModalCentered(MotivosAuditoriaMasivoView.class, datos, stage);
			if ((Boolean) datos.getOrDefault(MotivosAuditoriaMasivoController.CANCELADO, false)) {
				return null;
			}
			accionSeleccionada = (String) datos.get(ACCION_SELECIONADA);
		}
		return accionSeleccionada;
	}
}
