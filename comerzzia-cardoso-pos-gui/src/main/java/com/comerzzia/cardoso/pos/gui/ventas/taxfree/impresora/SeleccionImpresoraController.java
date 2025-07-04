package com.comerzzia.cardoso.pos.gui.ventas.taxfree.impresora;

import java.net.URL;
import java.util.ResourceBundle;

import javax.print.PrintService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;

import com.comerzzia.cardoso.pos.gui.ventas.taxfree.TaxfreeController;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

@Controller
public class SeleccionImpresoraController extends WindowController {

	public static final String PARAM_PRINTSERVICE = "printService";

	@FXML
	ComboBox<String> cbImpresora;

	protected ObservableList<String> impresoras;

	private PrintService[] ps;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ps = (PrintService[]) getDatos().get(TaxfreeController.PARAM_IMPRESORAS);
		impresoras = FXCollections.observableArrayList();
		for (PrintService printService : ps) {
			impresoras.add(printService.getName());
		}
		cbImpresora.setItems(impresoras);
	}

	@Override
	public void initializeFocus() {
	}

	@FXML
	public void accionAceptar() throws Exception {

		if (cbImpresora.getSelectionModel().getSelectedItem() == null || StringUtils.isBlank(cbImpresora.getSelectionModel().getSelectedItem())) {
			VentanaDialogoComponent.crearVentanaAviso("Debe seleccionar una impresora para imprimir el su ticket TaxFree", getStage());
		}
		else {
			String nombreImpresora = cbImpresora.getSelectionModel().getSelectedItem();
			PrintService servicioImpresion = null;
			for (PrintService printService : ps) {
				if (nombreImpresora.equals(printService.getName())) {
					servicioImpresion = printService;
				}
			}
			getDatos().put(PARAM_PRINTSERVICE, servicioImpresion);
			getStage().close();
		}

	}
	
	public void accionCancelar() {
		getDatos().put(TaxfreeController.PARAM_ES_CANCELADO,true);
		getStage().close();
	}

}
