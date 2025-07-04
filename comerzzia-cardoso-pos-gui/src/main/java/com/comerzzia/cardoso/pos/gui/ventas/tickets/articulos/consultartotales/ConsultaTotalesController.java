package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.consultartotales;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.services.pagos.worldline.ConsultaTotalesBean;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.util.format.FormatUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

@Component
public class ConsultaTotalesController extends WindowController {

	public static final String PARAMETRO_ENTRADA = "parametroEntrada";

	@FXML
	private Button btCancelar;

	@FXML
	private TextField tfNumVentas, tfImporteVentas, tfNumAnulaciones, tfImporteAnulaciones, tfNumDevoluciones, tfImporteDevoluciones, tfTotal;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ConsultaTotalesBean datos = (ConsultaTotalesBean) getDatos().get(PARAMETRO_ENTRADA);

		tfNumVentas.setText(datos.getNumVentas().toString());
		tfImporteVentas.setText(FormatUtil.getInstance().formateaImporte(datos.getTotalVentas()));
		tfNumAnulaciones.setText(datos.getNumAnulaciones().toString());
		tfImporteAnulaciones.setText(FormatUtil.getInstance().formateaImporte(datos.getTotalAnulaciones()));
		tfNumDevoluciones.setText(datos.getNumDevoluciones().toString());
		tfImporteDevoluciones.setText(FormatUtil.getInstance().formateaImporte(datos.getTotalDevoluciones()));

		BigDecimal total = datos.getTotalVentas().subtract(datos.getTotalDevoluciones()).subtract(datos.getTotalAnulaciones());
		tfTotal.setText(FormatUtil.getInstance().formateaImporte(total));

	}

	@Override
	public void initializeFocus() {
		btCancelar.requestFocus();
	}

}
