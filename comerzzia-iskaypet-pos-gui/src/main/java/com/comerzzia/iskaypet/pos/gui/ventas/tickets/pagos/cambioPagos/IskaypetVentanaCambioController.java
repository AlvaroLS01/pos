package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.cambioPagos;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.gui.ventas.tickets.pagos.cambioPagos.VentanaCambioController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Primary
@Controller
public class IskaypetVentanaCambioController extends VentanaCambioController {


	public static String ACCION_CONFIRMAR = "ACCION_CONFIRMAR";
	public static String ACCION_CANCELAR = "ACCION_CANCELAR";

	@FXML
	protected Button btnCancelar;


	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();

		Boolean mostarBotonCancelar = false;
		if(getDatos().containsKey(ACCION_CONFIRMAR)) {
			if ((Boolean) getDatos().get(ACCION_CONFIRMAR)) {
				mostarBotonCancelar = true;
			}
		}
		btnCancelar.setVisible(mostarBotonCancelar);

	}

	public void accionAceptar() {
		super.accionCancelar();
	}

	@Override
	public void accionCancelar() {
		getDatos().put(ACCION_CANCELAR, true);
		getStage().close();
	}
}
